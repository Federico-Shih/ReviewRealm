package ar.edu.itba.paw.models;

public class Genre {
    private final Long id;

    private final String name;

    public Genre(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
}
