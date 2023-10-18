package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.models.Pair;

import javax.ws.rs.core.UriInfo;

public class MissionResponse extends BaseResponse {
    private final static String BASE_PATH = "/missions";
    private final String id;
    private final String title;
    private final String description;
    private final float xp;
    private final float target;
    private final boolean repeatable;
    private final MissionFrequencyResponse frequency;

    private MissionResponse(String id, String title, String description, float xp, float target, boolean repeatable, MissionFrequencyResponse frequency) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.xp = xp;
        this.target = target;
        this.repeatable = repeatable;
        this.frequency = frequency;
    }

    public static MissionResponse fromEntity(UriInfo uriInfo, Mission mission, Pair<String, String> localizedMission, String localizedFreq) {
        MissionResponse response = new MissionResponse(
                mission.name(),
                localizedMission.getKey(),
                localizedMission.getValue(),
                mission.getXp(),
                mission.getTarget(),
                mission.isRepeatable(),
                MissionFrequencyResponse.fromEntity(mission.getFrequency(), localizedFreq)
            );
        response.link("self", uriInfo.getBaseUriBuilder().path(BASE_PATH).path(mission.name()).build());
        return response;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public float getXp() {
        return xp;
    }

    public float getTarget() {
        return target;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public MissionFrequencyResponse getFrequency() {
        return frequency;
    }

    private static class MissionFrequencyResponse {
        private final String localized;
        private final String name;

        private MissionFrequencyResponse(String locaized, String roleType) {
            this.localized = locaized;
            this.name = roleType;
        }

        public static MissionFrequencyResponse fromEntity(Mission.MissionFrequency frequency, String localized) {
            if (frequency.equals(Mission.MissionFrequency.NONE)) return null;
            return new MissionFrequencyResponse(localized, frequency.name());
        }

        public String getLocalized() {
            return localized;
        }

        public String getName() {
            return name;
        }
    }
}
