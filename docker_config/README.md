# docker_config

Este diretório contém os artefatos (Docker Compose e arquivo de realm) para subir rapidamente uma instância do Keycloak com Postgres já preparada para os exemplos desta repository.

Componentes provisionados:
- Keycloak (quay.io/keycloak/keycloak:latest) em modo dev, porta 8080
- Postgres 15 (porta 5432)
- Import automático de realm a partir de `project-realm.json`

Ao subir os containers, será importado o realm chamado `project-realm`, já contendo os clients:
- `project-client01`
- `project-client02`
- `project-client03`
- `project-client04`

IMPORTANTE sobre client secret:
- Após a subida dos containers, será necessário RECRIAR a secret dos clients `project-client01`, `project-client02` e `project-client03` no Keycloak, e copiar a nova secret para os respectivos projetos desta repo.
- Exceção: o `project-client04` não requer recriação de secret (é um cliente público).

Mapeamento rápido dos clients para os módulos da repo:
- `project-client01` → oauth2-fluxo1/fluxo1-rest-client (Client Credentials)
- `project-client02` → oauth2-fluxo2/fluxo2-webapp (Authorization Code)
- `project-client03` → oauth2-fluxo3/fluxo3-webapp (Direct Access Grants / Password)
- `project-client04` → oauth2-fluxo4/fluxo4-angular-app (SPA público)

Onde colar a nova secret (após recriar no Keycloak):
- fluxo1-rest-client: `oauth2-fluxo1/fluxo1-rest-client/src/main/resources/application.properties`
  - propriedade: `spring.security.oauth2.client.registration.keycloak.client-secret=`
- fluxo2-webapp: `oauth2-fluxo2/fluxo2-webapp/src/main/resources/application.properties`
  - propriedade: `spring.security.oauth2.client.registration.keycloak.client-secret=`
- fluxo3-webapp: `oauth2-fluxo3/fluxo3-webapp/src/main/resources/application.properties`
  - propriedade equivalente ao client-secret do client confidencial (veja README do módulo)
- fluxo4-angular-app (client04): não é necessário secret (cliente público/SPA)

Pré-requisitos
- Docker 20+ e Docker Compose (v2) instalados

Arquivos
- `docker-compose.yml`: define os serviços `keycloak03` e `postgres03`, mapeia `project-realm.json` para import automático.
- `project-realm.json`: export do realm com recursos necessários aos módulos.

Como subir
1. Entre neste diretório: `cd docker_config`
2. Suba os containers em background: `docker compose up -d`
3. Aguarde alguns segundos e acompanhe os logs (opcional): `docker compose logs -f keycloak03`
4. Acesse o Keycloak em: `http://localhost:8080`
5. Faça login no Admin Console:
   - Usuário: `admin`
   - Senha: `xpto`
6. Confirme que o realm `project-realm` foi importado (canto superior esquerdo → lista de realms).
7. Para cada um dos clients `project-client01`, `project-client02` e `project-client03`, gere uma nova secret (Credentials → Regenerate Secret) e atualize nos arquivos de propriedades dos módulos correspondentes (ver "Onde colar a nova secret").

Como derrubar
- Parar containers: `docker compose down`
- Parar e remover volumes (APAGARÁ dados do Postgres): `docker compose down -v`

Detalhes dos serviços
- Keycloak (`keycloak03`)
  - Porta: 8080 (host) → 8080 (container)
  - Admin: `admin` / `xpto`
  - Banco: Postgres via variáveis KC_DB_*
  - Importa o realm de `/opt/keycloak/data/import/realm.json` (mapeado a partir de `./project-realm.json`)
  - Comando: `start-dev --import-realm`
- Postgres (`postgres03`)
  - Porta: 5432 (host) → 5432 (container)
  - DB: `keycloak`, usuário: `admin`, senha: `xpto`

URLs úteis
- Admin Console: `http://localhost:8080/admin/`
- Endereço do Realm (issuer): `http://localhost:8080/realms/project-realm`
- Token endpoint: `${issuer}/protocol/openid-connect/token`

Dicas e troubleshooting
- Porta 8080 ocupada: pare qualquer serviço na mesma porta antes de subir.
- Realm não aparece após subir: aguarde a importação finalizar; verifique os logs do Keycloak com `docker compose logs -f keycloak03`.
- Erro de autenticação nos módulos:
  - Verifique se você regenerou as secrets dos clients 01–03 e atualizou nos respectivos `application.properties`.
  - Confirme as Redirect URIs e tipos de cliente conforme cada fluxo (veja os READMEs dos módulos).
- Resetar o ambiente: `docker compose down -v` e `docker compose up -d` (atenção: apaga dados persistidos no Postgres).

Observação
- O `docker-compose.yml` atual não persiste o volume de dados do Postgres (linhas comentadas). Se desejar persistência entre subidas, habilite a seção de volumes conforme sua necessidade.