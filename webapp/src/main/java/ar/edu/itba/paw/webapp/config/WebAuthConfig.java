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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.web.cors.CorsConfiguration.ALL;


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
                .antMatchers(HttpMethod.POST, "/api/reports", "/api/games","/api/reviews").authenticated()
                .antMatchers("/api/reports/**", "/api/reports").hasRole(RoleType.MODERATOR.getRole())
                .antMatchers(HttpMethod.PATCH, "/api/games/{id:\\d+}").hasRole(RoleType.MODERATOR.getRole())
                .antMatchers(HttpMethod.DELETE, "/api/games/{id:\\d+}").hasRole(RoleType.MODERATOR.getRole())
                .antMatchers(HttpMethod.POST,"/api/reviews/{id:\\d+}").authenticated()
                .antMatchers(HttpMethod.DELETE,"/api/reviews/{id:\\d+}").authenticated()
                .antMatchers(HttpMethod.PATCH,"/api/reviews/{id:\\d+}").authenticated()
                .antMatchers("/api/**").permitAll()
                /* ACÁ PONEMOS TODOS LOS PATHS QUE REQUIERAN NO HABER INICIADO SESIÓN */

                /* POR DEFAULT NO ES NECESARIO INICIAR SESIÓN */
                .and().exceptionHandling()
                .authenticationEntryPoint(new UnauthorizedRequestHandler(messageSource))
                .accessDeniedHandler(new ForbiddenRequestHandler(messageSource))
                .and().headers().cacheControl().disable()
                .and().cors().and().csrf().disable()
                .addFilterBefore(basicAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList(ALL));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.addAllowedHeader(ALL);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Refresh", "Link", "Location", "ETag", "X-Reviewrealm-TotalPages", "X-Reviewrealm-TotalElements"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
