package com.gervasio.fluxo2_webapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;


    /**
     * Constructs a SecurityConfig instance that manages security settings for the application.
     *
     * @param clientRegistrationRepository the repository containing client registration information
     */
    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    /**
     * Configures the security filter chain for the application, defining the behavior
     * for login, logout, authorization, and exception handling.
     *
     * @param http the {@link HttpSecurity} to configure the security settings,
     *             including OAuth2 login, custom logout success handling, request authorization,
     *             and exception handling.
     * @return the configured {@link SecurityFilterChain} instance.
     * @throws Exception if any error occurs while building the security filter chain.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(userAuthoritiesMapper())
                        )
                        .defaultSuccessUrl("/", true)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("login/**", "logout/**", "/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/acessonaoautorizado")
                );
        return http.build();
    }

    /**
     * Maps user authorities for OAuth2 and OIDC authentication flows. This method processes
     * a collection of user-granted authorities and transforms them into a potentially expanded
     * set of roles and authorities, considering specific logic for OpenID Connect (OIDC) and
     * other OAuth2 user authority types.
     *
     * For OIDC, it extracts roles from the ID token's claims, prefixed with "ROLE_", and adds
     * them to the collection of granted authorities. Default authorities and additional processing
     * for other OAuth2 user types can also be handled.
     *
     * @return an implementation of {@link GrantedAuthoritiesMapper} that processes and maps
     *         input authorities to a customized set of granted authorities.
     */
    @SuppressWarnings("unchecked")
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    Map<String, Object> realmAccess = oidcUserAuthority.getIdToken().getClaims();
                    if (realmAccess != null) {
                        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                        if (roles != null) {
                            mappedAuthorities.addAll(roles.stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                    .collect(Collectors.toSet()));
                        }
                    }
                } else if (authority instanceof OAuth2UserAuthority) {
                    // Handle other OAuth2 user types if needed
                }
                mappedAuthorities.add(authority);
            });
            return mappedAuthorities;
        };
    }

    /**
     * Creates a custom {@link LogoutSuccessHandler} for handling OpenID Connect (OIDC)-based
     * logout success. This handler initiates an OIDC client-initiated logout and redirects
     * users to a defined post-logout URI.
     *
     * @return a configured {@link LogoutSuccessHandler} implementation for handling OIDC
     *         logout success scenarios.
     */
    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }


}


