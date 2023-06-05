package ar.edu.itba.paw.models.keys;

import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import java.io.Serializable;

public class MissionProgressId implements Serializable {
    private User user;

    private Mission mission;

    public MissionProgressId(User user, Mission mission) {
        this.user = user;
        this.mission = mission;
    }

    protected MissionProgressId() {
        // For hibernate
    }

    public User getUser() {
        return user;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    @Override
    public int hashCode() {
        return user.hashCode() + mission.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof MissionProgressId)) return false;
        MissionProgressId other = (MissionProgressId) obj;
        return other.user.equals(this.user) && other.mission.equals(this.mission);
    }
}
