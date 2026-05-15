package br.com.serverest.services;

import br.com.serverest.constants.Endpoints;
import br.com.serverest.models.Usuario;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UsuarioService {

    private final RequestSpecification spec;

    public UsuarioService(RequestSpecification spec) {
        this.spec = spec;
    }

    @Step("GET /usuarios - listar todos")
    public Response listar() {
        return given().spec(spec).when().get(Endpoints.USUARIOS);
    }

    @Step("POST /usuarios - criar usuário {usuario.email}")
    public Response criar(Usuario usuario) {
        return given().spec(spec).body(usuario).when().post(Endpoints.USUARIOS);
    }

    @Step("GET /usuarios/{id}")
    public Response buscarPorId(String id) {
        return given().spec(spec).when().get(Endpoints.USUARIO_ID, id);
    }

    @Step("PUT /usuarios/{id}")
    public Response atualizar(String id, Usuario usuario) {
        return given().spec(spec).body(usuario).when().put(Endpoints.USUARIO_ID, id);
    }

    @Step("DELETE /usuarios/{id}")
    public Response deletar(String id) {
        return given().spec(spec).when().delete(Endpoints.USUARIO_ID, id);
    }

    @Step("Criar usuário e retornar ID")
    public String criarEObterID(Usuario usuario) {
        return criar(usuario)
                .then()
                .statusCode(201)
                .extract()
                .path("_id");
    }
}
