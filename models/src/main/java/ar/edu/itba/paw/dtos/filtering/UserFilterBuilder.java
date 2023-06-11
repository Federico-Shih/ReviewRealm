package ar.edu.itba.paw.dtos.filtering;

import java.util.List;

// To be used between Services and Persistence
public class UserFilterBuilder {
    private String username = null;
    private String email = null;
    private Long reputation = null;
    private Boolean enabled = null;
    private Long id = null;
    private Long notId = null;
    private String search;
    private List<Integer> preferences = null;
    private List<Long> gamesPlayed = null;

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

    public UserFilterBuilder notWithId(Long id) {
        this.notId = id;
        return this;
    }

    public UserFilterBuilder withSearch(String search) {
        this.search = search;
        return this;
    }

    public UserFilterBuilder withPreferences(List<Integer> preferences) {
        this.preferences = preferences;
        return this;
    }

    public UserFilterBuilder withGamesPlayed(List<Long> gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
        return this;
    }

    public UserFilter build() {
        return new UserFilter(id, username, email, enabled, reputation, notId, search, preferences, gamesPlayed);
    }
}
