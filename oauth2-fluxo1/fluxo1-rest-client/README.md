# fluxo1-rest-client

Cliente REST em Spring Boot que consome um Resource Server protegido por OAuth2 (Keycloak) usando o fluxo Client Credentials. Demonstra como configurar o `RestClient` do Spring com `OAuth2ClientHttpRequestInterceptor` para obter automaticamente o access token antes de cada requisição.

Faz parte do conjunto de exemplos desta repo, sob `oauth2-fluxo1/fluxo1-rest-client`.

## Visão geral
- Fluxo de autenticação: Client Credentials (máquina-a-máquina), sem interação do usuário.
- O `RestClient` é configurado com `OAuth2ClientHttpRequestInterceptor` que resolve sempre o `clientRegistrationId` como `keycloak`, usando o `OAuth2AuthorizedClientManager` para gerenciar o token.
- Endpoint exposto pelo cliente:
  - `GET /client/info/token` — chama o Resource Server em `/info/token` e retorna a resposta.
- O host/porta do Resource Server são configuráveis via propriedades `restserver.*`.

Principais arquivos:
- `src/main/java/com/gervasio/fluxo1_rest_client/security/RestClientOauthConfig.java` — configuração do `RestClient` com interceptor OAuth2.
- `src/main/java/com/gervasio/fluxo1_rest_client/controller/ClientInfoController.java` — endpoint `/client/info/token` que consome o servidor remoto.
- `src/main/resources/application.properties` — propriedades da aplicação e do cliente Keycloak.

## Requisitos
- JDK 21
- Maven 3.9+
- Keycloak em execução
- Um Resource Server disponível (por padrão, em `http://localhost:9000`) com endpoint protegido `/info/token`

Opcional (para subir Keycloak via Docker): veja `docker_config/docker-compose.yml` e `docker_config/project-realm.json` na raiz da repo.

## Configuração do Keycloak
Este projeto usa um cliente confidencial com o fluxo Client Credentials habilitado. Ajuste conforme seu ambiente.

- Realm: `project-realm`
- Client: `project-client01`
  - Client Type: Confidential
  - Client authentication: habilitado (usa `client-secret`)
  - Client Credentials (Service Accounts): habilitado
  - Client secret: gere e copie para a aplicação

URLs padrão do Keycloak (ajuste host/porta conforme o seu):
- Issuer/Realm: `http://localhost:8080/realms/project-realm`
- Token endpoint: `${issuer}/protocol/openid-connect/token`

## Configuração da aplicação
Arquivo: `src/main/resources/application.properties` (principais trechos)

```
# Porta da aplicação (cliente)
spring.application.name=fluxo1-rest-client
server.port=9001

# Onde está o Resource Server protegido
restserver.protocol=http
restserver.host=localhost
restserver.port=9000

# OAuth2 Client (Client Credentials) para Keycloak
keycloak.host=localhost
spring.security.oauth2.client.registration.keycloak.provider=keycloak
spring.security.oauth2.client.registration.keycloak.client-id=project-client01
spring.security.oauth2.client.registration.keycloak.client-secret=<COLOQUE_O_CLIENT_SECRET_AQUI>
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=client_credentials
spring.security.oauth2.client.provider.keycloak.token-uri=http://${keycloak.host}:8080/realms/project-realm/protocol/openid-connect/token
```

Observações:
- Substitua `client-secret` pelo secret real do client no Keycloak.
- Se necessário, ajuste `keycloak.host` e os parâmetros do Resource Server (`restserver.*`).

## Executando a aplicação
1. Certifique-se de que o Keycloak está em execução e que o cliente `project-client01` está configurado com Client Credentials habilitado.
2. (Opcional) Tenha o Resource Server rodando em `http://localhost:9000` com o endpoint protegido `/info/token`.
3. Build/Run com Maven:
   - `./mvnw spring-boot:run` (Linux/Mac)
   - `mvnw.cmd spring-boot:run` (Windows)
4. Acesse: `http://localhost:9001/client/info/token`
   - A aplicação obterá um access token via Client Credentials e chamará o Resource Server retornando a resposta.

## Dicas e solução de problemas
- 401/403 do Resource Server:
  - Verifique se o client `project-client01` possui as permissões/roles necessárias e se o token é aceito pelo Resource Server.
  - Confirme o `token-uri` e as credenciais (client-id/secret).
- Erro ao resolver o token:
  - Cheque a conectividade com o Keycloak (`localhost:8080`).
  - Valide se o fluxo Client Credentials está habilitado para o client e se o Service Account está ativo (quando aplicável).
- Resource Server em outra URL/porta:
  - Ajuste `restserver.protocol`, `restserver.host` e `restserver.port` nas propriedades.



