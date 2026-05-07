package com.jeferson.api_crud_biblioteca.app.livro.providers;

import com.jeferson.api_crud_biblioteca.domain.livro.entity.Livro;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.enums.GeneroPojo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LivroDataProviderImpl implements LivroDataProvider {

    private final LivroRepository livroRepository;

    @Override
    public Livro save(Livro livro) {
        return livroRepository.save(livro);
    }

    @Override
    public Optional<Livro> findById(String id) {
        return livroRepository.findById(id);
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return livroRepository.existsByIsbn(isbn);
    }

    @Override
    public boolean existsByIsbnAndIdNot(String isbn, String id) {
        return livroRepository.existsByIsbnAndIdNot(isbn, id);
    }

    @Override
    public Page<Livro> findByGenero(GeneroPojo genero, Pageable pageable) {
        return livroRepository.findByGenero(genero, pageable);
    }

    @Override
    public Page<Livro> findAll(Pageable pageable) {
        return livroRepository.findAll(pageable);
    }

    @Override
    public boolean existsById(String id) {
        return livroRepository.existsById(id);
    }

    @Override
    public void deleteById(String id) {
        livroRepository.deleteById(id);
    }
}
