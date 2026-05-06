package com.jeferson.api_crud_biblioteca.exception;

import org.springframework.http.HttpStatus;

public class NegocioException extends RuntimeException {

    private final String codigo;
    private final HttpStatus status;

    public NegocioException(String codigo, String mensagem) {
        this(codigo, mensagem, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public NegocioException(String codigo, String mensagem, HttpStatus status) {
        super(mensagem);
        this.codigo = codigo;
        this.status = status;
    }

    public String getCodigo() {
        return codigo;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
