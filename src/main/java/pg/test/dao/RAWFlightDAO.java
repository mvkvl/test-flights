package pg.test.dao;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import pg.test.model.FlightId;
import pg.test.model.RAWFlight;


/**
 * Utility class to support operations on initial (RAW) data table.
 * It's initialized with sessionFactory object, which is used to open
 * a new session for each request to database. This is needed to let 
 * parallel operations on data (so that parallel threads use each it's 
 * own session object)
 * 
 * @author kami
 *
 */
public class RAWFlightDAO {

	/*  
	 *  Should consider dynamic table name configuration for RAWFlight 
	 *  entity and related queries. For now using preconfigured variable 
	 *  here and @Table(name = "") in RAWFlight class 
	 */
	private final static String SOURCE_DATA_TABLE = "aenaflight_test"; // aenaflight_2017_01 // aenaflight_test
	
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
	public List<FlightId> getUniqueFlights(int pageId, int pageSize) {
		Session session = sessionFactory.openSession();
		try {
			String sql = "select flight_icao_code as \"flightCode\", flight_number as \"flightNumber\" from " + SOURCE_DATA_TABLE + " GROUP BY flight_icao_code, flight_number ORDER BY flight_icao_code, flight_number";
			@SuppressWarnings("deprecation")
			List<FlightId> flights = session.createSQLQuery(sql).setResultTransformer(
			    Transformers.aliasToBean(FlightId.class)).list(); 
			return flights;
		} catch (HibernateException ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		} finally {
			session.close();
		}
	}
	public List<FlightId> getUniqueFlights() {
		return getUniqueFlights(0, Integer.MAX_VALUE);
	}

	/**
	 *   get all flights from database by flightId
	 * 
	 * @param flightId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RAWFlight> getFlightsById(FlightId flightId) {
		Session session = sessionFactory.openSession();
		try {
			List<RAWFlight> list = session.getNamedQuery("raw_flights_byId")
							              .setParameter("code", flightId.getFlightCode())
							              .setParameter("num",  flightId.getFlightNumber())
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

		fm.getUniqueFlights().parallelStream().forEach(System.out::println);
		System.out.println("------------------------------------------");
		fm.getFlightsById(new FlightId("IBE", "3236")).stream().forEach(System.out::println);

		Hibernate.instance().shutdown();
	}
}
