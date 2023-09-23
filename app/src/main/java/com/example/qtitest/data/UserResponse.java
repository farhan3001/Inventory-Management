package com.example.qtitest.data;

import java.io.Serializable;

public class UserResponse implements Serializable {
    private String id;
    private String email;
    private String username;
    private boolean is_active;
    private String refreshed_token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getRefreshed_token() {
        return refreshed_token;
    }

    public void setRefreshed_token(String refreshed_token) {
        this.refreshed_token = refreshed_token;
    }
}
