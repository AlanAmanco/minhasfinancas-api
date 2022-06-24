package com.alanAmanco.minhasfinancas.DTO;

public class AtualizarStatusDTO {

    private String status;

    public AtualizarStatusDTO() {
    }

    public AtualizarStatusDTO(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
