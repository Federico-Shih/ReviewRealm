package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Category;
import ar.edu.itba.paw.persistenceinterfaces.CategoryDao;
import ar.edu.itba.paw.servicesinterfaces.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;
    @Autowired
    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public Category createCategory(String name) {
        return categoryDao.create(name);
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoryDao.getById(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDao.getAll();
    }
}
