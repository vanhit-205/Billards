package com.example.billards.Models;

public class Users {
    private String uid;
    private String name;
    private String email;
    private String role;

    public Users(){}

    public Users(String uid,String name,String email,String role){
        this.uid=uid;
        this.name=name;
        this.email=email;
        this.role=role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getUid() {
        return uid;
    }
}
