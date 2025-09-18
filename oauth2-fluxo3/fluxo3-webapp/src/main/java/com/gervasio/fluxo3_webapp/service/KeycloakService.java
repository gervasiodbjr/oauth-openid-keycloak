package com.gervasio.fluxo3_webapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KeycloakService {

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private final JwtDecoder jwtDecoder;

    public KeycloakService(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Authenticates a user by validating their credentials against a Keycloak server
     * and sets the authenticated user's security context in the session.
     *
     * @param username the username of the user trying to log in
     * @param password the password of the user trying to log in
     * @param request the current HTTP servlet request
     * @throws RuntimeException if the authentication fails due to invalid credentials
     */
    public void login(String username, String password, HttpServletRequest request) {
        String tokenUrl = issuerUri + "/protocol/openid-connect/token";

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("username", username);
        map.add("password", password);

        JsonNode tokenResponse = restTemplate.postForObject(tokenUrl, map, JsonNode.class);

        if (tokenResponse != null && tokenResponse.has("access_token")) {
            String accessToken = tokenResponse.get("access_token").asText();
            Jwt jwt = jwtDecoder.decode(accessToken);
            JwtAuthenticationConverter converter = getJwtAuthenticationConverter();
            JwtAuthenticationToken authentication = (JwtAuthenticationToken) converter.convert(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    /**
     * Provides a {@link JwtAuthenticationConverter} configured to map JWT roles
     * from a "realm_access" claim into Spring Security granted authorities.
     *
     * @return a configured instance of {@link JwtAuthenticationConverter} that
     *         converts JWT roles to {@link SimpleGrantedAuthority} objects prefixed
     *         with "ROLE_".
     */
    private JwtAuthenticationConverter getJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt1 -> {
            Map<String, Object> realmAccess = jwt1.getClaim("realm_access");
            //noinspection unchecked
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
        });
        return converter;
    }
}
