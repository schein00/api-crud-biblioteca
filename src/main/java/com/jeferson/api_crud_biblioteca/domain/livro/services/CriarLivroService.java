package com.jeferson.api_crud_biblioteca.domain.livro.services;

import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroRequestDTO;
import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.app.livro.providers.LivroDataProvider;
import com.jeferson.api_crud_biblioteca.domain.exception.NegocioException;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.Livro;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.CriarLivroUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class CriarLivroService implements CriarLivroUseCase {

    private final LivroDataProvider livroDataProvider;

    @Override
    public LivroResponseDTO criar(LivroRequestDTO dto) {
        int anoAtual = Year.now().getValue();
        if (dto.anoPublicacao() > anoAtual) {
            throw new NegocioException("ANO_INVALIDO",
                    "O ano de publicação não pode ser maior que o ano atual (" + anoAtual + ").");
        }

        if (livroDataProvider.existsByIsbn(dto.isbn())) {
            throw new NegocioException("ISBN_DUPLICADO",
                    "Já existe um livro cadastrado com o ISBN '" + dto.isbn() + "'.");
        }

        Livro livro = Livro.builder()
                .titulo(dto.titulo())
                .autor(dto.autor())
                .isbn(dto.isbn())
                .anoPublicacao(dto.anoPublicacao())
                .genero(dto.genero())
                .disponivel(dto.disponivel())
                .dataInclusao(LocalDateTime.now())
                .build();

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
