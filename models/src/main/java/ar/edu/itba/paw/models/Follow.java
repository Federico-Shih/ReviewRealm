package ar.edu.itba.paw.models;

public class Follow {
    private final long userId;
    private final long following;


    public Follow(long userId, long following) {
        this.userId = userId;
        this.following = following;
    }

    public long getUserId() {
        return userId;
    }

    public long getFollowing() {
        return following;
    }
}
