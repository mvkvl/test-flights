package pg.test.app;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import pg.test.dao.RAWFlightDAO;
import pg.test.dao.SourceFlightDAO;
import pg.test.model.DestFlight;
import pg.test.model.FlightId;
import pg.test.model.SourceFlight;
import pg.test.dao.DestFlightDAO;
import pg.test.dao.Hibernate;
import pg.test.dao.ProcessingDAO;

/**
 * версии (помечены тегами в git-дереве) 
 * 
 * v1 - простая (и, видимо, не вполне правильная) реализация без промежуточной отфильтрованной таблицы
 *      flightId == flight_number
 * v2 - реализация полностью отражающая поставленную задачу; минус - для правильного порядка обработки 
 *      записей из RAW-таблицы, они сортируются по возрастанию created_at (что, потенциально, 
 *      увеличивает общее время обработки)
 *      flightId == {flight_icao_code, flight_number}
 * v3 - переработана методика фильтрации; теперь данные не сортируются; 
 *      тестовые заезды не показали существенного уменьшения времени обработки
 */

/**
 * Общее описание:
 * ---------------------------------------------------------------------------------------
 *    методика:
 * 		- получаем список уникальных номеров рейсов
 * 		- распараллеливаем процесс (parallelStream) получения всех данных по каждому рейсу
 * 
 *    плюсы:
 *    	- можно параллельно обрабатывать для каждого рейса
 *      - сохранением полученного списка рейсов, легко реализуется функционал
 *        продолжения прерванной загрузки
 *         
 *    минусы:
 *    	- нужно предварительно подгружать список рейсов (память/время)
 *      - (минус реализации) из RAW-данных подружаются все записи для конкретного 
 *        flightId в List<>; возможно, есть смысл прикрутить курсор и подтягивать 
 *        данные постепенно
 *        
 *    мысли вслух:
 *      - при каждом запуске обрабатывает всю таблицу RAW-данных (если только не 
 *        продолжает прерванный сеанс); возможно, есть смысл обрабатывать только 
 *        новые данные, записанные в RAW-таблицу с момента прошлой загрузки;
 *        сейчас это не реализовано, но при необходимости можно сделать
 *
 *      - по условию задачи поля schd_arr_lt и schd_dep_lt в финальной таблице
 *        должны объявляться как NOT NULL, но, т.к. в исходной таблице есть записи 
 *        рейсов, для которых вообще не указаны запланированные даты/времена 
 *        (по крайней мере на тестовых выборках наблюдается такая ситуация),
 *        многие записи не могут быть сохранены в БД; тут надо определиться: 
 *        либо действительно не нужно сохранять такие записи, либо надо поменять 
 *        условие (пока объявил их как NULLABLE)
 *           
 *    результаты тестовых заездов:
 *      +-----------------+----------------+---------------+    
 *      | размер таблицы  |   количество   |     общее     |
 *      |   с исходными   |   уникальных   |     время     |
 *      |     данными     |     рейсов     |   обработки   |
 *      +-----------------+----------------+---------------+    
 *      |       1 000     |        957     |    00:00:19   |
 *      |       2 000     |      1 854     |    00:00:35   |
 *      |       5 000     |      4 097     |    00:01:15   |
 *      |      10 000     |      4 879     |    00:01:55   |
 *      |      25 000     |      4 950     |    00:02:36   |
 *      |      50 000     |      5 072     |    00:03:31   |
 *      |     100 000     |      6 174     |    00:06:22   |
 *      |     250 000     |      6 874     |    00:32:51   |
 *      +-----------------+----------------+---------------+    
 *
 * @author kami
 *
 */

public class Application {

	static Logger log = Logger.getLogger(Application.class.getName());

	private SessionFactory  sessionFactory;
	private RAWFlightDAO    rawDAO;
	private SourceFlightDAO sourceDAO;
	private DestFlightDAO   destDAO;
	private ProcessingDAO   procDAO;
	
	public Application(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		rawDAO    = new RAWFlightDAO(this.sessionFactory);
		sourceDAO = new SourceFlightDAO(this.sessionFactory);
		destDAO   = new DestFlightDAO(this.sessionFactory);
		procDAO   = new ProcessingDAO(this.sessionFactory);
	}

	/**
	 * process all flights with given flightId
	 * (the main method which is being run in parallel threads)
	 * 
	 * @param flightId
	 */
	private void processFlight(FlightId flightId) {
		log.trace("processFlight(" + flightId +").enter");

		// get target record from filtered table or create new one
		SourceFlight sf = sourceDAO.get(flightId);
		log.trace("processFlight: sourceFlight (initial) = " + sf);
		
		// process raw records, updating resulting record
		rawDAO.getFlightsById(flightId).stream().forEach(f -> sf.update(f));
		log.trace("processFlight: sourceFlight (processed) = " + sf);
		
		// save result && update processing table (remove flightId for processed flight)
		// (should be done within one transaction)
		Session session = sessionFactory.openSession();
	    Transaction tx = null;
	    try {
	    	tx = session.beginTransaction();
	    	// save filtered record
	    	if (!sourceDAO.save(sf, session)) throw new HibernateException("could not save sourceFlight");
			// get target record from final result table or create new one
	    	// and update it with values from filtered record
	    	DestFlight df = destDAO.get(flightId).update(sf);
	    	if (!destDAO.save(df, session)) throw new HibernateException("could not save destFlight");
	    	// delete processing record
	    	procDAO.delete(flightId, session);
	    	tx.commit();
			log.debug(String.format("flight [%15s] processed", flightId));
	    } catch (HibernateException e) {
	    	// e.printStackTrace();
	    	log.error(e.getMessage());
	    	tx.rollback();
	    } finally {
	    	session.close();
	    }
		log.trace("processFlight(" + flightId + ").exit");
	}

	/**
	 * get a list of flights needed to be processed
	 * it can be loaded from 
	 *   - processing table (if previous session was interrupted, and this table is not empty)
	 *   - raw data (if it's a new run)
	 * 
	 * @return
	 */
	private List<FlightId> getFlightsForProcessing() {
		log.debug("getFlightsForProcessing().enter");

		// get flights list from processing table 
		List<FlightId> flights = procDAO.flights();
		log.debug("unprocessed flights from broken run: " + flights.size());

		if (flights == null || flights.isEmpty()) {
			// if list of processing flights is empty, we are 
			// starting new processing and need to get unique 
			// flight IDs for further processing from RAW data table
			log.debug("start new processing session");
			
			flights = rawDAO.getUniqueFlights();
			log.debug("new unique flights: " + flights.size());
			
			// ... and save them into processing table
			procDAO.save(flights);
		} else {
			// if list is not empty we should continue 
			// interrupted processing (so go on with current list) 
			log.debug("continue previous processing session");
		}

		log.debug("getFlightsForProcessing().exit(flights to process " + flights.size() + ")");

		// return a list of flight numbers for further processing 
		return flights;
	}

	/**
	 * non-default run method (to use given number of threads in parallel)
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void run(int numberOfThreads) throws InterruptedException, ExecutionException {
		log.trace("run(" + numberOfThreads +").enter");

		// get flights for processing
		List<FlightId> flights = getFlightsForProcessing();
		log.trace("run(" + numberOfThreads +"): flights to process " + flights.size());

		// start parallel processing of each flight number from a list
		if (numberOfThreads > 0) {
			// if 'numberOfThreads' parameter is passed to the method, use given number of threads for processing
			ForkJoinPool pool = new ForkJoinPool(numberOfThreads);
			pool.submit(() ->
				flights.parallelStream().forEach(f -> processFlight(f))
			).get();
		} else {
			// use default number of threads 
			flights.parallelStream().forEach(f -> processFlight(f));
		}

		log.info(String.format("%d flights processed", flights.size()));

		log.trace("run(" + numberOfThreads +").exit");
	}

	/**
	 * default run method (to use default number of threads in parallel)
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void run() throws InterruptedException, ExecutionException {
		log.trace("run().enter");
		run(-1);
		log.trace("run().exit");
	}

	/**
	 * utility method to pretty print elapsed time
	 * 
	 * 
	 * @param ms
	 * @return
	 */
	private static String msToTime(long ms) {
		long millis = ms % 1000;
		long second = (ms / 1000) % 60;
		long minute = (ms / (1000 * 60)) % 60;
		long hour   = (ms / (1000 * 60 * 60)) % 24;
		return String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
	}

	/**
	 * main method
	 * 
	 * 
	 * @param args
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		long start = System.currentTimeMillis();
		new Application(Hibernate.instance().getSessionFactory()).run();
		long end   = System.currentTimeMillis();
		System.out.println(String.format("Threads: default; TimeTaken: %s", msToTime(end-start)));
		Hibernate.instance().shutdown();
	}
}
