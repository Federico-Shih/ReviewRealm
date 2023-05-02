package ar.edu.itba.paw.models;

import ar.edu.itba.paw.enums.Genre;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {

    private String username;
    private final Long id;
    private final String email;
    private String password;
    private List<Genre> preferences;
    private final boolean enabled;

    public User(Long id, String username, String email, String password, List<Genre> preferences, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.preferences = preferences;
        this.enabled = enabled;
    }

    public User(Long id, String username, String email, String password) {
        this(id, username, email, password, new ArrayList<>(), false);
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

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return this.getId().equals(user.getId());
    }

    public boolean isEnabled() {
        return enabled;
    }
}

