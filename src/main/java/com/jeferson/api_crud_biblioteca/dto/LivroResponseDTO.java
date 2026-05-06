package com.jeferson.api_crud_biblioteca.dto;

import com.jeferson.api_crud_biblioteca.domain.enums.GeneroPojo;

import java.time.LocalDateTime;

public record LivroResponseDTO(
        String id,
        String titulo,
        String autor,
        String isbn,
        Integer anoPublicacao,
        GeneroPojo genero,
        Boolean disponivel,
        LocalDateTime dataInclusao,
        LocalDateTime dataAtualizacao
) {}
