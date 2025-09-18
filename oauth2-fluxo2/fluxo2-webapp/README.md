# fluxo2-webapp

Aplicação Spring Boot (Web MVC + Spring Security) integrada ao Keycloak usando OAuth2 Login (Authorization Code Flow com OIDC). Este módulo demonstra login redirecionando para o Keycloak e autorização baseada em roles.

Faz parte do conjunto de exemplos desta repo, sob `oauth2-fluxo2/fluxo2-webapp`.

## Visão geral
- Fluxo de autenticação: Authorization Code (OIDC) via `oauth2Login` do Spring Security.
- Ao acessar qualquer rota protegida, o usuário é redirecionado ao Keycloak para login; após autenticação, retorna com ID Token/Access Token.
- As roles (perfis) são mapeadas para `ROLE_*` no Spring Security por meio de um `GrantedAuthoritiesMapper` (ver SecurityConfig). [Observação] As roles são lidas da claim `roles` do ID Token; ajuste se necessário para o seu cenário (ex.: `realm_access.roles` no Access Token).
- Regras de acesso:
  - `/` Home: autenticado
  - `/user`: requer `ROLE_USER` ou `ROLE_ADMIN`
  - `/admin`: requer `ROLE_ADMIN`
  - `/acessonaoautorizado`: página de acesso negado
  - `/logout`: efetua logout no provedor (RP-Initiated Logout) e retorna para a aplicação

Principais arquivos:
- `src/main/java/com/gervasio/fluxo2_webapp/security/SecurityConfig.java` — configuração de segurança (oauth2Login, mapeamento de authorities, logout OIDC, página de acesso negado).
- `src/main/java/com/gervasio/fluxo2_webapp/controller/InfoController.java` — rotas `/`, `/user`, `/admin`, `/acessonaoautorizado`, `/logout` (retornam HTML simples).
- `src/main/resources/application.properties` — propriedades da aplicação e do cliente Keycloak.

## Requisitos
- JDK 21
- Maven 3.9+
- Keycloak em execução

Opcional (para subir Keycloak via Docker): veja `docker_config/docker-compose.yml` e `docker_config/project-realm.json` na raiz da repo.

## Configuração do Keycloak
Este projeto usa um cliente confidencial com Standard Flow (Authorization Code) habilitado. Ajuste conforme seu ambiente.

- Realm: `project-realm`
- Client: `project-client02`
  - Client Type: Confidential
  - Client authentication: habilitado (há `client-secret` na aplicação)
  - Standard Flow (Authorization Code): ON
  - Direct Access Grants: OFF (não utilizado neste fluxo)
  - Client secret: gere e copie para a aplicação
- Roles no Realm: `USER`, `ADMIN`
  - Atribua as roles aos usuários de teste

Redirect URI padrão (ajuste host/porta se necessário):
- `http://localhost:9001/login/oauth2/code/keycloak`

URLs padrão do Keycloak (ajuste host/porta conforme o seu):
- Issuer/Realm: `http://localhost:8080/realms/project-realm`
- Endpoints OIDC: `${issuer}/protocol/openid-connect/*`

## Configuração da aplicação
Arquivo: `src/main/resources/application.properties` (principais trechos)

```
server.port=9001

# Parâmetros do Keycloak
keycloak.protocol=http
keycloak.host=localhost
keycloak.port=8080
keycloak.realm=project-realm

# Client (CONFIDENTIAL) para Authorization Code
spring.security.oauth2.client.registration.keycloak.client-id=project-client02
spring.security.oauth2.client.registration.keycloak.client-secret=<COLOQUE_O_CLIENT_SECRET_AQUI>
spring.security.oauth2.client.registration.keycloak.provider=keycloak
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.keycloak.scope=openid,profile,email
# spring.security.oauth2.client.registration.keycloak.redirect-uri=http://localhost:9001/login/oauth2/code/keycloak

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
- O mapeamento de roles usa a claim `roles` do ID Token. Se suas roles estiverem em `realm_access.roles` (padrão do Keycloak no Access Token), ajuste o `userAuthoritiesMapper` em `SecurityConfig` conforme sua necessidade.

## Executando a aplicação
1. Build/Run com Maven:
   - `./mvnw spring-boot:run` (Linux/Mac)
   - `mvnw.cmd spring-boot:run` (Windows)
2. Acesse: `http://localhost:9001`
3. Você será redirecionado para o Keycloak para autenticação. Informe as credenciais do usuário do Keycloak.

Rotas de exemplo:
- `GET /` — Home (autenticado)
- `GET /user` — requer `USER` ou `ADMIN`
- `GET /admin` — requer `ADMIN`
- `GET /acessonaoautorizado` — acesso negado
- `GET /logout` — encerra a sessão na aplicação e inicia logout OIDC (retorna para a base URL)

## Dicas e solução de problemas
- 401/403 inesperado:
  - Verifique se o usuário possui as roles esperadas no realm (`USER`/`ADMIN`).
  - Confirme como as roles estão sendo emitidas no token (ID Token claim `roles` vs Access Token `realm_access.roles`). Ajuste o mapeamento se necessário.
- Falha no login ou redirecionamento incorreto:
  - Confira `client-id` e `client-secret` no `application.properties`.
  - Garanta que o Standard Flow (Authorization Code) está habilitado no cliente do Keycloak.
  - Verifique a Redirect URI cadastrada no cliente (`http://localhost:9001/login/oauth2/code/keycloak`).
  - Verifique a conectividade entre a aplicação (`localhost:9001`) e o Keycloak (`localhost:8080`).
- Logout não retorna para a aplicação:
  - O projeto utiliza `OidcClientInitiatedLogoutSuccessHandler` com `postLogoutRedirectUri` para `{baseUrl}`. Garanta que o `issuer-uri` esteja correto e que o Keycloak aceite o RP-Initiated Logout.




