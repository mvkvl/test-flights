package pg.test.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;

import pg.test.tools.FieldTools;

@Entity
@Table(name = "aenaflight_source")
@NamedQueries({
    @NamedQuery(name = "dest_flight_byId", query = "SELECT dfr FROM DestFlight dfr WHERE flight_code = :code AND flight_number = :num"),
})
public class DestFlight {

	static Logger log = Logger.getLogger(DestFlight.class.getName());
	
	@Id
	@Column(name = "id", nullable = false, unique=true, columnDefinition="bigserial")
	@GeneratedValue(generator = "assigned")
	private long id;
	
	@Column(name = "adep", length = 8, nullable = false)
	private String adep = ""; // 					<- [L] dep_apt_code_iata

	@Column(name = "ades", length = 8, nullable = false)
	private String ades = ""; // 					<- [L] arr_apt_code_iata

	@Column(name = "flight_code", length = 8, nullable = false)
    private String flight_code = ""; // 			<- [L] flight_icao_code

	@Column(name = "flight_number", length = 8, nullable = false)
    private String flight_number = ""; // 			<- [L] flight_number

	@Column(name = "carrier_code", length = 8, nullable = true)
	private String carrier_code; // 			<- [L] carrier_icao_code 

	@Column(name = "carrier_number", length = 8, nullable = true)
    private String carrier_number; // 			<- [L] carrier_number

	@Column(name = "status_info", length = 256, nullable = false)
    private String status_info = ""; // 			<- [L] status_info

	/** 
	 *  By task it should be NOT NULL, but raw data table has
	 *  flights for which this field is not set, which prevents 
	 *  saving the final record into database (for quite many 
	 *  records, according to performed tests). Here clarification 
	 *  needed either we really do not want to save records with blank 
	 *  scheduled dates, or we should change NOT NULL condition to 
	 *  NULLABLE. For now changing to NULLABLE.
	 */
	@Column(name = "schd_dep_lt", columnDefinition= "TIMESTAMP WITHOUT TIME ZONE", nullable = true) // should be false
	@Temporal(TemporalType.TIMESTAMP)
    private Date schd_dep_lt; // 				<- [L] timestamp(schd_dep_only_date_lt + schd_dep_only_time_lt)

	/** 
	 *  By task it should be NOT NULL, but raw data table has
	 *  flights for which this field is not set, which prevents 
	 *  saving the final record into database (for quite many 
	 *  records, according to performed tests). Here clarification 
	 *  needed either we really do not want to save records with blank 
	 *  scheduled dates, or we should change NOT NULL condition to 
	 *  NULLABLE. For now changing to NULLABLE.
	 */
	@Column(name = "schd_arr_lt", columnDefinition= "TIMESTAMP WITHOUT TIME ZONE", nullable = true)  // should be false
	@Temporal(TemporalType.TIMESTAMP)
    private Date schd_arr_lt; // 				<- [L] timestamp(schd_arr_only_date_lt + schd_arr_only_time_lt)

	@Column(name = "est_dep_lt", columnDefinition= "TIMESTAMP WITHOUT TIME ZONE", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
    private Date est_dep_lt; // 				<- [L] est_dep_date_time_lt 

	@Column(name = "est_arr_lt", columnDefinition= "TIMESTAMP WITHOUT TIME ZONE", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date est_arr_lt; // 				<- [L] est_arr_date_time_lt

	@Column(name = "act_dep_lt", columnDefinition= "TIMESTAMP WITHOUT TIME ZONE", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
    private Date act_dep_lt; // 				<- [L] act_dep_date_time_lt
	
	@Column(name = "act_arr_lt", columnDefinition= "TIMESTAMP WITHOUT TIME ZONE", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
    private Date act_arr_lt; // 				<- [L] act_arr_date_time_lt

	@Column(name = "flt_leg_seq_no", nullable = false)
    private int flt_leg_seq_no; // 				<- [L] flt_leg_seq_no

	@Column(name = "aircraft_name_scheduled", columnDefinition = "TEXT", nullable = true)
    private String aircraft_name_scheduled; //	<- [L] aircraft_name_scheduled

	@Column(name = "baggage_info", length = 128, nullable = true)
    private String baggage_info; // 			<- [S] baggage_info

	@Column(name = "counter", length = 128, nullable = true)
    private String counter; // 					<- [S] counter
	
	@Column(name = "gate_info", length = 128, nullable = true)
    private String gate_info; // 				<- [S] gate_info
	
	@Column(name = "lounge_info", length = 128, nullable = true)
    private String lounge_info; // 				<- [S] lounge_info

	@Column(name = "terminal_info", length = 128, nullable = true)
    private String terminal_info; // 			<- [S] terminal_info

	@Column(name = "arr_terminal_info", length = 128, nullable = true)
	private String arr_terminal_info; // 		<- [S] arr_terminal_info
    
	@Column(name = "source_data", columnDefinition = "TEXT", nullable = true)
	private String source_data; // 				<- [L] source_data
	
	@Column(name = "created_at", columnDefinition= "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
    private Date created_at; // 				<- created_at

	public DestFlight() {}
	
	/**
	 * update current record with new values from RAW data source
	 * 
	 * @param sourceFlight
	 */
	public void update(SourceFlight sourceFlight) {
		this.adep                    = FieldTools.notNullOrCurrent(sourceFlight.dep_apt_code_iata, adep);
		this.ades                    = FieldTools.notNullOrCurrent(sourceFlight.arr_apt_code_iata, ades); 
	    this.flight_code             = FieldTools.notNullOrCurrent(sourceFlight.flight_icao_code,  flight_code);
	    this.flight_number           = FieldTools.notNullOrCurrent(sourceFlight.flight_number,     flight_number); 
		this.carrier_code            = FieldTools.notNullOrCurrent(sourceFlight.carrier_icao_code, carrier_code);  
	    this.carrier_number          = FieldTools.notNullOrCurrent(sourceFlight.carrier_number,    carrier_number);
	    this.status_info             = FieldTools.notNullOrCurrent(sourceFlight.status_info,       status_info);
	    this.flt_leg_seq_no          = FieldTools.notNullOrCurrent(sourceFlight.flt_leg_seq_no, flt_leg_seq_no); 
	    this.aircraft_name_scheduled = FieldTools.notNullOrCurrent(sourceFlight.aircraft_name_scheduled, aircraft_name_scheduled); 
		this.source_data             = FieldTools.notNullOrCurrent(sourceFlight.source_data, source_data); 
	    this.schd_dep_lt             = FieldTools.notNullDateOrCurrent(sourceFlight.schd_dep_only_date_lt + " " + sourceFlight.schd_dep_only_time_lt, schd_dep_lt);
	    this.schd_arr_lt             = FieldTools.notNullDateOrCurrent(sourceFlight.schd_arr_only_date_lt + " " + sourceFlight.schd_arr_only_time_lt, schd_arr_lt);
	    this.est_dep_lt              = FieldTools.notNullDateOrCurrent(sourceFlight.est_dep_date_time_lt, est_dep_lt);  
		this.est_arr_lt              = FieldTools.notNullDateOrCurrent(sourceFlight.est_arr_date_time_lt, est_arr_lt);
	    this.act_dep_lt              = FieldTools.notNullDateOrCurrent(sourceFlight.act_dep_date_time_lt, act_dep_lt); 
	    this.act_arr_lt              = FieldTools.notNullDateOrCurrent(sourceFlight.act_arr_date_time_lt, act_arr_lt); 
		this.baggage_info            = FieldTools.notNullOrCurrent(sourceFlight.baggage_info,      baggage_info);
	    this.counter                 = FieldTools.notNullOrCurrent(sourceFlight.counter,           counter);
	    this.gate_info               = FieldTools.notNullOrCurrent(sourceFlight.gate_info,         gate_info);
	    this.lounge_info             = FieldTools.notNullOrCurrent(sourceFlight.lounge_info,       lounge_info);
	    this.terminal_info           = FieldTools.notNullOrCurrent(sourceFlight.terminal_info,     terminal_info);
		this.arr_terminal_info       = FieldTools.notNullOrCurrent(sourceFlight.arr_terminal_info, arr_terminal_info);
	}
	
	public void updateCreatedAt() {
		this.created_at = new Date();
	}

	public String toString() {
		return String.format("%8s %8s %8s %8s %15s %15s", adep, ades, flight_code, flight_number, baggage_info, gate_info);
	}
	
}
