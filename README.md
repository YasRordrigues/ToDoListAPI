# API de Lista de Tarefas (ToDo List)

## Visão Geral

Este projeto é uma aplicação web reativa para gestão de uma lista de tarefas. Utiliza o Spring Boot com R2DBC para acesso reativo ao banco de dados e o Spring WebFlux para manipular dados de forma assíncrona e não bloqueante. A API permite criar, recuperar, atualizar e excluir tarefas, com ênfase em operações reativas e não bloqueantes.

## Documentação da API

A documentação da API é gerada automaticamente usando o Springdoc OpenAPI. A documentação interativa da API, fornecida pela Swagger UI, pode ser acessada em `http://localhost:8080/swagger-doc/swagger-ui.html` quando o servidor está em execução.

![Captura de Tela do Swagger UI](assets/swagger.png)

Além da Swagger UI, uma coleção do Insomnia também é fornecida para facilitar os testes:

![Captura de Tela do Cliente REST Insomnia](assets/insomnia.png)

## Pilha de Tecnologia

- **Spring Boot**: Framework para criação de aplicações Spring independentes e de nível de produção.
- **R2DBC**: Conectividade de Banco de Dados Relacional Reativo para acesso escalável e orientado a eventos ao banco de dados.
- **Spring WebFlux**: Framework web para pilha reativa, permitindo aplicações web assíncronas e não bloqueantes.
- **PostgreSQL**: Banco de dados relacional de código aberto.
- **Docker**: Plataforma para desenvolver, enviar e executar aplicações em contêineres isolados.

## Executando a Aplicação

Duas configurações do Docker Compose são fornecidas com este projeto:

1. **Produção**: Configuração padrão do Docker Compose.
    - Para executar a aplicação usando esta configuração, execute o comando:
      ```sh
      docker-compose up
      ```

2. **Desenvolvimento**: Configuração do Docker Compose adaptada para fins de desenvolvimento.
    - Para executar a aplicação usando a configuração de desenvolvimento, utilize o comando:
      ```sh
      docker-compose -f docker-compose-dev.yaml up --build
      ```

O arquivo Docker Compose de desenvolvimento constrói a imagem do zero, garantindo que quaisquer alterações locais no código-fonte sejam incluídas no ambiente conteinerizado.

## Detalhes da Configuração

A aplicação está configurada para rodar na porta `8080`. A URL do R2DBC, nome de usuário e senha são definidos para acesso ao banco de dados PostgreSQL. Esses valores são especificados no arquivo `application.yaml` para a configuração do Spring e substituídos pelas variáveis de ambiente nos arquivos Docker Compose quando executados em um ambiente conteinerizado.

O `docker-compose.yaml` configura dois serviços principais:

- `todolist-application`: O serviço principal da aplicação.
- `todolist-database`: Um serviço de banco de dados PostgreSQL.

## Volumes

Os dados do banco de dados PostgreSQL são persistidos usando um volume Docker chamado `postgres_data`. Isso garante que o estado do banco de dados seja mantido entre reinícios dos contêineres.

## Log

O log está configurado para saída no nível INFO para as classes do Spring Framework. Essa configuração pode ser ajustada conforme necessário no arquivo `application.yaml`.

## Executando os Testes

Para garantir a qualidade e funcionalidade da API de Lista de Tarefas, uma suíte abrangente de testes foi incluída. Estes testes cobrem uma ampla gama de cenários, desde testes unitários que validam componentes individuais até testes de integração que garantem que todo o sistema funcione conforme esperado.

### Executar os Testes

Para rodar os testes, você pode usar o seguinte comando, caso esteja utilizando o Maven:

```sh
./mvnw test
```

Para limpar e instalar as dependências, ignorando os testes:

```sh
`./mvnw clean install -DskipTests`
```

### Cobertura de Testes

Os testes foram projetados para cobrir casos de sucesso e falha, garantindo que:

*   As tarefas sejam criadas corretamente com dados de entrada válidos.
*   O tratamento de erros esteja em vigor para tarefas com datas de expira