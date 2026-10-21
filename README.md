# Simba DJ — Backend

API REST do **Simba DJ**, uma plataforma para divulgação de produtores musicais e DJs, desenvolvida com Java 21 e Spring Boot. O backend centraliza a autenticação, a segurança, o gerenciamento de conteúdo e a disponibilização de perfis públicos de artistas.

A aplicação utiliza PostgreSQL para persistência de dados, Spring Security com JWT para autenticação e autorização, Flyway para versionamento do banco de dados e Docker para execução em ambiente containerizado.

## Sobre o projeto

O Simba DJ surgiu com o objetivo de oferecer a produtores musicais uma plataforma própria para divulgar seus trabalhos, reunindo músicas, vídeos, fotos, eventos e links externos em um único perfil público, acessível por meio de um shortlink.

O backend foi desenvolvido para atender tanto às operações administrativas e autenticadas quanto à consulta pública dos perfis dos artistas.

O projeto contempla regras de negócio, persistência relacional, validação de dados, tratamento de exceções, autenticação e integração com serviços de e-mail.

## Funcionalidades

- **Autenticação e segurança:** cadastro, login e autorização de requisições utilizando JWT.
- **Gerenciamento de usuários:** consulta dos dados do usuário autenticado e controle de acesso.
- **Verificação de e-mail:** envio e validação de tokens para confirmação do endereço de e-mail.
- **Recuperação de senha:** solicitação e redefinição de senha.
- **Perfis de artistas:** criação, consulta e atualização de perfis.
- **Gerenciamento de músicas:** cadastro e manutenção de músicas e links para plataformas de streaming.
- **Gerenciamento de vídeos:** organização de vídeos e links externos.
- **Gerenciamento de fotos:** cadastro e armazenamento de imagens.
- **Gerenciamento de eventos:** publicação de eventos e links para ingressos.
- **Perfil público:** consulta dos dados e conteúdos do produtor por shortlink.
- **Upload de mídias:** armazenamento local de arquivos enviados à aplicação.

## Tecnologias utilizadas

| Tecnologia | Aplicação no projeto |
| --- | --- |
| Java 21 | Linguagem de programação |
| Spring Boot 3.5.x | Estrutura principal da aplicação |
| Spring Web | Desenvolvimento dos endpoints REST |
| Spring Security | Autenticação e autorização |
| JWT | Autenticação baseada em tokens |
| Spring Data JPA | Persistência e acesso a dados |
| Hibernate | Mapeamento objeto-relacional |
| PostgreSQL | Banco de dados relacional |
| Flyway | Versionamento do schema do banco |
| Spring Validation | Validação de dados de entrada |
| Spring Mail | Envio de e-mails transacionais |
| Spring Cache | Recursos de cache |
| Swagger / OpenAPI | Documentação interativa da API |
| Thymeleaf | Recursos de templates do projeto |
| Maven | Gerenciamento de dependências e build |
| Docker | Execução em ambiente containerizado |

## Arquitetura

O backend segue uma arquitetura em camadas, com separação de responsabilidades entre a interface HTTP, as regras de negócio e a persistência de dados.

```text
src/main/java/com/icaro
├── config/       # Configurações e infraestrutura
├── controller/   # Endpoints e requisições HTTP
├── dto/          # Objetos de entrada e saída
├── exception/    # Exceções e tratamento de erros
├── mapper/       # Conversão entre entidades e DTOs
├── model/        # Entidades e modelos do domínio
├── repository/   # Acesso e persistência de dados
└── service/      # Regras de negócio
```

### Responsabilidades das camadas

- **Controller:** recebe as requisições HTTP e coordena as respostas da API.
- **Service:** concentra as regras de negócio e os fluxos da aplicação.
- **Repository:** abstrai as operações de persistência por meio do Spring Data JPA.
- **Model:** representa as entidades e os conceitos do domínio.
- **DTO:** define os dados utilizados na comunicação com os clientes da API.
- **Mapper:** centraliza as conversões entre objetos do domínio e DTOs.
- **Exception:** organiza o tratamento de erros e as respostas de falha.
- **Config:** reúne configurações de segurança, JWT, CORS e outros componentes de infraestrutura.

Essa organização facilita a localização das responsabilidades e a manutenção dos diferentes recursos da aplicação.

## Principais recursos da API

| Recurso | Rota base | Responsabilidade |
| --- | --- | --- |
| Usuários e autenticação | `/simba` | Cadastro, login e dados do usuário |
| Perfis | `/profile` | Criação, consulta e atualização de perfis |
| Músicas | `/song` | Gerenciamento de músicas e links externos |
| Vídeos | `/video` | Gerenciamento de vídeos |
| Fotos | `/photo` | Gerenciamento de fotos |
| Eventos | `/event` | Gerenciamento de eventos e ingressos |
| Recuperação de senha | `/password/reset` | Solicitação e redefinição de senha |
| Verificação de e-mail | `/mail/api` | Fluxos de verificação de e-mail |
| Perfil público | `/public/producer` | Consulta pública de produtores por shortlink |

> As rotas acima representam os recursos principais. Os métodos HTTP, os parâmetros e as rotas específicas podem ser consultados na documentação OpenAPI.

### Autenticação

Os endpoints protegidos utilizam JWT para autenticação. Após obter um token válido, o cliente deve enviá-lo no cabeçalho `Authorization`:

```http
Authorization: Bearer <token>
```

Os endpoints públicos, como os destinados à consulta dos perfis de artistas, podem ser acessados sem autenticação, conforme as regras de segurança configuradas na aplicação.

## Pré-requisitos

Para executar o projeto localmente, você precisará de:

- Git
- Docker
- Docker Compose
- Credenciais e variáveis de ambiente necessárias para a aplicação

Java 21 e Maven são úteis para executar e desenvolver o backend diretamente na máquina. Quando a imagem Docker já inclui o ambiente necessário para a aplicação, não é preciso instalar o Java ou o Maven apenas para iniciar o container.

## Executando com Docker

A aplicação é executada em ambiente containerizado, facilitando a configuração e a inicialização do ambiente de desenvolvimento.

### 1. Clone o repositório

```bash
git clone https://github.com/icaroLins/artist-Simba-api.git
cd artist-Simba-api
```

### 2. Configure as variáveis de ambiente

Configure as variáveis exigidas pela aplicação no ambiente Docker. Se o projeto utiliza um arquivo `.env`, preencha-o localmente conforme a configuração do Compose.

| Variável | Finalidade |
| --- | --- |
| `DB_URL` | URL de conexão com o PostgreSQL |
| `DB_USERNAME` | Usuário do banco de dados |
| `DB_PASSWORD` | Senha do banco de dados |
| `JWT_SECRET` | Chave de assinatura dos tokens JWT |
| `MAIL_USERNAME` | Conta utilizada para envio de e-mails |
| `MAIL_PASSWORD` | Senha ou senha de aplicativo da conta de e-mail |

Configure também a URL do frontend utilizada nos links enviados por e-mail:

```properties
app.frontend-url=http://localhost:5173
```

Os nomes das variáveis devem corresponder aos definidos no projeto e no arquivo de configuração do Docker.

**Importante:** não versione arquivos `.env` com credenciais reais. Mantenha um `.env.example` com os nomes das variáveis e valores ilustrativos, sem segredos.

### 3. Inicie a aplicação

Se o repositório contiver um `compose.yaml` ou `docker-compose.yml` configurado para a aplicação e suas dependências, execute na raiz do projeto:

```bash
docker compose up --build -d
```

O comando solicita a construção das imagens necessárias e inicia os serviços em segundo plano.

Para acompanhar os logs:

```bash
docker compose logs -f
```

Para verificar os containers em execução:

```bash
docker compose ps
```

Para encerrar os serviços:

```bash
docker compose down
```

A inicialização do PostgreSQL, a configuração das variáveis e o mapeamento de portas devem seguir o arquivo Compose do repositório.

### 4. Acesse a API

Com a aplicação iniciada e a porta padrão do Spring Boot publicada pelo Docker, a API estará disponível em:

```text
http://localhost:8080
```

A porta efetivamente disponível depende do mapeamento definido no Docker Compose.

## Documentação da API

A aplicação utiliza Swagger/OpenAPI para documentar os endpoints e permitir a exploração interativa das operações disponíveis.

Com o backend em execução, acesse:

[Swagger UI — http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Caso a documentação esteja habilitada no ambiente atual, será possível consultar os recursos, os parâmetros, os formatos de resposta e os requisitos de autenticação.

## Banco de dados e migrações

O PostgreSQL é utilizado para persistência dos dados, enquanto o Flyway gerencia as migrações e a evolução do schema.

As migrações devem ser versionadas no diretório:

```text
src/main/resources/db/migration/
```

Cada alteração estrutural deve ser registrada em uma nova migração, preservando o histórico de mudanças do banco.

Em ambientes de produção, recomenda-se utilizar o Flyway como mecanismo de evolução do schema e configurar o Hibernate com `ddl-auto=validate`, evitando alterações estruturais automáticas no banco.

## Upload e armazenamento de arquivos

As mídias enviadas à aplicação são armazenadas localmente no diretório:

```text
uploads/
```

Como a aplicação é executada em Docker, é importante configurar um volume persistente para esse diretório caso os arquivos precisem sobreviver à recriação ou substituição do container.

Para um ambiente de produção, também pode ser considerada a adoção de armazenamento de objetos, dependendo dos requisitos de disponibilidade, escalabilidade e operação da plataforma.

## Testes

Os testes automatizados podem ser executados pelo Maven Wrapper, caso ele esteja disponível no repositório.

**Windows:**

```powershell
.\mvnw.cmd test
```

**Linux ou macOS:**

```bash
./mvnw test
```

Os testes ajudam a verificar o comportamento dos componentes e a reduzir regressões durante a evolução do sistema.

## Segurança e configuração

O projeto utiliza Spring Security e JWT para proteger os recursos autenticados. As credenciais de banco de dados, as chaves de assinatura e as credenciais de e-mail devem ser fornecidas por variáveis de ambiente ou por um mecanismo de gerenciamento de segredos apropriado.

Antes de disponibilizar o sistema publicamente, é importante revisar:

- a validade e a configuração da assinatura dos tokens JWT;
- as regras de autorização de cada endpoint;
- a proteção dos fluxos de verificação de e-mail e recuperação de senha;
- a validação dos arquivos enviados e os limites de upload;
- a configuração de CORS e HTTPS;
- a persistência dos dados e dos arquivos;
- o gerenciamento seguro de credenciais nos ambientes de execução.

## Status do projeto

**Em desenvolvimento.**

O Simba DJ possui uma base de backend para autenticação, gerenciamento de conteúdo e disponibilização de perfis públicos, com execução em ambiente Docker.

A evolução do projeto contempla melhorias de segurança, testes, configuração de infraestrutura e preparação para disponibilização pública.

## Repositórios

- **Backend:** [artist-Simba-api](https://github.com/icaroLins/artist-Simba-api)
- **Frontend:** [artist-Simba-Frontend](https://github.com/icaroLins/artist-Simba-Frontend)

## Licença

O projeto ainda não possui uma licença definida. Os termos de utilização e distribuição deverão ser estabelecidos em um arquivo `LICENSE`.
