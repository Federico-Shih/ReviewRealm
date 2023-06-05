package ar.edu.itba.paw.dtos.saving;

import java.util.Locale;

public class SaveUserBuilder {
    private String username = null;
    private String email = null;
    private String password = null;
    private Boolean enabled = null;
    private Long avatar = null;
    private Long reputation = null;
    private Locale language = null;
    private Float xp = null;

    public SaveUserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public SaveUserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public SaveUserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public SaveUserBuilder withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public SaveUserBuilder withReputation(Long reputation) {
        this.reputation = reputation;
        return this;
    }

    public SaveUserBuilder withAvatar(Long avatar) {
        this.avatar = avatar;
        return this;
    }

    public SaveUserBuilder withLanguage(Locale language) {
        this.language = language;
        return this;
    }

    public SaveUserBuilder withXp(Float xp) {
        this.xp = xp;
        return this;
    }

    public SaveUserDTO build() {
        return new SaveUserDTO(username, email, password, enabled, reputation, avatar, language, xp);
    }
}
