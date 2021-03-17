package com.example.traveladvisor.services;

import android.os.AsyncTask;

import com.example.traveladvisor.bll.Aktion;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServicePostAktionEinloesen extends AsyncTask<Void, Void, String> {
    private static final String URL = "TravelAdvisor_WebServices/TravelGuide/praemienDetail/einloesen";
    private static String ipHost = null;
    private Aktion aktion = null;

    public static void setIpHost(String ip) {
        ipHost = ip;
    }
    public void setAktion(Aktion aktion) {
        this.aktion = aktion;
    }
    @Override
    protected String doInBackground(Void... voids) {

        java.net.URL url = null;
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String content = null;
        try {
            String fullURL = URL + "/"+ aktion.getId()+"/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"";
            url = new URL(ipHost + fullURL );
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            if (conn.getResponseCode() != 201) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                content = conn.getResponseCode() + " " + sb.toString();
            } else {
                content = "Successful, ResponseCode: " + conn.getResponseCode();
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
