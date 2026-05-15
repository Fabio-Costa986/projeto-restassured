package br.com.serverest.tests;

import br.com.serverest.config.BaseTest;
import br.com.serverest.constants.Messages;
import br.com.serverest.models.Usuario;
import br.com.serverest.services.LoginService;
import br.com.serverest.services.UsuarioService;
import br.com.serverest.utils.DataFactory;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.hamcrest.Matchers.*;

@Epic("Autenticação")
@Feature("Login")
@Tag("login")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginTest extends BaseTest {

    private LoginService loginService;
    private UsuarioService usuarioService;
    private Usuario usuario;
    private String usuarioId;

    @BeforeAll
    void inicializar() {
        loginService = new LoginService(requestSpec);
        usuarioService = new UsuarioService(requestSpec);

        usuario = DataFactory.usuarioAdminValido();
        usuarioId = usuarioService.criarEObterID(usuario);
    }

    @AfterAll
    void limparDados() {
        if (usuarioId != null) {
            usuarioService.deletar(usuarioId);
        }
    }

    @Test
    @Tag("smoke")
    @DisplayName("Login com credenciais válidas deve retornar token")
    @Description("POST /login com email e senha corretos deve retornar status 200 e o token Bearer")
    @Severity(SeverityLevel.BLOCKER)
    void loginComCredenciaisValidas() {
        loginService.login(usuario.getEmail(), usuario.getPassword())
                .then()
                .statusCode(200)
                .body("message", equalTo(Messages.LOGIN_SUCESSO))
                .body("authorization", startsWith("Bearer "));
    }

    @Test
    @Tag("regression")
    @DisplayName("Login com senha incorreta deve retornar 401")
    @Description("POST /login com senha errada deve retornar status 401")
    @Severity(SeverityLevel.CRITICAL)
    void loginComSenhaIncorreta() {
        loginService.login(usuario.getEmail(), "senhaErrada")
                .then()
                .statusCode(401)
                .body("message", equalTo(Messages.LOGIN_INVALIDO));
    }

    @Test
    @Tag("regression")
    @DisplayName("Login com email inexistente deve retornar 401")
    @Description("POST /login com email não cadastrado deve retornar status 401")
    @Severity(SeverityLevel.CRITICAL)
    void loginComEmailInexistente() {
        loginService.login("naoexiste@email.com", "qualquerSenha")
                .then()
                .statusCode(401)
                .body("message", equalTo(Messages.LOGIN_INVALIDO));
    }

    @Test
    @Tag("regression")
    @DisplayName("Login sem credenciais deve retornar 400")
    @Description("POST /login com body vazio deve retornar status 400 com mensagens de validação")
    @Severity(SeverityLevel.NORMAL)
    void loginSemCredenciais() {
        loginService.login("", "")
                .then()
                .statusCode(400)
                .body("email", notNullValue())
                .body("password", notNullValue());
    }
}
