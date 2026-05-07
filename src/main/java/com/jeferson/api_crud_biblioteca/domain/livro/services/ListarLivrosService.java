package com.jeferson.api_crud_biblioteca.domain.livro.services;

import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.app.livro.providers.LivroDataProvider;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.Livro;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.enums.GeneroPojo;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.ListarLivrosUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListarLivrosService implements ListarLivrosUseCase {

    private final LivroDataProvider livroDataProvider;

    @Override
    public Page<LivroResponseDTO> listar(int pagina, int tamanho, GeneroPojo genero) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<Livro> livros = (genero != null)
                ? livroDataProvider.findByGenero(genero, pageable)
                : livroDataProvider.findAll(pageable);
        return livros.map(this::toResponseDTO);
    }

    private LivroResponseDTO toResponseDTO(Livro livro) {
        return new LivroResponseDTO(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getAnoPublicacao(),
                livro.getGenero(),
                livro.getDisponivel(),
                livro.getDataInclusao(),
                livro.getDataAtualizacao()
        );
    }
}
