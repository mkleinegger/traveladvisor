package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import bll.Besitzer;
import bll.Besucher;

public class BesucherDAL {
	public static List<Besucher> getAll() throws SQLException {
		Connection conn = Database.connect();

		String query = "SELECT * FROM Besucher";
		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		List<Besucher> list = new ArrayList<Besucher>();

		while (rs.next()) {
			String id = rs.getString("benutzer_id");

			Besucher b = new Besucher(id);

			list.add(b);
		}
		st.close();
		conn.close();

		return list;
	}

	public static void delete(String id) throws Exception {
		try {
			Connection conn = Database.connect();

			String query = "delete from Besucher where benutzer_id = ?";
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

	public static int getPunkte(String id) throws Exception {
		try {
			Connection conn = Database.connect();

			String query = "select punkte from Besucher where benutzer_id = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);

			ResultSet rs = preparedStmt.executeQuery();

			int punkte = -1;
			while (rs.next()) {
				punkte = rs.getInt("punkte");
			}
			
			conn.close();
			if(punkte == -1)
				throw new Exception("Benutzer mit id '" + id + "' nicht gefunden");
			return punkte;
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten! ");
			System.err.println(e.getMessage());
			throw e;
		}
	}
	
	public static void create(Besucher new_bes) throws Exception {
		try {
			Connection conn = Database.connect();

			String query = " insert into Besucher " + " values (?, ?)";

			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, new_bes.getId().toString());
			preparedStmt.setInt(2, 0);

			preparedStmt.execute();

			conn.close();
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten!");
			System.err.println(e.getMessage());
			throw e;
		}
	}
}
