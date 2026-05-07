package com.jeferson.api_crud_biblioteca.domain.livro.services;

import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.app.livro.providers.LivroDataProvider;
import com.jeferson.api_crud_biblioteca.domain.exception.NegocioException;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.Livro;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.enums.GeneroPojo;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.BuscarLivroPorIdUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarLivroPorIdServiceTest {

    @Mock
    private LivroDataProvider livroDataProvider;

    @InjectMocks
    private BuscarLivroPorIdService buscarLivroPorIdService;

    private BuscarLivroPorIdUseCase buscarLivroPorIdUseCase;
    private Livro livro;

    @BeforeEach
    void setUp() {
        buscarLivroPorIdUseCase = buscarLivroPorIdService;

        livro = Livro.builder()
                .id("1")
                .titulo("Clean Code")
                .autor("Robert C. Martin")
                .isbn("978-0132350884")
                .anoPublicacao(2008)
                .genero(GeneroPojo.TECNOLOGIA)
                .disponivel(true)
                .dataInclusao(LocalDateTime.now())
                .build();
    }

    @Test
    void buscarPorId_encontrado_retornaLivroResponseDTO() {
        when(livroDataProvider.findById("1")).thenReturn(Optional.of(livro));

        LivroResponseDTO resultado = buscarLivroPorIdUseCase.buscarPorId("1");

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo("1");
        assertThat(resultado.titulo()).isEqualTo("Clean Code");
    }

    @Test
    void buscarPorId_naoEncontrado_lancaNegocioException() {
        when(livroDataProvider.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarLivroPorIdUseCase.buscarPorId("999"))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo("LIVRO_NAO_ENCONTRADO");
    }

    @Test
    void buscarPorId_naoEncontrado_retornaStatus404() {
        when(livroDataProvider.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarLivroPorIdUseCase.buscarPorId("999"))
                .isInstanceOf(NegocioException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
