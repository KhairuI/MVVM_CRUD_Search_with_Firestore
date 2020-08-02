package com.example.firestore_mvvm.model;

import java.io.Serializable;

public class SignInUser implements Serializable {

    public String uId;
    public String name;
    public String email;
    public String imageUrl;
    public boolean isAuth;

    public SignInUser() {
        //empty
    }

    public SignInUser(String uId, String name, String email, String imageUrl) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
