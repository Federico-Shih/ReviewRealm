package ar.edu.itba.paw.models;

import ar.edu.itba.paw.enums.Genre;

import java.util.*;

public class User {

    private String username;
    private final Long id;
    private final String email;
    private String password;
    private Set<Genre> preferences;
    private final boolean enabled;
    private final Long reputation;
    private final Set<DisabledNotification> disabledNotifications;
    private final Set<Role> roles;

    private final Long avatarId;

    public User(Long id,
                String username,
                String email,
                String password,
                Set<Genre> preferences,
                boolean enabled,
                Long reputation,
                Set<DisabledNotification> disabledNotifications,
                Set<Role> roles,
                Long avatarId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.preferences = preferences;
        this.enabled = enabled;
        this.reputation = reputation;
        this.disabledNotifications = disabledNotifications;
        this.roles = roles;
        this.avatarId = avatarId;
    }

    public User(Long id, String username, String email, String password) {
        this(id, username, email, password, new HashSet<>(), false,0L, new HashSet<>(), new HashSet<>(), 0L);
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

    public Long getReputation() {
        return reputation;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Genre> getPreferences() {
        return preferences;
    }

    public Boolean hasPreferencesSet() {
        return !preferences.isEmpty();
    }

    public void setPreferences(Set<Genre> preferences) {
        this.preferences = preferences;
    }

    public Long getAvatarId() {
        return avatarId;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public Set<DisabledNotification> getDisabledNotifications() {
        return disabledNotifications;
    }
}

