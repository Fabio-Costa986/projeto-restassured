package br.com.serverest.constants;

public final class Endpoints {

    public static final String LOGIN      = "/login";
    public static final String USUARIOS   = "/usuarios";
    public static final String USUARIO_ID = "/usuarios/{id}";
    public static final String PRODUTOS   = "/produtos";
    public static final String PRODUTO_ID = "/produtos/{id}";
    public static final String CARRINHOS          = "/carrinhos";
    public static final String CONCLUIR_COMPRA    = "/carrinhos/concluir-compra";
    public static final String CANCELAR_COMPRA    = "/carrinhos/cancelar-compra";

    private Endpoints() {}
}
