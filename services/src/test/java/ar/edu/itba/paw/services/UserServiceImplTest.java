package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.exceptions.UsernameAlreadyExistsException;
import ar.edu.itba.paw.models.Follow;
import ar.edu.itba.paw.models.FollowerFollowingCount;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import com.sun.org.apache.xpath.internal.Arg;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    private static final Long ID = 1L;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private GenreService genreService;
    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserServiceImpl us;

    @Test
    public void testCreate() throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        //1.prepare
        // TODO: Preguntar si está bien lo de any(), el problema es que el password pasa por el passwordencoder,
        // siento que al test no le debería importar eso, pero no sé cómo considerarlo
        Mockito.when(userDao.create(ArgumentMatchers.eq(USERNAME),ArgumentMatchers.eq(EMAIL), ArgumentMatchers.any()))
                .thenReturn(new User(ID, USERNAME, EMAIL, PASSWORD));
        //2.execute
        User newUser = us.createUser(USERNAME, EMAIL, PASSWORD);

        //3.assert
        Assert.assertNotNull(newUser);
        Assert.assertEquals(ID,newUser.getId());
        Assert.assertEquals(USERNAME,newUser.getUsername());
        Assert.assertEquals(EMAIL,newUser.getEmail());
    }

    @Test(expected = UsernameAlreadyExistsException.class)
    public void testCreateAlreadyExists() throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        //1.prepare
        // Esto lo siento raro, tengo que conocer la implementación para hacer lo de Mockito.when..., pero sino no
        // sé cómo hacerlo
        Mockito.when(userDao.getByUsername(USERNAME))
                .thenReturn(Optional.of(new User(ID,USERNAME,EMAIL,PASSWORD)));
        Mockito.when(userDao.getByEmail(EMAIL))
                .thenReturn(Optional.empty());
        //2.execute
        User newUser = us.createUser(USERNAME, EMAIL,PASSWORD);
    }

    @Test(expected = EmailAlreadyExistsException.class)
    public void testCreateAlreadyExistsEmail() throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        //1.prepare
        // Esto lo siento raro, tengo que conocer la implementación para hacer lo de Mockito.when..., pero sino no
        // sé cómo hacerlo
        Mockito.when(userDao.getByEmail(EMAIL))
                .thenReturn(Optional.of(new User(ID,USERNAME,EMAIL,PASSWORD)));
        //2.execute
        User newUser = us.createUser(USERNAME, EMAIL,PASSWORD);
    }

    @Test
    public void testFindById(){
        //1.prepare
        Mockito.when(userDao.findById(ArgumentMatchers.eq(ID))).thenReturn(Optional.of(new User(ID, USERNAME,EMAIL,PASSWORD)));
        //2.execute
        Optional<User> newUser = us.getUserById(ID);

        //3.assert
        Assert.assertTrue(newUser.isPresent());
        Assert.assertEquals(ID,newUser.get().getId());
        Assert.assertEquals(EMAIL,newUser.get().getEmail());
        Assert.assertEquals(PASSWORD,newUser.get().getPassword());

    }


    @Test
    public void testGetUserByEmail() {
        // se puede hacer esto porque hicimos un static import de Mockito.when
        when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(new User(ID,USERNAME,EMAIL,PASSWORD)));

        Optional<User> newUser = us.getUserByEmail(EMAIL);

        Assert.assertTrue(newUser.isPresent());
        Assert.assertEquals(EMAIL,newUser.get().getEmail());
        Assert.assertEquals(ID,newUser.get().getId());
        Assert.assertEquals(PASSWORD,newUser.get().getPassword());

    }

    @Test
    public void testGetUserByUsername() {
        // se puede hacer esto porque hicimos un static import de Mockito.when
        when(userDao.getByUsername(USERNAME)).thenReturn(Optional.of(new User(ID,USERNAME,EMAIL,PASSWORD)));

        Optional<User> newUser = us.getUserByUsername(USERNAME);

        Assert.assertTrue(newUser.isPresent());
        Assert.assertEquals(EMAIL,newUser.get().getEmail());
        Assert.assertEquals(ID,newUser.get().getId());
        Assert.assertEquals(PASSWORD,newUser.get().getPassword());

    }

    @Test
    public void testGetPreferences() {
        List<Integer> list = new ArrayList<>();
        Integer roguelikeId = Genre.ROGUELIKE.getId();
        Integer adventureId = Genre.ADVENTURE.getId();
        list.add(roguelikeId);
        list.add(adventureId);
        when(userDao.getPreferencesById(ID)).thenReturn(list);
        when(genreService.getGenreById(roguelikeId)).thenReturn(Optional.of(Genre.ROGUELIKE));
        when(genreService.getGenreById(adventureId)).thenReturn(Optional.of(Genre.ADVENTURE));

        List<Genre> secondList = new ArrayList<>();
        secondList.add(Genre.ROGUELIKE);
        secondList.add(Genre.ADVENTURE);

        Assert.assertEquals(secondList, us.getPreferences(ID));
    }

    /*
    List<User> getFollowers(Long id);
    List<User> getFollowing(Long id);
    FollowerFollowingCount getFollowerFollowingCount(Long id);

    Optional<Follow> followUserById(Long userId, Long otherId);

    boolean unfollowUserById(Long userId, Long otherId);

    boolean userFollowsId(Long userId, Long otherId);

    List<Role> getUserRoles(Long userId);
    */
}
