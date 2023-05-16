package ar.edu.itba.paw.enums;

public enum RoleType {
    MODERATOR("MODERATOR");

    private final String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }

    public String getRole() {
        return roleName;
    }
}
