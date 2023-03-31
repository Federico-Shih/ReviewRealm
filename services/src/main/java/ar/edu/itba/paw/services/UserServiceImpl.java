package ar.edu.itba.paw.services;

import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User createUser(String email, String password) {
        return userDao.create(email, password); // No deberia estar aca, pero es para probar
    }
}
