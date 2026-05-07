package com.jeferson.api_crud_biblioteca.domain.livro.services;

import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroRequestDTO;
import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.app.livro.providers.LivroDataProvider;
import com.jeferson.api_crud_biblioteca.domain.exception.NegocioException;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.Livro;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.enums.GeneroPojo;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.AtualizarLivroUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarLivroServiceTest {

    @Mock
    private LivroDataProvider livroDataProvider;

    @InjectMocks
    private AtualizarLivroService atualizarLivroService;

    private AtualizarLivroUseCase atualizarLivroUseCase;
    private LivroRequestDTO requestDTO;
    private Livro livro;

    @BeforeEach
    void setUp() {
        atualizarLivroUseCase = atualizarLivroService;

        requestDTO = new LivroRequestDTO(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008,
                GeneroPojo.TECNOLOGIA,
                true
        );

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
    void atualizar_sucesso_retornaLivroAtualizado() {
        when(livroDataProvider.findById("1")).thenReturn(Optional.of(livro));
        when(livroDataProvider.existsByIsbnAndIdNot(requestDTO.isbn(), "1")).thenReturn(false);
        when(livroDataProvider.save(any(Livro.class))).thenReturn(livro);

        LivroResponseDTO resultado = atualizarLivroUseCase.atualizar("1", requestDTO);

        assertThat(resultado).isNotNull();
        verify(livroDataProvider).save(any(Livro.class));
    }

    @Test
    void atualizar_naoEncontrado_lancaNegocioException() {
        when(livroDataProvider.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atualizarLivroUseCase.atualizar("999", requestDTO))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo("LIVRO_NAO_ENCONTRADO");

        verify(livroDataProvider, never()).save(any());
    }

    @Test
    void atualizar_isbnDuplicadoEmOutroLivro_lancaNegocioException() {
        when(livroDataProvider.findById("1")).thenReturn(Optional.of(livro));
        when(livroDataProvider.existsByIsbnAndIdNot(requestDTO.isbn(), "1")).thenReturn(true);

        assertThatThrownBy(() -> atualizarLivroUseCase.atualizar("1", requestDTO))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo("ISBN_DUPLICADO");

        verify(livroDataProvider, never()).save(any());
    }

    @Test
    void atualizar_anoFuturo_lancaNegocioException() {
        LivroRequestDTO dtoAnoFuturo = new LivroRequestDTO(
                "Livro", "Autor", "isbn-999", 9999, GeneroPojo.ROMANCE, true
        );
        when(livroDataProvider.findById("1")).thenReturn(Optional.of(livro));

        assertThatThrownBy(() -> atualizarLivroUseCase.atualizar("1", dtoAnoFuturo))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo("ANO_INVALIDO");
    }
}
