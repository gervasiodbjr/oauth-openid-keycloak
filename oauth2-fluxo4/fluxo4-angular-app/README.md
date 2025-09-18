# fluxo4-angular-app

Aplicação Angular (standalone components, Angular 20) demonstrando autenticação e autorização com Keycloak. Faz parte do conjunto de exemplos do diretório `oauth2-fluxo4` desta repo.

## Visão geral
- Login via Keycloak (keycloak-js)
- Proteção de rotas com guard simples e checagem de roles (realm roles)
- Páginas de exemplo:
  - Home (pública)
  - User (requer role USER ou ADMIN)
  - Admin (requer role ADMIN)
  - Unautorized (exibida quando o usuário não tem permissão)

## Requisitos
- Node.js 18+ (recomendado 20+)
- npm 9+
- Keycloak em execução e acessível pelo navegador/servidor de dev

## Configuração do Keycloak
A configuração padrão está em `src/app/keycloak.service.ts`:
```ts
new Keycloak({
  url: 'http://localhost:8080/',
  realm: 'project-realm',
  clientId: 'project-client04',
});
```
Ajuste estes valores conforme o seu ambiente.

No Keycloak, crie/configure:
- Realm: `project-realm`
- Client (Public): `project-client04`
  - Permitir redirect para: `http://localhost:4200/*`
- Realm Roles: `USER`, `ADMIN`
- Atribua as roles aos usuários de teste

## Instalação e execução
1. Instale as dependências:
   - `npm install`
2. Rode a aplicação em modo dev:
   - `npm start`
3. Acesse em: http://localhost:4200

Ao acessar uma rota protegida sem estar autenticado, o fluxo de login do Keycloak será iniciado automaticamente.

## Rotas e autorização
- `/` Home (pública)
- `/user` requer uma das roles: `USER` ou `ADMIN`
- `/admin` requer a role: `ADMIN`
- `/unautorized` página para acesso negado

A lógica do guard está em `src/app/app.routes.ts` (função `authGuard`). A verificação de roles utiliza `KeycloakService.hasRole(role)` que checa roles de realm.

## Scripts úteis
- `npm start` — inicia o servidor de desenvolvimento (Angular CLI)
- `npm run build` — compila para produção
- `npm test` — executa testes (Karma/Jasmine)

## Estrutura relevante
- `src/app/keycloak.service.ts` — inicialização e helpers do Keycloak
- `src/app/app.routes.ts` — rotas + guard com roles
- `src/app/pages/*` — componentes de página (Home, User, Admin, Unautorized)
- `src/app/app.config.ts` — providers e router

## Dicas e solução de problemas
- Erro de CORS ou redirecionamento: verifique os Redirect URIs do client no Keycloak e a URL (`http://localhost:4200/*`).
- Não faz login/timeout: confirme o `url`, `realm` e `clientId` no `KeycloakService` e se o Keycloak está acessível na rede.
- Roles não reconhecidas: certifique-se de estar usando roles de realm (não de client) ou ajuste o método `hasRole` conforme sua necessidade.
- Página fica em branco após login: veja o console do navegador. O projeto habilita `provideBrowserGlobalErrorListeners()` em `app.config.ts` para logs de erros globais.



