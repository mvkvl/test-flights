package pg.test.dao;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import pg.test.model.RAWFlight;


/**
 * Utility class to support operations on source (RAW) data table.
 * It's initialized with sessionFactory object, which is used to open
 * a new session for each request to database. This is needed to let 
 * parallel operations on data (so that parallel threads use each it's 
 * own session object)
 * 
 * @author kami
 *
 */
public class RAWFlightDAO {

	static Logger log = Logger.getLogger(RAWFlightDAO.class.getName());
	
	private SessionFactory sessionFactory;
	
	public RAWFlightDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	

	/**
	 *   get unique flights from database
	 * 
	 * @return list of flight IDs
	 */
	@SuppressWarnings("unchecked")
	public List<String> getUniqueFlights(int pageId, int pageSize) {
		Session session = sessionFactory.openSession();
		try {
			List<String> list = session.getNamedQuery("flights.unique")
			   		   .setFirstResult(pageId)
			   		   .setMaxResults(pageSize)
			   		   .list();
			return list;
		} catch (HibernateException ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		} finally {
			session.close();
		}
	}
	public List<String> getUniqueFlights() {
		return getUniqueFlights(0, Integer.MAX_VALUE);
	}

	/**
	 *   get all flights from database by flightId
	 * 
	 * @param flightId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RAWFlight> getFlightsById(String flightId) {
		Session session = sessionFactory.openSession();
		try {
			List<RAWFlight> list = session.getNamedQuery("flights.byId")
		               .setParameter("value", flightId)
		               .list();
			return list;
		} catch (HibernateException ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		} finally {
			session.close();
		}
	}
	
	public static void main(String[] args) {
		RAWFlightDAO fm = new RAWFlightDAO(Hibernate.instance().getSessionFactory());

		long start = System.currentTimeMillis();
		List<String> flights = fm.getUniqueFlights(new Random().nextInt(900), 30);
		long total = flights.size();
		long end   = System.currentTimeMillis();
		System.out.println(String.format("UNIQUE FLIGHTS: %d (time taken %d sec.)", total, (end - start) / 1000));

		flights.parallelStream().forEach(f -> System.out.print(fm.getFlightsById(f).size() + " "));

		Hibernate.instance().shutdown();
	}
}
