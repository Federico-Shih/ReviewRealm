package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.CategoryFilter;
import ar.edu.itba.paw.models.Category;
import ar.edu.itba.paw.models.Game;

import java.time.LocalDate;
import java.util.List;

public interface GameDao {

    Game create(String name, String developer, String publisher, String imageUrl, List<Category> categories, LocalDate publishDate);

    Game getById(Integer id);

    List<Game> getAll();
}
