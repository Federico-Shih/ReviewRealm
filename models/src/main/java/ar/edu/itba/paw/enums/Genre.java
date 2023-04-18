package ar.edu.itba.paw.enums;

import java.util.Optional;

public enum Genre {
    ACTION(1, "genre.action"),
    ADVENTURE(2, "genre.adventure"),
    CASUAL(3, "genre.casual"),
    RPG(4,"genre.rpg"),
    SIM(5, "genre.sim"),
    STRATEGY(6,"genre.strategy"),
    ROGUELIKE(7, "genre.roguelike"),
    SHOOTER(8, "genre.shooter"),
    VISUALNOVEL(9, "genre.visualnovel"),
    TERROR(10, "genre.terror"),
    MUSIC(11, "genre.music"),
    PUZZLES(12, "genre.puzzles"),
    FIGHTER(13, "genre.fighter"),
    RACING(14, "genre.racing"),
    PLATFORMER(15, "genre.platformer"),
    CITYBUILDER(16, "genre.citybuilder"),
    TD(17, "genre.towerdefense"),
    SANDBOX(18, "genre.sandbox"),
    SPORTS(19, "genre.sports"),
    MOBA(20, "genre.moba"),
    MMO(21, "genre.mmo"),
    ARCADE(22, "genre.arcade"),
    SURVIVAL(23, "genre.survival"),
    VR(24, "genre.vr"),
    ADMIN(25, "genre.administration"),
    EDUCATIONAL(26, "genre.educational"),
    TTRPG(27, "genre.ttrpg"),
    STEALTH(28, "genre.stealth"),
    CLICKER(29, "genre.clicker"),
    JRPG(30, "genre.jrpg"),
    SOULS(31, "genre.souls"),
    IDLE(32, "genre.idle");

    private final String name;
    private final int id;
    Genre(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return this.id;
    }

    public static Optional<Genre> getById(int id) {
        for (Genre genre : values()) {
            if (genre.id == id) {
                return Optional.of(genre);
            }
        }
        return Optional.empty();
    }
}

/*


 */