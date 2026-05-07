package com.jeferson.api_crud_biblioteca.domain.livro.usecases;

import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroRequestDTO;
import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroResponseDTO;

public interface AtualizarLivroUseCase {

    LivroResponseDTO atualizar(String id, LivroRequestDTO dto);
}
