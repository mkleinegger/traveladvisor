package com.example.traveladvisor.bll;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class Branche implements Parcelable {
    private UUID id;
    private String bezeichnung;

    public Branche() {
    }

    public Branche(String id, String bezeichnung) {
        super();
        this.id = UUID.fromString(id);
        this.bezeichnung = bezeichnung;
    }

    public Branche(String bezeichnung) {
        super();
        this.bezeichnung = bezeichnung;
    }

    protected Branche(Parcel in) {
        setId(in.readString());
        setBezeichnung(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id.toString());
        dest.writeString(bezeichnung);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Branche> CREATOR = new Creator<Branche>() {
        @Override
        public Branche createFromParcel(Parcel in) {
            return new Branche(in);
        }

        @Override
        public Branche[] newArray(int size) {
            return new Branche[size];
        }
    };

    public UUID getId() {
        return id;
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public void generateUUID() {
        this.id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return this.bezeichnung + " mit der id: " + this.id;
    }
}
