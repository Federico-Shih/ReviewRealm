package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.models.User;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Locale;
import java.util.Map;

public class UserResponse extends BaseResponse {
    private long id;
    private String username;
    private String email;
    private Boolean enabled;
    private Long reputation;
    private Long avatarId;
    private Locale language;
    private Float xp;

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

        response.add("self", uri.getBaseUriBuilder().path("/users").path(String.valueOf(user.getId())).build());
        // TODO: rest of paths
        return response;
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
}
