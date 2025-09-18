# fluxo3-webapp

Aplicação Spring Boot (Web MVC + Thymeleaf + Spring Security) integrando com Keycloak. Este módulo demonstra autenticação via formulário próprio (Resource Owner Password Credentials – Direct Access Grants) e autorização baseada em roles de realm.

Faz parte do conjunto de exemplos desta repo, sob `oauth2-fluxo3/fluxo3-webapp`.

## Visão geral
- Login por formulário em `/login` que envia usuário/senha para o Keycloak (grant_type=password) via `KeycloakService`.
- Após autenticação, o access_token (JWT) é validado e as roles do realm são mapeadas para `ROLE_*` do Spring Security.
- Regras de acesso:
  - `/` Home: autenticado
  - `/user`: requer `ROLE_USER` ou `ROLE_ADMIN`
  - `/admin`: requer `ROLE_ADMIN`
  - `/acessonaoautorizado`: página de acesso negado
  - `/logout`: efetua logout e redireciona para `/login?logout`

Principais arquivos:
- `src/main/java/com/gervasio/fluxo3_webapp/config/SecurityConfig.java` — configuração de segurança (login/logout, handler de sucesso, página de acesso negado).
- `src/main/java/com/gervasio/fluxo3_webapp/controller/LoginController.java` — GET/POST `/login` e `/logout`.
- `src/main/java/com/gervasio/fluxo3_webapp/controller/WebController.java` — rotas `/`, `/user`, `/admin`, `/acessonaoautorizado`.
- `src/main/java/com/gervasio/fluxo3_webapp/service/KeycloakService.java` — autenticação contra Keycloak usando `grant_type=password` e mapeamento de roles.
- `src/main/resources/templates/*.html` — páginas Thymeleaf (login, index, user, admin, acessonaoautorizado) + fragmentos.
- `src/main/resources/application.properties` — propriedades da aplicação e do cliente Keycloak.

## Requisitos
- JDK 21
- Maven 3.9+
- Keycloak em execução

Opcional (para subir Keycloak via Docker): veja `docker_config/docker-compose.yml` e `docker_config/project-realm.json` na raiz da repo.

## Configuração do Keycloak
Este projeto usa um cliente confidencial com Direct Access Grants (password grant). Ajuste conforme seu ambiente.

- Realm: `project-realm`
- Client: `project-client03`
  - Client Type: Confidential
  - Access Type (Keycloak 24+): Client authentication habilitado
  - Client secret: gere e copie para a aplicação
  - Direct Access Grants: ON (habilitado)
  - Standard Flow (Authorization Code): pode estar OFF (não é utilizado neste fluxo)
- Roles no Realm: `USER`, `ADMIN`
  - Atribua as roles aos usuários de teste

URLs padrão do Keycloak (ajuste host/porta conforme o seu):
- Issuer/Realm: `http://localhost:8080/realms/project-realm`
- Token endpoint: `${issuer}/protocol/openid-connect/token`

## Configuração da aplicação
Arquivo: `src/main/resources/application.properties` (principais trechos)

```
server.port=9001

# Parâmetros do Keycloak
keycloak.protocol=http
keycloak.host=localhost
keycloak.port=8080
keycloak.realm=project-realm

# Client (CONFIDENTIAL) utilizado no Direct Access Grants
auth.client-id=spring usará esta propriedade mapeada; ver abaixo
spring.security.oauth2.client.registration.keycloak.client-id=project-client03
spring.security.oauth2.client.registration.keycloak.client-secret=<COLOQUE_O_CLIENT_SECRET_AQUI>

# Provider (issuer e endpoints)
keycloak.uri=${keycloak.protocol}://${keycloak.host}:${keycloak.port}
spring.security.oauth2.client.provider.keycloak.issuer-uri=${keycloak.uri}/realms/${keycloak.realm}
spring.security.oauth2.client.provider.keycloak.authorization-uri=${spring.security.oauth2.client.provider.keycloak.issuer-uri}/protocol/openid-connect/auth
spring.security.oauth2.client.provider.keycloak.token-uri=${spring.security.oauth2.client.provider.keycloak.issuer-uri}/protocol/openid-connect/token
spring.security.oauth2.client.provider.keycloak.user-info-uri=${spring.security.oauth2.client.provider.keycloak.issuer-uri}/protocol/openid-connect/userinfo
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username
```

Observações:
- Substitua `spring.security.oauth2.client.registration.keycloak.client-secret` pelo secret real do client no Keycloak.
- Se necessário, altere `keycloak.host`, `keycloak.port` e `keycloak.realm`.
- As roles de realm são lidas da claim `realm_access.roles` do JWT e convertidas para `ROLE_*` pelo `KeycloakService`.

## Executando a aplicação
1. Build/Run com Maven:
   - `./mvnw spring-boot:run` (Linux/Mac)
   - `mvnw.cmd spring-boot:run` (Windows)
2. Acesse: `http://localhost:9001`
3. Você será redirecionado para `/login`. Informe as credenciais do usuário do Keycloak.

Rotas de exemplo:
- `GET /` — Home (autenticado)
- `GET /user` — requer `USER` ou `ADMIN`
- `GET /admin` — requer `ADMIN`
- `GET /acessonaoautorizado` — acesso negado
- `GET /logout` — encerra sessão e redireciona para `/login?logout`

## Dicas e solução de problemas
- 401/403 inesperado:
  - Verifique se o usuário possui as roles de realm esperadas (`USER`/`ADMIN`).
  - Confirme que o token retornado possui a claim `realm_access.roles`.
- Falha no login (`/login?error`):
  - Certifique-se de que o client é CONFIDENTIAL e o Direct Access Grants está HABILITADO.
  - Confira `client-id` e `client-secret` no `application.properties`.
  - Verifique a conectividade entre a aplicação (`localhost:9001`) e o Keycloak (`localhost:8080`).
- Alterar host/porta do Keycloak:
  - Ajuste `keycloak.host` e `keycloak.port` e reinicie a aplicação.
- Logout não redireciona como esperado:
  - O projeto utiliza `OidcClientInitiatedLogoutSuccessHandler` com `postLogoutRedirectUri` para `{baseUrl}/login?logout`. Garanta que o `issuer-uri` esteja correto.




