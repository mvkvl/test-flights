package pg.test.model;

import java.util.Date;

import javax.persistence.*;

import org.apache.log4j.Logger;

import pg.test.tools.FieldTools;

//@NamedQuery(name = "source.flights.all"    , query = "from SourceFlight"),
//@NamedQuery(name = "source.flights.total"  , query = "select count(flight_number) from SourceFlight"),
//@NamedQuery(name = "source.flights.unique" , query = "select distinct flight_number from SourceFlight"),
@Entity
@Table(name = "source_data")
@NamedQueries({
    @NamedQuery(name = "source_flight_byId", query = "SELECT sfr FROM SourceFlight sfr WHERE flight_icao_code = :code AND flight_number = :num"),
})
public class SourceFlight {
	
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(SourceFlight.class.getName());

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "assigned")
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
	
	@Column(name = "act_dep_date_time_lt", length = 64)
	protected String act_dep_date_time_lt;
	
	@Column(name = "schd_dep_only_date_lt", length = 32)
	protected String schd_dep_only_date_lt;
	
	@Column(name = "schd_dep_only_time_lt", length = 32)
	protected String schd_dep_only_time_lt;

	@Column(name = "created_at")
	protected long created_at;
	
	public SourceFlight() {
		
	}
	
	/**
	 * update current record with new values from RAW data source
	 * 
	 * @param sourceFlight
	 */
	public void update(RAWFlight rawFlight) {
		act_arr_date_time_lt     = FieldTools.notNullOrCurrent(rawFlight.act_arr_date_time_lt, act_arr_date_time_lt, rawFlight.created_at, created_at);
		aircraft_name_scheduled  = FieldTools.notNullOrCurrent(rawFlight.aircraft_name_scheduled, aircraft_name_scheduled, rawFlight.created_at, created_at);
		arr_apt_name_es          = FieldTools.notNullOrCurrent(rawFlight.arr_apt_name_es, arr_apt_name_es, rawFlight.created_at, created_at);
		arr_apt_code_iata        = FieldTools.notNullOrCurrent(rawFlight.arr_apt_code_iata, arr_apt_code_iata, rawFlight.created_at, created_at);
		carrier_airline_name_en  = FieldTools.notNullOrCurrent(rawFlight.carrier_airline_name_en, carrier_airline_name_en, rawFlight.created_at, created_at);
		carrier_icao_code        = FieldTools.notNullOrCurrent(rawFlight.carrier_icao_code, carrier_icao_code, rawFlight.created_at, created_at);
		carrier_number           = FieldTools.notNullOrCurrent(rawFlight.carrier_number, carrier_number, rawFlight.created_at, created_at);
		dep_apt_name_es          = FieldTools.notNullOrCurrent(rawFlight.dep_apt_name_es, dep_apt_name_es, rawFlight.created_at, created_at);
		dep_apt_code_iata        = FieldTools.notNullOrCurrent(rawFlight.dep_apt_code_iata, dep_apt_code_iata, rawFlight.created_at, created_at);
		est_arr_date_time_lt     = FieldTools.notNullOrCurrent(rawFlight.est_arr_date_time_lt, est_arr_date_time_lt, rawFlight.created_at, created_at);
		est_dep_date_time_lt     = FieldTools.notNullOrCurrent(rawFlight.est_dep_date_time_lt, est_dep_date_time_lt, rawFlight.created_at, created_at);
		flight_airline_name_en   = FieldTools.notNullOrCurrent(rawFlight.flight_airline_name_en, flight_airline_name_en, rawFlight.created_at, created_at);
		flight_airline_name      = FieldTools.notNullOrCurrent(rawFlight.flight_airline_name, flight_airline_name, rawFlight.created_at, created_at);
		flight_icao_code         = FieldTools.notNullOrCurrent(rawFlight.flight_icao_code, flight_icao_code, rawFlight.created_at, created_at);
		flight_number            = FieldTools.notNullOrCurrent(rawFlight.flight_number, flight_number, rawFlight.created_at, created_at);
		flt_leg_seq_no           = FieldTools.notNullOrCurrent(rawFlight.flt_leg_seq_no, flt_leg_seq_no, rawFlight.created_at, created_at);
		schd_arr_only_date_lt    = FieldTools.notNullOrCurrent(rawFlight.schd_arr_only_date_lt, schd_arr_only_date_lt, rawFlight.created_at, created_at);
		schd_arr_only_time_lt    = FieldTools.notNullOrCurrent(rawFlight.schd_arr_only_time_lt, schd_arr_only_time_lt, rawFlight.created_at, created_at);
		source_data              = FieldTools.notNullOrCurrent(rawFlight.source_data, source_data, rawFlight.created_at, created_at);
		status_info              = FieldTools.notNullOrCurrent(rawFlight.status_info, status_info, rawFlight.created_at, created_at);
		act_dep_date_time_lt     = FieldTools.notNullOrCurrent(rawFlight.act_dep_date_time_lt, act_dep_date_time_lt, rawFlight.created_at, created_at);
		schd_dep_only_date_lt    = FieldTools.notNullOrCurrent(rawFlight.schd_dep_only_date_lt, schd_dep_only_date_lt, rawFlight.created_at, created_at);
		schd_dep_only_time_lt    = FieldTools.notNullOrCurrent(rawFlight.schd_dep_only_time_lt, schd_dep_only_time_lt, rawFlight.created_at, created_at);

	    baggage_info             = FieldTools.addStackValue(rawFlight.baggage_info, baggage_info);
	    counter                  = FieldTools.addStackValue(rawFlight.counter, counter);
	    gate_info                = FieldTools.addStackValue(rawFlight.gate_info, gate_info);
	    lounge_info              = FieldTools.addStackValue(rawFlight.lounge_info, lounge_info);
	    terminal_info            = FieldTools.addStackValue(rawFlight.terminal_info, terminal_info);
		arr_terminal_info        = FieldTools.addStackValue(rawFlight.arr_terminal_info, arr_terminal_info);

		created_at  = (created_at > rawFlight.created_at) ? created_at : rawFlight.created_at;
	}

	public void updateCreatedAt() {
		this.created_at = (new Date()).getTime();
	}

	public String toString() {
		return "'" + this.id + " - " + this.aircraft_name_scheduled + " - " + this.arr_apt_code_iata + "'";
	}
}
