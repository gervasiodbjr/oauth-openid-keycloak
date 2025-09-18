# Descrição do Projeto

Este repositório reúne conteúdos teóricos e projetos de exemplo sobre segurança de aplicações web com os protocolos OAuth 2 e OpenID Connect, utilizando o Keycloak como servidor de identidade (Identity Provider / Authorization Server).

- Protocolos: [OAuth 2](https://datatracker.ietf.org/doc/html/rfc6749) e [OpenID](https://openid.net/papers/)
- Provedor de Identidade: [Keycloak](https://www.keycloak.org)

## Pré-requisito: subir os containers do Keycloak (docker_config)
Antes de executar qualquer projeto deste diretório, é necessário subir os containers do Keycloak e Postgres a partir do diretório `docker_config`.

- Guia completo: [docker_config/README.md](docker_config/README.md)
- Após a subida, o realm `project-realm` será importado com os clients `project-client01`, `project-client02`, `project-client03` e `project-client04`.
- Importante: será necessário RECRIAR a secret dos clients `project-client01`, `project-client02` e `project-client03` no Keycloak e copiar as novas secrets para os respectivos projetos (o `project-client04` é público e não requer secret).

## Conteúdo teórico (documentos)
1. [Segurança, OAuth2 e OpenID](documentos/Seguranca_OAuth2_OpenID.md)
2. [Spring Security](documentos/spring-security-servlet-docs.md)

## Visão geral dos projetos (oauth-openid-keycloak)
A pasta contém quatro exemplos práticos cobrindo diferentes fluxos do OAuth2/OpenID Connect. Cada item abaixo aponta para o README detalhado do respectivo módulo:

- Fluxo 1 — Client Credentials (machine-to-machine)
  - Resource Server: oauth2-fluxo1/fluxo1-rest-server — valida JWT pelo `issuer-uri` e expõe `/info/token`.
    - README: [oauth2-fluxo1/fluxo1-rest-server/README.md](oauth2-fluxo1/fluxo1-rest-server/README.md)
  - REST Client: oauth2-fluxo1/fluxo1-rest-client — obtém token via Client Credentials e consome o Resource Server.
    - README: [oauth2-fluxo1/fluxo1-rest-client/README.md](oauth2-fluxo1/fluxo1-rest-client/README.md)

- Fluxo 2 — Authorization Code (OIDC) com OAuth2 Login (Web MVC)
  - Web App: oauth2-fluxo2/fluxo2-webapp — autenticação via redirecionamento para o Keycloak, mapeamento de roles e logout OIDC.
    - README: [oauth2-fluxo2/fluxo2-webapp/README.md](oauth2-fluxo2/fluxo2-webapp/README.md)

- Fluxo 3 — Direct Access Grants (password grant)
  - Web App: oauth2-fluxo3/fluxo3-webapp — login por formulário próprio enviando usuário/senha ao Keycloak e mapeando roles.
    - README: [oauth2-fluxo3/fluxo3-webapp/README.md](oauth2-fluxo3/fluxo3-webapp/README.md)

- Fluxo 4 — SPA (Angular) com Public Client
  - Aplicação Angular: oauth2-fluxo4/fluxo4-angular-app — integração como cliente público (não requer secret).
    - README: [oauth2-fluxo4/fluxo4-angular-app/README.md](oauth2-fluxo4/fluxo4-angular-app/README.md)

## Como começar
1. Suba o ambiente do Keycloak: siga [docker_config/README.md](docker_config/README.md)
2. Gere/atualize as secrets dos clients 01–03 no Keycloak e copie para os `application.properties` correspondentes de cada módulo.
3. Acesse o README de cada projeto (links acima) e siga as instruções de execução.


## Estrutura de build (sem agregador Maven)
A partir desta atualização, este repositório NÃO usa mais um POM agregador na raiz. Cada subprojeto é independente:
- Backends Spring Boot (Maven):
  - oauth2-fluxo1/fluxo1-rest-server
  - oauth2-fluxo1/fluxo1-rest-client
  - oauth2-fluxo2/fluxo2-webapp
  - oauth2-fluxo3/fluxo3-webapp
- Frontend Angular (Node/NPM):
  - oauth2-fluxo4/fluxo4-angular-app

Como executar:
- Para cada backend, entre no diretório do módulo e execute via Maven (./mvnw spring-boot:run ou mvnw.cmd ...).
- Para o Angular, entre em oauth2-fluxo4/fluxo4-angular-app e use Node 18+:
  - Instalação: npm install
  - Execução: npm run start (ou ng serve)

Observação: O app Angular (client público) já faz parte do projeto git, mas não é construído por Maven; ele mantém seu próprio fluxo de build com Node/NPM.