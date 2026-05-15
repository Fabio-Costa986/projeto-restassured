package br.com.serverest.utils;

import br.com.serverest.config.ConfigManager;
import br.com.serverest.models.Produto;
import br.com.serverest.models.Usuario;
import com.github.javafaker.Faker;

import java.util.Locale;

public final class DataFactory {

    private static final Faker faker = new Faker(new Locale("pt-BR"));

    private DataFactory() {}

    public static Usuario usuarioAdminValido() {
        return Usuario.builder()
                .nome(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .password(ConfigManager.getDefaultPassword())
                .administrador("true")
                .build();
    }

    public static Usuario usuarioComumValido() {
        return Usuario.builder()
                .nome(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .password(ConfigManager.getDefaultPassword())
                .administrador("false")
                .build();
    }

    public static Produto produtoValido() {
        return Produto.builder()
                .nome(faker.commerce().productName() + " " + faker.number().digits(4))
                .preco(faker.number().numberBetween(10, 5000))
                .descricao(faker.lorem().sentence())
                .quantidade(faker.number().numberBetween(1, 200))
                .build();
    }
}
