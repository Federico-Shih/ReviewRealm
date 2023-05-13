package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.SaveUserDTO;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import ar.edu.itba.paw.persistenceinterfaces.ValidationTokenDao;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.MailingService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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
    @Mock
    private ValidationTokenDao tokenDao;

    @Mock
    private MailingService mailingService;

    @InjectMocks
    private UserServiceImpl us;

    @Test
    public void testCreate() throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        //1.prepare
        // TODO: Preguntar si está bien lo de any(), el problema es que el password pasa por el passwordencoder,
        // siento que al test no le debería importar eso, pero no sé cómo considerarlo
        Mockito.when(userDao.create(eq(USERNAME), eq(EMAIL), ArgumentMatchers.any()))
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

    @Test(expected = UserNotFoundException.class)
    public void changePasswordWithoutExistentUser() {
        //1.prepare
        Mockito.when(userDao.getByEmail(eq(EMAIL))).thenReturn(Optional.empty());
        //2.execute
        us.changeUserPassword(EMAIL, PASSWORD);
    }

    @Test
    public void changePassword() {
        //1.prepare
        Mockito.when(userDao.getByEmail(eq(EMAIL))).thenReturn(Optional.of(new User(ID,USERNAME,EMAIL,PASSWORD)));
        Mockito.when(passwordEncoder.encode(eq("hola"))).thenReturn("hola");

        //2.execute
        us.changeUserPassword(EMAIL, "hola");
        Mockito.verify(userDao, Mockito.times(1)).update(eq(ID), Mockito.any());
    }

    @Test
    public void testFindById(){
        //1.prepare
        Mockito.when(userDao.findById(eq(ID))).thenReturn(Optional.of(new User(ID, USERNAME,EMAIL,PASSWORD)));
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
        List<Genre> list = new ArrayList<>();
        list.add(Genre.ROGUELIKE);
        list.add(Genre.ADVENTURE);
        when(userDao.getPreferencesById(ID)).thenReturn(list);

        List<Genre> secondList = new ArrayList<>();
        secondList.add(Genre.ROGUELIKE);
        secondList.add(Genre.ADVENTURE);

        Assert.assertEquals(secondList, us.getPreferences(ID));
    }

    @Test(expected = UserNotFoundException.class)
    public void followUserDoesNotExist() {
        Mockito.when(userDao.exists(1L)).thenReturn(false);
        Mockito.when(userDao.exists(2L)).thenReturn(false);
        us.followUserById(1L, 2L);
    }

    @Test
    public void followUserTest() {
        Mockito.when(userDao.exists(1L)).thenReturn(true);
        Mockito.when(userDao.exists(2L)).thenReturn(true);
        Mockito.when(userDao.createFollow(1L, 2L)).thenReturn(Optional.of(new Follow(1L, 2L)));
        Assert.assertTrue(us.followUserById(1L, 2L).isPresent());
    }

    @Test(expected = TokenExpiredException.class)
    public void validateTokenExpired() throws TokenExpiredException {
        Mockito.when(tokenDao.getByToken("aaa")).thenReturn(Optional.of(new ExpirationToken(1L, "aaa", 1L, "aaa", LocalDateTime.MIN)));
        us.validateToken("aaa");
    }

    @Test
    public void tokenNotExistent() throws TokenExpiredException {
        Mockito.when(tokenDao.getByToken("aaa")).thenReturn(Optional.empty());
        Assert.assertFalse(us.validateToken("aaa").isPresent());
    }

    @Test
    public void validateExistentToken() throws TokenExpiredException {
        Mockito.when(tokenDao.getByToken("aaa")).thenReturn(Optional.of(new ExpirationToken(1L, "aaa", 1L, "aaa", LocalDateTime.MAX)));
        Mockito.when(userDao.update(1L, new SaveUserDTO(USERNAME, EMAIL, PASSWORD, false, 10L, 1L))).thenReturn(1);
        Mockito.when(userDao.findById(1L)).thenReturn(Optional.of(new User(1L, USERNAME, EMAIL, PASSWORD)));
        Assert.assertTrue(us.validateToken("aaa").isPresent());
        Mockito.verify(userDao, Mockito.times(1)).update(eq(1L), Mockito.any());
        Mockito.verify(tokenDao, Mockito.times(1)).delete(1L);
    }

    @Test(expected = UserNotFoundException.class)
    public void resendTokenEmailNotExistent() throws UserAlreadyEnabled {
        Mockito.when(userDao.getByEmail(EMAIL)).thenReturn(Optional.empty());
        us.resendToken(EMAIL);
    }

    @Test(expected = UserAlreadyEnabled.class)
    public void resendTokenUserEnabled() throws UserAlreadyEnabled {
        Mockito.when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(new User(1L, USERNAME, EMAIL, PASSWORD, new ArrayList<>(), true, 10L, new HashSet<>(), new HashSet<>(), 0L)));
        us.resendToken(EMAIL);
    }

    @Test
    public void resendTokenSuccessTest() throws UserAlreadyEnabled {
        ExpirationToken token = new ExpirationToken(1L, "aaa", 1L, "aaa", LocalDateTime.MAX);
        ExpirationToken newToken = new ExpirationToken(2L, "aaas", 1L, "aaa", LocalDateTime.MAX);
        User user = new User(1L, USERNAME, EMAIL, PASSWORD, new ArrayList<>(), false, 10L, new HashSet<>(), new HashSet<>(), 0L);
        Mockito.when(userDao.getByEmail(eq(EMAIL))).thenReturn(Optional.of(user));
        Mockito.when(tokenDao.findLastPasswordToken(1L)).thenReturn(Optional.of(token));
        Mockito.when(tokenDao.create(anyString(), anyLong(), anyString(), any())).thenReturn(newToken);
        us.resendToken(EMAIL);
        Mockito.verify(mailingService, Mockito.times(1)).sendValidationTokenEmail(eq(newToken), eq(user));
        Mockito.verify(tokenDao, Mockito.times(1)).delete(eq(token.getId()));
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
