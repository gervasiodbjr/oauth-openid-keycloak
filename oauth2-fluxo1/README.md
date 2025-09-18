# oauth2-fluxo1 — Como usar o arquivo testes.http

Este diretório contém dois projetos que demonstram o fluxo OAuth2 Client Credentials usando o Keycloak:
- fluxo1-rest-server (Resource Server, porta padrão 9000)
- fluxo1-rest-client (REST Client, porta padrão 9001)

Para facilitar os testes, há um arquivo de requisições HTTP em: oauth2-fluxo1/testes.http. Ele permite validar rapidamente os dois serviços usando o HTTP Client do IntelliJ IDEA/JetBrains.

## Pré-requisitos
1. Suba o ambiente do Keycloak via docker_config (pré-requisito geral do repositório):
   - Siga: docker_config/README.md
   - Após a subida, REGENERE a secret do client project-client01 e atualize em:
     - oauth2-fluxo1/fluxo1-rest-client/src/main/resources/application.properties
       - spring.security.oauth2.client.registration.keycloak.client-secret=
2. Compile/execute os módulos deste diretório:
   - Servidor (Resource Server): oauth2-fluxo1/fluxo1-rest-server
     - Porta: 9000 (config em application.properties)
   - Cliente (REST Client): oauth2-fluxo1/fluxo1-rest-client
     - Porta: 9001 (config em application.properties)

Observação: os endpoints esperam o Keycloak acessível em http://localhost:8080 e realm project-realm, conforme as propriedades padrão dos módulos e o ambiente docker_config.

## Abrindo e executando o testes.http
Arquivo: oauth2-fluxo1/testes.http

Conteúdo do arquivo:

```
### GET Rest Server Not Authorized Getting Token Info [ HTTP/1.1 401 - Unauthorized ]
GET /info/token 
Host: localhost:9000

### GET Rest Client Getting Token Info From Rest Server [ HTTP/1.1 200 - OK ]
GET /client/info/token
Host: localhost:9001
```

Como executar no IntelliJ IDEA (ou IDE JetBrains com HTTP Client):
1. No projeto, navegue até oauth2-fluxo1/testes.http e abra o arquivo.
2. Inicie os dois aplicativos:
   - fluxo1-rest-server (porta 9000)
   - fluxo1-rest-client (porta 9001)
3. No arquivo testes.http, passe o mouse sobre cada requisição e clique no ícone Run (▶) exibido pela IDE.
   - Alternativamente, posicione o cursor na linha do método (GET ...) e use o atalho de execução sugerido pela IDE.

## O que cada requisição valida
1) GET http://localhost:9000/info/token — esperado 401 Unauthorized
   - Chamada direta ao Resource Server, sem enviar Bearer Token. Serve para mostrar que o endpoint está protegido.

2) GET http://localhost:9001/client/info/token — esperado 200 OK
   - Chama o endpoint do cliente, que por sua vez:
     - Obtém um access token no Keycloak usando Client Credentials (client project-client01).
     - Faz a chamada ao Resource Server em http://localhost:9000/info/token com o header Authorization adequado.
     - Retorna o corpo de resposta do servidor (texto contendo o token e suas claims), útil para depuração.

## Problemas comuns e soluções
- 401 no passo 2 (cliente → servidor):
  - Confirme o client-id/secret em fluxo1-rest-client/application.properties.
  - Verifique o token-uri e a conectividade com o Keycloak (localhost:8080).
  - Garanta que o Resource Server está executando na porta 9000 e que o issuer-uri aponta para o realm correto.

- Erro ao subir os apps:
  - Garanta JDK 21 e Maven instalados.
  - Rode via Maven: ./mvnw spring-boot:run (Linux/Mac) ou mvnw.cmd spring-boot:run (Windows) dentro de cada módulo.

- Keycloak não importado/realm ausente:
  - Revise docker_config/README.md e os logs do container do Keycloak.

## Comandos cURL equivalentes (opcional)
Caso não utilize a IDE com HTTP Client, você pode simular os testes com cURL:
- Requisição 1 (deve retornar 401):
  curl -i http://localhost:9000/info/token

- Requisição 2 (via cliente — apenas para verificar se está de pé):
  curl -i http://localhost:9001/client/info/token

Para testar o servidor diretamente com Bearer Token, obtenha manualmente um token via Client Credentials e envie no header Authorization (não é necessário quando se usa o cliente).