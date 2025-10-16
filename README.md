# 🏦 Projeto Bancário Modular
Este repositório contém um sistema bancário modular completo, dividido em micro-serviços Java Spring Boot, um front-end em Angular e banco de dados PostgreSQL. O objetivo é demonstrar boas práticas de desenvolvimento full-stack: segurança, testes, CI/CD e deploy.

---
## Mídia

![Imagem 1](./assets/images/1.png)
![Imagem 2](./assets/images/2.png)
![Imagem 3](./assets/images/3.png)
![Imagem 4](./assets/images/4.png)
![Imagem 5](./assets/images/5.png)
![Imagem 6](./assets/images/6.png)

## Estrutura do Projeto
- **auth-service/**: serviço de autenticação (JWT, Spring Security) 🔒
- **user-service/**: gerenciamento de perfis de usuário 👥
- **account-service/**: CRUD de contas bancárias 💳
- **transaction-service/**: operações financeiras (depósito, saque, transferência, agendamento) 💰
- **notification-service/**: envio de notificações via eventos (RabbitMQ/Kafka) 📧
- **api-gateway/** (opcional): roteamento e segurança centralizada 🌐
- **frontend/**: aplicação Angular para consumo das APIs e exibição de dashboards 📊
- **docker-compose.yml**: orquestração de containers locais (PostgreSQL, RabbitMQ, serviços) 🐳

---

## 🚀 Funcionalidades Implementadas

- **Configuração de Segurança** com Spring Security e JWT
   - Autenticação stateless (Bearer Token)
   - Controle de acesso por roles: `ADMIN`, `GERENTE`, `CLIENTE`
- **Endpoints de Auth** (`/api/auth`)
   - `POST /register`: cadastro de usuário com hashing de senha (BCrypt)
   - `POST /login`: geração de token JWT
   - `GET /me`: retorno de informações do usuário autenticado
- **Filtros de Autenticação**
   - `JwtAuthenticationFilter`: intercepta requisições, valida token e popula contexto de segurança
- **Controllers de Acesso**
   - `AdminController` (`/api/admin`): dashboards e relatórios para ADMIN e GERENTE
   - `GerenteController` (`/api/gerente`): dashboards e relatórios para GERENTE
   - `ClienteController` (`/api/cliente`): endpoints públicos de cliente
   - `TestController` (`/api/test/hello`): endpoint de teste protegido

---
## 🛠 Tecnologias
- **Back-end**: Java 17, Spring Boot, Spring Data JPA, Spring Security, JWT
- **Front-end**: Angular 14+, TypeScript, RxJS, Angular Material (ou Tailwind)
- **Banco de Dados**: PostgreSQL
- **Mensageria**: RabbitMQ (ou Kafka)
- **CI/CD**: GitHub Actions (build, test, Docker)
- **Deploy**: Docker Compose (desenvolvimento)

---

## ⚙️ Como Executar Localmente
1. Clone o repositório:
   ```bash
   git clone https://github.com/seuusuario/meu-projeto-bancario.git
   cd meu-projeto-bancario
   ```
2. Ajuste variáveis de ambiente no arquivo `.env` (na raiz e em cada serviço):
   ```dotenv
   JWT_SECRET=seuSegredoSuperSecreto
   JWT_EXPIRATION=86400000
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/meuprojeto
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=senha
   ```
3. Levante containers de banco e mensageria:
   ```bash
   docker-compose up -d
   ```
4. Inicie cada micro-serviço (auth, user, account, transaction, notification):
   ```bash
   mvn spring-boot:run -f auth-service/pom.xml
   mvn spring-boot:run -f user-service/pom.xml
   # ...
   ```
5. No diretório `frontend/`, instale e inicie Angular:
   ```bash
   cd frontend
   npm install
   ng serve --open
   ```
6. Acesse `http://localhost:4200` e utilize o sistema.

---

## 🧪 Testes
- **Back-end**: JUnit 5 + Mockito para testes unitários; `mvn test`
- **Front-end**: Jasmine + Karma; `ng test`

