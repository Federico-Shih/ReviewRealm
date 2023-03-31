package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.models.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(String name);
    Category getCategoryById(Integer id);
    List<Category> getAllCategories();

}
