package com.jeferson.api_crud_biblioteca.domain.livro.services;

import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroRequestDTO;
import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.app.livro.providers.LivroDataProvider;
import com.jeferson.api_crud_biblioteca.domain.exception.NegocioException;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.Livro;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.enums.GeneroPojo;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.CriarLivroUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarLivroServiceTest {

    @Mock
    private LivroDataProvider livroDataProvider;

    @InjectMocks
    private CriarLivroService criarLivroService;

    private CriarLivroUseCase criarLivroUseCase;
    private LivroRequestDTO requestDTO;
    private Livro livro;

    @BeforeEach
    void setUp() {
        criarLivroUseCase = criarLivroService;

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
    void criar_sucesso_retornaLivroResponseDTO() {
        when(livroDataProvider.existsByIsbn(requestDTO.isbn())).thenReturn(false);
        when(livroDataProvider.save(any(Livro.class))).thenReturn(livro);

        LivroResponseDTO resultado = criarLivroUseCase.criar(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.titulo()).isEqualTo("Clean Code");
        assertThat(resultado.isbn()).isEqualTo("978-0132350884");
        assertThat(resultado.genero()).isEqualTo(GeneroPojo.TECNOLOGIA);
        verify(livroDataProvider).save(any(Livro.class));
    }

    @Test
    void criar_isbnDuplicado_lancaNegocioException() {
        when(livroDataProvider.existsByIsbn(requestDTO.isbn())).thenReturn(true);

        assertThatThrownBy(() -> criarLivroUseCase.criar(requestDTO))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("ISBN")
                .extracting("codigo")
                .isEqualTo("ISBN_DUPLICADO");

        verify(livroDataProvider, never()).save(any());
    }

    @Test
    void criar_anoFuturo_lancaNegocioException() {
        LivroRequestDTO dtoAnoFuturo = new LivroRequestDTO(
                "Livro do Futuro", "Autor", "isbn-999", 9999, GeneroPojo.FANTASIA, true
        );

        assertThatThrownBy(() -> criarLivroUseCase.criar(dtoAnoFuturo))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo("ANO_INVALIDO");

        verify(livroDataProvider, never()).save(any());
    }
}
