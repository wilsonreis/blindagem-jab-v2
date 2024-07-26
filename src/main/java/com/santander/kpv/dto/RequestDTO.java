package com.santander.kpv.dto;

import java.io.Serial;
import java.io.Serializable;

public class RequestDTO implements Serializable {
    private String conteudo;
    private String rotina;

    public RequestDTO(String conteudo, String rotina) {
        this.conteudo = conteudo;
        this.rotina = rotina;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getRotina() {
        return rotina;
    }

    public void setRotina(String rotina) {
        this.rotina = rotina;
    }
}
