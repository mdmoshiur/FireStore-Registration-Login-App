package com.moshiur.firestoreloginregistration.models;

import com.google.firebase.firestore.Exclude;

public class User {
    private String documentID;
    private String name;
    private String user_id;
    private String password;

    //add constructor
    public User() {
        //empty constructor is needed for firestore
    }

    public User(String name, String user_id, String password) {
        this.name = name;
        this.user_id = user_id;
        this.password = password;
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getName() {
        return name;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPassword() {
        return password;
    }
}
