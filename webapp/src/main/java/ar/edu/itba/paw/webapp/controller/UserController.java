package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.exceptions.UsernameAlreadyExistsException;
import ar.edu.itba.paw.forms.RegisterForm;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

        return new ModelAndView("redirect:/login");
    }

    /* Para reconocer el usuario actual en cualquier página se puede usar:
    *
    * final PawAuthUserDetails userDetails = (PawAuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    * final User user = us.getUserByEmail(userDetails.getUsername()).orElseThrow(UserNotFoundException::new);
    * mav.addObject("user", user); o como querramos usarlo
    *
    * */

}
