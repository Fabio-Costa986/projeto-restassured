package br.com.serverest.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class BaseTest {

    protected static RequestSpecification requestSpec;
    protected static ResponseSpecification responseSpec;

    @BeforeAll
    static void setup() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getBaseUrl())
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();

        RestAssured.requestSpecification = requestSpec;
        RestAssured.responseSpecification = responseSpec;

        gerarEnvironmentProperties();
    }

    private static void gerarEnvironmentProperties() {
        try {
            Files.createDirectories(Paths.get("target/allure-results"));
            Properties env = new Properties();
            env.setProperty("Environment",   System.getProperty("env", "dev"));
            env.setProperty("Base.URL",       ConfigManager.getBaseUrl());
            env.setProperty("Java.Version",   System.getProperty("java.version"));
            env.setProperty("OS",             System.getProperty("os.name"));
            env.store(new FileWriter("target/allure-results/environment.properties"), null);
        } catch (IOException e) {
            System.err.println("Aviso: não foi possível gerar environment.properties — " + e.getMessage());
        }
    }
}
