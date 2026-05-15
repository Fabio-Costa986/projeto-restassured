package br.com.serverest.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final Properties properties = new Properties();

    static {
        String env = System.getProperty("env", "config");
        String fileName = env + ".properties";
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new RuntimeException("Arquivo de configuração não encontrado: " + fileName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar configurações: " + e.getMessage(), e);
        }
    }

    private ConfigManager() {}

    public static String getBaseUrl() {
        return get("base.url");
    }

    public static int getRequestTimeout() {
        return Integer.parseInt(get("request.timeout"));
    }

    public static String getDefaultPassword() {
        return get("default.password");
    }

    private static String get(String key) {
        String value = System.getProperty(key, properties.getProperty(key));
        if (value == null) {
            throw new RuntimeException("Propriedade não encontrada: " + key);
        }
        return value;
    }
}
