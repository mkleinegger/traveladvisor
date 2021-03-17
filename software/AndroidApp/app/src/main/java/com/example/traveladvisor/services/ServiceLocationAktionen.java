package com.example.traveladvisor.services;

import android.os.AsyncTask;

import com.example.traveladvisor.bll.Location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServiceLocationAktionen extends AsyncTask<Void, Void, String> {
    private static final String URL = "TravelAdvisor_WebServices/TravelGuide/locationDetail";
    private static String ipHost = null;
    private Location location = null;

    public static void setIpHost(String ip) {
        ipHost = ip;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
    @Override
    protected String doInBackground(Void... voids) {
        boolean isError = false;
        java.net.URL url = null;
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String content = null;

        try {
            String fullURL = URL + "/"+location.getId()+"/praemien";
            url = new URL(ipHost + fullURL );
            conn = (HttpURLConnection) url.openConnection();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            content = sb.toString();
            if (isError) {
                throw new Exception(conn.getResponseCode() + "; " + content);
            }
        } catch (Exception ex) {
            content = ex.getMessage();
        } finally {
            try {
                reader.close();
                conn.disconnect();
            } catch (Exception e) {
            }
        }
        return content;
    }
}
