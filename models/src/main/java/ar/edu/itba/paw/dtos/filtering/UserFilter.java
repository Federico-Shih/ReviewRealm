package ar.edu.itba.paw.dtos.filtering;

import org.apache.commons.lang3.ObjectUtils;

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
    private final Long samePreferencesQueryId;
    private final Long sameGamesPlayedQueryId;

    public UserFilter(Long id, String username, String email, Boolean enabled, Long reputation, Long notId, String search, List<Integer> preferences, List<Long> gamesPlayed, Long followingQueryId, Long followersQueryId, Long samePreferencesQueryId, Long sameGamesPlayedQueryId) {
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
        this.samePreferencesQueryId = samePreferencesQueryId;
        this.sameGamesPlayedQueryId = sameGamesPlayedQueryId;
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

    public boolean hasSamePreferencesQuery() {
        return samePreferencesQueryId != null;
    }

    public boolean hasSameGamesPlayedQuery() {
        return sameGamesPlayedQueryId != null;
    }

    public Long getFollowingQueryId() {
        return followingQueryId;
    }

    public Long getFollowersQueryId() {
        return followersQueryId;
    }

    public Long getSamePreferencesQueryId() {
        return samePreferencesQueryId;
    }

    public Long getSameGamesPlayedQueryId() {
        return sameGamesPlayedQueryId;
    }

    private boolean allBasicPropertiesNull() {
        return ObjectUtils.allNull(this.username, this.email, this.reputation, this.enabled, this.id,
                this.notId, this.search) && (this.preferences == null || this.preferences.isEmpty()) && (this.gamesPlayed == null || this.gamesPlayed.isEmpty());
    }

    public boolean isProperFollowersQuery() {
        return allBasicPropertiesNull() && ObjectUtils.allNull(this.samePreferencesQueryId, this.followingQueryId, this.sameGamesPlayedQueryId);
    }

    public boolean isProperFollowingQuery() {
        return allBasicPropertiesNull() && ObjectUtils.allNull(this.samePreferencesQueryId, this.followersQueryId, this.sameGamesPlayedQueryId);
    }

    public boolean isProperSamePreferencesAs() {
        return allBasicPropertiesNull() && ObjectUtils.allNull(this.followersQueryId, this.followingQueryId, this.sameGamesPlayedQueryId);
    }

    public boolean isProperSameGamesPlayedAs() {
        return allBasicPropertiesNull() && ObjectUtils.allNull(this.followersQueryId, this.followingQueryId, this.samePreferencesQueryId);
    }
}
