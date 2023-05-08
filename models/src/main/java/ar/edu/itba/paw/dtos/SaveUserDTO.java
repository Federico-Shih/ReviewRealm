package ar.edu.itba.paw.dtos;

public class SaveUserDTO {
    private final String username;
    private final String email;
    private final String password;
    private final Boolean enabled;

    public SaveUserDTO(String username, String email, String password, Boolean enabled) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Boolean isEnabled() {
        return enabled;
    }
}
