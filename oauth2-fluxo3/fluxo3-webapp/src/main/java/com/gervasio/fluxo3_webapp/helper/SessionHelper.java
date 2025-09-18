package com.gervasio.fluxo3_webapp.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;

public class SessionHelper {

    /**
     * Retrieves the current {@link Authentication} object from the security context.
     *
     * @return the current {@link Authentication} object, or null if no authentication information is available
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Retrieves the collection of {@link GrantedAuthority} objects associated with the current {@link Authentication}.
     *
     * @return a collection of {@link GrantedAuthority}, or an empty collection if the current {@link Authentication} is null or has no authorities
     */
    @SuppressWarnings("unchecked")
    public static Collection<GrantedAuthority> getGrantedAuthority() {
        return (Collection<GrantedAuthority>) getAuthentication().getAuthorities();
    }

    /**
     * Retrieves a list of authority strings associated with the current {@link Authentication}.
     *
     * @return a list of authority strings, or an empty list if the current {@link Authentication} is null
     *         or has no associated {@link GrantedAuthority} objects
     */
    public static List<String> getAuthorities() {
        return getGrantedAuthority().stream().map(GrantedAuthority::getAuthority).toList();
    }

}
