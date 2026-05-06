package com.jeferson.api_crud_biblioteca.dto;

import com.jeferson.api_crud_biblioteca.domain.enums.GeneroPojo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LivroRequestDTO(

        @NotBlank(message = "O título é obrigatório")
        String titulo,

        @NotBlank(message = "O autor é obrigatório")
        String autor,

        @NotBlank(message = "O ISBN é obrigatório")
        String isbn,

        @NotNull(message = "O ano de publicação é obrigatório")
        @Min(value = 1000, message = "O ano de publicação deve ser maior que 1000")
        Integer anoPublicacao,

        @NotNull(message = "O gênero é obrigatório")
        GeneroPojo genero,

        @NotNull(message = "A disponibilidade é obrigatória")
        Boolean disponivel
) {}
