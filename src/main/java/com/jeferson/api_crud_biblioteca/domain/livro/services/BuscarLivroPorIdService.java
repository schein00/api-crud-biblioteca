package com.jeferson.api_crud_biblioteca.domain.livro.services;

import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.app.livro.providers.LivroDataProvider;
import com.jeferson.api_crud_biblioteca.domain.exception.NegocioException;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.Livro;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.BuscarLivroPorIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarLivroPorIdService implements BuscarLivroPorIdUseCase {

    private final LivroDataProvider livroDataProvider;

    @Override
    @Cacheable(value = "biblioteca:livro", key = "#id")
    public LivroResponseDTO buscarPorId(String id) {
        Livro livro = livroDataProvider.findById(id)
                .orElseThrow(() -> new NegocioException("LIVRO_NAO_ENCONTRADO",
                        "Livro com id '" + id + "' não encontrado.", HttpStatus.NOT_FOUND));
        return toResponseDTO(livro);
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
