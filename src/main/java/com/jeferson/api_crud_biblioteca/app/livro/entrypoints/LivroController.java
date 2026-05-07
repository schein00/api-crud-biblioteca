package com.jeferson.api_crud_biblioteca.app.livro.entrypoints;

import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroRequestDTO;
import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.enums.GeneroPojo;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.AtualizarLivroUseCase;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.BuscarLivroPorIdUseCase;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.CriarLivroUseCase;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.DeletarLivroUseCase;
import com.jeferson.api_crud_biblioteca.domain.livro.usecases.ListarLivrosUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LivroController implements LivroResource {

    private final CriarLivroUseCase criarLivroUseCase;
    private final BuscarLivroPorIdUseCase buscarLivroPorIdUseCase;
    private final ListarLivrosUseCase listarLivrosUseCase;
    private final AtualizarLivroUseCase atualizarLivroUseCase;
    private final DeletarLivroUseCase deletarLivroUseCase;

    @Override
    public ResponseEntity<LivroResponseDTO> criar(LivroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(criarLivroUseCase.criar(dto));
    }

    @Override
    public ResponseEntity<LivroResponseDTO> buscarPorId(String id) {
        return ResponseEntity.ok(buscarLivroPorIdUseCase.buscarPorId(id));
    }

    @Override
    public ResponseEntity<Page<LivroResponseDTO>> listar(int pagina, int tamanho, GeneroPojo genero) {
        return ResponseEntity.ok(listarLivrosUseCase.listar(pagina, tamanho, genero));
    }

    @Override
    public ResponseEntity<LivroResponseDTO> atualizar(String id, LivroRequestDTO dto) {
        return ResponseEntity.ok(atualizarLivroUseCase.atualizar(id, dto));
    }

    @Override
    public ResponseEntity<Void> deletar(String id) {
        deletarLivroUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
