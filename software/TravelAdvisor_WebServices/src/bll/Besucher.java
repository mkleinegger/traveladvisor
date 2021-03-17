package bll;

import java.util.ArrayList;
import java.util.List;

public class Besucher extends Benutzer{

	private List<Aktion> aktionen_offen;
	private List<Location> locations_besucht;
	private int punkte;
	
	public Besucher() {
		super();
		aktionen_offen = new ArrayList<Aktion>();
		locations_besucht = new ArrayList<Location>();
		// TODO Auto-generated constructor stub
	}

	public Besucher(String firebaseId) {
		super(firebaseId);
		aktionen_offen = new ArrayList<Aktion>();
		locations_besucht = new ArrayList<Location>();
		// TODO Auto-generated constructor stub
	}

	public void addAktion(Aktion aktion) {
		this.aktionen_offen.add(aktion);
	}
	
	public List<Aktion> getAktionen() {
		return this.aktionen_offen;
	}
	
	public void locationBesucht(Location location) {
		this.locations_besucht.add(location);
	}

	public int getPunkte() {
		return punkte;
	}

	public void setPunkte(int punkte) {
		this.punkte = punkte;
	}
	
	public void addPunkte(int punkte) {
		this.punkte += punkte;
	}
	
}
