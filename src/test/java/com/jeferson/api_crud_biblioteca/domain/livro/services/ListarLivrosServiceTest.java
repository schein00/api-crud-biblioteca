package com.jeferson.api_crud_biblioteca.domain.livro.services;

import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.app.livro.providers.LivroDataProvider;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.Livro;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.enums.GeneroPojo;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.ListarLivrosUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarLivrosServiceTest {

    @Mock
    private LivroDataProvider livroDataProvider;

    @InjectMocks
    private ListarLivrosService listarLivrosService;

    private ListarLivrosUseCase listarLivrosUseCase;
    private Livro livro;

    @BeforeEach
    void setUp() {
        listarLivrosUseCase = listarLivrosService;

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
    void listar_semFiltroGenero_retornaPaginaComLivros() {
        Page<Livro> page = new PageImpl<>(List.of(livro), PageRequest.of(0, 10), 1);
        when(livroDataProvider.findAll(any(PageRequest.class))).thenReturn(page);

        Page<LivroResponseDTO> resultado = listarLivrosUseCase.listar(0, 10, null);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).titulo()).isEqualTo("Clean Code");
    }

    @Test
    void listar_comFiltroGenero_retornaPaginaFiltrada() {
        Page<Livro> page = new PageImpl<>(List.of(livro), PageRequest.of(0, 10), 1);
        when(livroDataProvider.findByGenero(eq(GeneroPojo.TECNOLOGIA), any(PageRequest.class))).thenReturn(page);

        Page<LivroResponseDTO> resultado = listarLivrosUseCase.listar(0, 10, GeneroPojo.TECNOLOGIA);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).genero()).isEqualTo(GeneroPojo.TECNOLOGIA);
        verify(livroDataProvider, never()).findAll(any(PageRequest.class));
    }
}
