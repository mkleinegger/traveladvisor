package bll;

import java.util.UUID;

public class Aktion {
	private UUID id;
	private String bezeichnung;
	private int punkte;
	private boolean aktiv;
	private UUID locationId;
	
	public Aktion() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id.toString();
	}
	public void setId(String id) {
		this.id = UUID.fromString(id);
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getBezeichnung() {
		return bezeichnung;
	}
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	public int getPunkte() {
		return punkte;
	}
	public void setPunkte(int punkte) {
		this.punkte = punkte;
	}
	public boolean isAktiv() {
		return aktiv;
	}
	public void setAktiv(boolean aktiv) {
		this.aktiv = aktiv;
	}
	public UUID getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = UUID.fromString(locationId);
	}
	public void setLocationId(UUID locationId) {
		this.locationId = locationId;
	}

	public void generateUUID() {
		this.id = UUID.randomUUID();
	}
	
}
