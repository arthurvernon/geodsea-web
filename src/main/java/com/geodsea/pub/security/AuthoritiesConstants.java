package com.geodsea.pub.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    /**
     * Skipper of a vessel
     */
    public static final String SKIPPER = "ROLE_SKIPPER";

    /**
     * Owner of a vessel
     */
    public static final String OWNER = "ROLE_OWNER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
}
