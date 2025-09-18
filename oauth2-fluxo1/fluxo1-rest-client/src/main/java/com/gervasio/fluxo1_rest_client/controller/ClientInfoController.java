package com.gervasio.fluxo1_rest_client.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/client/info")
public class ClientInfoController {

    private final RestClient restClient;

    @Value("${restserver.protocol:http}")
    private String serverProtocol;

    @Value("${restserver.host:localhost}")
    private String serverHost;

    @Value("${restserver.port:9000}")
    private String serverPort;

    public ClientInfoController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("token")
    public String getMethodName() {
        return restClient.get()
        .uri(createServerUri() + "/info/token")
        .retrieve()
        .body(String.class);
    }
    
    private String createServerUri() {
        return serverProtocol + "://" + serverHost + ":" + serverPort;
    }

}
