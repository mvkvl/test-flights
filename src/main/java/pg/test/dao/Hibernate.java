package pg.test.dao;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Hibernate {
	
	static Logger log = Logger.getLogger(Hibernate.class.getName());

	private static final SessionFactory sessionFactory = buildSessionFactory();

	 private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xm
            return new Configuration()
//            						  .addPackage("pg.test.model")
//            						  .addClass(Example.class)
//            						  .addClass(SourceFlightRecord.class)
            		                  .configure()
            		                  .buildSessionFactory();
        }
        catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
                throw new ExceptionInInitializerError(ex);
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }
    
	/*
	 *   SINGLETON PATTERN
	 */
	private static class LazyHolder {
	    private static final Hibernate INSTANCE = new Hibernate();
	}
	protected Object readResolve() {
		return instance();
	}
	public static Hibernate instance() {
	    return LazyHolder.INSTANCE;
	}

}
