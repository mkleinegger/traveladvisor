package bll;

import java.util.ArrayList;
import java.util.List;

public class Besucher_Verlauf {

	private String id_besucher;
	private List<Taetigkeit> aktionen;

	public Besucher_Verlauf() {
		this.aktionen = new ArrayList<Taetigkeit>();
	}
	public String getBesucher() {
		return id_besucher;
	}
	public void setBesucher(String besucher) {
		this.id_besucher = besucher;
	}
	public List<Taetigkeit> getAktionen() {
		return aktionen;
	}
	public void setAktionen(List<Taetigkeit> aktionen) {
		this.aktionen = aktionen;
	}
	
	public void addTaetigkeit(Taetigkeit t) {
		this.aktionen.add(t);
	}
}
