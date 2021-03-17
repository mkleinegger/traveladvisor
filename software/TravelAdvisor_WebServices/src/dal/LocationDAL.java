package dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bll.Besitzer;
import bll.Branche;
import bll.Location;
import bll.Point;
import service.LocationList;
import bll.Error404;
import bll.Location;

public class LocationDAL {

	private static List<Location> cache = null;
	private static boolean changed = true;
	
	public static List<Location> getAll(boolean loadBranchen) throws SQLException, Error404 {
		if(cache != null && changed == false) {
			return cache;
		}
		
		Connection conn = Database.connect();

		String query = "SELECT id, id_besitzer, bezeichnung, beschreibung,  punkte, aktiv, tl.koordinaten.SDO_POINT.X as X, "
				+ "tl.koordinaten.SDO_POINT.Y as Y, UTL_RAW.CAST_TO_VARCHAR2(bild) as img FROM TravelLocation tl";
		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		List<Location> locations = new ArrayList<Location>();

		while (rs.next()) {

			String id = rs.getString("id");
			String bezeichnung = rs.getString("bezeichnung");
			String beschreibung = rs.getString("beschreibung");

			String id_besitzer = rs.getString("id_besitzer");
			int punkte = rs.getInt("punkte");
			String aktiv = rs.getString("aktiv");
			double X = rs.getDouble("X");
			double Y = rs.getDouble("Y");
			String img = rs.getString("img");
			
			Location l = new Location();
			l.setId(id);
			l.setBezeichnung(bezeichnung);
			l.setBeschreibung(beschreibung);
			l.setPunkte(punkte);
			if (aktiv.equals("J"))
				l.setAktiv(true);
			else
				l.setAktiv(false);

			l.setKoordinaten(new Point(X, Y));
			l.setBesitzer(new Besitzer(id_besitzer));
			System.out.println(l.getId().toString());
			if(loadBranchen == true)
				l.setBranchen(BrancheDAL.getByLocationId(l.getId().toString()));
			//l.setImg(img);

			locations.add(l);
		}
		
		//Locations lokal zwischenspeichern
		//aber nur, wenn Branchen mitgeladen wurden, sonst ist es nicht vollständig
		
		if(loadBranchen == true) {
			changed = false;
			cache = locations;
		}
		
		st.close();
		conn.close();

		return locations;
	}
	
	public static List<Location> getByBesitzer(Boolean loadBranchen, String id_besitzer) throws SQLException, Error404 {
		if(cache != null && changed == false) {
			List<Location> result = new ArrayList<Location>();
			for(Location l : cache) {
				if(l.getBesitzer().getId() != null) {
					if(l.getBesitzer().getId().equals(id_besitzer))
						result.add(l);
				}
				
			}
			return result;
		}
		Connection conn = Database.connect();

		String query = "SELECT id, id_besitzer, bezeichnung, beschreibung,  punkte, aktiv, tl.koordinaten.SDO_POINT.X as X, "
				+ "tl.koordinaten.SDO_POINT.Y as Y, UTL_RAW.CAST_TO_VARCHAR2(bild) as img FROM TravelLocation tl where id_besitzer = '" + id_besitzer + "'";
		Statement st = conn.createStatement();

		System.out.println(query);
		ResultSet rs = st.executeQuery(query);

		// iterate through the java resultset
		List<Location> locations = new ArrayList<Location>();

		while (rs.next()) {

			String id = rs.getString("id");
			String bezeichnung = rs.getString("bezeichnung");
			String beschreibung = rs.getString("beschreibung");

			int punkte = rs.getInt("punkte");
			String aktiv = rs.getString("aktiv");
			double X = rs.getDouble("X");
			double Y = rs.getDouble("Y");
			String img = rs.getString("img");
			
			Location l = new Location();
			l.setId(id);
			// ToDO:
			l.setBesitzer(null);
			l.setBezeichnung(bezeichnung);
			l.setBeschreibung(beschreibung);
			l.setPunkte(punkte);
			l.setBesitzer(new Besitzer(id_besitzer));
			
			if (aktiv.equals("J"))
				l.setAktiv(true);
			else
				l.setAktiv(false);

			l.setKoordinaten(new Point(X, Y));
			System.out.println(l.getId().toString());
			if(loadBranchen != null && loadBranchen == true)
				l.setBranchen(BrancheDAL.getByLocationId(l.getId().toString()));
			//l.setImg(img);

			locations.add(l);
		}
		
		st.close();
		conn.close();

		return locations;
	}

	public static Location getById(String id) throws Exception {
		if(cache != null && changed == false) {
			for(Location l : cache) {
				if(l.getId().toString().equals(id))
					return l;
			}
		}
		Connection conn = Database.connect();

		String query = "SELECT  id, id_besitzer, bezeichnung, punkte, aktiv, tl.koordinaten.SDO_POINT.X as X, "
				+ "tl.koordinaten.SDO_POINT.Y as Y FROM TravelLocation tl WHERE tl.id = '" + id + "'";

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery(query);

		Location result = null;
		while (rs.next()) {
			String bezeichnung = rs.getString("Bezeichnung");
			String id_besitzer = rs.getString("id_besitzer");
			int punkte = rs.getInt("punkte");
			String aktiv = rs.getString("aktiv");
			double X = rs.getDouble("X");
			double Y = rs.getDouble("Y");

			Location l = new Location();
			l.setId(id);
			// ToDO:
			l.setBesitzer(new Besitzer(id_besitzer));
			l.setBezeichnung(bezeichnung);
			l.setPunkte(punkte);
			if (aktiv.equals("J"))
				l.setAktiv(true);
			else
				l.setAktiv(false);

			l.setKoordinaten(new Point(X, Y));

			l.setBranchen(BrancheDAL.getByLocationId(l.getId().toString()));
			result = l;
		}
		st.close();

		if (result == null)
			throw new Exception("Location nicht gefunden");
		return result;
	}

	public static void update(String id, Location new_loc) throws Exception {
		int result = 0;
		try {
			System.out.println("Location-update läuft . . .");
			Connection conn = Database.connect();
			System.out.println("Verbindung hergestellt . . . ");

			System.out.println("Bezeichnung: " + new_loc.getBezeichnung());
			System.out.println("Punkte: " + new_loc.getPunkte());

			String koordinaten = "SDO_GEOMETRY( 2001, NULL, SDO_POINT_TYPE(" + new_loc.getKoordinaten().getLon() +", " + new_loc.getKoordinaten().getLat() +", NULL), NULL,"
					+ " NULL)";

			String query = "update TravelLocation set id_besitzer = ?, bezeichnung = ?, beschreibung = ?, punkte = ?, aktiv = ?, koordinaten = " + koordinaten + " where id = ?";

			//String query = "update TravelLocation set koordinaten = " + koordinaten + " where id = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			if(new_loc.getBesitzer() != null)
				preparedStmt.setString(1, new_loc.getBesitzer().getId().toString());
			else
				preparedStmt.setString(1, null);

			preparedStmt.setString(2, new_loc.getBezeichnung());
			preparedStmt.setString(3, new_loc.getBeschreibung());
			preparedStmt.setInt(4, new_loc.getPunkte());
			if (new_loc.isAktiv())
				preparedStmt.setString(5, "J");
			else
				preparedStmt.setString(5, "N");

			preparedStmt.setString(6, id);

			System.out.println("Statememt fertig gebaut . . .");
			result = preparedStmt.executeUpdate();
			System.out.println("Statement ausgeführt!");

			conn.close();
			System.out.println("Verbindung abgebaut");
			//changed = true;
			
			System.out.println("Cache: " + cache.size() + ":   " + cache);
			if(cache != null) {
				Location locToRemove = null;
				for(Location l : cache) {
					System.out.println(l.getId().toString());
					if(l.getId().toString().equals(id)) {
						locToRemove = l;
						System.out.println("Location gefunden");
					}
				}
				if(locToRemove != null) {
					cache.remove(locToRemove);
					new_loc.setId(id);
					cache.add(new_loc);
				}
			}
			
			//System.out.println("Verbindung geschlossen");
			if (result == 0)
				throw new Exception("Fehler beim Updaten der Location mit der ID " + new_loc.getId());

			new_loc.setId(id);
			
			BrancheDAL.removeBranchen(id);

			for(Branche b : new_loc.getBranchen()) 
				LocationDAL.addBranche(new_loc, b);
			
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten! ");
			System.err.println(e.getMessage());
			throw e;
		}

	}

	public static void delete(String id) {
		try {
			BrancheDAL.removeBranchen(id);
			RezensionenDAL.deleteWithLocId(id);
			PraemienDAL.deletyWithLocId(id);
			BesuchDAL.deleteWithLocId(id);
			
			Connection conn = Database.connect();

			String query = "delete from TravelLocation where id = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);

			preparedStmt.execute();

			conn.close();
			
			//changed = true;
			if(cache != null) {
				Location locToRemove = null;
				for(Location l : cache) {
					if(l.getId().toString().equals(id)) {
						locToRemove = l;
					}
				}
				cache.remove(locToRemove);
			}
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten! ");
			System.err.println(e.getMessage());
		}
	}

	public static void create(Location new_loc) {
		try {
			Connection conn = Database.connect();

			String koordinaten = "SDO_GEOMETRY( " + "2001, " + "NULL," + "SDO_POINT_TYPE(?, ?, NULL)," + "NULL,"
					+ "NULL)";

			String query = " insert into TravelLocation values (?, ?, ?, ?, ?, ?, " + koordinaten + ""
					+ ", utl_raw.cast_to_raw(?), 0"
					+ ")";

			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, new_loc.getId().toString());
			preparedStmt.setString(2, new_loc.getBesitzer().getId().toString());
			preparedStmt.setString(3, new_loc.getBezeichnung());
			preparedStmt.setString(4, new_loc.getBeschreibung());
			preparedStmt.setInt(5, new_loc.getPunkte());
			if (new_loc.isAktiv())
				preparedStmt.setString(6, "J");
			else
				preparedStmt.setString(6, "N");

			preparedStmt.setDouble(7, new_loc.getKoordinaten().getLon());
			preparedStmt.setDouble(8, new_loc.getKoordinaten().getLat());
			/*
			String img = new_loc.getImg();
			if(img != null)
				preparedStmt.setString(9, img);
			else*/
			preparedStmt.setString(9, "null");
			
			preparedStmt.execute();

			
			conn.close();

			//changed = true;
			
			if(cache != null) {
				cache.add(new_loc);
			}
			
			System.out.println("Location erstellt!");
			for (Branche b : new_loc.getBranchen()) {
				System.out.println("Branche " + b + " wird zur Location " + new_loc + " hinzugefügt . . . ");
				LocationDAL.addBranche(new_loc, b);
			}
			
			
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten!");
			System.err.println(e.getMessage());
		}
	}

	public static void addBranche(Location l, Branche branche) {
		try {
			Connection conn = Database.connect();

			String query = " insert into Location_Branche_Zuordnung values (?, ?)";

			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, l.getId().toString());
			preparedStmt.setString(2, branche.getId().toString());

			preparedStmt.execute();

			conn.close();
			System.out.println("Branche " + branche+ " erfolgreich zur Location " + l + " hinzugefügt!");
			
	
		} catch (Exception e) {
			System.err.println("Ein Fehler ist aufgetreten!");
			System.err.println(e.getMessage());
		}
	}
	
	public static List<Location> getWithinDistance(double distanz, double x, double y, Boolean loadBranchen) throws Exception{
		List<Location> locations = new ArrayList<Location>();
		try {
			Connection conn = Database.connect();

			String query = "SELECT id, id_besitzer, bezeichnung, beschreibung,  punkte, aktiv, t.koordinaten.SDO_POINT.X as X,t.koordinaten.SDO_POINT.Y as Y" + 
					"    from TravelLocation t" + 
					"    where SDO_WITHIN_DISTANCE(t.KOORDINATEN, " + 
					"	SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE(" + x + ", " + y + ", NULL), NULL, NULL), 'distance = " + (distanz / 100000) + "') = 'TRUE'";
			System.out.println(query);
			
			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery(query);

			// iterate through the java resultset
			

			while (rs.next()) {

				String id = rs.getString("id");
				String bezeichnung = rs.getString("bezeichnung");
				String beschreibung = rs.getString("beschreibung");

				String id_besitzer = rs.getString("id_besitzer");
				int punkte = rs.getInt("punkte");
				String aktiv = rs.getString("aktiv");
				double X = rs.getDouble("X");
				double Y = rs.getDouble("Y");
				//String img = rs.getString("img");
				
				Location l = new Location();
				l.setId(id);
				// ToDO:
				l.setBesitzer(new Besitzer(id_besitzer));
				l.setBezeichnung(bezeichnung);
				l.setBeschreibung(beschreibung);
				l.setPunkte(punkte);
				if (aktiv.equals("J"))
					l.setAktiv(true);
				else
					l.setAktiv(false);

				l.setKoordinaten(new Point(X, Y));
				System.out.println(l.getId().toString());
				if(loadBranchen != null && loadBranchen == true)
					l.setBranchen(BrancheDAL.getByLocationId(l.getId().toString()));

				locations.add(l);
			}
			st.close();
			conn.close();

			
		}
		catch(Exception e) {
			System.err.println("Ein Fehler ist aufgetreten!");
			System.err.println(e.getMessage());
			throw e;
		}
		return locations;
	}

}
