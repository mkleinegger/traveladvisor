package bll;

public class Point {
	private double lon;
	private double lat;
	
	public Point() {}
	
	public Point(double x, double y) {
		super();
		lon = x;
		lat = y;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double x) {
		lon = x;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double y) {
		lat = y;
	}
	
	
}
