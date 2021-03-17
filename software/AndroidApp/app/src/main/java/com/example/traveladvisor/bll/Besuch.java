package com.example.traveladvisor.bll;

import java.sql.Timestamp;
import java.util.UUID;

public class Besuch {
    private UUID id;
    private String besucherId;
    private UUID locationId;
    private Timestamp zeitpunkt;

    public Besuch() {
    }

    public Besuch(String locationId, String besucherId){
        this.setLocationId(locationId);
        this.setBesucherId(besucherId);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    public String getBesucherId() {
        return besucherId;
    }

    public void setBesucherId(String besucherId) {
        this.besucherId = besucherId;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = UUID.fromString(locationId);
    }

    public void generateId() {
        this.id = UUID.randomUUID();
    }

    public void setZeitpunkt(Timestamp timestamp) {
        this.zeitpunkt = timestamp;
    }

    public Timestamp getZeitpunkt() {
        return this.zeitpunkt;
    }
}
