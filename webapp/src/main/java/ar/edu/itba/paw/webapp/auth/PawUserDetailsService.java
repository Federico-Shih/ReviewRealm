package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PawUserDetailsService implements UserDetailsService {

    private final UserService us;

    private final Pattern BCRYPT_PATTERN = Pattern
            .compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

    @Autowired
    public PawUserDetailsService(final UserService us) {
        this.us = us;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = us.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found for email: " + email));

        if(!BCRYPT_PATTERN.matcher(user.getPassword()).matches()) {
            throw new UsernameNotFoundException("User password is not hashed");
        }

        final List<Role> roleList = us.getUserRoles(user.getId());
        //TODO: implement logic to grant only required authorities
        final Collection<GrantedAuthority> authorities = new HashSet<>();
        roleList.forEach((role -> authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", role.getRoleName())))));
        System.out.println(authorities);
        //authorities.add(new SimpleGrantedAuthority("ROLE_REVIEWER"));
        // TODO: definir roles (más de uno por user)
        return new PawAuthUserDetails(user.getEmail(), user.getPassword(), authorities);
    }
}