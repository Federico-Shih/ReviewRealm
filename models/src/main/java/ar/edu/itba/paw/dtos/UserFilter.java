package ar.edu.itba.paw.dtos;

public class UserFilter {
    private final String username;
    private final String email;
    private final Long reputation;
    private final Boolean enabled;
    private final Long id;

    private final String search;

    public UserFilter(Long id, String username, String email, Boolean enabled, Long reputation, String search) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.reputation = reputation;
        this.search = search;
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

    public String getSearch() {
        return search;
    }
}
