package com.jeferson.api_crud_biblioteca.service;

import com.jeferson.api_crud_biblioteca.domain.Livro;
import com.jeferson.api_crud_biblioteca.domain.enums.GeneroPojo;
import com.jeferson.api_crud_biblioteca.dto.LivroRequestDTO;
import com.jeferson.api_crud_biblioteca.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.exception.NegocioException;
import com.jeferson.api_crud_biblioteca.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    public LivroResponseDTO criar(LivroRequestDTO dto) {
        validarAnoPublicacao(dto.anoPublicacao());

        if (livroRepository.existsByIsbn(dto.isbn())) {
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

        return toResponseDTO(livroRepository.save(livro));
    }

    @Cacheable(value = "biblioteca:livro", key = "#id")
    public LivroResponseDTO buscarPorId(String id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new NegocioException("LIVRO_NAO_ENCONTRADO",
                        "Livro com id '" + id + "' não encontrado.", HttpStatus.NOT_FOUND));
        return toResponseDTO(livro);
    }

    public Page<LivroResponseDTO> listar(int pagina, int tamanho, GeneroPojo genero) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<Livro> livros = (genero != null)
                ? livroRepository.findByGenero(genero, pageable)
                : livroRepository.findAll(pageable);
        return livros.map(this::toResponseDTO);
    }

    @CacheEvict(value = "biblioteca:livro", key = "#id")
    public LivroResponseDTO atualizar(String id, LivroRequestDTO dto) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new NegocioException("LIVRO_NAO_ENCONTRADO",
                        "Livro com id '" + id + "' não encontrado.", HttpStatus.NOT_FOUND));

        validarAnoPublicacao(dto.anoPublicacao());

        if (livroRepository.existsByIsbnAndIdNot(dto.isbn(), id)) {
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

        return toResponseDTO(livroRepository.save(livro));
    }

    @CacheEvict(value = "biblioteca:livro", key = "#id")
    public void deletar(String id) {
        if (!livroRepository.existsById(id)) {
            throw new NegocioException("LIVRO_NAO_ENCONTRADO",
                    "Livro com id '" + id + "' não encontrado.", HttpStatus.NOT_FOUND);
        }
        livroRepository.deleteById(id);
    }

    private void validarAnoPublicacao(Integer ano) {
        int anoAtual = Year.now().getValue();
        if (ano > anoAtual) {
            throw new NegocioException("ANO_INVALIDO",
                    "O ano de publicação não pode ser maior que o ano atual (" + anoAtual + ").");
        }
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
