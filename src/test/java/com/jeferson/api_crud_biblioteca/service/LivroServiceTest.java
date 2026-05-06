package com.jeferson.api_crud_biblioteca.service;

import com.jeferson.api_crud_biblioteca.domain.Livro;
import com.jeferson.api_crud_biblioteca.domain.enums.GeneroPojo;
import com.jeferson.api_crud_biblioteca.dto.LivroRequestDTO;
import com.jeferson.api_crud_biblioteca.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.exception.NegocioException;
import com.jeferson.api_crud_biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private LivroService livroService;

    private LivroRequestDTO requestDTO;
    private Livro livro;

    @BeforeEach
    void setUp() {
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

    // ========== criar ==========

    @Test
    void criar_sucesso_retornaLivroResponseDTO() {
        when(livroRepository.existsByIsbn(requestDTO.isbn())).thenReturn(false);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        LivroResponseDTO resultado = livroService.criar(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.titulo()).isEqualTo("Clean Code");
        assertThat(resultado.isbn()).isEqualTo("978-0132350884");
        assertThat(resultado.genero()).isEqualTo(GeneroPojo.TECNOLOGIA);
        verify(livroRepository).save(any(Livro.class));
    }

    @Test
    void criar_isbnDuplicado_lancaNegocioException() {
        when(livroRepository.existsByIsbn(requestDTO.isbn())).thenReturn(true);

        assertThatThrownBy(() -> livroService.criar(requestDTO))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("ISBN")
                .extracting("codigo")
                .isEqualTo("ISBN_DUPLICADO");

        verify(livroRepository, never()).save(any());
    }

    @Test
    void criar_anoFuturo_lancaNegocioException() {
        LivroRequestDTO dtoAnoFuturo = new LivroRequestDTO(
                "Livro do Futuro", "Autor", "isbn-999", 9999, GeneroPojo.FANTASIA, true
        );

        assertThatThrownBy(() -> livroService.criar(dtoAnoFuturo))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo("ANO_INVALIDO");

        verify(livroRepository, never()).save(any());
    }

    // ========== buscarPorId ==========

    @Test
    void buscarPorId_encontrado_retornaLivroResponseDTO() {
        when(livroRepository.findById("1")).thenReturn(Optional.of(livro));

        LivroResponseDTO resultado = livroService.buscarPorId("1");

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo("1");
        assertThat(resultado.titulo()).isEqualTo("Clean Code");
    }

    @Test
    void buscarPorId_naoEncontrado_lancaNegocioException() {
        when(livroRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livroService.buscarPorId("999"))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo("LIVRO_NAO_ENCONTRADO");
    }

    @Test
    void buscarPorId_naoEncontrado_retornaStatus404() {
        when(livroRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livroService.buscarPorId("999"))
                .isInstanceOf(NegocioException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ========== listar ==========

    @Test
    void listar_semFiltroGenero_retornaPaginaComLivros() {
        Page<Livro> page = new PageImpl<>(List.of(livro), PageRequest.of(0, 10), 1);
        when(livroRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<LivroResponseDTO> resultado = livroService.listar(0, 10, null);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).titulo()).isEqualTo("Clean Code");
    }

    @Test
    void listar_comFiltroGenero_retornaPaginaFiltrada() {
        Page<Livro> page = new PageImpl<>(List.of(livro), PageRequest.of(0, 10), 1);
        when(livroRepository.findByGenero(eq(GeneroPojo.TECNOLOGIA), any(PageRequest.class))).thenReturn(page);

        Page<LivroResponseDTO> resultado = livroService.listar(0, 10, GeneroPojo.TECNOLOGIA);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).genero()).isEqualTo(GeneroPojo.TECNOLOGIA);
        verify(livroRepository, never()).findAll(any(PageRequest.class));
    }

    // ========== atualizar ==========

    @Test
    void atualizar_sucesso_retornaLivroAtualizado() {
        when(livroRepository.findById("1")).thenReturn(Optional.of(livro));
        when(livroRepository.existsByIsbnAndIdNot(requestDTO.isbn(), "1")).thenReturn(false);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        LivroResponseDTO resultado = livroService.atualizar("1", requestDTO);

        assertThat(resultado).isNotNull();
        verify(livroRepository).save(any(Livro.class));
    }

    @Test
    void atualizar_naoEncontrado_lancaNegocioException() {
        when(livroRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livroService.atualizar("999", requestDTO))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo("LIVRO_NAO_ENCONTRADO");

        verify(livroRepository, never()).save(any());
    }

    @Test
    void atualizar_isbnDuplicadoEmOutroLivro_lancaNegocioException() {
        when(livroRepository.findById("1")).thenReturn(Optional.of(livro));
        when(livroRepository.existsByIsbnAndIdNot(requestDTO.isbn(), "1")).thenReturn(true);

        assertThatThrownBy(() -> livroService.atualizar("1", requestDTO))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo("ISBN_DUPLICADO");

        verify(livroRepository, never()).save(any());
    }

    @Test
    void atualizar_anoFuturo_lancaNegocioException() {
        LivroRequestDTO dtoAnoFuturo = new LivroRequestDTO(
                "Livro", "Autor", "isbn-999", 9999, GeneroPojo.ROMANCE, true
        );
        when(livroRepository.findById("1")).thenReturn(Optional.of(livro));

        assertThatThrownBy(() -> livroService.atualizar("1", dtoAnoFuturo))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo("ANO_INVALIDO");
    }

    // ========== deletar ==========

    @Test
    void deletar_sucesso_chamaDeleteById() {
        when(livroRepository.existsById("1")).thenReturn(true);
        doNothing().when(livroRepository).deleteById("1");

        livroService.deletar("1");

        verify(livroRepository).deleteById("1");
    }

    @Test
    void deletar_naoEncontrado_lancaNegocioException() {
        when(livroRepository.existsById("999")).thenReturn(false);

        assertThatThrownBy(() -> livroService.deletar("999"))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo("LIVRO_NAO_ENCONTRADO");

        verify(livroRepository, never()).deleteById(any());
    }
}
