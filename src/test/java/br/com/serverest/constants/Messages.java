package br.com.serverest.constants;

public final class Messages {

    // Login
    public static final String LOGIN_SUCESSO         = "Login realizado com sucesso";
    public static final String LOGIN_INVALIDO         = "Email e/ou senha inválidos";
    public static final String TOKEN_INVALIDO         = "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais";

    // Usuários
    public static final String USUARIO_CADASTRADO     = "Cadastro realizado com sucesso";
    public static final String USUARIO_ALTERADO       = "Registro alterado com sucesso";
    public static final String USUARIO_EXCLUIDO       = "Registro excluído com sucesso";
    public static final String USUARIO_NAO_ENCONTRADO = "Usuário não encontrado";
    public static final String USUARIO_EMAIL_USADO    = "Este email já está sendo usado";
    public static final String NENHUM_REGISTRO        = "Nenhum registro excluído";

    // Produtos
    public static final String PRODUTO_CADASTRADO     = "Cadastro realizado com sucesso";
    public static final String PRODUTO_ALTERADO       = "Registro alterado com sucesso";
    public static final String PRODUTO_EXCLUIDO       = "Registro excluído com sucesso";
    public static final String PRODUTO_NOME_USADO     = "Já existe produto com esse nome";

    // Carrinhos
    public static final String CARRINHO_CADASTRADO    = "Cadastro realizado com sucesso";
    public static final String CARRINHO_CONCLUIDO     = "Registro excluído com sucesso";
    public static final String CARRINHO_CANCELADO     = "Registro excluído com sucesso. Estoque dos produtos reabastecido";
    public static final String CARRINHO_NAO_ENCONTRADO = "Não foi encontrado carrinho para esse usuário";

    private Messages() {}
}
