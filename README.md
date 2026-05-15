# ServeRest — Automação de Testes de API

Projeto de automação de testes para a API [ServeRest](https://serverest.dev), desenvolvido em Java com REST Assured, JUnit 5 e Allure Report.

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 20 | Linguagem |
| Maven | 3.9.6 | Build e gerenciamento de dependências |
| REST Assured | 5.4.0 | Cliente HTTP e asserções de API |
| JUnit 5 | 5.10.2 | Framework de testes |
| Lombok | 1.18.32 | Builder pattern e redução de boilerplate |
| Jackson | 2.17.0 | Serialização/desserialização JSON |
| AssertJ | 3.25.3 | Soft assertions |
| JavaFaker | 1.0.2 | Geração de dados dinâmicos |
| Allure | 2.27.0 | Relatório de testes |

---

## Pré-requisitos

- Java 11+ ([Download JDK](https://www.oracle.com/java/technologies/downloads/))
- Maven 3.8+ ([Download Maven](https://maven.apache.org/download.cgi))

Verificar instalação:
```bash
java -version
mvn -version
```

---

## Estrutura do Projeto

```
src/test/
├── java/br/com/serverest/
│   ├── config/
│   │   ├── BaseTest.java          # Configuração global do REST Assured + environment.properties
│   │   └── ConfigManager.java     # Leitura de configurações por ambiente
│   ├── constants/
│   │   ├── Endpoints.java         # Rotas da API
│   │   └── Messages.java          # Mensagens de resposta esperadas
│   ├── extensions/
│   │   ├── Retry.java             # Anotação @Retry(n)
│   │   └── RetryExtension.java    # Extensão JUnit 5 para retry automático
│   ├── models/
│   │   ├── Usuario.java
│   │   ├── Produto.java
│   │   ├── Carrinho.java
│   │   ├── ItemCarrinho.java
│   │   ├── LoginRequest.java
│   │   └── LoginResponse.java
│   ├── services/
│   │   ├── LoginService.java      # POST /login
│   │   ├── UsuarioService.java    # CRUD /usuarios
│   │   ├── ProdutoService.java    # CRUD /produtos
│   │   └── CarrinhoService.java   # /carrinhos, concluir, cancelar
│   ├── tests/
│   │   ├── LoginTest.java
│   │   ├── UsuariosTest.java
│   │   ├── ProdutosTest.java
│   │   └── CarrinhosTest.java
│   └── utils/
│       ├── AuthUtils.java         # Criação de usuário + obtenção de token
│       └── DataFactory.java       # Geração de dados de teste com Faker
└── resources/
    ├── config.properties          # Ambiente padrão (dev)
    ├── staging.properties         # Ambiente staging
    ├── allure.properties          # Configuração do Allure
    ├── categories.json            # Classificação de falhas no Allure
    └── schemas/
        ├── usuario-schema.json
        ├── produto-schema.json
        └── lista-usuarios-schema.json
```

---

## Cobertura de Testes

| Suite | Endpoint | Testes | Tags |
|---|---|---|---|
| `LoginTest` | `POST /login` | 4 | smoke, regression |
| `UsuariosTest` | `GET/POST/PUT/DELETE /usuarios` | 14 | smoke, regression |
| `ProdutosTest` | `GET/POST/PUT/DELETE /produtos` | 11 | smoke, regression |
| `CarrinhosTest` | `GET/POST /carrinhos` + concluir/cancelar | 6 | smoke, regression |
| **Total** | | **35+** | |

### O que cada teste valida
- ✅ Status code HTTP
- ✅ Contrato JSON Schema
- ✅ Mensagens de resposta
- ✅ Headers (`Content-Type`)
- ✅ Tempo de resposta (SLA < 2s)
- ✅ Dados retornados (Soft Assertions)
- ✅ Cenários com dados inválidos (`@ParameterizedTest`)

---

## Executando os Testes

### Todos os testes
```bash
mvn test
```

### Por tag
```bash
# Apenas smoke (fluxo principal)
mvn test -Dgroups="smoke"

# Apenas regression (cenários negativos e de borda)
mvn test -Dgroups="regression"

# Suite específica
mvn test -Dtest="UsuariosTest"
```

### Por ambiente
```bash
# Padrão (dev) — usa config.properties
mvn test

# Staging — usa staging.properties
mvn test -Denv=staging
```

---

## Relatório Allure

### Gerar e visualizar
```bash
# Gerar relatório
mvn allure:report

# Visualizar (requer Python)
cd target/site/allure-maven-plugin
python -m http.server 8080
# Acesse: http://localhost:8080
```

### Usando o script utilitário (Windows)
```powershell
.\run.ps1 test        # todos os testes
.\run.ps1 smoke       # só smoke
.\run.ps1 regression  # só regression
.\run.ps1 report      # gera o report
.\run.ps1 serve       # gera + abre no navegador
```

### O que o relatório exibe
- **Overview** — total de testes, taxa de sucesso, tempo de execução
- **Suites** — testes organizados por classe
- **Behaviors** — organizado por Epic → Feature → Teste
- **Categories** — falhas classificadas em: Bug do Produto, Falha de Ambiente, Falha de Contrato, Erro de Automação
- **Environment** — ambiente, URL, versão do Java usados na execução
- **Steps** — cada chamada HTTP com request/response completo

---

## Configuração de Ambientes

Crie um arquivo `{env}.properties` em `src/test/resources/`:

```properties
# src/test/resources/staging.properties
base.url=https://staging.serverest.dev
request.timeout=5000
default.password=Senha@123
```

Execute com:
```bash
mvn test -Denv=staging
```

---

## Padrões do Projeto

### Camada de Service
Toda chamada HTTP está encapsulada nos Services — os testes nunca chamam REST Assured diretamente:

```java
// ✅ Correto — usa o service
produtoService.criar(produto, token).then().statusCode(201);

// ❌ Evitar — chamada direta nos testes
given().spec(requestSpec).body(produto).post("/produtos");
```

### Builder Pattern nos models
```java
Usuario usuario = Usuario.builder()
        .nome("João Silva")
        .email("joao@email.com")
        .password("Senha@123")
        .administrador("true")
        .build();
```

### Testes parametrizados para cenários de borda
```java
@ParameterizedTest(name = "Email inválido ({0}) deve retornar 400")
@MethodSource("emailsInvalidos")
void cadastrarUsuarioEmailInvalido(String descricao, String email) { ... }
```

### Retry para testes instáveis
```java
@Test
@Retry(3)
void testeCritico() { ... }
```

---

## CI/CD

O pipeline `.github/workflows/tests.yml` executa automaticamente em:
- Push nas branches `main` e `develop`
- Pull Requests para `main`
- Agendamento: segunda a sexta às 06h UTC

**Fluxo:**
```
Push/PR
  └── Smoke Tests
        └── Regression Tests
              └── Publicar Allure Report (GitHub Pages)
```

---

## Contribuindo

1. Crie uma branch: `git checkout -b feat/nome-da-feature`
2. Implemente o teste seguindo os padrões do projeto
3. Rode `mvn test -Dgroups="smoke"` antes de abrir PR
4. Abra o PR apontando para `develop`
