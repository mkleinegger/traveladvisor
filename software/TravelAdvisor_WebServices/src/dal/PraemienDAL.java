package dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bll.Aktion;
import bll.Besitzer;
import bll.Branche;
import bll.Location;
import bll.Point;
import bll.Error404;

public class PraemienDAL {

	public static List<Aktion> getAll() throws SQLException, Error404 {
		Connection conn = Database.connect();

		System.out.println("connected");
		String query = "SELECT id_aktion, id_location, beschreibung, punkte, aktiv FROM Aktion";
		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		List<Aktion> aktionen = new ArrayList<Aktion>();

		while (rs.next()) {
			// Bezeichnung
			String id = rs.getString("id_aktion");
			String bezeichnung = rs.getString("beschreibung");
			String id_location = rs.getString("id_location");
			int punkte = rs.getInt("punkte");
			String aktiv = rs.getString("aktiv");

			Aktion a = new Aktion();
			a.setId(id);
			a.setLocationId(id_location);
			a.setBezeichnung(bezeichnung);
			a.setPunkte(punkte);
			if (aktiv.equals("J"))
				a.setAktiv(true);
			else
				a.setAktiv(false);


			aktionen.add(a);
		}
		st.close();
		conn.close();

		return aktionen;
	}

	public static Aktion getById(String id) throws Exception {
		Connection conn = Database.connect();

		//String query = "SELECT id_aktion, id_location, beschreibung, punkte, aktiv FROM Aktion";
		
		String query = "SELECT id_aktion, id_location, beschreibung, punkte, aktiv FROM Aktion WHERE id_aktion = '" + id + "'";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		Aktion result = null;
		while (rs.next()) {
			String beschreibung = rs.getString("beschreibung");
			int punkte = rs.getInt("punkte");
			String aktiv = rs.getString("aktiv");
			String locationId = rs.getString("id_location");
			
			Aktion a = new Aktion();
			a.setId(id);
			a.setBezeichnung(beschreibung);
			a.setPunkte(punkte);
			if (aktiv.equals("J"))
				a.setAktiv(true);
			else
				a.setAktiv(false);
			a.setLocationId(locationId);
			
			result = a;
		}
		st.close();

		if (result == null)
			throw new Error404("Prämie nicht gefunden");
		return result;
	}

	public static List<Aktion> getByLocation(String loc_id) throws Exception {
		Connection conn = Database.connect();

		//String query = "SELECT id_aktion, id_location, beschreibung, punkte, aktiv FROM Aktion";
		
		String query = "SELECT id_aktion, id_location, beschreibung, punkte, aktiv FROM Aktion WHERE id_location = '" + loc_id + "'";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		List<Aktion> result = new ArrayList<Aktion>();
		while (rs.next()) {
			String beschreibung = rs.getString("beschreibung");
			String id_aktion = rs.getString("id_aktion");
			int punkte = rs.getInt("punkte");
			String aktiv = rs.getString("aktiv");
			String locationId = rs.getString("id_location");
			
			Aktion a = new Aktion();
			a.setId(id_aktion);
			a.setBezeichnung(beschreibung);
			a.setPunkte(punkte);
			if (aktiv.equals("J"))
				a.setAktiv(true);
			else
				a.setAktiv(false);
			a.setLocationId(locationId);

			result.add(a);
		}
		st.close();

		if (result == null)
			throw new Exception("Keine Prämien für die Location mit der ID " + loc_id + " gefunden");
		return result;
	}
	
	public static void update(String id, Aktion new_akt) throws Exception {
		int result = 0;
		try {
			if(new_akt.getLocationId() == null || new_akt.getLocationId().toString() == "") 
				throw new Exception("Prämie braucht eine gültige Location der sie zugeordnet wird.");
			if(new_akt.getBezeichnung() == null || new_akt.getBezeichnung() == "") 
				throw new Exception("Prämie braucht eine Bezeichnung.");
			
			Connection conn = Database.connect();

			
			//String query = "SELECT id_aktion, id_location, beschreibung, punkte, aktiv FROM Aktion";


			String query = "update Aktion set id_location = ?, beschreibung = ?, punkte = ?, aktiv = ? where id_aktion = ?";
			
			
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, new_akt.getLocationId().toString());
			preparedStmt.setString(2, new_akt.getBezeichnung());
			preparedStmt.setInt(3, new_akt.getPunkte());
			if (new_akt.isAktiv())
				preparedStmt.setString(4, "J");
			else
				preparedStmt.setString(4, "N");

			preparedStmt.setString(5, id);
			

			result = preparedStmt.executeUpdate();

			conn.close();

			if (result == 0)
				throw new Exception("Fehler beim Updaten der Prämie mit der ID " + id);
		} catch (Exception e) {
			System.err.println("Fehler beim Updaten der Prämie mit der ID " + id);
			System.err.println(e.getMessage());
			throw e;
		}

	}

	public static void delete(String id) {
		try {
			Connection conn = Database.connect();

			String query = "delete from Aktion where id_aktion = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);

			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten! ");
			System.err.println(e.getMessage());
		}
	}

	public static void create(Aktion new_akt) throws Exception {
		try {
			//String query = "SELECT id_aktion, id_location, beschreibung, punkte, aktiv FROM Aktion";

			if(new_akt.getLocationId() == null || new_akt.getLocationId().toString() == "") 
				throw new Exception("Prämie braucht eine gültige Location der sie zugeordnet wird.");
			if(new_akt.getBezeichnung() == null || new_akt.getBezeichnung() == "") 
				throw new Exception("Prämie braucht eine Bezeichnung.");
			
			
			Connection conn = Database.connect();

			String query = "insert into Aktion values (?, ?, ?, ?, ?)";

			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, new_akt.getId());
			preparedStmt.setString(2, new_akt.getLocationId().toString());
			preparedStmt.setString(3, new_akt.getBezeichnung());
			preparedStmt.setInt(4, new_akt.getPunkte());
			if (new_akt.isAktiv())
				preparedStmt.setString(5, "J");
			else
				preparedStmt.setString(5, "N");


			System.out.println(preparedStmt.toString());
			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten!");
			System.err.println(e.getMessage());
			throw e;
		}
	}

	public static void deletyWithLocId(String id) throws SQLException {

		Connection conn = Database.connect();

		String query = "delete from aktion where id_location = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		preparedStmt.setString(1, id);

		preparedStmt.execute();

		conn.close();


	
	}

	public static void einloesen(String id_besucher, String id_praemie) throws Exception {

		try {
			Connection conn = Database.connect();

			String query = "insert into Besucher_loest_Aktion_ein values (?, ?, ?)";

			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id_besucher);
			preparedStmt.setString(2, id_praemie);
			preparedStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten!");
			System.err.println(e.getMessage());
			throw new Exception("Fehler beim Einlösen der Aktion " + id_praemie + " durch besucher " + id_besucher 
					+ "  \n Entweder Aktion oder Besucher existiert nicht");
		}
	
	}
	
	public static List<Aktion> getEingeloesteAktionenByBesucher(String id_besucher) throws SQLException {

		Connection conn = Database.connect();

		
		String query = "SELECT a.id_aktion, a.id_location, a.beschreibung, a.punkte, a.aktiv FROM Besucher_loest_Aktion_ein blae"
				+ "   inner join Aktion a on blae.id_aktion = a.id_aktion"
				+ "   WHERE id_besucher = ?";

		PreparedStatement preparedStmt = conn.prepareStatement(query);
		preparedStmt.setString(1, id_besucher);
		
		ResultSet rs = preparedStmt.executeQuery();

		List<Aktion> result = new ArrayList<Aktion>();
		while (rs.next()) {
			String id_aktion = rs.getString("id_aktion");
			String id_location = rs.getString("id_location");
			String beschreibung = rs.getString("beschreibung");
			int punkte = rs.getInt("punkte");
			String aktiv = rs.getString("aktiv");
			
			Aktion a = new Aktion();
			a.setId(id_aktion);
			a.setLocationId(id_location);
			a.setBezeichnung(beschreibung);
			a.setPunkte(punkte);
			
			if(aktiv.equals("J"))
				a.setAktiv(true);
			else if (aktiv.equals("N"))
				a.setAktiv(false);
			
			result.add(a);
		}
		preparedStmt.close();

		return result;
	
	}
}
