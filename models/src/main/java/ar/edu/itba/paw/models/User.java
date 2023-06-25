package ar.edu.itba.paw.models;

import ar.edu.itba.paw.converters.GenreAttributeConverter;
import ar.edu.itba.paw.converters.NotificationTypeAttributeConverter;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.LevelRange;
import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.enums.RoleType;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(sequenceName = "users_id_seq", name = "users_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(targetClass = Genre.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "genreforusers", joinColumns = @JoinColumn(name = "userid", referencedColumnName = "id"))
    @Column(name = "genreid")
    @Convert(converter = GenreAttributeConverter.class)
    private Set<Genre> preferences = new HashSet<>();

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "reputation")
    private Long reputation = 0L;

    @ElementCollection(targetClass = NotificationType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "user_disabled_notifications", joinColumns = @JoinColumn(name = "userid", referencedColumnName = "id"))
    @Column(name = "notification")
    @Convert(converter = NotificationTypeAttributeConverter.class)
    private Set<NotificationType> disabledNotifications = new HashSet<>();

    @ElementCollection(targetClass = RoleType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "userid", referencedColumnName = "id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<RoleType> roles = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "followers",
            uniqueConstraints = {@UniqueConstraint(name = "uniqueFollowers", columnNames = {"userid", "following"})},
            joinColumns = @JoinColumn(name = "userid", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "following", referencedColumnName = "id")
    )
    private Set<User> following;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "following")
    private Set<User> followers;

    @Column(name = "avatar")
    private Long avatarId;

    @Column(name = "language")
    private Locale language;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<ReviewFeedback> reviewFeedbackList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    private List<Review> reviews;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "favoritegames",
            joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "gameid", referencedColumnName = "id")
    )
    private List<Game> favoriteGames;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<ExpirationToken> expirationTokenList;

    @Column(name = "xp")
    private Float xp = 0f;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<MissionProgress> missions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reporter")
    private List<Report> reportsMade;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reportedUser")
    private List<Report> infractions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "moderator")
    private List<Report> resolvedReports;

    public User(String username,
                String email,
                String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.avatarId = 0L;
        this.reputation = 0L;
        this.enabled = false;
    }

    public User(Long id,
                String username,
                String email,
                String password,
                Set<Genre> preferences,
                boolean enabled,
                Long reputation,
                Set<NotificationType> disabledNotifications,
                Set<RoleType> roles,
                Long avatarId,
                Set<User> following,
                Set<User> followers
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.preferences = preferences;
        this.enabled = enabled;
        this.reputation = reputation;
        this.disabledNotifications = disabledNotifications;
        this.roles = roles;
        this.avatarId = avatarId;
        this.followers = followers;
        this.following = following;
    }

    public User(Long id, String username, String email, String password) {
        this(id, username, email, password, new HashSet<>(), false, 0L, new HashSet<>(), new HashSet<>(), 0L, new HashSet<>(), new HashSet<>());
    }

    protected User() {

    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setReputation(Long reputation) {
        this.reputation = reputation;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Long getReputation() {
        return reputation;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Genre> getPreferences() {
        return preferences;
    }

    public Boolean hasPreferencesSet() {
        return !preferences.isEmpty();
    }

    public void setPreferences(Set<Genre> preferences) {
        this.preferences = preferences;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public List<Game> getFavoriteGames() {
        return favoriteGames;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return this.getId().equals(user.getId());
    }

    public List<ExpirationToken> getExpirationTokenList() {
        return expirationTokenList;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<RoleType> getRoles() {
        if (roles.size() == 0) {
            return new HashSet<>(Collections.singletonList(RoleType.USER));
        }
        return roles;
    }

    public Set<NotificationType> getDisabledNotifications() {
        return disabledNotifications;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public List<ReviewFeedback> getReviewFeedbackList() {
        return reviewFeedbackList;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public Locale getLanguage() {
        if (language == null) {
            return new Locale("es");
        }
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public List<MissionProgress> getMissions() {
        return missions;
    }

    public Float getXp() {
        return xp;
    }

    public void setXp(Float xp) {
        this.xp = xp;
    }

    public int getLevel() {
        return (int) (xp / 100);
    }

    public LevelRange getLevelRange() {
        return LevelRange.levelToRange(getLevel());
    }

    public void setRoles(Set<RoleType> objects) {
        this.roles = objects;
    }

    public List<Report> getReportsMade() {
        return reportsMade;
    }
}

