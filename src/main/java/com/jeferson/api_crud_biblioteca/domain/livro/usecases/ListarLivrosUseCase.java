package com.jeferson.api_crud_biblioteca.domain.livro.usecases;

import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.enums.GeneroPojo;
import org.springframework.data.domain.Page;

public interface ListarLivrosUseCase {

    Page<LivroResponseDTO> listar(int pagina, int tamanho, GeneroPojo genero);
}
