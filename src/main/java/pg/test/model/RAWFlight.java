package pg.test.model;

import javax.persistence.*;

import org.apache.log4j.Logger;

@Entity
@Table(name = "aenaflight_test") // aenaflight_2017_01 // aenaflight_test
@NamedQueries({
	@NamedQuery(name = "raw_flights_byId"  , query = "SELECT sfr FROM RAWFlight sfr WHERE flight_icao_code = :code AND flight_number = :num"), //  order by created_at
})
public class RAWFlight {
	
	static Logger log = Logger.getLogger(RAWFlight.class.getName());

	@Id
	@Column(name = "id")
	protected long id;

	@Column(name = "act_arr_date_time_lt", length = 64)
	protected String act_arr_date_time_lt;
	
	@Column(name = "aircraft_name_scheduled", columnDefinition = "TEXT")
	protected String aircraft_name_scheduled;
	
	@Column(name = "arr_apt_name_es", length = 128)
	protected String arr_apt_name_es;
	
	@Column(name = "arr_apt_code_iata", length = 8)
	protected String arr_apt_code_iata;
	
	@Column(name = "baggage_info", length = 128)
	protected String baggage_info;
	
	@Column(name = "carrier_airline_name_en", length = 128)
	protected String carrier_airline_name_en;
	
	@Column(name = "carrier_icao_code", length = 8)
	protected String carrier_icao_code;
	
	@Column(name = "carrier_number", length = 8)
	protected String carrier_number;
	
	@Column(name = "counter", length = 64)
	protected String counter;
	
	@Column(name = "dep_apt_name_es", length = 128)
	protected String dep_apt_name_es;
	
	@Column(name = "dep_apt_code_iata", length = 8)
	protected String dep_apt_code_iata;
	
	@Column(name = "est_arr_date_time_lt", length = 64)
	protected String est_arr_date_time_lt;
	
	@Column(name = "est_dep_date_time_lt", length = 64)
	protected String est_dep_date_time_lt;
	
	@Column(name = "flight_airline_name_en", length = 128)
	protected String flight_airline_name_en;
	
	@Column(name = "flight_airline_name", length = 128)
	protected String flight_airline_name;
	
	@Column(name = "flight_icao_code", length = 8)
	protected String flight_icao_code;
	
	@Column(name = "flight_number", length = 8)
	protected String flight_number;
	
	@Column(name = "flt_leg_seq_no", length = 8)
	protected String flt_leg_seq_no;
	
	@Column(name = "gate_info", length = 128)
	protected String gate_info;
	
	@Column(name = "lounge_info", length = 128)
	protected String lounge_info;
	
	@Column(name = "schd_arr_only_date_lt", length = 32)
	protected String schd_arr_only_date_lt;
	
	@Column(name = "schd_arr_only_time_lt", length = 32)
	protected String schd_arr_only_time_lt;
	
	@Column(name = "source_data", columnDefinition = "TEXT")
	protected String source_data;
	
	@Column(name = "status_info", length = 128)
	protected String status_info;
	
	@Column(name = "terminal_info", length = 128)
	protected String terminal_info;
	
	@Column(name = "arr_terminal_info", length = 128)
	protected String arr_terminal_info;
	
	@Column(name = "created_at")
	protected long created_at;
	
	@Column(name = "act_dep_date_time_lt", length = 64)
	protected String act_dep_date_time_lt;
	
	@Column(name = "schd_dep_only_date_lt", length = 32)
	protected String schd_dep_only_date_lt;
	
	@Column(name = "schd_dep_only_time_lt", length = 32)
	protected String schd_dep_only_time_lt;
	
	public String toString() {
		return "'" + this.id + " - " + this.aircraft_name_scheduled + " - " + this.arr_apt_code_iata + "'";
	}
	
}
