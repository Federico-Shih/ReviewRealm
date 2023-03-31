package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.models.Category;

import java.util.List;

public interface CategoryDao {
    Category create(String name);
    Category getById(Integer id);
    List<Category> getAll();
}
