package com.example.traveladvisor.services;

import android.os.AsyncTask;

import com.example.traveladvisor.bll.Besuch;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class ServicePostBesuch extends AsyncTask<Void, Void, String> {
    private static final String URL = "TravelAdvisor_WebServices/TravelGuide/locationBesuchDetail";
    private static String ipHost = null;
    private Besuch besuch = null;

    public static void setIpHost(String ip) {
        ipHost = ip;
    }
    public void setBesuch(Besuch besuch) {
        this.besuch = besuch;
    }

    @Override
    protected String doInBackground(Void... params) {
        java.net.URL url = null;
        HttpURLConnection conn = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;
        String content = null;
        Gson gson = new Gson();

        try {
            url = new java.net.URL(this.ipHost + URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Content-Type", "application/json");
            writer = new BufferedWriter(new OutputStreamWriter((conn.getOutputStream())));
            writer.write(gson.toJson(besuch));
            writer.flush();
            writer.close();

            if (conn.getResponseCode() != 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                content = conn.getResponseCode() + " " + sb.toString();
            } else {
                content = "ResponseCode: " + conn.getResponseCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                writer.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }
}