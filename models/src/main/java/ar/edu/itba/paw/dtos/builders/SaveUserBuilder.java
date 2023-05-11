package ar.edu.itba.paw.dtos.builders;

import ar.edu.itba.paw.dtos.SaveUserDTO;

public class SaveUserBuilder {
    private String username = null;
    private String email = null;
    private String password = null;
    private Boolean enabled = null;
    private Long reputation;

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

    public SaveUserDTO build() {
        return new SaveUserDTO(username, email, password, enabled, reputation);
    }
}
