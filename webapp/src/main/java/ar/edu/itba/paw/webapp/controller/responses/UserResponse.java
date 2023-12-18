package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UserResponse extends BaseResponse {
    public static final String BASE_PATH = "/users";
    private long id;
    private String username;
    private String email;
    private Boolean enabled;
    private Long reputation;
    private Locale language;
    private Float xp;
    private URI avatar;
    private Set<URI> preferences;
    private RoleType role;
    private int followers;
    private int following;


    public static UserResponse fromEntity(final UriInfo uri, User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setEnabled(user.isEnabled());
        response.setReputation(user.getReputation());
        response.setLanguage(user.getLanguage());
        response.setXp(user.getXp());
        response.setPreferences(user.getPreferences().stream().map(preferences -> GenreResponse.getLinkFromEntity(uri, preferences)).collect(Collectors.toSet()));
        response.setAvatar(uri.getBaseUriBuilder().replacePath(uri.getBaseUriBuilder().build().getPath().replace("/api", "/static")).path("avatars").path(String.format("%d.png", user.getAvatarId())).build());
        response.setRole(user.getRoles().stream().findFirst().orElse(RoleType.USER));
        response.setFollowers(user.getFollowersCount());
        response.setFollowing(user.getFollowingCount());

        response.link("self", getLinkFromEntity(uri, user));
        response.link("followers", uri.getBaseUriBuilder().path(BASE_PATH).queryParam("followers", user.getId()).build());
        response.link("following", uri.getBaseUriBuilder().path(BASE_PATH).queryParam("following", user.getId()).build());
        response.link("preferences", uri.getBaseUriBuilder().path("genres").queryParam("forUser", user.getId()).build());
        response.link("favoriteGames", uri.getBaseUriBuilder().path(GameResponse.BASE_PATH).queryParam("favoriteOf", user.getId()).build());
        response.link("reviews", uri.getBaseUriBuilder().path(ReviewResponse.BASE_PATH).queryParam("authors", user.getId()).build());
        return response;
    }

    public static URI getLinkFromEntity(final UriInfo uri, User user) {
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

    public Locale getLanguage() {
        return language;
    }

    public Float getXp() {
        return xp;
    }

    public Set<URI> getPreferences() {
        return preferences;
    }

    public void setPreferences(Set<URI> preferences) {
        this.preferences = preferences;
    }

    public URI getAvatar() {
        return avatar;
    }

    public void setAvatar(URI avatar) {
        this.avatar = avatar;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }


    public static class UserResponseBuilder {
        private UserResponse userResponse;
        private User user;
        private UriInfo uriInfo;

        protected UserResponseBuilder() {
        }

        public static UserResponseBuilder fromUser(User user, UriInfo info) {
            UserResponseBuilder builder = new UserResponseBuilder();
            builder.userResponse = UserResponse.fromEntity(info, user);
            builder.user = user;
            builder.uriInfo = info;
            return builder;
        }

        public UserResponseBuilder withAuthed(User currentUser) {
            if (currentUser == null) return this;
            if (user.equals(currentUser)) {
                userResponse.link("updateNotifications",
                        uriInfo.getBaseUriBuilder().path(BASE_PATH).path(String.valueOf(user.getId())).path("notifications").build());
                userResponse.link("patchUser",
                        uriInfo.getBaseUriBuilder().path(BASE_PATH).path(String.valueOf(user.getId())).build());
                userResponse.link("recommendedReviews",
                        uriInfo.getBaseUriBuilder()
                                .path(ReviewResponse.BASE_PATH)
                                .queryParam("recommendedFor", String.valueOf(user.getId()))
                                .build());
                userResponse.link("followingReviews",
                        uriInfo.getBaseUriBuilder()
                                .path(ReviewResponse.BASE_PATH)
                                .queryParam("fromFollowing", String.valueOf(user.getId()))
                                .build());
                userResponse.link("newReviews",
                        uriInfo.getBaseUriBuilder()
                                .path(ReviewResponse.BASE_PATH)
                                .queryParam("newForUser", String.valueOf(user.getId()))
                                .build());
                userResponse.link("recommendedGames",
                        uriInfo.getBaseUriBuilder()
                                .path(GameResponse.BASE_PATH)
                                .queryParam("recommendedFor", String.valueOf(user.getId()))
                                .build());
            } else {
                if (user.isFollowing()) {
                    userResponse.link("unfollow", uriInfo.getBaseUriBuilder().path(BASE_PATH).path(String.valueOf(currentUser.getId())).path("following").path(String.valueOf(user.getId())).build());
                } else {
                    userResponse.link("follow", uriInfo.getBaseUriBuilder().path(BASE_PATH).path(String.valueOf(currentUser.getId())).path("following").build());
                }
            }
            return this;
        }

        public UserResponse build() {
            return userResponse;
        }
    }
}
