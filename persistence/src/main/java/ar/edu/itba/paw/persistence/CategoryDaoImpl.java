package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Category;
import ar.edu.itba.paw.persistenceinterfaces.CategoryDao;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
@Repository
public class CategoryDaoImpl implements CategoryDao {

    @Override
    public Category create(String name) {
        return new Category(1,"FPS");
    }

    @Override
    public Category getById(Integer id) {
        return new Category(1,"FPS");
    }

    @Override
    public List<Category> getAll() {
        return Arrays.asList(getById(0), getById(0));
    }
}
