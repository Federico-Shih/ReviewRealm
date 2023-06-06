package ar.edu.itba.paw.dtos.saving;

import java.util.Locale;
import java.util.Objects;

public class SaveUserDTO {
    private final String username;
    private final String email;
    private final String password;
    private final Long reputation;
    private final Long avatar;
    private final Boolean enabled;
    private final Locale language;

    private final Float xp;

    public SaveUserDTO(String username, String email, String password, Boolean enabled, Long reputation, Long avatar, Locale language, Float xp) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.reputation = reputation;
        this.avatar = avatar;
        this.language = language;
        this.xp = xp;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public Long getReputation() {
        return reputation;
    }

    public Long getAvatar() { return avatar;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SaveUserDTO)) return false;
        SaveUserDTO that = (SaveUserDTO) o;
        return Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(password, that.password) && Objects.equals(reputation, that.reputation) && Objects.equals(enabled, that.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, password, reputation, enabled);
    }

    public Locale getLanguage() {
        return language;
    }

    public Float getXp() {
        return xp;
    }
}
