package br.com.serverest.tests;

import br.com.serverest.config.BaseTest;
import br.com.serverest.constants.Messages;
import br.com.serverest.models.Usuario;
import br.com.serverest.services.UsuarioService;
import br.com.serverest.utils.DataFactory;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@Epic("Usuários")
@Feature("CRUD Usuários")
@Tag("usuarios")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuariosTest extends BaseTest {

    private UsuarioService usuarioService;
    private final List<String> idsParaLimpar = new CopyOnWriteArrayList<>();

    @BeforeAll
    void inicializar() {
        usuarioService = new UsuarioService(requestSpec);
    }

    @AfterAll
    void limparDados() {
        idsParaLimpar.forEach(id -> usuarioService.deletar(id));
    }

    static Stream<Arguments> camposObrigatoriosAusentes() {
        return Stream.of(
            Arguments.of("nome ausente",          "", "a@b.com",             "Senha@123", "true"),
            Arguments.of("email ausente",         "João", "",                "Senha@123", "true"),
            Arguments.of("password ausente",      "João", "a@b.com",         "",          "true"),
            Arguments.of("administrador ausente", "João", "a@b.com",         "Senha@123", "")
        );
    }

    static Stream<Arguments> emailsInvalidos() {
        return Stream.of(
            Arguments.of("sem @",        "emailsemarroba.com"),
            Arguments.of("sem domínio",  "email@"),
            Arguments.of("espaço",       "email invalido@b.com")
        );
    }

    @Test
    @Tag("smoke")
    @DisplayName("Listar usuários deve retornar 200, contrato e headers corretos")
    @Description("GET /usuarios valida status, JSON Schema e Content-Type")
    @Severity(SeverityLevel.NORMAL)
    void listarUsuarios() {
        usuarioService.listar()
                .then()
                .statusCode(200)
                .time(lessThan(2000L))
                .header("Content-Type", containsString("application/json"))
                .body(matchesJsonSchemaInClasspath("schemas/lista-usuarios-schema.json"))
                .body("quantidade", greaterThanOrEqualTo(0));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Cadastrar usuário administrador deve retornar 201 e contrato válido")
    @Severity(SeverityLevel.CRITICAL)
    void cadastrarUsuarioAdmin() {
        Usuario novoUsuario = DataFactory.usuarioAdminValido();

        String id = usuarioService.criar(novoUsuario)
                .then()
                .statusCode(201)
                .time(lessThan(2000L))
                .header("Content-Type", containsString("application/json"))
                .body("message", equalTo(Messages.USUARIO_CADASTRADO))
                .body("_id", notNullValue())
                .extract().path("_id");

        idsParaLimpar.add(id);
    }

    @Test
    @Tag("regression")
    @DisplayName("Cadastrar usuário com email duplicado deve retornar 400")
    @Severity(SeverityLevel.NORMAL)
    void cadastrarUsuarioEmailDuplicado() {
        Usuario usuario = DataFactory.usuarioAdminValido();
        String id = usuarioService.criarEObterID(usuario);
        idsParaLimpar.add(id);

        usuarioService.criar(usuario)
                .then()
                .statusCode(400)
                .body("message", equalTo(Messages.USUARIO_EMAIL_USADO));
    }

    @ParameterizedTest(name = "Cadastrar com {0} deve retornar 400")
    @MethodSource("camposObrigatoriosAusentes")
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    void cadastrarUsuarioComCampoAusente(String descricao, String nome, String email, String password, String admin) {
        Usuario usuario = Usuario.builder()
                .nome(nome.isEmpty() ? null : nome)
                .email(email.isEmpty() ? null : email)
                .password(password.isEmpty() ? null : password)
                .administrador(admin.isEmpty() ? null : admin)
                .build();

        usuarioService.criar(usuario)
                .then()
                .statusCode(400);
    }

    @ParameterizedTest(name = "Email inválido ({0}) deve retornar 400")
    @MethodSource("emailsInvalidos")
    @Tag("regression")
    @Severity(SeverityLevel.MINOR)
    void cadastrarUsuarioEmailInvalido(String descricao, String email) {
        Usuario usuario = Usuario.builder()
                .nome("Teste")
                .email(email)
                .password("Senha@123")
                .administrador("true")
                .build();

        usuarioService.criar(usuario)
                .then()
                .statusCode(400);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Buscar usuário por ID deve retornar 200, schema e dados corretos")
    @Severity(SeverityLevel.NORMAL)
    void buscarUsuarioPorId() {
        Usuario usuario = DataFactory.usuarioComumValido();
        String id = usuarioService.criarEObterID(usuario);
        idsParaLimpar.add(id);

        var response = usuarioService.buscarPorId(id)
                .then()
                .statusCode(200)
                .time(lessThan(2000L))
                .header("Content-Type", containsString("application/json"))
                .body(matchesJsonSchemaInClasspath("schemas/usuario-schema.json"))
                .extract().response();

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(response.path("_id").toString()).isEqualTo(id);
        soft.assertThat(response.path("email").toString()).isEqualTo(usuario.getEmail());
        soft.assertThat(response.path("nome").toString()).isEqualTo(usuario.getNome());
        soft.assertAll();
    }

    @Test
    @Tag("regression")
    @DisplayName("Buscar usuário com ID inexistente deve retornar 400")
    @Severity(SeverityLevel.MINOR)
    void buscarUsuarioIdInexistente() {
        usuarioService.buscarPorId("idInexistente000")
                .then()
                .statusCode(400)
                .body("message", equalTo(Messages.USUARIO_NAO_ENCONTRADO));
    }

    @Test
    @Tag("regression")
    @DisplayName("Atualizar usuário existente deve retornar 200")
    @Severity(SeverityLevel.NORMAL)
    void atualizarUsuario() {
        Usuario usuario = DataFactory.usuarioComumValido();
        String id = usuarioService.criarEObterID(usuario);
        idsParaLimpar.add(id);

        Usuario atualizado = Usuario.builder()
                .nome("Nome Atualizado")
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .administrador(usuario.getAdministrador())
                .build();

        usuarioService.atualizar(id, atualizado)
                .then()
                .statusCode(200)
                .time(lessThan(2000L))
                .body("message", equalTo(Messages.USUARIO_ALTERADO));
    }

    @Test
    @Tag("regression")
    @DisplayName("Excluir usuário existente deve retornar 200")
    @Severity(SeverityLevel.NORMAL)
    void excluirUsuario() {
        String id = usuarioService.criarEObterID(DataFactory.usuarioComumValido());

        usuarioService.deletar(id)
                .then()
                .statusCode(200)
                .body("message", equalTo(Messages.USUARIO_EXCLUIDO));
    }

    @Test
    @Tag("regression")
    @DisplayName("Excluir usuário inexistente deve retornar 200 sem registro")
    @Severity(SeverityLevel.MINOR)
    void excluirUsuarioInexistente() {
        usuarioService.deletar("idInexistente000")
                .then()
                .statusCode(200)
                .body("message", equalTo(Messages.NENHUM_REGISTRO));
    }
}
