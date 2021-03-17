package com.example.traveladvisor.bll;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Location implements Parcelable {
    private UUID id;
    private String bezeichnung;
    private String beschreibung;
    private boolean aktiv;
    public int punkte;
    private List<Branche> branchen = new ArrayList<Branche>();;
    private Besitzer besitzer;
    private Point koordinaten;

    public Location() {
        branchen = new ArrayList<Branche>();
        bezeichnung = "";
        beschreibung = "";
        koordinaten = new Point();
    }

    protected Location(Parcel in) {
        setId(in.readString());
        bezeichnung = in.readString();
        beschreibung = in.readString();
        aktiv = in.readByte() != 0;
        punkte = in.readInt();
        in.readList(branchen, Branche.class.getClassLoader());
    }

    public UUID getId() {
        return id;
    }

    public void setId(String id) {
        try {
            this.id = UUID.fromString(id);
        } catch (Exception ex) {
            this.id = null;
        }
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public boolean isAktiv() {
        return aktiv;
    }

    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }

    public int getPunkte() {
        return punkte;
    }

    public void setPunkte(int punkte) {
        this.punkte = punkte;
    }

    public List<Branche> getBranchen() {
        return branchen;
    }

    public void setBranchen(List<Branche> branche) {
        this.branchen = branche;
    }

    public void addBranche(Branche branche) {
        if (this.branchen == null)
            this.branchen = new ArrayList<Branche>();
        if (!this.branchen.contains(branche))
            this.branchen.add(branche);
    }

    public String getBranchenAsString() {
        String result = "";

        for (Branche b : branchen)
            result += b.getBezeichnung() + ", ";

        return result.substring(0, result.length() - 2);
    }

    public Besitzer getBesitzer() {
        return besitzer;
    }

    public void setBesitzer(Besitzer besitzer) {
        this.besitzer = besitzer;
    }

    public Point getKoordinaten() {
        return koordinaten;
    }

    public void setKoordinaten(Point koordinaten) {
        this.koordinaten = koordinaten;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public void generateUUID() {
        this.id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return this.bezeichnung + " mit der id: " + this.id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id.toString());
        dest.writeString(bezeichnung);
        dest.writeString(beschreibung);
        dest.writeByte((byte) (aktiv ? 1 : 0));
        dest.writeInt(punkte);
        dest.writeList(branchen);
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
