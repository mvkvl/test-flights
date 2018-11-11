package pg.test.dao;

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import pg.test.model.DestFlight;

/**
 * Utility class to support operations on destination data table.
 * It's initialized with sessionFactory object, which is used to open
 * a new session for each request to database. This is needed to let 
 * parallel operations on data (so that parallel threads use each it's 
 * own session object)
 * 
 * @author kami
 *
 */
public class DestFlightDAO {

	static Logger log = Logger.getLogger(DestFlightDAO.class.getName());

	private SessionFactory sessionFactory;
	
	public DestFlightDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * get existing record from destination table
	 * or create new one
	 * 
	 * @param flightId
	 * @return
	 */
	public DestFlight get(String flightId) {
	    Session session = sessionFactory.openSession();
		try {
			DestFlight flight = (DestFlight)session.get(DestFlight.class, flightId);
			if (flight != null)
				return flight;
			else
				return new DestFlight();
		} catch (HibernateException ex) {
			ex.printStackTrace();
			return new DestFlight();
		} finally {
			session.close();
		}
	}
	
	/**
	 * ordinary 'transactional' save (for completeness)
	 * 
	 * @param destFlight
	 * @return
	 */
	public boolean saveTransactional(DestFlight destFlight) {
		Session session = sessionFactory.openSession();
	    Transaction tx = null;
	    try {
	        tx = session.beginTransaction();
	        if (!save(destFlight, session))
	        	throw new HibernateException("could not save object into database");
	        tx.commit();
	        return true;
	    } catch (HibernateException e) {
	        tx.rollback();
	        e.printStackTrace();
	        return false;
	    } finally {
	        session.close();
	    }		
	}

	/**
	 * As we not only save object after update, but also need to 
	 * remove related record from processing table, this save operation 
	 * should be combined into one transaction with call to processingDAO.delete. 
	 * That is why session object is being passed here. Later related transaction 
	 * will be committed or rolled back
	 * 
	 * @param destFlight
	 * @param session
	 * @return
	 */
	public boolean save(DestFlight destFlight, Session session) {
	    try {
	        destFlight.setCreationDate(new Date().getTime());
	        session.saveOrUpdate(destFlight);
	        return true;
	    } catch (HibernateException e) {
	        e.printStackTrace();
	    	return false;
	    }
	}
}
