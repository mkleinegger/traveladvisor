package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import bll.Branche;
import bll.Location;

public class BrancheDAL {
public static List<Branche> branchen;

	public static List<Branche> getAll() throws SQLException {
		Connection conn = Database.connect();

		String query = "SELECT * FROM Branche";
		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		branchen = new ArrayList<Branche>();

		while (rs.next()) {
			String id = rs.getString("id");
			String bezeichnung = rs.getString("Bezeichnung");

			Branche b = new Branche(id, bezeichnung);

			branchen.add(b);
		}
		st.close();
		conn.close();

		return branchen;
	}

	public static List<Branche> getByLocationId(String loc) throws SQLException {
		Connection conn = Database.connect();
		  
		String query = "SELECT b.id as id, b.bezeichnung as bezeichnung FROM Location_Branche_Zuordnung lbz " +
				"inner join Branche b on b.id = lbz.id_branche " +
				"where id_location = '" + loc +"'";
		
		System.out.println(query);
		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		List<Branche> branchen = new ArrayList<Branche>();

		while (rs.next()) {
			String id = rs.getString("id");
			String bezeichnung = rs.getString("Bezeichnung");

			Branche b = new Branche(id, bezeichnung);

			branchen.add(b);
		}
		st.close();
		conn.close();
		
		return branchen;
	}
	
	public static void removeBranchen(String id_loc) throws SQLException {		
		Connection conn = Database.connect();

		String query = "delete from Location_Branche_Zuordnung where id_location = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		preparedStmt.setString(1,id_loc );

		preparedStmt.execute();

		conn.close();
		
	}
	
	
	public static Branche getById(String id) throws Exception {
		Connection conn = Database.connect();

		String query = "SELECT * FROM Branche WHERE id = '" + id + "'";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		Branche result = null;
		while (rs.next()) {
			String description = rs.getString("Bezeichnung");
			result = new Branche(id, description);
			// print the results
		}
		st.close();

		if(result == null)
			throw new Exception("Branche nicht gefunden");
		return result;
	}

	public static void update(String id, Branche new_bra) throws Exception {
		int result = 0;
		try {
			Connection conn = Database.connect();
			
			String query = "update Branche set Bezeichnung = ? " + " where id = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, new_bra.getBezeichnung());

			preparedStmt.setString(2, id);

			result = preparedStmt.executeUpdate();

			conn.close();

			if (result == 0)
				throw new Exception("Fehler beim Updaten der Branche mit der ID " + new_bra.getId());
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten! ");
			System.err.println(e.getMessage());
			throw e;
		}

	}

	public static void delete(String id) {
		try {
			Connection conn = Database.connect();

			String query = "delete from Branche where id = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);

			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten! ");
			System.err.println(e.getMessage());
		}
	}

	public static void create(Branche new_bra) {
		try {

			Connection conn = Database.connect();

			String query = " insert into Branche values (?, ?)";

			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, new_bra.getId().toString());
			preparedStmt.setString(2, new_bra.getBezeichnung());

			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten!");
			System.err.println(e.getMessage());
		}
	}
}
