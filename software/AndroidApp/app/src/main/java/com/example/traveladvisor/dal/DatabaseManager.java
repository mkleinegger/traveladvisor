package com.example.traveladvisor.dal;

import com.example.traveladvisor.bll.Aktion;
import com.example.traveladvisor.bll.Besuch;
import com.example.traveladvisor.bll.Location;
import com.example.traveladvisor.services.ServiceGetCurrentPointsFromUser;
import com.example.traveladvisor.services.ServiceGetLocationList;
import com.example.traveladvisor.services.ServiceLocationAktionen;
import com.example.traveladvisor.services.ServicePostAktionEinloesen;
import com.example.traveladvisor.services.ServicePostBesuch;
import com.example.traveladvisor.services.ServiceSaveUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class DatabaseManager {
    private static DatabaseManager db = null;
    private static String ipHost = "http://192.168.8.143:8080/";

    private DatabaseManager() {
    }

    public static DatabaseManager newInstance() {
        if (db == null) {
            db = new DatabaseManager();
        }
        return db;
    }

    public static DatabaseManager newInstance(String ip) {
        if (db == null) {
            db = new DatabaseManager();
        }
        ipHost = ip;
        return db;
    }

    public ArrayList<Location> getAllLocations() throws Exception {
        Gson gson = new Gson();
        ArrayList<Location> retLocations;

        ServiceGetLocationList controller = new ServiceGetLocationList();
        ServiceGetLocationList.setIpHost(ipHost);

        controller.execute();

        String strFromWebService = controller.get();
        try {
            Type colltype = new TypeToken<ArrayList<Location>>() {
            }.getType();

            retLocations = gson.fromJson(strFromWebService, colltype);
        } catch (Exception ex) {
            throw new Exception(strFromWebService);
        }

        return retLocations;
    }

    public ArrayList<Aktion> getAktionenFromLocation(Location location) throws Exception {
        Gson gson = new Gson();
        ArrayList<Aktion> retAktionen;

        ServiceLocationAktionen controller = new ServiceLocationAktionen();
        ServiceLocationAktionen.setIpHost(ipHost);
        controller.setLocation(location);

        controller.execute();
        String strFromWebService = controller.get();
        try {
            Type colltype = new TypeToken<ArrayList<Aktion>>() {
            }.getType();
            retAktionen = gson.fromJson(strFromWebService, colltype);
        } catch (Exception ex) {
            throw new Exception(strFromWebService);
        }

        return retAktionen;
    }

    public String userVisitsLocations(Besuch besuch) throws Exception {
        ServicePostBesuch controller = new ServicePostBesuch();
        ServicePostBesuch.setIpHost(ipHost);

        controller.setBesuch(besuch);
        controller.execute();
        return controller.get();
    }

    public String postUser() throws Exception {
        ServiceSaveUser controller = new ServiceSaveUser();
        ServiceSaveUser.setIpHost(ipHost);

        controller.execute();
        return controller.get();
    }

    public int getCurrentAmountOfPoints() throws Exception {
        ServiceGetCurrentPointsFromUser controller = new ServiceGetCurrentPointsFromUser();
        ServiceGetCurrentPointsFromUser.setIpHost(ipHost);

        controller.execute();
        return Integer.parseInt(controller.get());
    }

    public String userRedeemAktion(Aktion aktion) throws Exception{
        ServicePostAktionEinloesen controller = new ServicePostAktionEinloesen();
        ServicePostAktionEinloesen.setIpHost(ipHost);

        controller.setAktion(aktion);
        controller.execute();
        return controller.get();
    }

    public void saveUser(String uid) {

    }
}
