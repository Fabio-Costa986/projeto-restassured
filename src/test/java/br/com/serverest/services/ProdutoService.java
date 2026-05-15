package br.com.serverest.services;

import br.com.serverest.constants.Endpoints;
import br.com.serverest.models.Produto;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ProdutoService {

    private final RequestSpecification spec;

    public ProdutoService(RequestSpecification spec) {
        this.spec = spec;
    }

    @Step("GET /produtos - listar todos")
    public Response listar() {
        return given().spec(spec).when().get(Endpoints.PRODUTOS);
    }

    @Step("POST /produtos - criar produto (autenticado)")
    public Response criar(Produto produto, String token) {
        return given().spec(spec).header("Authorization", token).body(produto).when().post(Endpoints.PRODUTOS);
    }

    @Step("POST /produtos - criar produto (sem token)")
    public Response criar(Produto produto) {
        return given().spec(spec).body(produto).when().post(Endpoints.PRODUTOS);
    }

    @Step("GET /produtos/{id}")
    public Response buscarPorId(String id) {
        return given().spec(spec).when().get(Endpoints.PRODUTO_ID, id);
    }

    @Step("PUT /produtos/{id}")
    public Response atualizar(String id, Produto produto, String token) {
        return given().spec(spec).header("Authorization", token).body(produto).when().put(Endpoints.PRODUTO_ID, id);
    }

    @Step("DELETE /produtos/{id}")
    public Response deletar(String id, String token) {
        return given().spec(spec).header("Authorization", token).when().delete(Endpoints.PRODUTO_ID, id);
    }

    @Step("Criar produto e retornar ID")
    public String criarEObterID(Produto produto, String token) {
        return criar(produto, token)
                .then()
                .statusCode(201)
                .extract()
                .path("_id");
    }
}
