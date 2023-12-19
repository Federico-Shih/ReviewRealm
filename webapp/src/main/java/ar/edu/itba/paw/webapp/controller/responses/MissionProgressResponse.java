package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.models.MissionProgress;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class MissionProgressResponse {
    private float progress;
    private URI mission;
    private String startDate;
    private int completedTimes;
    public static MissionProgressResponse fromEntity(UriInfo uriInfo, MissionProgress progress) {
        MissionProgressResponse missionProgress = new MissionProgressResponse();
        missionProgress.progress = progress.getProgress();
        missionProgress.mission = MissionResponse.getLinkFromEntity(uriInfo, progress.getMission());
        missionProgress.startDate = progress.getStartDate().toString();
        missionProgress.completedTimes = progress.getTimes();
        return missionProgress;
    }

    public float getProgress() {
        return progress;
    }

    public URI getMission() {
        return mission;
    }

    public String getStartDate() {
        return startDate;
    }

    public int getCompletedTimes() {
        return completedTimes;
    }
}
