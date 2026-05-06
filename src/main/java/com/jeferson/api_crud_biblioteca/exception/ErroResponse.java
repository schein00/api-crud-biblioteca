package com.jeferson.api_crud_biblioteca.exception;

import java.time.LocalDateTime;

public record ErroResponse(
        String codigo,
        String mensagem,
        LocalDateTime timestamp
) {}
