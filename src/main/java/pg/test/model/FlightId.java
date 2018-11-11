package pg.test.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * helper class for representation of combined key (flightId: {flightCode, flightNumber})
 * 
 * @author kami
 *
 */
@Embeddable
public class FlightId implements Serializable { 
	private static final long serialVersionUID = 7347021964995282392L;

	public FlightId() {}
	
	public FlightId(RAWFlight rawFlight) {
		this.flightCode    = rawFlight.flight_icao_code;
		this.flightNumber  = rawFlight.flight_number;
	}
	
	public FlightId(String code, String num) {
		this.flightCode    = code;
		this.flightNumber  = num;
	}
	
	@Column // (name="flight_icao_code")
	private String flightCode;
	public String getFlightCode() {
		return flightCode;
	}
	public void setFlightCode(String flightCode) {
		this.flightCode = flightCode;
	}

	@Column // (name="flight_number")
    private String flightNumber;
	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNum) {
		this.flightNumber = flightNum;
	}

	public String toString() {
    	return flightCode + " " + flightNumber;
    }
}
