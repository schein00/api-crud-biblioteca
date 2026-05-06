package com.jeferson.api_crud_biblioteca.repository;

import com.jeferson.api_crud_biblioteca.domain.Livro;
import com.jeferson.api_crud_biblioteca.domain.enums.GeneroPojo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivroRepository extends MongoRepository<Livro, String> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, String id);

    Page<Livro> findByGenero(GeneroPojo genero, Pageable pageable);
}
