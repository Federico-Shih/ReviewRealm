package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.webapp.auth.AccessControl;
import ar.edu.itba.paw.webapp.auth.BasicAuthFilter;
import ar.edu.itba.paw.webapp.auth.JwtTokenFilter;
import ar.edu.itba.paw.webapp.mappers.ForbiddenRequestHandler;
import ar.edu.itba.paw.webapp.mappers.UnauthorizedRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private AccessControl accessControl;

    @Autowired
    private BasicAuthFilter basicAuthFilter;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    private static final String ACCESS_CONTROL_CHECK_REVIEW_OWNER = "@accessControl.checkReviewAuthorOwner(#id)";

    private static final String ACCESS_CONTROL_CHECK_REVIEW_FEEDBACK = "@accessControl.checkReviewAuthorforFeedback(#id)";

    private static final String ACCESS_CONTROL_CHECK_GAME_REVIEWED = "@accessControl.checkGameAuthorReviewed(#id)";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoterList = Arrays.asList(webExpressionVoter(), new RoleVoter(), new AuthenticatedVoter());
        return new UnanimousBased(decisionVoterList);
    }

    @Bean
    public WebExpressionVoter webExpressionVoter() {
        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(webSecurityExpressionHandler());
        return webExpressionVoter;
    }

    @Bean
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
        return new DefaultWebSecurityExpressionHandler();
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
        http
                // redirecciona si uno trata de ir a login o register y ya está conectado
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .accessDecisionManager(accessDecisionManager())
                .antMatchers(HttpMethod.PATCH, "/api/users/{id:\\d+}").authenticated()
                .antMatchers(HttpMethod.POST, "/api/reports", "/api/games").authenticated()
                .antMatchers("/api/reports/**").hasRole(RoleType.MODERATOR.getRole())
// todo: agregar rutas a medida que se van haciendo las APIs
                //.antMatchers("/admin/**").hasRole("ADMIN")  lo que requiera un rol especial
                //.antMatchers("/review/edit/").access("@AccessHelper.canEdit") cuando se requiera un acceso especial segun el usuario (Spring Expression Language)
//                .antMatchers("/review/{id:\\d+}/edit").access(ACCESS_CONTROL_CHECK_REVIEW_OWNER)
//                .antMatchers(HttpMethod.POST,"/review/feedback/{id:\\d+}").access(ACCESS_CONTROL_CHECK_REVIEW_FEEDBACK)
//                .antMatchers("/review/submit/{id:\\d+}").access(ACCESS_CONTROL_CHECK_GAME_REVIEWED)
//                /* ACÁ PONEMOS TODOS LOS PATHS QUE REQUIERAN INICIAR SESIÓN Y TENER UN ROL */
//                .antMatchers("/review/delete/{\\d+}", "/game/submissions", "/game/submissions/**", "/game/{\\d+}/edit", "/game/delete/{\\d+}", "/report/reviews/**", "/report/reviews").hasRole(RoleType.MODERATOR.getRole())
//                /* ACÁ PONEMOS TODOS LOS PATHS QUE REQUIERAN INICIAR SESIÓN, PERO NO ROLES */
//                .antMatchers(HttpMethod.GET, "/api/users").authenticated()
//                .antMatchers("/review/submit/**",
//                        "/profile/following",
//                        "/profile/followers",
//                        "/profile/follow/{\\d+}",
//                        "/profile/unfollow/{\\d+}",
//                        "/for-you/**",
//                        "/profile/settings/**",
//                        "/profile/settings/**",
//                        "/game/submit",
//                        "/profile/missions"
//                ).authenticated()
                .antMatchers("/api/**").permitAll()
                /* ACÁ PONEMOS TODOS LOS PATHS QUE REQUIERAN NO HABER INICIADO SESIÓN */

                /* POR DEFAULT NO ES NECESARIO INICIAR SESIÓN */
                .and().exceptionHandling()
                .authenticationEntryPoint(new UnauthorizedRequestHandler(messageSource))
                .accessDeniedHandler(new ForbiddenRequestHandler(messageSource))
                .and().headers().cacheControl().disable()
                .and().csrf().disable()
                .addFilterBefore(basicAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
