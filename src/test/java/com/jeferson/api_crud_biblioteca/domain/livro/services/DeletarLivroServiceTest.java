package com.jeferson.api_crud_biblioteca.domain.livro.services;

import com.jeferson.api_crud_biblioteca.app.livro.providers.LivroDataProvider;
import com.jeferson.api_crud_biblioteca.domain.exception.NegocioException;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.DeletarLivroUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletarLivroServiceTest {

    @Mock
    private LivroDataProvider livroDataProvider;

    @InjectMocks
    private DeletarLivroService deletarLivroService;

    private DeletarLivroUseCase deletarLivroUseCase;

    @BeforeEach
    void setUp() {
        deletarLivroUseCase = deletarLivroService;
    }

    @Test
    void deletar_sucesso_chamaDeleteById() {
        when(livroDataProvider.existsById("1")).thenReturn(true);
        doNothing().when(livroDataProvider).deleteById("1");

        deletarLivroUseCase.deletar("1");

        verify(livroDataProvider).deleteById("1");
    }

    @Test
    void deletar_naoEncontrado_lancaNegocioException() {
        when(livroDataProvider.existsById("999")).thenReturn(false);

        assertThatThrownBy(() -> deletarLivroUseCase.deletar("999"))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo("LIVRO_NAO_ENCONTRADO");

        verify(livroDataProvider, never()).deleteById(any());
    }
}
