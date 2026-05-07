package com.jeferson.api_crud_biblioteca.domain.livro.services;

import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroRequestDTO;
import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.app.livro.providers.LivroDataProvider;
import com.jeferson.api_crud_biblioteca.domain.exception.NegocioException;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.Livro;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.AtualizarLivroUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class AtualizarLivroService implements AtualizarLivroUseCase {

    private final LivroDataProvider livroDataProvider;

    @Override
    @CacheEvict(value = "biblioteca:livro", key = "#id")
    public LivroResponseDTO atualizar(String id, LivroRequestDTO dto) {
        Livro livro = livroDataProvider.findById(id)
                .orElseThrow(() -> new NegocioException("LIVRO_NAO_ENCONTRADO",
                        "Livro com id '" + id + "' não encontrado.", HttpStatus.NOT_FOUND));

        int anoAtual = Year.now().getValue();
        if (dto.anoPublicacao() > anoAtual) {
            throw new NegocioException("ANO_INVALIDO",
                    "O ano de publicação não pode ser maior que o ano atual (" + anoAtual + ").");
        }

        if (livroDataProvider.existsByIsbnAndIdNot(dto.isbn(), id)) {
            throw new NegocioException("ISBN_DUPLICADO",
                    "Já existe um livro cadastrado com o ISBN '" + dto.isbn() + "'.");
        }

        livro.setTitulo(dto.titulo());
        livro.setAutor(dto.autor());
        livro.setIsbn(dto.isbn());
        livro.setAnoPublicacao(dto.anoPublicacao());
        livro.setGenero(dto.genero());
        livro.setDisponivel(dto.disponivel());
        livro.setDataAtualizacao(LocalDateTime.now());

        return toResponseDTO(livroDataProvider.save(livro));
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
