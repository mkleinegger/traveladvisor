package com.example.traveladvisor;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.traveladvisor.dal.DatabaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User {
    private static User user = null;

    private String email;
    private String fistname;
    private String lastname;
    private String uid;
    private String typ;
    private String info;



    private User() {
    }

    public static User getInstance() {
        if(user == null)
            user = new User();

        if(user.uid == null || user.uid == "")
            user.setUid(getCurrentLoggedInUser_Uid());

        if(user.email == null || user.email == "")
            user.setEmail(getCurrentLoggedInUser_Email());

        return user;
    }


    public static String getCurrentLoggedInUser_Uid() {
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseUser fuser = fauth.getCurrentUser();

        if(fuser != null)
            return fuser.getUid();

        return null;
    }

    public static String getCurrentLoggedInUser_Email() {
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseUser fuser = fauth.getCurrentUser();

        if(fuser != null)
            return fuser.getEmail();

        return null;
    }

    public String getInfo() {
        return info;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFistname() {
        return fistname;
    }

    public void setFistname(String fistname) {
        this.fistname = fistname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", fistname='" + fistname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", uid='" + uid + '\'' +
                ", typ='" + typ + '\'' +
                '}';
    }

    public void saveToSQLDB() throws Exception {
        DatabaseManager.newInstance().postUser();
    }
}
