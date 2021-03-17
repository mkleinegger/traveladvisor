package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import bll.Branche;
import bll.Error404;
import bll.Location;
import bll.Rezension;

public class RezensionenDAL {
	public static List<Rezension> getAll() throws SQLException {
		Connection conn = Database.connect();

		String query = "SELECT * FROM Rezension";
		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		ArrayList<Rezension> rez = new ArrayList<Rezension>();

		while (rs.next()) {
			String id = rs.getString("id_rezension");
			String idLocation = rs.getString("id_location");
			String idBesucher = rs.getString("id_besucher");
			int bewertung = rs.getInt("bewertung");
			Timestamp ts = rs.getTimestamp("zeitpunkt");
			String text = rs.getString("text");
			
			Rezension r = new Rezension();

			r.setId(id);
			r.setBesucherid(idBesucher);
			r.setLocationid(idLocation);
			r.setBewertung(bewertung);
			r.setTimestamp(ts);
			r.setText(text);
			rez.add(r);
		}
		st.close();
		conn.close();

		return rez;
	}

	public static List<Rezension> get(Location loc) throws SQLException {
		Connection conn = Database.connect();
		  
		String query = "SELECT id_rezension, id_besucher, bewertung, zeitpunkt FROM Rezension r " +
				"where id_location = '" + loc.getId().toString() +"'";
		
		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		List<Rezension> rez = new ArrayList<Rezension>();

		while (rs.next()) {
			String id = rs.getString("id_rezension");
			String idBesucher = rs.getString("id_besucher");
			int bewertung = rs.getInt("bewertung");
			Timestamp ts = rs.getTimestamp("zeitpunkt");
			String text = rs.getString("text");

			Rezension r = new Rezension();

			r.setId(id);
			r.setBesucherid(idBesucher);
			r.setLocationid(loc.getId());
			r.setBewertung(bewertung);
			r.setTimestamp(ts);
			r.setText(text);
			
			rez.add(r);
		}
		st.close();
		conn.close();
		
		return rez;
	}
	
	public static Rezension getById(String id) throws Exception {
		Connection conn = Database.connect();

		String query = "SELECT * FROM Rezension WHERE id_rezension = '" + id + "'";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		Rezension result = null;
		while (rs.next()) {
			String idLocation = rs.getString("id_location");
			String idBesucher = rs.getString("id_besucher");
			int bewertung = rs.getInt("bewertung");
			Timestamp ts = rs.getTimestamp("zeitpunkt");
			String text = rs.getString("text");

			result = new Rezension();
			
			result.setId(id);
			result.setBewertung(bewertung);
			result.setTimestamp(ts);
			result.setBesucherid(idBesucher);
			result.setLocationid(idLocation);
			result.setText(text);
			// print the results
		}
		st.close();

		if(result == null)
			throw new Error404("Rezension nicht gefunden");
		return result;
	}
	
	public static List<Rezension> getByLocation(String id_location) throws Exception {
		Connection conn = Database.connect();

		String query = "SELECT * FROM Rezension WHERE id_location = '" + id_location + "'";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		List<Rezension> rez = new ArrayList<Rezension>();
		while (rs.next()) {
			String id = rs.getString("id_rezension");
			int bewertung = rs.getInt("bewertung");
			Timestamp ts = rs.getTimestamp("zeitpunkt");
			String id_besucher = rs.getString("id_besucher");
			String text = rs.getString("text");

			
			Rezension result = new Rezension();
			
			result.setId(id);
			result.setBewertung(bewertung);
			result.setTimestamp(ts);
			result.setBesucherid(id_besucher);
			result.setLocationid(id_location);
			result.setText(text);
		
			rez.add(result);
		}
		st.close();

		
		return rez;
	}
	
	public static List<Rezension> getByBesucher(String id_besucher) throws Exception {
		Connection conn = Database.connect();

		String query = "SELECT * FROM Rezension WHERE id_besucher = '" + id_besucher + "'";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		List<Rezension> list = new ArrayList<Rezension>();
		while (rs.next()) {
			String id = rs.getString("id_rezension");
			int bewertung = rs.getInt("bewertung");
			Timestamp ts = rs.getTimestamp("zeitpunkt");
			String id_location = rs.getString("id_location");
			String text = rs.getString("text");

			Rezension result = null;
			result = new Rezension();
			
			result.setId(id);
			result.setBewertung(bewertung);
			result.setTimestamp(ts);
			result.setBesucherid(id_besucher);
			result.setLocationid(id_location);
			result.setText(text);
			
			list.add(result);
		}
		st.close();


		return list;
	}

	public static void update(String id, Rezension new_rez) throws Exception {
		int result = 0;
		try {
			Connection conn = Database.connect();
			
			String query = "update Rezension set bewertung = ?, zeitpunkt = ?, text = ? WHERE id_rezension = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			
			preparedStmt.setInt(1, new_rez.getBewertung());
			preparedStmt.setTimestamp(2, new_rez.getTimestamp());
			preparedStmt.setString(3, new_rez.getText());
			preparedStmt.setString(4, id);
			
			result = preparedStmt.executeUpdate();

			conn.close();

			if (result == 0)
				throw new Exception("Fehler beim Updaten der Rezension");
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten! ");
			System.err.println(e.getMessage());
			throw e;
		}

	}

	public static void delete(String id) throws Exception {
		try {
			Connection conn = Database.connect();

			String query = "delete from Rezension where id_rezension = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);

			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten! ");
			System.err.println(e.getMessage());
			throw e;
		}
	}

	public static void deleteByLocation(String id_location) {
		try {
			Connection conn = Database.connect();

			String query = "delete from Rezension where id_location = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(2, id_location);

			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten! ");
			System.err.println(e.getMessage());
		}
	}
	
	public static void deleteByBesucher(String id_besucher) {
		try {
			Connection conn = Database.connect();

			String query = "delete from Rezension where id_besucher = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id_besucher);

			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten! ");
			System.err.println(e.getMessage());
		}
	}
	
	public static void create(Rezension new_rez) throws Exception {
		try {

			if(new_rez.getBesucherid() == null || new_rez.getBesucherid().toString() == "") 
			{
				throw new Exception("Rezension brauch eine Besucher-ID");
			}
			if(new_rez.getLocationid() == null || new_rez.getLocationid().toString() == "") 
			{
				throw new Exception("Rezension brauch eine Location-ID");
			}
			
			Connection conn = Database.connect();

			String query = " insert into Rezension values (?, ?, ?, ?, ?, ?)";

			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, new_rez.getId().toString());
			preparedStmt.setString(2, new_rez.getBesucherid().toString());
			preparedStmt.setString(3, new_rez.getLocationid().toString());
			
			preparedStmt.setInt(4, new_rez.getBewertung());
			preparedStmt.setString(5, new_rez.getText());

			preparedStmt.setTimestamp(6, new_rez.getTimestamp());

			
			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten!");
			System.err.println(e.getMessage());
			throw e;
		}
	}
	
	public static void getWithinDistance(double distanz) {
		
	}

	public static void deleteWithLocId(String id) throws SQLException {
		Connection conn = Database.connect();

		String query = "delete from rezension where id_location = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		preparedStmt.setString(1, id);

		preparedStmt.execute();

		conn.close();
	}
}
