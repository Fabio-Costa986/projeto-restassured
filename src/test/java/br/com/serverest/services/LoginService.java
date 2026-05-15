package br.com.serverest.services;

import br.com.serverest.constants.Endpoints;
import br.com.serverest.models.LoginRequest;
import br.com.serverest.models.LoginResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class LoginService {

    private final RequestSpecification spec;

    public LoginService(RequestSpecification spec) {
        this.spec = spec;
    }

    @Step("POST /login com email={email}")
    public Response login(String email, String password) {
        return given()
                .spec(spec)
                .body(LoginRequest.builder().email(email).password(password).build())
                .when()
                .post(Endpoints.LOGIN);
    }

    @Step("Obter token de autenticação para {email}")
    public String obterToken(String email, String password) {
        return login(email, password)
                .then()
                .statusCode(200)
                .extract()
                .as(LoginResponse.class)
                .getAuthorization();
    }
}
