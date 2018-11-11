package pg.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.log4j.Logger;

@Entity
@Table(name = "processing")
@NamedQueries({
    @NamedQuery(name = "flights", query = "select flightId from ProcessingRecord"),
})
public class ProcessingRecord {

	static Logger log = Logger.getLogger(ProcessingRecord.class.getName());

	@Id
	@Column(name = "flightId", nullable = false)
	protected String flightId;

	public ProcessingRecord() {}
	
	public ProcessingRecord(String flightId) {
		this.flightId = flightId;
	}
	
	public String toString() {
		return this.flightId;
	}
	
}
