package ar.edu.itba.paw.webapp.controller.datacontainers;

public class RoleContainer {
    private final boolean moderator;

    public RoleContainer(boolean isModerator) {
        this.moderator = isModerator;
    }

    public boolean getModerator() {
        return moderator;
    }
}
