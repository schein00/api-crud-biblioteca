package com.jeferson.api_crud_biblioteca.app.livro.providers;

import com.jeferson.api_crud_biblioteca.domain.livro.entity.Livro;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.enums.GeneroPojo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LivroDataProvider {

    Livro save(Livro livro);

    Optional<Livro> findById(String id);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, String id);

    Page<Livro> findByGenero(GeneroPojo genero, Pageable pageable);

    Page<Livro> findAll(Pageable pageable);

    boolean existsById(String id);

    void deleteById(String id);
}
