package ar.edu.itba.paw.models;

import ar.edu.itba.paw.converters.LocalDateTimeConverter;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.enums.GamelengthUnit;
import ar.edu.itba.paw.enums.Platform;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reviews_id_seq")
    @SequenceGenerator(sequenceName = "reviews_id_seq", name = "reviews_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "authorid", referencedColumnName = "id")
    private User author;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "createddate", nullable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime created;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "gameid", referencedColumnName = "id", nullable = false)
    private Game reviewedGame;

    @Column(name = "difficulty", nullable = true)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(name = "gamelength", nullable = true)
    private Double gameLength;

    @Column(name = "platform", nullable = true)
    @Enumerated(EnumType.STRING)
    private Platform platform;

    @Column(name = "completed", nullable = true)
    private Boolean completed;

    @Column(name = "replayability", nullable = true)
    private Boolean replayability;

    @Column(name = "likes", nullable = false)
    private Long likes = 0L;

    @Column(name = "dislikes", nullable = false)
    private Long dislikes = 0L;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "review", cascade = CascadeType.ALL)
    private Set<ReviewFeedback> feedbacks = new HashSet<>();

    @Formula("likes - dislikes")
    private Integer popularity = 0;

    @Formula("likes + dislikes")
    private Integer controversial = 0;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reportedReview")
    private List<Report> reports;

    @Transient
    private FeedbackType feedbackType = null;

    // Used to set existing reports in null
    @PreRemove
    private void cleanReports() {
        for (Report report : reports) {
            report.setReportedReview(null);
        }
    }

    protected Review() {
        // For hibernate
    }

    public Review(String title,
                  String content,
                  Integer rating,
                  Game reviewedGame,
                  User author,
                  Difficulty difficulty,
                  Double gameLength,
                  Platform platform,
                  Boolean completed,
                  Boolean replayability) {
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.reviewedGame = reviewedGame;
        this.author = author;
        this.created = LocalDateTime.now();
        this.difficulty = difficulty;
        this.gameLength = gameLength;
        this.platform = platform;
        this.completed = completed;
        this.replayability = replayability;
    }

    public Review(long id, String title, String content, LocalDateTime createddate, int rating, Difficulty difficulty, Double aDouble, Platform platform, boolean completed, boolean replayability, long likes, long dislikes, boolean deleted) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.created = createddate;
        this.rating = rating;
        this.difficulty = difficulty;
        this.gameLength = aDouble;
        this.platform = platform;
        this.completed = completed;
        this.replayability = replayability;
        this.likes = likes;
        this.dislikes = dislikes;
        this.deleted = deleted;
    }

    // For testing
    public Review(long l, User user, String s, String s1, LocalDateTime now, int i, Game game, Difficulty difficulty, double v, Platform platform, boolean b, boolean b1, Object o, long l1) {
        this.id = l;
        this.author = user;
        this.title = s;
        this.content = s1;
        this.created = now;
        this.rating = i;
        this.reviewedGame = game;
        this.difficulty = difficulty;
        this.gameLength = v;
        this.platform = platform;
        this.completed = b;
        this.replayability = b1;
        this.likes = l;
        this.dislikes = l1;
    }

    public Review(long l, String s, String s1, int i, Game game, User user, Difficulty difficulty, double v, Platform platform, boolean b, boolean b1) {
        this(s, s1, i, game, user, difficulty, v, platform, b, b1);
        this.id = l;
    }

    public Long getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public String getCreatedFormatted() {
        return created.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
    }

    public Integer getRating() {
        return rating;
    }

    public Game getReviewedGame() {
        return reviewedGame;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Double getGameLength() {
        return gameLength;
    }

    public Pair<GamelengthUnit, Double> getGameLengthInUnits() {
        GamelengthUnit unit;
        double gametime;
        if(gameLength == null) {
            return null;
        }
        if (gameLength > GamelengthUnit.HOURS.toSeconds(1.0)) {
            gametime = gameLength / GamelengthUnit.HOURS.toSeconds(1.0);
            unit = GamelengthUnit.HOURS;
        } else {
            gametime = gameLength / GamelengthUnit.MINUTES.toSeconds(1.0);
            unit = GamelengthUnit.MINUTES;
        }
        return Pair.of(unit, gametime);
    }

    public Platform getPlatform() {
        return platform;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public Boolean getReplayability() {
        return replayability;
    }

    public FeedbackType getFeedback() {
        return feedbackType;
    }

    public Long getLikes() {
        return likes;
    }

    public Long getDislikes() {
        return dislikes;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setFeedback(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public Long getLikeCounter() {
        return (long) (likes - dislikes);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setGameLength(Double gameLength) {
        this.gameLength = gameLength;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setReplayability(Boolean replayability) {
        this.replayability = replayability;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public Integer getControversial() {
        return controversial;
    }
}
