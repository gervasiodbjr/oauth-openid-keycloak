package com.gervasio.fluxo3_webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    /**
     * Creates and returns a {@link NimbusJwtDecoder} configured to validate JWT tokens
     * based on the issuer's location.
     *
     * @return a {@link NimbusJwtDecoder} instance configured with the issuer's URI for JWT decoding.
     */
    @Bean
    public NimbusJwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }

    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    /**
     * Configures the security filter chain for the application, defining authorization rules,
     * OAuth2 login configuration, JWT decoding, logout behavior, and exception handling.
     *
     * @param http the {@link HttpSecurity} object to configure security behavior such as
     *             request authorization, login processing, resource server settings, logout,
     *             and exception handling.
     * @return the configured {@link SecurityFilterChain} instance.
     * @throws Exception if an error occurs during the configuration of the security settings.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
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
     * Creates and returns an {@link AuthenticationSuccessHandler} that handles successful
     * authentication events by redirecting users to the specified URL.
     *
     * @return an {@link AuthenticationSuccessHandler} instance configured to redirect to the "/" URL upon successful authentication.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler("/");
    }

    /**
     * Configures an {@link OidcClientInitiatedLogoutSuccessHandler} for handling OIDC logout success events.
     * The handler redirects the user to a specified post-logout URI after successful logout.
     *
     * @return a {@link LogoutSuccessHandler} configured to process OpenID Connect client-initiated logout success events.
     */
    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/login?logout");
        return oidcLogoutSuccessHandler;
    }

}
