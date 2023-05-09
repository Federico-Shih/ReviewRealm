package ar.edu.itba.paw.webapp.config;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;


@EnableWebSecurity
@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers( "/static/**", "/css/**", "/js/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .invalidSessionUrl("/login")
            .and().authorizeRequests()

                //.antMatchers("/admin/**").hasRole("ADMIN") TODO: lo que requiera un rol especial
                //.antMatchers("/review/edit/").access("@AccessHelper.canEdit") cuando se requiera un acceso especial segun el usuario (Spring Expression Language)

                /* ACÁ PONEMOS TODOS LOS PATHS QUE REQUIERAN INICIAR SESIÓN Y TENER UN ROL */
                .antMatchers("/review/delete/{\\d+}", "/game/submit").hasRole("MODERATOR")

                /* ACÁ PONEMOS TODOS LOS PATHS QUE REQUIERAN INICIAR SESIÓN, PERO NO ROLES */
                .antMatchers("/review/submit",
                        "/review/submit/{\\d+}",
                        "/profile/following",
                        "/profile/followers",
                        "/profile/follow/{\\d+}",
                        "/profile/unfollow/{\\d+}",
                        "/for-you",
                        "/review/feedback/{id:\\d+}",
                        "/profile/settings/**"
                ).authenticated()

                /* ACÁ PONEMOS TODOS LOS PATHS QUE REQUIERAN NO HABER INICIADO SESIÓN */
                .antMatchers("/login", "/register", "/recover").anonymous()

                /* POR DEFAULT NO ES NECESARIO INICIAR SESIÓN */
                .antMatchers("/**").permitAll()
            .and().formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/", false)
                .failureUrl("/login?error=true")
            .and().rememberMe()
                .rememberMeParameter("remember-me")
                .userDetailsService(userDetailsService)
                .key(getRememberMeKey())
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
            .and().logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
            .and().exceptionHandling()
                .accessDeniedPage("/403") //TODO: hacer página 403
            .and().csrf().disable();
    }

    private String getRememberMeKey() throws IOException {
        ClassPathResource resource = new ClassPathResource("keys/rememberme_key.pem");
        byte[] bytes = IOUtils.toByteArray(resource.getInputStream());
        return new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
    }

}
