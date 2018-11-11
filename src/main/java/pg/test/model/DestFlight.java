package pg.test.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.log4j.Logger;

@Entity
@Table(name = "aenaflight_source")
@NamedQueries({
    @NamedQuery(name = "flight.byId"   , query = "SELECT dfr FROM DestFlight dfr WHERE flight_number = :value"),
})
public class DestFlight {

	static Logger log = Logger.getLogger(DestFlight.class.getName());
	
	// should be the same as in RAW database
	public static final String DATE_TIME_FORMAT = "dd/MM/yy hh:mm"; 	

//  no need dedicated ID - use 'flight_number' for PK
//	@Id
//	@Column(name = "id", nullable = false, unique=true, columnDefinition="bigserial")
//	@GeneratedValue(generator = "assigned")
//	private long id;
	
	@Column(name = "adep", length = 8, nullable = false)
	private String adep = ""; // 					<- [L] dep_apt_code_iata

	@Column(name = "ades", length = 8, nullable = false)
	private String ades = ""; // 					<- [L] arr_apt_code_iata

	@Column(name = "flight_code", length = 8, nullable = false)
    private String flight_code = ""; // 			<- [L] flight_icao_code

	@Id
	@Column(name = "flight_number", unique=true, length = 8, nullable = false)
    private String flight_number = ""; // 			<- [L] flight_number

	@Column(name = "carrier_code", length = 8, nullable = true)
	private String carrier_code = ""; // 			<- [L] carrier_icao_code 

	@Column(name = "carrier_number", length = 8, nullable = false)
    private String carrier_number = ""; // 			<- [L] carrier_number

	@Column(name = "status_info", length = 256, nullable = false)
    private String status_info = ""; // 			<- [L] status_info

	@Column(name = "schd_dep_lt", nullable = false)
    private long schd_dep_lt; // 				<- [L] timestamp(schd_dep_only_date_lt + schd_dep_only_time_lt)

	@Column(name = "schd_arr_lt", nullable = false)
    private long schd_arr_lt; // 				<- [L] timestamp(schd_arr_only_date_lt + schd_arr_only_time_lt)

	@Column(name = "est_dep_lt", nullable = true)
    private Long est_dep_lt; // 				<- [L] est_dep_date_time_lt 

	@Column(name = "est_arr_lt", nullable = true)
	private Long est_arr_lt; // 				<- [L] est_arr_date_time_lt

	@Column(name = "act_dep_lt", nullable = true)
    private Long act_dep_lt; // 				<- [L] act_dep_date_time_lt
	
	@Column(name = "act_arr_lt", nullable = true)
    private Long act_arr_lt; // 				<- [L] act_arr_date_time_lt

	@Column(name = "flt_leg_seq_no", nullable = false)
    private int flt_leg_seq_no; // 				<- [L] flt_leg_seq_no

	@Column(name = "aircraft_name_scheduled", columnDefinition = "TEXT", nullable = true)
    private String aircraft_name_scheduled = ""; //	<- [L] aircraft_name_scheduled

	@Column(name = "baggage_info", length = 128, nullable = true)
    private String baggage_info = ""; // 			<- [S] baggage_info

	@Column(name = "counter", length = 128, nullable = true)
    private String counter = ""; // 					<- [S] counter
	
	@Column(name = "gate_info", length = 128, nullable = true)
    private String gate_info = ""; // 				<- [S] gate_info
	
	@Column(name = "lounge_info", length = 128, nullable = true)
    private String lounge_info = ""; // 				<- [S] lounge_info

	@Column(name = "terminal_info", length = 128, nullable = true)
    private String terminal_info = ""; // 			<- [S] terminal_info

	@Column(name = "arr_terminal_info", length = 128, nullable = true)
	private String arr_terminal_info = ""; // 		<- [S] arr_terminal_info
    
	@Column(name = "source_data", columnDefinition = "TEXT", nullable = true)
	private String source_data = ""; // 				<- [L] source_data
	
	@Column(name = "created_at", nullable = false)
    private Long created_at; // 				<- created_at

	
	public DestFlight() {}
	
	
	/**
	 * returns new value (from RAW datasource) if it's not null
	 * or keeps current value
	 * (for String-type values)
	 * 
	 * @param newValue
	 * @param currentValue
	 * @return
	 */
	private String notNullOrCurrent(String newValue, String currentValue) {
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
	private int notNullOrCurrent(String newValue, int currentValue) {
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
	private Long notNullDateOrCurrent(String newValue, Long currentValue) {
		if (newValue == null || newValue.trim().isEmpty()) {
			return currentValue;
		}
		else {
			DateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
			try {
				Date date = format.parse(newValue);	
				return date.getTime();
			} catch (ParseException ex) {
				ex.printStackTrace();
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
	private String addStackValue(String value, String source) {
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
	
	/**
	 * update current record with new values from RAW data source
	 * 
	 * @param sourceFlight
	 */
	public void add(RAWFlight sourceFlight) {
		this.adep                    = notNullOrCurrent(sourceFlight.dep_apt_code_iata, adep);
		this.ades                    = notNullOrCurrent(sourceFlight.arr_apt_code_iata, ades); 
	    this.flight_code             = notNullOrCurrent(sourceFlight.flight_icao_code,  flight_code);
	    this.flight_number           = notNullOrCurrent(sourceFlight.flight_number,     flight_number); 
		this.carrier_code            = notNullOrCurrent(sourceFlight.carrier_icao_code, carrier_code);  
	    this.carrier_number          = notNullOrCurrent(sourceFlight.carrier_number,    carrier_number);
	    this.status_info             = notNullOrCurrent(sourceFlight.status_info,       status_info);
	    this.flt_leg_seq_no          = notNullOrCurrent(sourceFlight.flt_leg_seq_no, flt_leg_seq_no); 
	    this.aircraft_name_scheduled = notNullOrCurrent(sourceFlight.aircraft_name_scheduled, aircraft_name_scheduled); 
		this.source_data             = notNullOrCurrent(sourceFlight.source_data, source_data); 
	    this.schd_dep_lt             = notNullDateOrCurrent(sourceFlight.schd_dep_only_date_lt + " " + sourceFlight.schd_dep_only_time_lt, schd_dep_lt);
	    this.schd_arr_lt             = notNullDateOrCurrent(sourceFlight.schd_arr_only_date_lt + " " + sourceFlight.schd_arr_only_time_lt, schd_arr_lt);
	    this.est_dep_lt              = notNullDateOrCurrent(sourceFlight.est_dep_date_time_lt, est_dep_lt);  
		this.est_arr_lt              = notNullDateOrCurrent(sourceFlight.est_arr_date_time_lt, est_arr_lt);
	    this.act_dep_lt              = notNullDateOrCurrent(sourceFlight.act_dep_date_time_lt, act_dep_lt); 
	    this.act_arr_lt              = notNullDateOrCurrent(sourceFlight.act_arr_date_time_lt, act_arr_lt); 
		this.baggage_info            = addStackValue(sourceFlight.baggage_info,      baggage_info); 	// this.stackBaggageInfo);
	    this.counter                 = addStackValue(sourceFlight.counter,           counter);      	// this.stackCounter);
	    this.gate_info               = addStackValue(sourceFlight.gate_info,         gate_info);    	// this.stackGateInfo);
	    this.lounge_info             = addStackValue(sourceFlight.lounge_info,       lounge_info);  	// this.stackLaungeInfo);
	    this.terminal_info           = addStackValue(sourceFlight.terminal_info,     terminal_info);    // this.stackTerminalInfo);
		this.arr_terminal_info       = addStackValue(sourceFlight.arr_terminal_info, arr_terminal_info);// this.stackArrTerminalInfo);
		// "created_at" should be set on save
	}
	
	public void setCreationDate(long date) {
		this.created_at = date;
	}

	public String toString() {
		return String.format("%8s %8s %8s %8s %15s %15s", adep, ades, flight_code, flight_number, baggage_info, gate_info);
	}
	
}


/*
// <- [L] arr_apt_name_es
// <- [L] carrier_airline_name_en
// <- [L] dep_apt_name_es
// <- [L] flight_airline_name_en
// <- [L] flight_airline_name
// <- [L] schd_arr_only_date_lt
// <- [L] schd_arr_only_time_lt
// <- [L] act_dep_date_time_lt
// <- [L] schd_dep_only_date_lt
// <- [L] schd_dep_only_time_lt
*/


