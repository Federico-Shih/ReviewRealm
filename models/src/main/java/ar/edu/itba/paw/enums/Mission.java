package ar.edu.itba.paw.enums;

public enum Mission {
    REPUTATION_GOAL("missions.reputationgoal.title", "missions.reputationgoal.description", 20f, 5f, true, MissionFrequency.NONE, null),
    REVIEWS_GOAL("missions.reviewsgoal.title", "missions.reviewsgoal.description", 50f, 3f, true, MissionFrequency.NONE, null),
    FOLLOWERS_GOAL("missions.followersgoal.title", "missions.followersgoal.description", 100f, 3f, true, MissionFrequency.NONE, null),
    FOLLOWING_GOAL("missions.followinggoal.title", "missions.followinggoal.description", 80f, 5f, true, MissionFrequency.NONE, null),
    CHANGE_AVATAR("missions.avatarchange.title", "missions.avatarchange.description", 100f, 1f, true, MissionFrequency.MONTHLY, null),
    SETUP_PREFERENCES("missions.preferences.title", "missions.preferences.description", 100f, 1f, false, MissionFrequency.NONE, null),
    MANAGE_GAME_SUBMISSIONS("missions.accept.title", "missions.accept.description", 50f, 1f, true, MissionFrequency.NONE, RoleType.MODERATOR),
    RECOMMEND_GAMES("missions.recommend.title", "missions.recommend.description", 20f, 5f, true, MissionFrequency.WEEKLY, RoleType.USER),
    ACCEPTED_GAMES("missions.acceptedgame.title", "missions.acceptedgame.description", 20f, 1f, true, MissionFrequency.NONE, RoleType.USER);

    // cannot be repeatable and have a frequency
    private final boolean repeatable;
    private final MissionFrequency frequency;
    private final float xp;
    private final float target;
    private final String title;
    private final String description;
    private final RoleType roleType;

    Mission(String title, String description, float xp, float target, boolean repeatable, MissionFrequency frequency, RoleType roleType) {
        this.repeatable = repeatable;
        this.frequency = frequency;
        this.xp = xp;
        this.target = target;
        this.title = title;
        this.description = description;
        this.roleType = roleType;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public MissionFrequency getFrequency() {
        return frequency;
    }

    public float getXp() {
        return xp;
    }

    public float getTarget() {
        return target;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public enum MissionFrequency {
        NONE("missions.none"), DAILY("missions.daily"), MONTHLY("missions.monthly"), YEARLY("missions.yearly"), WEEKLY("missions.weekly");

        private final String name;

        MissionFrequency(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
