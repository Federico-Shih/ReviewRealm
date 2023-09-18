package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.User;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class UserResponse extends BaseResponse {
    private static String BASE_PATH = "/users";
    private long id;
    private String username;
    private String email;
    private Boolean enabled;
    private Long reputation;
    private Long avatarId;
    private Locale language;
    private Float xp;

    private Set<Genre> preferences;


    public static UserResponse fromEntity(final UriInfo uri, User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setEnabled(user.isEnabled());
        response.setReputation(user.getReputation());
        response.setLanguage(user.getLanguage());
        response.setXp(user.getXp());
        response.setAvatarId(user.getAvatarId());
        response.setPreferences(user.getPreferences());

        response.link("self", createUserLink(uri, user));
        response.link("followers", uri.getBaseUriBuilder().path(BASE_PATH).path(String.valueOf(user.getId())).path("followers").build());
        response.link("following", uri.getBaseUriBuilder().path(BASE_PATH).path(String.valueOf(user.getId())).path("following").build());
        // TODO: rest of paths
        return response;
    }

    public static URI createUserLink(final UriInfo uri, User user) {
        return uri.getBaseUriBuilder().path(BASE_PATH).path(String.valueOf(user.getId())).build();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, URI> getLinks() {
        return super.getLinks();
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setReputation(Long reputation) {
        this.reputation = reputation;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public void setXp(Float xp) {
        this.xp = xp;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Long getReputation() {
        return reputation;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public Locale getLanguage() {
        return language;
    }

    public Float getXp() {
        return xp;
    }

    public void setPreferences(Set<Genre> preferences) {
        this.preferences = preferences;
    }

    public Set<Genre> getPreferences() {
        return preferences;
    }
}
