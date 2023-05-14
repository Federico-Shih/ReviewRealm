package ar.edu.itba.paw.dtos.filtering;

public class UserFilterBuilder {
    private String username = null;
    private String email = null;
    private Long reputation = null;
    private Boolean enabled = null;

    private Long id = null;
    private String search;

    public UserFilterBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserFilterBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserFilterBuilder withReputation(Long reputation) {
        this.reputation = reputation;
        return this;
    }

    public UserFilterBuilder withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public UserFilterBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserFilterBuilder withSearch(String search) {
        this.search = search;
        return this;
    }
    public UserFilter build() {
        return new UserFilter(id, username, email, enabled, reputation, search);
    }
}
