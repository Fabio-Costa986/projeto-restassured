package br.com.serverest.services;

import br.com.serverest.constants.Endpoints;
import br.com.serverest.models.Carrinho;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class CarrinhoService {

    private final RequestSpecification spec;

    public CarrinhoService(RequestSpecification spec) {
        this.spec = spec;
    }

    @Step("GET /carrinhos - listar todos")
    public Response listar() {
        return given().spec(spec).when().get(Endpoints.CARRINHOS);
    }

    @Step("POST /carrinhos - criar carrinho (autenticado)")
    public Response criar(Carrinho carrinho, String token) {
        return given().spec(spec).header("Authorization", token).body(carrinho).when().post(Endpoints.CARRINHOS);
    }

    @Step("POST /carrinhos - criar carrinho (sem token)")
    public Response criar(Carrinho carrinho) {
        return given().spec(spec).body(carrinho).when().post(Endpoints.CARRINHOS);
    }

    @Step("DELETE /carrinhos/concluir-compra")
    public Response concluirCompra(String token) {
        return given().spec(spec).header("Authorization", token).when().delete(Endpoints.CONCLUIR_COMPRA);
    }

    @Step("DELETE /carrinhos/cancelar-compra")
    public Response cancelarCompra(String token) {
        return given().spec(spec).header("Authorization", token).when().delete(Endpoints.CANCELAR_COMPRA);
    }
}
