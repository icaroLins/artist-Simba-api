# Simba DJ — Backend

API REST do ecossistema **Simba DJ**, desenvolvida com Java e Spring Boot. O backend concentra autenticação, segurança, gerenciamento de usuários e perfis, publicação de conteúdos e os fluxos de verificação de e-mail e recuperação de senha.

## Sobre o projeto

O Simba DJ é uma plataforma para divulgação do trabalho de produtores musicais e DJs. Por meio dela, o produtor pode reunir músicas, vídeos, fotos, eventos e links externos em um perfil público acessível por shortlink.

Este repositório contém a camada de backend da aplicação, construída com foco em:

- organização das APIs por domínio;
- autenticação e autorização com JWT;
- persistência de dados com PostgreSQL e JPA;
- versionamento do banco de dados com Flyway;
- validação dos dados de entrada;
- tratamento centralizado de exceções;
- documentação da API com Swagger/OpenAPI;
- preparação para containerização e hospedagem em nuvem.

## Tecnologias utilizadas

- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Mail
- Spring Cache
- Flyway
- PostgreSQL
- JWT
- Swagger / OpenAPI
- Thymeleaf
- Maven

## Funcionalidades

- Cadastro e autenticação de usuários
- Consulta dos dados do usuário autenticado
- Verificação de endereço de e-mail
- Recuperação e redefinição de senha
- Criação e gerenciamento de perfis
- Gerenciamento de músicas e links para plataformas de streaming
- Gerenciamento de vídeos
- Gerenciamento de fotos
- Gerenciamento de eventos e links para ingressos
- Acesso público ao perfil do produtor por shortlink
- Upload e armazenamento local de arquivos de mídia

## Principais recursos da API

| Recurso | Rota base | Responsabilidade |
| --- | --- | --- |
| Usuários e autenticação | `/simba` | Cadastro, login e dados do usuário |
| Perfis | `/profile` | Criação, consulta e atualização de perfis |
| Músicas | `/song` | Gerenciamento de músicas e links externos |
| Vídeos | `/video` | Gerenciamento de vídeos |
| Fotos | `/photo` | Gerenciamento de fotos |
| Eventos | `/event` | Gerenciamento de eventos e ingressos |
| Recuperação de senha | `/password/reset` | Solicitação e conclusão da redefinição de senha |
| Verificação de e-mail | `/mail/api` | Envio e validação de tokens de verificação |
| Página pública | `/public/producer` | Consulta pública do produtor por shortlink |

> Os endpoints protegidos exigem um token JWT válido no cabeçalho `Authorization`, utilizando o formato `Bearer <token>`.

## Arquitetura

O projeto utiliza uma arquitetura em camadas, separando as responsabilidades entre:

```text
src/main/java
├── config       # Configurações de segurança, JWT, CORS e infraestrutura
├── controller   # Recebimento das requisições HTTP
├── dto          # Objetos de entrada e saída da API
├── exception    # Exceções e tratamento global de erros
├── mapper       # Conversão entre entidades e DTOs
├── model        # Entidades e modelos do domínio
├── repository   # Acesso e persistência dos dados
└── service      # Regras de negócio da aplicação
```

Essa separação facilita a manutenção, os testes e a evolução independente dos diferentes domínios do sistema.

## Pré-requisitos

Antes de executar o projeto, instale ou configure:

- Java 21
- PostgreSQL
- Maven 3.9+ ou o Maven Wrapper incluído no repositório
- Variáveis de ambiente da aplicação
- Docker, opcionalmente, para executar o banco de dados local

## Configuração do ambiente

Defina as seguintes variáveis de ambiente:

| Variável | Descrição |
| --- | --- |
| `DB_URL` | URL de conexão com o PostgreSQL |
| `DB_USERNAME` | Usuário do banco de dados |
| `DB_PASSWORD` | Senha do banco de dados |
| `JWT_SECRET` | Chave utilizada para assinatura dos tokens JWT |
| `MAIL_USERNAME` | Usuário da conta responsável pelo envio de e-mails |
| `MAIL_PASSWORD` | Senha ou senha de aplicativo da conta de e-mail |

A URL do frontend, utilizada nos links enviados por e-mail, também deve ser configurada:

```properties
app.frontend-url=http://localhost:5173
```

> Nunca envie senhas, chaves JWT ou outras credenciais reais para o repositório.

## Execução local

### 1. Inicie o banco de dados

Caso o projeto possua um arquivo `compose.yaml`, o PostgreSQL pode ser iniciado com:

```bash
docker compose up -d
```

Também é possível utilizar uma instalação local do PostgreSQL.

### 2. Configure as variáveis de ambiente

Garanta que todas as variáveis obrigatórias estejam disponíveis no ambiente antes de iniciar a aplicação.

### 3. Execute a aplicação

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Após a inicialização, a API ficará disponível na porta configurada no Spring Boot. Na configuração padrão:

```text
http://localhost:8080
```

## Documentação da API

Com a aplicação em execução, acesse a interface do Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

Caso o Spring Boot Actuator esteja habilitado, o estado da aplicação poderá ser consultado em:

```text
http://localhost:8080/actuator/health
```

## Testes

Para executar os testes automatizados:

No Linux ou macOS:

```bash
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```

## Banco de dados e migrações

O projeto utiliza PostgreSQL como banco de dados relacional e Flyway para controlar a evolução do schema.

As novas alterações estruturais devem ser adicionadas como migrações versionadas no diretório:

```text
src/main/resources/db/migration
```

Durante o desenvolvimento, a configuração `spring.jpa.hibernate.ddl-auto=update` pode auxiliar na evolução local. Para ambientes de homologação e produção, recomenda-se controlar o schema exclusivamente pelo Flyway e utilizar `ddl-auto=validate`.

## Upload de arquivos

Atualmente, imagens e outras mídias são armazenadas localmente no diretório:

```text
uploads/
```

Esse modelo é adequado para o ambiente de desenvolvimento. Em produção, o recomendado é utilizar armazenamento persistente, como um volume externo ou serviço de armazenamento de objetos.

## Containerização e deploy

O projeto está preparado para avançar para a etapa de containerização e publicação em nuvem. As próximas melhorias previstas incluem:

- criação do `Dockerfile` da aplicação;
- configuração do ambiente com Docker Compose;
- definição segura das variáveis de ambiente no provedor;
- utilização de armazenamento persistente para os uploads;
- publicação da API em um ambiente de produção;
- inclusão do endereço público da API nesta documentação.

## Status do projeto

🚧 **Em desenvolvimento ativo.**

O backend possui uma base funcional para autenticação, gerenciamento de conteúdo e disponibilização do perfil público, mas continua recebendo melhorias antes da publicação em produção.

## Licença

Este projeto ainda não possui uma licença definida. Caso o repositório seja disponibilizado publicamente, adicione um arquivo `LICENSE` com os termos escolhidos.
