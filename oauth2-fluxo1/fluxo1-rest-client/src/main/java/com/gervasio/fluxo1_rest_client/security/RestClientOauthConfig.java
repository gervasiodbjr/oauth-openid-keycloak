package com.gervasio.fluxo1_rest_client.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientOauthConfig {

    /**
     * Configures and returns a {@link RestClient} instance customized to use OAuth2 authorization with Keycloak.
     * The request interceptor is set up to resolve the client registration ID to "keycloak" for all requests,
     * leveraging the provided {@link OAuth2AuthorizedClientManager} for authorization management.
     *
     * @param builder the {@link RestClient.Builder} object used to construct the {@link RestClient} instance.
     * @param authorizedClientManager the {@link OAuth2AuthorizedClientManager} responsible for managing OAuth2 authorized clients.
     * @return a configured {@link RestClient} instance with an OAuth2 client HTTP request interceptor.
     */
    @Bean
    RestClient keycloakRestClientOauth(RestClient.Builder builder, OAuth2AuthorizedClientManager authorizedClientManager) {
        OAuth2ClientHttpRequestInterceptor requestInterceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        requestInterceptor.setClientRegistrationIdResolver((HttpRequest request) -> "keycloak");
        return builder.requestInterceptor(requestInterceptor).build();
    }

}
