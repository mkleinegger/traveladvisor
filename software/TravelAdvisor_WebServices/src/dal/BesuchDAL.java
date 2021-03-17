package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;

import bll.Besuch;
import bll.Besucher;
import bll.Besucher_Verlauf;
import bll.Branche;
import bll.Error404;
import bll.Location;
import bll.Point;
import bll.Rezension;
import bll.Taetigkeit;
import service.LocationList;

public class BesuchDAL {
	public static List<Besuch> getAll(String besucherId, String locationId) throws SQLException, Error404 {
		Connection conn = Database.connect();

		String query = "SELECT * FROM Location_besuch ";
		if(besucherId != null) {
			query += " where id_besucher = " + besucherId + "' "; 
			if(locationId != null) {
				query += " and id_location = '" + locationId + "' "; 
			}
		}
		else if (locationId != null) {
			query += " where id_location = '" + locationId + "' "; 
		}
		
		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		List<Besuch> besuche = new ArrayList<Besuch>();

		while (rs.next()) {

			String id = rs.getString("id_besuch");
			String besucherid = rs.getString("id_besucher");
			String locationid = rs.getString("id_location");

			Timestamp ts = rs.getTimestamp("zeitpunkt");
			
			Besuch b = new Besuch();
			b.setBesucherId(besucherid);
			b.setLocationId(locationid);
			b.setId(id);
			b.setZeitpunkt(ts);
			
			besuche.add(b);
		}
		
		st.close();
		conn.close();

		return besuche;
	}
	
	
	public static void create(Besuch new_besuch) throws Exception {
		try {
			Connection conn = Database.connect();

			String query = " insert into Location_besuch values (?, ?, ?, ?)";

			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, new_besuch.getId().toString());
			preparedStmt.setString(3, new_besuch.getBesucherId().toString());
			preparedStmt.setString(2, new_besuch.getLocationId().toString());
			preparedStmt.setTimestamp(4, new_besuch.getZeitpunkt());
						
			preparedStmt.execute();

			
			conn.close();
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten!");
			System.err.println(e.getMessage());
			throw e;
		}
	}

	
	public static Besucher_Verlauf
	getByBesucherId(String id_besucher) throws SQLException, Error404 {
		Connection conn = Database.connect();

		PreparedStatement pstmt = conn.prepareStatement(
					"   select zeitpunkt, tl.BEZEICHNUNG, punkte, lb.ID_BESUCH as aktion, NVL(null, 'Besuch') as besuch, tl.BEZEICHNUNG as location  from location_besuch lb" + 
					"   inner join travellocation tl on tl.id = lb.id_location" + 
					"   where id_besucher = ?" + 
					"   UNION" + 
					"   select zeitpunkt, a.beschreibung, a.punkte, NVL(null, 'Aktion'), a.id_aktion, tl.BEZEICHNUNG from Besucher_loest_Aktion_ein blae" + 
					"   inner join aktion a on a.ID_AKTION = blae.ID_AKTION" + 
					"   inner join travellocation tl on tl.id = a.ID_LOCATION" + 
					"   where id_besucher = ?");
  
		pstmt.setString(1, id_besucher);
		pstmt.setString(2, id_besucher);


		ResultSet rs = pstmt.executeQuery();


		Besucher_Verlauf verlauf = new Besucher_Verlauf();
		verlauf.setBesucher(id_besucher);
		System.out.println("start");
		while (rs.next()) {

			Taetigkeit t = new Taetigkeit();
			
			Timestamp ts = rs.getTimestamp("zeitpunkt");
			String bezeichnung = rs.getString("bezeichnung");
			int punkte = rs.getInt("punkte");
			
			String location = rs.getString("location");
			
			t.setZeitpunkt(ts);
			t.setLocation(location);
			System.out.println(location);

			if(rs.getString("aktion").equals("Aktion")) {
				//Aktion
				System.out.println(1);
				t.setBeschreibung("aktion eingelöst: " + bezeichnung);
				
				t.setPunkte((-1) * punkte);
				System.out.println(2);
			}
			else if(rs.getString("besuch").equals("Besuch")) {
				//Besuch
				t.setBeschreibung("Location besucht: " + bezeichnung);
				t.setPunkte(punkte);
			}			
			
			verlauf.addTaetigkeit(t);
			
			System.out.println("a");
		}
		
		pstmt.close();
		conn.close();

		return verlauf;
	}


	public static void deleteWithLocId(String id) throws SQLException {
		Connection conn = Database.connect();

		String query = "delete from Location_besuch where id_location = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		preparedStmt.setString(1, id);

		preparedStmt.execute();

		conn.close();
	}
	
	public static void deleteWithBesuchId(String id) throws SQLException {
		Connection conn = Database.connect();

		String query = "delete from Location_besuch where id_besuch = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		preparedStmt.setString(1, id);

		preparedStmt.execute();

		conn.close();
	}
	
	public static Besuch getById(String id) throws Exception {
		Connection conn = Database.connect();

		String query = "SELECT * FROM Location_besuch WHERE id_besuch = '" + id + "'";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		Besuch b = null;
		while (rs.next()) {
			String idLocation = rs.getString("id_location");
			String idBesucher = rs.getString("id_besucher");
			Timestamp ts = rs.getTimestamp("zeitpunkt");

			b = new Besuch();
			
			b.setId(id);
			b.setZeitpunkt(ts);
			b.setLocationId(idLocation);
			b.setBesucherId(idBesucher);
		}
		st.close();

		if(b == null)
			throw new Error404("Besuch nicht gefunden");
		return b;
	}
}
