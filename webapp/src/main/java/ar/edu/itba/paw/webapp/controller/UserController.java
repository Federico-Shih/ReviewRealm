package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import ar.edu.itba.paw.webapp.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.webapp.forms.ChangePasswordForm;
import ar.edu.itba.paw.webapp.forms.RegisterForm;
import ar.edu.itba.paw.webapp.forms.ResendEmailForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService us;

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    public UserController(UserService us) {
        this.us = us;
    }

    @RequestMapping("/login")
    public ModelAndView loginForm(
            @RequestParam(value = "error", defaultValue = "") final String error
    ) {
        ModelAndView mav = new ModelAndView("user/login");
        mav.addObject("error", error);
        return mav;
    }

    @RequestMapping("/register")
    public ModelAndView registerForm(
            @ModelAttribute("registerForm") final RegisterForm form
    ) {
        return new ModelAndView("user/register");
    }

    @RequestMapping(value="/register",method = RequestMethod.POST)
    public ModelAndView register(
            @Valid @ModelAttribute("registerForm") final RegisterForm form,
            final BindingResult errors
    ) {
        if(errors.hasErrors()) {
            System.out.println(errors.getAllErrors());
            return registerForm(form);
        }
        try {
            us.createUser(form.getUsername(), form.getEmail(), form.getPassword(), httpServletRequest.getLocale());
        } catch (EmailAlreadyExistsException e) {
            errors.rejectValue("email", "error.email.already.exists");
            return registerForm(form);
        } catch (UsernameAlreadyExistsException e) {
            errors.rejectValue("username", "error.username.already.exists");
            return registerForm(form);
        }

        return new ModelAndView("redirect:/recover?registered=true");
    }

    @RequestMapping(value = "/recover", method = RequestMethod.GET)
    public ModelAndView validateForm(
            @RequestParam(value = "registered", required = false) boolean registered,
            @RequestParam(value = "resent", required = false) boolean resent
    ) {

        ModelAndView mav = new ModelAndView("user/recovery");
        mav.addObject("registered", registered);
        mav.addObject("resent", resent);
        return mav;
    }

    @RequestMapping(value = "/resend-email", method = RequestMethod.GET)
    public ModelAndView resendEmailForm(
            @ModelAttribute("resendEmailForm") ResendEmailForm form,
            @RequestParam(value = "email", required = false) String email
    ) {
        if (email != null && !email.equals("")) {
            form.setEmail(email);
        }
        return new ModelAndView("user/resend-email");
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public ModelAndView validateToken(@RequestParam("validationCode") String code) {
        Optional<User> user;
        try {
            user = us.validateToken(code);
        } catch (TokenExpiredException e) {
            LOGGER.error("Token expired " + code);
            us.refreshToken(code);
            return new ModelAndView("user/recovery").addObject("expiredToken", true);
        }
        if (!user.isPresent()) {
            return new ModelAndView("user/recovery").addObject("unknownToken", true);
        }
        try {
            authWithoutPassword(user.get());
        } catch (UserNotFoundException e) {
            throw new ObjectNotFoundException();
        }
        return new ModelAndView("user/validated");
    }

    private void authWithoutPassword(User user) {
         Set<RoleType> roles = us.getUserRoles(user.getId());
        List<GrantedAuthority> authorities = roles.stream().map(roleType -> new SimpleGrantedAuthority("ROLE_" + roleType.getRole())).collect(Collectors.toList());
         Authentication result = new UsernamePasswordAuthenticationToken(new PawAuthUserDetails(user.getEmail(), user.getPassword(), authorities), null, authorities);
         SecurityContextHolder.getContext().setAuthentication(result);
    }

    @RequestMapping(value = "/validate/{token}", method = RequestMethod.GET)
    public ModelAndView validateByPath(@PathVariable("token") String token) {
        return validateToken(token);
    }

    @RequestMapping(value = "/resend-email", method = RequestMethod.POST)
    public ModelAndView resendEmail(@Valid @ModelAttribute("resendEmailForm") ResendEmailForm form, final BindingResult errors) {
        if (errors.hasErrors()) {
            return resendEmailForm(form, form.getEmail());
        }
        try {
            us.resendToken(form.getEmail());
        } catch (UserAlreadyEnabled e) {
            errors.rejectValue("email", "user.already.exists");
            return resendEmailForm(form, form.getEmail());
        } catch (UserNotFoundException e) {
            errors.rejectValue("email", "user.not.exists");
            return resendEmailForm(form, form.getEmail());
        }
        return new ModelAndView("redirect:/recover?resent=true");
    }

    @RequestMapping(value = "/changepassword", method = RequestMethod.GET)
    public ModelAndView changePasswordForm(@RequestParam(value = "token", required = true) String token,
                                           @ModelAttribute("passwordForm") ChangePasswordForm passwordForm) {
        ModelAndView mav = new ModelAndView("user/change-password");
        mav.addObject("token", token);
        return mav;
    }

    @RequestMapping(value = "/recover-password", method = RequestMethod.GET)
    public ModelAndView recoverPasswordForm(@ModelAttribute("emailForm") ResendEmailForm emailForm) {
        return new ModelAndView("user/resend-recover");
    }

    @RequestMapping(value = "/resend-password", method = RequestMethod.POST)
    public ModelAndView sendChangePasswordRequest(@Valid @ModelAttribute("emailForm") ResendEmailForm emailForm,
                                                  final BindingResult errors) {
        if (errors.hasErrors()) {
            return recoverPasswordForm(emailForm);
        }
        try {
            us.sendPasswordResetToken(emailForm.getEmail());
        } catch (UserNotFoundException e) {
            errors.rejectValue("email", "user.not.exists");
            return recoverPasswordForm(emailForm);
        }
        return new ModelAndView("user/resend-recover").addObject("resent", true);
    }

    @RequestMapping(value = "/changepassword/{token}", method = RequestMethod.POST)
    public ModelAndView changePassword(@PathVariable("token") String token,
                                       @Valid @ModelAttribute("passwordForm") ChangePasswordForm passwordForm,
                                       final BindingResult errors) {
        if (errors.hasErrors()) {
            return changePasswordForm(token, passwordForm);
        }
        if (!passwordForm.passwordsMatch()) {
            errors.rejectValue("repeatPassword", "error.passwords.dont.match");
            return changePasswordForm(token, passwordForm);
        }
        try {
            us.resetPassword(token, passwordForm.getPassword());
        } catch (TokenExpiredException e) {
            LOGGER.error("Token expired " + token);
            errors.rejectValue("repeatPassword", "error.token.expired");
            return changePasswordForm(token, passwordForm);
        } catch (TokenNotFoundException e) {
            errors.rejectValue("repeatPassword", "error.token.not.found");
            return changePasswordForm(token, passwordForm);
        }
        return new ModelAndView("user/password-changed");
    }

}
