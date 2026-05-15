package br.com.serverest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Carrinho {

    private String _id;
    private List<ItemCarrinho> produtos;
    private Integer precoTotal;
    private Integer quantidadeTotal;
    private String idUsuario;
}
