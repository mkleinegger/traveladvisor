package com.example.traveladvisor.services;

import android.os.AsyncTask;

import com.example.traveladvisor.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServiceSaveUser extends AsyncTask<Void, Void, String> {
    private static final String URL = "TravelAdvisor_WebServices/TravelGuide/besucherDetail";
    private static String ipHost = null;

    private static String uid;

    public static void setIpHost(String ip) {
        ipHost = ip;
    }



    @Override
    protected String doInBackground(Void... voids) {

        java.net.URL url = null;
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String content = null;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        BufferedWriter writer = null;
        Gson gson = new Gson();

        try {
            url = new URL(ipHost + URL );
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            writer = new BufferedWriter(new OutputStreamWriter((conn.getOutputStream())));
            writer.write(gson.toJson(User.getInstance()));
            writer.flush();
            writer.close();

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
