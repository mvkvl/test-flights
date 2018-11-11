package pg.test.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.log4j.Logger;

@Entity
@Table(name = "processing")
@NamedQueries({
    @NamedQuery(name = "processing_flights", query = "from ProcessingRecord"),
})
public class ProcessingRecord {

	static Logger log = Logger.getLogger(ProcessingRecord.class.getName());
	
	@EmbeddedId
	private FlightId id;

	public ProcessingRecord() {}
	
	public ProcessingRecord(String flightCode, String flightNum) {
		this.id = new FlightId(flightCode, flightNum);
	}

	public ProcessingRecord(FlightId flightId) {
		this.id = flightId;
	}

	public String toString() {
		return this.id.toString();
	}
	
}
