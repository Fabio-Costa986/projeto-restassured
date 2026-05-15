package br.com.serverest.utils;

import br.com.serverest.models.Usuario;
import br.com.serverest.services.LoginService;
import br.com.serverest.services.UsuarioService;
import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;

public final class AuthUtils {

    private AuthUtils() {}

    @Step("Criar usuário admin e obter token Bearer")
    public static String criarUsuarioEObterToken(RequestSpecification spec) {
        UsuarioService usuarioService = new UsuarioService(spec);
        LoginService loginService = new LoginService(spec);

        Usuario admin = DataFactory.usuarioAdminValido();
        usuarioService.criar(admin).then().statusCode(201);

        return loginService.obterToken(admin.getEmail(), admin.getPassword());
    }
}
