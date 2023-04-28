package ar.edu.itba.paw.models;

public class FollowerFollowingCount {
    private final long followerCount;
    private final long followingCount;

    public FollowerFollowingCount(long followerCount, long followingCount) {
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }

    public long getFollowerCount() {
        return followerCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }
}
