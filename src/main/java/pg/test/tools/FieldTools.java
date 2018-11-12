package pg.test.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FieldTools {

	// should be the same as in RAW database
	private static final String DATE_TIME_FORMAT = "dd/MM/yy hh:mm"; 	
	

	/**
	 * returns new value (from RAW datasource) if it's not null
	 * or keeps current value
	 * (for String-type values)
	 * 
	 * @param newValue
	 * @param currentValue
	 * @return
	 */
	public static String notNullOrCurrent(String newValue, String currentValue) {
		return (newValue != null && !newValue.isEmpty()) ? newValue : currentValue;
	}
	/**
	 * returns new value (from RAW datasource) if it's not null
	 * or keeps current value
	 * (for int-type values)
	 * 
	 * @param newValue
	 * @param currentValue
	 * @return
	 */
	public static int notNullOrCurrent(String newValue, int currentValue) {
		return (newValue != null && !newValue.isEmpty()) ? Integer.parseInt(newValue) : currentValue;
	}
	/**
	 * returns new value (from RAW datasource) if it's not null
	 * or keeps current value
	 * (for timestamp-type values)
	 * 
	 * @param newValue
	 * @param currentValue
	 * @return
	 * @throws ParseException 
	 */
	public static Date notNullDateOrCurrent(String newValue, Date currentValue) {
		if (newValue == null || newValue.trim().isEmpty()) {
			return currentValue;
		}
		else {
			DateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
			try {
				Date date = format.parse(newValue);	
				return date;
			} catch (ParseException ex) {
//				ex.printStackTrace();
				return currentValue;
			}
		}
	}
	
	/**
	 *   previous functions 'notNullOrCurrent' also could be implemented with 
	 *   a help of Java 8 Optional class. Something like this:
	 *   
	 *   private String notNullOrCurrent(String newValue, String currentValue) {
	 *   	return Optional.of(newValue).orElse(currentValue);
	 *   }
	 */

	/**
	 * reworked filtering method (checks create_at date to figure out, which value to return)  
	 * 
	 * @param newValue
	 * @param currentValue
	 * @param new_created_at
	 * @param cur_created_at
	 * @return
	 */
	public static String notNullOrCurrent(String newValue, String currentValue, 
			                          long new_created_at, long cur_created_at) {
		if (cur_created_at > new_created_at) {
			return (currentValue != null && !currentValue.isEmpty()) ? currentValue : newValue;
		} else {
			return (newValue != null && !newValue.isEmpty()) ? newValue : currentValue;
		}
	}

	
	/**
	 * simple "LIFO" implementation: checks if string contains a value;
	 * if it does not contain it, returns old string prefixed with the new value
	 * 
	 * this implementations fulfills the condition of adding new values to the start 
	 * of resulting list and keeping values unique; 
	 * 
	 * but if old value is observed in newer records after some other values, this old value  
	 * won't be moved to start (top) of list; if such a behavior is needed, the implementation 
	 * of this method should be reworked (a kind of "extended" stack would be needed for this, 
	 * or just a LinkedList with add(0, item) as a push for stack and removing item from the 
	 * middle of the list)   
	 * 
	 * @param value
	 * @param source
	 * @return
	 */
	public static String addStackValue(String value, String source) {
		// to prevent "null", empty or "-" values in result filter passed value
		String trimmedValue = value.trim();
		String finalValue = (trimmedValue != null && !trimmedValue.isEmpty() && !trimmedValue.equals("-")) ?
				trimmedValue : "";
		if (finalValue.isEmpty()) {
			return (source == null || source.trim().isEmpty()) ? "" : source;
		} else if (source == null || source.isEmpty()) {
			return finalValue;			
		} else if (source.contains(finalValue)) {
			return source;
		} else {
			return finalValue + ", " + source; 
		}
	}
	
}
