package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;


@Repository
public class UserDaoImpl implements UserDao {

    @Override
    public User create(String email, String password) {
        return new User(0, email, password);
    }

    @Override
    public User getByEmail(String email) {
        return new User(123, email, "12345678");
    }

}
