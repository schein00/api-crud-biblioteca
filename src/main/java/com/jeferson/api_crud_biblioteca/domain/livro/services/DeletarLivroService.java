package com.jeferson.api_crud_biblioteca.domain.livro.services;

import com.jeferson.api_crud_biblioteca.app.livro.providers.LivroDataProvider;
import com.jeferson.api_crud_biblioteca.domain.exception.NegocioException;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.DeletarLivroUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletarLivroService implements DeletarLivroUseCase {

    private final LivroDataProvider livroDataProvider;

    @Override
    @CacheEvict(value = "biblioteca:livro", key = "#id")
    public void deletar(String id) {
        if (!livroDataProvider.existsById(id)) {
            throw new NegocioException("LIVRO_NAO_ENCONTRADO",
                    "Livro com id '" + id + "' não encontrado.", HttpStatus.NOT_FOUND);
        }
        livroDataProvider.deleteById(id);
    }
}
