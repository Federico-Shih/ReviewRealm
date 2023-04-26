package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.concurrent.TimeUnit;


@EnableWebSecurity
@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

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

                /* ACÁ PONEMOS TODOS LOS PATHS QUE REQUIERAN INICIAR SESIÓN, PERO NO ROLES */
                .antMatchers("/review/delete/{\\d+}").hasRole("MODERATOR")
                .antMatchers("/review/submit",
                        "/review/submit/{\\d+}",
                        "/profile/following",
                        "/profile/followers",
                        "/profile/follow/{\\d+}",
                        "/profile/unfollow/{\\d+}"
                ).authenticated()

                /* ACÁ PONEMOS TODOS LOS PATHS QUE REQUIERAN NO HABER INICIADO SESIÓN */
                .antMatchers("/login", "/register").anonymous()

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
                .key("TODO: cambiar esto por algo más seguro, desde un archivo generado por openssl") //TODO
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
            .and().logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
            .and().exceptionHandling()
                .accessDeniedPage("/403") //Todo: hacer página 403
            .and().csrf().disable();
    }
}