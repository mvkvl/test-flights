package pg.test.dao;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

import pg.test.model.FlightId;
import pg.test.model.ProcessingRecord;

/**
 * Utility class to support operations on processing data table (to 
 * support continuation of broken runs).
 * 
 * It's initialized with sessionFactory object, which is used to open
 * a new session for each request to database. This is needed to let 
 * parallel operations on data (so that parallel threads use each it's 
 * own session object)
 * 
 * @author kami
 *
 */
public class ProcessingDAO {

	static Logger log = Logger.getLogger(ProcessingDAO.class.getName());
	
	// for bulk operation support 
	// (will need to save large list in one transaction) 
	private static final int BATCH_SIZE = 200; // same as hibernate.jdbc.batch_size set in cfg.xml
	
	private SessionFactory sessionFactory;
	
	public ProcessingDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * get list of unprocessed flights (flight IDs only, actually)
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<FlightId> flights() {
		Session session = sessionFactory.openSession();
		try {
			String sql = "select flightcode   as \"flightCode\","
					         + " flightnumber as \"flightNumber\""
					         + " from processing";
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

	/**
	 * get one record (by flightID)
	 * 
	 * @param flightId
	 * @return
	 */
	public ProcessingRecord get(FlightId flightId) {
		Session session = sessionFactory.openSession();
		try {
			return (ProcessingRecord) session.get(ProcessingRecord.class, flightId);
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        } finally {
        	session.close();
        }
	}
	
	/**
	 * save a list of flights to be processed
	 * 
	 * @param flightId
	 * @return
	 */
	public void save(List<FlightId> flights) {
		Session session = sessionFactory.openSession();
	    Transaction tx  = session.beginTransaction();
	    int cnt = 0;
	    for (FlightId flight : flights) {
	    	try {
	    		session.saveOrUpdate(new ProcessingRecord(flight));
	    	} catch (Exception ex) {
	    	}
	    	if (++cnt >= BATCH_SIZE) {
	    		session.flush();
    	        session.clear();
    	        cnt = 0;
	    	}
	    }
		tx.commit();
	    session.close();
	}

	/**
	 * As we delete processing flight record in the same transaction, where we
	 * save resulting data into destination table we pass session object here. 
	 * Later related transaction will be committed or rolled back.
	 * 
	 * @param flightId
	 * @param session
	 * @return
	 */
	public void delete(FlightId flightId, Session session) {
		log.trace("delete(" + flightId + ").enter");
		ProcessingRecord pr = (ProcessingRecord) session.get(ProcessingRecord.class, flightId);
		log.debug("ProcessingRecord to delete: " + pr);
		session.delete(pr);	
		log.trace("delete(" + flightId + ").exit");
	}

	/**
	 * ordinary 'transactional' delete (for completeness)
	 * 
	 * @param flightId
	 */
	public void delete(FlightId flightId) {
		Session session = sessionFactory.openSession();
	    Transaction tx = session.beginTransaction();
	    try {
	    	delete(flightId, session);
	    	tx.commit();
	    } catch (HibernateException e) {
	    	tx.rollback();
	    } finally {
		    session.close();
	    }
	}
}
