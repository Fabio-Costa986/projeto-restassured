package br.com.serverest.tests;

import br.com.serverest.config.BaseTest;
import br.com.serverest.constants.Messages;
import br.com.serverest.models.Produto;
import br.com.serverest.services.ProdutoService;
import br.com.serverest.utils.AuthUtils;
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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@Epic("Produtos")
@Feature("CRUD Produtos")
@Tag("produtos")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProdutosTest extends BaseTest {

    private ProdutoService produtoService;
    private String tokenAdmin;
    private final List<String> idsParaLimpar = new CopyOnWriteArrayList<>();

    @BeforeAll
    void inicializar() {
        produtoService = new ProdutoService(requestSpec);
        tokenAdmin = AuthUtils.criarUsuarioEObterToken(requestSpec);
    }

    @AfterAll
    void limparDados() {
        idsParaLimpar.forEach(id -> produtoService.deletar(id, tokenAdmin));
    }

    static Stream<Arguments> camposObrigatoriosAusentes() {
        return Stream.of(
            Arguments.of("nome ausente",       null,       100,  "desc",  10),
            Arguments.of("preço zero",         "Produto",    0,  "desc",  10),
            Arguments.of("preço negativo",     "Produto",   -1,  "desc",  10),
            Arguments.of("descrição ausente",  "Produto",  100,    null,  10),
            Arguments.of("quantidade negativa","Produto",  100,  "desc",  -1)
        );
    }

    @Test
    @Tag("smoke")
    @DisplayName("Listar produtos deve retornar 200 e headers corretos")
    @Severity(SeverityLevel.NORMAL)
    void listarProdutos() {
        produtoService.listar()
                .then()
                .statusCode(200)
                .time(lessThan(2000L))
                .header("Content-Type", containsString("application/json"))
                .body("quantidade", greaterThanOrEqualTo(0))
                .body("produtos", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("Cadastrar produto com token admin deve retornar 201 e contrato válido")
    @Description("POST /produtos valida schema, dados e tempo de resposta")
    @Severity(SeverityLevel.CRITICAL)
    void cadastrarProduto() {
        Produto produto = DataFactory.produtoValido();

        String id = produtoService.criar(produto, tokenAdmin)
                .then()
                .statusCode(201)
                .time(lessThan(2000L))
                .header("Content-Type", containsString("application/json"))
                .body("message", equalTo(Messages.PRODUTO_CADASTRADO))
                .body("_id", notNullValue())
                .extract().path("_id");

        idsParaLimpar.add(id);

        var response = produtoService.buscarPorId(id)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/produto-schema.json"))
                .extract().response();

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(response.path("nome").toString()).isEqualTo(produto.getNome());
        soft.assertThat((Integer) response.path("preco")).isEqualTo(produto.getPreco());
        soft.assertAll();
    }

    @Test
    @Tag("regression")
    @DisplayName("Cadastrar produto sem token deve retornar 401")
    @Severity(SeverityLevel.CRITICAL)
    void cadastrarProdutoSemToken() {
        produtoService.criar(DataFactory.produtoValido())
                .then()
                .statusCode(401)
                .body("message", equalTo(Messages.TOKEN_INVALIDO));
    }

    @Test
    @Tag("regression")
    @DisplayName("Cadastrar produto com nome duplicado deve retornar 400")
    @Severity(SeverityLevel.NORMAL)
    void cadastrarProdutoNomeDuplicado() {
        Produto produto = DataFactory.produtoValido();
        String id = produtoService.criarEObterID(produto, tokenAdmin);
        idsParaLimpar.add(id);

        produtoService.criar(produto, tokenAdmin)
                .then()
                .statusCode(400)
                .body("message", equalTo(Messages.PRODUTO_NOME_USADO));
    }

    @ParameterizedTest(name = "Criar produto com {0} deve retornar 400")
    @MethodSource("camposObrigatoriosAusentes")
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    void cadastrarProdutoComDadosInvalidos(String descricao, String nome, Integer preco, String desc, Integer qtd) {
        Produto produto = Produto.builder()
                .nome(nome)
                .preco(preco)
                .descricao(desc)
                .quantidade(qtd)
                .build();

        produtoService.criar(produto, tokenAdmin)
                .then()
                .statusCode(400);
    }

    @Test
    @Tag("regression")
    @DisplayName("Atualizar produto existente deve retornar 200")
    @Severity(SeverityLevel.NORMAL)
    void atualizarProduto() {
        Produto produto = DataFactory.produtoValido();
        String id = produtoService.criarEObterID(produto, tokenAdmin);
        idsParaLimpar.add(id);

        Produto atualizado = Produto.builder()
                .nome(produto.getNome())
                .preco(9999)
                .descricao(produto.getDescricao())
                .quantidade(produto.getQuantidade())
                .build();

        produtoService.atualizar(id, atualizado, tokenAdmin)
                .then()
                .statusCode(200)
                .time(lessThan(2000L))
                .body("message", equalTo(Messages.PRODUTO_ALTERADO));
    }

    @Test
    @Tag("regression")
    @DisplayName("Excluir produto existente deve retornar 200")
    @Severity(SeverityLevel.NORMAL)
    void excluirProduto() {
        String id = produtoService.criarEObterID(DataFactory.produtoValido(), tokenAdmin);

        produtoService.deletar(id, tokenAdmin)
                .then()
                .statusCode(200)
                .body("message", equalTo(Messages.PRODUTO_EXCLUIDO));
    }
}
