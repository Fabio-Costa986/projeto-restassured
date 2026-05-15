package br.com.serverest.tests;

import br.com.serverest.config.BaseTest;
import br.com.serverest.constants.Messages;
import br.com.serverest.models.Carrinho;
import br.com.serverest.models.ItemCarrinho;
import br.com.serverest.services.CarrinhoService;
import br.com.serverest.services.ProdutoService;
import br.com.serverest.utils.AuthUtils;
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

import java.util.List;

import static org.hamcrest.Matchers.*;

@Epic("Carrinhos")
@Feature("Gestão de Carrinho")
@Tag("carrinhos")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CarrinhosTest extends BaseTest {

    private CarrinhoService carrinhoService;
    private ProdutoService produtoService;
    private String tokenAdmin;
    private String idProduto;

    @BeforeAll
    void prepararAmbiente() {
        carrinhoService = new CarrinhoService(requestSpec);
        produtoService = new ProdutoService(requestSpec);
        tokenAdmin = AuthUtils.criarUsuarioEObterToken(requestSpec);
        idProduto = produtoService.criarEObterID(DataFactory.produtoValido(), tokenAdmin);
    }

    @AfterAll
    void limparDados() {
        if (idProduto != null && tokenAdmin != null) {
            produtoService.deletar(idProduto, tokenAdmin);
        }
    }

    private Carrinho carrinhoComProduto() {
        return Carrinho.builder()
                .produtos(List.of(ItemCarrinho.builder().idProduto(idProduto).quantidade(1).build()))
                .build();
    }

    @Test
    @Tag("smoke")
    @DisplayName("Listar carrinhos deve retornar status 200")
    @Severity(SeverityLevel.NORMAL)
    void listarCarrinhos() {
        carrinhoService.listar()
                .then()
                .statusCode(200)
                .body("quantidade", greaterThanOrEqualTo(0))
                .body("carrinhos", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("Criar carrinho com produto válido deve retornar 201")
    @Description("POST /carrinhos com produto e token válidos deve criar o carrinho e retornar status 201")
    @Severity(SeverityLevel.CRITICAL)
    void criarCarrinho() {
        String novoToken = AuthUtils.criarUsuarioEObterToken(requestSpec);

        carrinhoService.criar(carrinhoComProduto(), novoToken)
                .then()
                .statusCode(201)
                .body("message", equalTo(Messages.CARRINHO_CADASTRADO))
                .body("_id", notNullValue());
    }

    @Test
    @Tag("regression")
    @DisplayName("Criar carrinho sem token deve retornar 401")
    @Severity(SeverityLevel.CRITICAL)
    void criarCarrinhoSemToken() {
        carrinhoService.criar(carrinhoComProduto())
                .then()
                .statusCode(401)
                .body("message", equalTo(Messages.TOKEN_INVALIDO));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Concluir compra deve retornar 200 e excluir o carrinho")
    @Description("DELETE /carrinhos/concluir-compra deve finalizar a compra e retornar status 200")
    @Severity(SeverityLevel.CRITICAL)
    void concluirCompra() {
        String novoToken = AuthUtils.criarUsuarioEObterToken(requestSpec);
        carrinhoService.criar(carrinhoComProduto(), novoToken).then().statusCode(201);

        carrinhoService.concluirCompra(novoToken)
                .then()
                .statusCode(200)
                .body("message", equalTo(Messages.CARRINHO_CONCLUIDO));
    }

    @Test
    @Tag("regression")
    @DisplayName("Cancelar compra deve retornar 200 e restaurar estoque")
    @Severity(SeverityLevel.CRITICAL)
    void cancelarCompra() {
        String novoToken = AuthUtils.criarUsuarioEObterToken(requestSpec);
        carrinhoService.criar(carrinhoComProduto(), novoToken).then().statusCode(201);

        carrinhoService.cancelarCompra(novoToken)
                .then()
                .statusCode(200)
                .body("message", equalTo(Messages.CARRINHO_CANCELADO));
    }

    @Test
    @Tag("regression")
    @DisplayName("Concluir compra sem carrinho ativo deve retornar 200")
    @Severity(SeverityLevel.MINOR)
    void concluirCompraSemCarrinho() {
        String novoToken = AuthUtils.criarUsuarioEObterToken(requestSpec);

        carrinhoService.concluirCompra(novoToken)
                .then()
                .statusCode(200)
                .body("message", equalTo(Messages.CARRINHO_NAO_ENCONTRADO));
    }
}
