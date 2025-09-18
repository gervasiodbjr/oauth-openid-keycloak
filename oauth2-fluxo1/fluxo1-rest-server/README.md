# fluxo1-rest-server

Resource Server em Spring Boot protegido por OAuth2 (JWT) usando Keycloak como Authorization Server. Expõe um endpoint protegido que retorna informações do token recebido.

Faz parte do conjunto de exemplos desta repo, sob `oauth2-fluxo1/fluxo1-rest-server`. Normalmente é consumido pelo módulo cliente `fluxo1-rest-client` (Client Credentials).

## Visão geral
- Este serviço é um OAuth2 Resource Server (Spring Security) que valida JWTs emitidos pelo Keycloak.
- A validação é baseada no `issuer-uri` do realm (descoberta OIDC/JWKs automáticas).
- Endpoints:
  - `GET /info/token` — protegido; requer Bearer Token válido. Retorna texto com o token e suas claims (somente para fins de depuração/demonstração).

Principais arquivos:
- `src/main/java/com/gervasio/fluxo1_rest_server/controller/InfoController.java` — endpoint `/info/token` que lê o `Jwt` autenticado e cria a resposta.
- `src/main/resources/application.properties` — porta da aplicação e configuração do Resource Server (`issuer-uri`).
- `pom.xml` — dependências principais: `spring-boot-starter-oauth2-resource-server` e `spring-boot-starter-web`.

## Requisitos
- JDK 21
- Maven 3.9+
- Keycloak em execução (com o realm e os clients configurados)

Opcional (para subir Keycloak via Docker): veja `docker_config/docker-compose.yml` e `docker_config/project-realm.json` na raiz da repo.

## Configuração do Keycloak
Este projeto valida tokens emitidos pelo realm abaixo. Para testes com o cliente do fluxo Client Credentials, utilize um client confidencial com Service Accounts habilitado (ex.: `project-client01`), conforme o módulo `fluxo1-rest-client`.

- Realm: `project-realm`
- URLs padrão do Keycloak (ajuste host/porta conforme o seu):
  - Issuer/Realm: `http://localhost:8080/realms/project-realm`
  - JWKS/Metadata são resolvidos automaticamente via discovery (`.well-known/openid-configuration`)

Observação sobre roles/escopos:
- Este exemplo não impõe regras finas de autorização por role/scope no servidor; ele apenas exige um JWT válido emitido pelo realm configurado. Regras adicionais podem ser adicionadas conforme necessidade.

## Configuração da aplicação
Arquivo: `src/main/resources/application.properties` (principais trechos)

```
spring.application.name=fluxo1-rest-server
server.port=9000

# Resource Server (JWT via Keycloak)
keycloak.host=localhost
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://${keycloak.host}:8080/realms/project-realm
```

Observações:
- Ajuste `keycloak.host` se o Keycloak não estiver em `localhost`.
- Se preferir, é possível configurar `jwk-set-uri` diretamente, porém `issuer-uri` já permite a descoberta automática.

## Executando a aplicação
1. Certifique-se de que o Keycloak está em execução e que o realm `project-realm` existe.
2. Build/Run com Maven:
   - `./mvnw spring-boot:run` (Linux/Mac)
   - `mvnw.cmd spring-boot:run` (Windows)
3. Teste o endpoint protegido:
   - `GET http://localhost:9000/info/token`
   - Envie um header `Authorization: Bearer <ACCESS_TOKEN>` emitido pelo Keycloak para este realm.
   - Dica: use o cliente do módulo `fluxo1-rest-client` em `http://localhost:9001/client/info/token` para obter a resposta do servidor automaticamente (o cliente buscará o token via Client Credentials e chamará este serviço).

## Dicas e solução de problemas
- 401 Unauthorized:
  - Verifique se o `issuer-uri` aponta para o realm correto e acessível.
  - Garanta que o token foi emitido pelo mesmo realm configurado em `issuer-uri` (mismatch de issuer).
  - Cheque a validade do token (expiração) e possíveis diferenças de horário (clock skew) entre sistemas.
- 403 Forbidden (se você adicionar regras de autorização futuras):
  - Confirme se o token contém as roles/escopos esperados e como elas são validadas no servidor.
- Erros de descoberta/JWKs:
  - Confirme a conectividade entre a aplicação (porta 9000) e o Keycloak (porta 8080).
  - Verifique se o endpoint `.well-known/openid-configuration` do realm está acessível.


