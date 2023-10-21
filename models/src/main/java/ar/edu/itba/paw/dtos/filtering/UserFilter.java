package ar.edu.itba.paw.dtos.filtering;

import java.util.List;

// To be used between Services and Persistence
public class UserFilter {
    private final String username;
    private final String email;
    private final Long reputation;
    private final Boolean enabled;
    private final Long id;
    private final Long notId;
    private final String search;
    private final List<Integer> preferences;
    private final List<Long> gamesPlayed;
    private final Long followingQueryId;
    private final Long followersQueryId;

    public UserFilter(Long id, String username, String email, Boolean enabled, Long reputation, Long notId, String search, List<Integer> preferences, List<Long> gamesPlayed, Long followingQueryId, Long followersQueryId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.reputation = reputation;
        this.notId = notId;
        this.search = search;
        this.preferences = preferences;
        this.gamesPlayed = gamesPlayed;
        this.followingQueryId = followingQueryId;
        this.followersQueryId = followersQueryId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public Long getReputation() {
        return reputation;
    }

    public Long getId() {
        return id;
    }

    public Long getNotId() {
        return notId;
    }

    public String getSearch() {
        return search;
    }

    public List<Integer> getPreferences() {
        return preferences;
    }

    public List<Long> getGamesPlayed() {
        return gamesPlayed;
    }

    public boolean hasFollowingQuery() {
        return followingQueryId != null;
    }

    public boolean hasFollowersQuery() {
        return followersQueryId != null;
    }

    public Long getFollowingQueryId() {
        return followingQueryId;
    }

    public Long getFollowersQueryId() {
        return followersQueryId;
    }
}
