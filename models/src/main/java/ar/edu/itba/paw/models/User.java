package ar.edu.itba.paw.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String username;
    private final Long id;
    private final String email;
    private String password;
    private List<Genre> preferences;

    public User(Long id, String username, String email, String password, List<Genre> preferences) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.preferences = preferences;
    }

    public User(Long id, String email, String password) {
        this(id, email.split("@")[0], email, password, new ArrayList<>());
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Genre> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<Genre> preferences) {
        this.preferences = preferences;
    }
}

