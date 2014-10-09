package com.geodsea.pub.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     * @return the username or null if no user is logged on
     */
    public static String getCurrentLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails springSecurityUser = null;
        String userName = null;

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                springSecurityUser = (UserDetails) authentication.getPrincipal();
                userName = springSecurityUser.getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                userName = (String) authentication.getPrincipal();
            }
        }
        return userName;
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        final Collection<? extends GrantedAuthority> authorities = getAuthorities();
        if (authorities == null)
            return false;

        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @see com.geodsea.pub.security.AuthoritiesConstants
     * @param role
     * @return
     */
    public static boolean userHasRole(String role)
    {
        final Collection<? extends GrantedAuthority> authorities = getAuthorities();
        if (authorities != null)
            for (GrantedAuthority auth : authorities) {
                if (role.equals(auth.getAuthority()))
                    return true;
            }

        return false;
    }

    private static Collection<? extends GrantedAuthority> getAuthorities()
    {
        // get security context from thread local
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null)
            return null;

        Authentication authentication = context.getAuthentication();
        if (authentication == null)
            return null;

        return authentication.getAuthorities();
    }

    public static void addAuthority(String role) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(auth.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(role));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(),auth.getCredentials(),authorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
