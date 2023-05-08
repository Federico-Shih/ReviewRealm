package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.forms.ChangePasswordForm;
import ar.edu.itba.paw.forms.RegisterForm;
import ar.edu.itba.paw.forms.ResendEmailForm;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService us;
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
        if(!form.getPassword().equals(form.getRepeatPassword())) {
            errors.rejectValue("repeatPassword", "error.passwords.dont.match");
        }
        if(errors.hasErrors()) {
            return registerForm(form);
        }
        try {
            us.createUser(form.getUsername(), form.getEmail(), form.getPassword());
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
            @ModelAttribute("resendEmailForm") ResendEmailForm form,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "registered", required = false) boolean registered
            ) {
        if (email != null && !email.equals("")) {
            form.setEmail(email);
        }
        ModelAndView mav = new ModelAndView("user/recovery");
        if (registered) mav.addObject("emailSuccess", true);
        return mav;
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public ModelAndView validateToken(@RequestParam("validationCode") String code, @ModelAttribute("resendEmailForm") ResendEmailForm form) {
        boolean validated;
        try {
            validated = us.validateToken(code);
        } catch (TokenExpiredException e) {
            LOGGER.error("Token expired " + code);
            us.refreshToken(code);
            return new ModelAndView("user/recovery").addObject("expiredToken", true);
        }
        if (!validated) {
            return new ModelAndView("user/recovery").addObject("unknownToken", true);
        }
        return new ModelAndView("user/validated");
    }

    @RequestMapping(value = "/validate/{token}", method = RequestMethod.GET)
    public ModelAndView validateByPath(@PathVariable("token") String token, @ModelAttribute("resendEmailForm") ResendEmailForm form) {
        return validateToken(token, form);
    }

    @RequestMapping(value = "/resend-email", method = RequestMethod.POST)
    public ModelAndView resendEmail(@Valid @ModelAttribute("resendEmailForm") ResendEmailForm form, final BindingResult errors) {
        if (errors.hasErrors()) {
            return validateForm(form, form.getEmail(), false);
        }
        try {
            us.resendToken(form.getEmail());
        } catch (UserAlreadyEnabled e) {
            errors.rejectValue("email", "user.already.exists");
            return validateForm(form, form.getEmail(), false);
        } catch (UserNotFoundException e) {
            errors.rejectValue("email", "user.not.exists");
            return validateForm(form, form.getEmail(), false);
        }
        return validateForm(form, form.getEmail(), true);
    }

    @RequestMapping(value = "/changepassword", method = RequestMethod.GET)
    public ModelAndView changePasswordForm(@RequestParam(value = "token", required = false) String token,
                                           @ModelAttribute("passwordForm") ChangePasswordForm passwordForm,
                                           @ModelAttribute("emailForm") ResendEmailForm emailForm) {
        ModelAndView mav = new ModelAndView("user/change-password");
        mav.addObject("token", token);
        return mav;
    }

    @RequestMapping(value = "/changepassword", method = RequestMethod.POST)
    public ModelAndView sendChangePasswordRequest(@ModelAttribute("passwordForm") ChangePasswordForm passwordForm,
                                                  @Valid @ModelAttribute("emailForm") ResendEmailForm emailForm,
                                                  final BindingResult errors) {
        if (errors.hasErrors()) {
            return changePasswordForm(null, new ChangePasswordForm(), emailForm);
        }
        try {
            us.sendPasswordResetToken(emailForm.getEmail());
        } catch (UserNotFoundException e) {
            errors.rejectValue("email", "user.not.exists");
            return changePasswordForm(null, new ChangePasswordForm(), emailForm);
        }
        return changePasswordForm(null, new ChangePasswordForm(), emailForm).addObject("emailSent", true);
    }

    @RequestMapping(value = "/changepassword/{token}", method = RequestMethod.POST)
    public ModelAndView changePassword(@PathVariable("token") String token,
                                       @ModelAttribute("emailForm") ResendEmailForm emailForm,
                                       @Valid @ModelAttribute("passwordForm") ChangePasswordForm passwordForm,
                                       final BindingResult errors) {
        if (errors.hasErrors()) {
            return changePasswordForm(token, passwordForm, emailForm);
        }
        if (!passwordForm.passwordsMatch()) {
            errors.rejectValue("repeatPassword", "error.passwords.dont.match");
            return changePasswordForm(token, passwordForm, emailForm);
        }
        try {
            us.resetPassword(token, passwordForm.getPassword());
        } catch (TokenExpiredException e) {
            LOGGER.error("Token expired " + token);
            errors.rejectValue("repeatPassword", "error.token.expired");
            return changePasswordForm(token, passwordForm, emailForm);
        } catch (TokenNotFoundException e) {
            errors.rejectValue("repeatPassword", "error.token.not.found");
            return changePasswordForm(token, passwordForm, emailForm);
        }
        return new ModelAndView("user/password-changed");
    }

    /* Para reconocer el usuario actual en cualquier página se puede usar:
    *
    * final PawAuthUserDetails userDetails = (PawAuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    * final User user = us.getUserByEmail(userDetails.getUsername()).orElseThrow(UserNotFoundException::new);
    * mav.addObject("user", user); o como querramos usarlo
    *
    * */

}
