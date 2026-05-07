package com.jeferson.api_crud_biblioteca.app.config;

import java.time.LocalDateTime;

public record ErroResponse(
        String codigo,
        String mensagem,
        LocalDateTime timestamp
) {}
