package com.example.billards.Models;

public class UserSession {
    private static UserSession instance;
    private Users currentUser;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(Users user) {
        this.currentUser = user;
    }

    public Users getUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }

    public void clear() {
        currentUser = null;
    }
}

