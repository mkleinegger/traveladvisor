package bll;

import java.sql.Timestamp;
import java.util.UUID;

public class Rezension {
	private UUID id;
	private UUID locationid;
	private String besucherid;
	private int bewertung;
	private Timestamp timestamp;
	private String text;
	
	public Rezension() {
		
	}
	
	public UUID getLocationid() {
		return locationid;
	}
	public void setLocationid(UUID locationid) {
		this.locationid = locationid;
	}
	public void setLocationid(String locationid) {
		this.locationid = UUID.fromString(locationid);
	}
	public String getBesucherid() {
		return besucherid;
	}
	public void setBesucherid(String besucherid) {
		this.besucherid = besucherid;
	}

	public int getBewertung() {
		return bewertung;
	}
	public void setBewertung(int bewertung) {
		this.bewertung = bewertung;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public void generateId() {
		this.id = UUID.randomUUID();
	}
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setId(String id) {
		this.id = UUID.fromString(id);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
}	
