package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.ExpirationToken;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import ar.edu.itba.paw.persistenceinterfaces.ValidationTokenDao;
import ar.edu.itba.paw.servicesinterfaces.MailingService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

import static ar.edu.itba.paw.services.utils.UserTestModels.getUser1;
import static ar.edu.itba.paw.services.utils.UserTestModels.getUser2;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final User USER = getUser1();
    private static final String USERNAME = USER.getUsername();
    private static final String EMAIL = USER.getEmail();
    private static final String PASSWORD = USER.getPassword();

    private static final String OTHER_PASSWORD = "hola";
    private static final String TOKEN = "holsadfsadfasdfa";
    private static final String OTHER_TOKEN = "sadffsadfasdfa";

    private static final LocalDateTime EXPIRATION_DATE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime EXPIRED_DATE = LocalDateTime.now().minusDays(1);

    private static final Long ID = USER.getId();

    @Mock
    private PasswordEncoder passwordEncoder;

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
        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        Mockito.when(userDao.create(eq(USERNAME), eq(EMAIL), eq("")))
                .thenReturn(new User(ID, USERNAME, EMAIL, PASSWORD));
        Mockito.when(userDao.findById(eq(ID))).thenReturn(Optional.of(USER));
        Mockito.when(userDao.update(eq(ID), any())).thenReturn(Optional.of(USER));
        //2.execute
        User newUser = us.createUser(USERNAME, EMAIL, PASSWORD, Locale.ENGLISH);

        //3.assert
        Assert.assertNotNull(newUser);
        Assert.assertEquals(ID,newUser.getId());
        Assert.assertEquals(USERNAME,newUser.getUsername());
        Assert.assertEquals(EMAIL,newUser.getEmail());
    }

    @Test(expected = UsernameAlreadyExistsException.class)
    public void testCreateAlreadyExists() throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        //1.prepare
        Mockito.when(userDao.getByUsername(USERNAME))
                .thenReturn(Optional.of(new User(ID,USERNAME,EMAIL,PASSWORD)));
        Mockito.when(userDao.getByEmail(EMAIL))
                .thenReturn(Optional.empty());
        //2.execute
        User newUser = us.createUser(USERNAME, EMAIL,PASSWORD, Locale.ENGLISH);
    }

    @Test(expected = EmailAlreadyExistsException.class)
    public void testCreateAlreadyExistsEmail() throws UsernameAlreadyExistsException, EmailAlreadyExistsException {

        Mockito.when(userDao.getByEmail(EMAIL))
                .thenReturn(Optional.of(new User(ID,USERNAME,EMAIL,PASSWORD)));
        //2.execute
        User newUser = us.createUser(USERNAME, EMAIL,PASSWORD, Locale.ENGLISH);
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
        Mockito.when(passwordEncoder.encode(eq(OTHER_PASSWORD))).thenReturn(OTHER_PASSWORD);

        //2.execute
        us.changeUserPassword(EMAIL, OTHER_PASSWORD);
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

    @Test(expected = UserNotFoundException.class)
    public void followUserDoesNotExist() {
        Mockito.when(userDao.exists(getUser2().getId())).thenReturn(false);
        us.followUserById(ID, getUser2().getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void followUserWhoDoesNotExist() {
        Mockito.when(userDao.exists(ID)).thenReturn(true);
        Mockito.when(userDao.exists(getUser2().getId())).thenReturn(false);
        us.followUserById(ID, getUser2().getId());
    }

    @Test
    public void followUserTest() {
        Mockito.when(userDao.exists(ID)).thenReturn(true);
        Mockito.when(userDao.exists(getUser2().getId())).thenReturn(true);
        Mockito.when(userDao.createFollow(ID, getUser2().getId())).thenReturn(Optional.of(USER));

        User ans = us.followUserById(ID, getUser2().getId());

        Assert.assertEquals(USER, ans);
    }

    @Test(expected = TokenExpiredException.class)
    public void validateTokenExpired() throws TokenExpiredException {
        Mockito.when(tokenDao.create(anyLong(), any(), any())).thenReturn(new ExpirationToken(TOKEN, USER, PASSWORD, EXPIRED_DATE));
        Mockito.when(tokenDao.getByToken(any())).thenReturn(Optional.of(new ExpirationToken(TOKEN, USER, PASSWORD, EXPIRED_DATE)));

        us.validateToken(PASSWORD);
    }

    @Test
    public void tokenNotExistent() throws TokenExpiredException {
        Mockito.when(tokenDao.getByToken(TOKEN)).thenReturn(Optional.empty());
        Assert.assertFalse(us.validateToken(TOKEN).isPresent());
    }

    @Test
    public void validateExistentToken() throws TokenExpiredException {
        Mockito.when(tokenDao.getByToken(TOKEN)).thenReturn(Optional.of(new ExpirationToken(TOKEN, USER, PASSWORD, LocalDateTime.MAX)));
        Mockito.when(userDao.findById(ID)).thenReturn(Optional.of(USER));

        Assert.assertTrue(us.validateToken(TOKEN).isPresent());
    }

    @Test(expected = UserNotFoundException.class)
    public void resendTokenEmailNotExistent() throws UserAlreadyEnabled {
        Mockito.when(userDao.getByEmail(EMAIL)).thenReturn(Optional.empty());
        us.resendToken(EMAIL);
    }

    @Test(expected = UserAlreadyEnabled.class)
    public void resendTokenUserEnabled() throws UserAlreadyEnabled {
        User user = USER;
        user.setEnabled(true);
        Mockito.when(userDao.getByEmail(EMAIL)).thenReturn(Optional.of(user));
        us.resendToken(EMAIL);
    }

    @Test
    public void resendTokenSuccessTest() throws UserAlreadyEnabled {
        ExpirationToken token = new ExpirationToken(TOKEN, USER, PASSWORD, EXPIRATION_DATE);
        ExpirationToken newToken = new ExpirationToken(OTHER_TOKEN, USER, PASSWORD, EXPIRATION_DATE);
        Mockito.when(userDao.getByEmail(eq(EMAIL))).thenReturn(Optional.of(USER));
        Mockito.when(tokenDao.findLastPasswordToken(ID)).thenReturn(Optional.of(token));
        Mockito.when(tokenDao.create(anyLong(), anyString(), any())).thenReturn(newToken);
        us.resendToken(EMAIL);
    }


}
