package com.jeferson.api_crud_biblioteca.app.livro.entrypoints;

import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroRequestDTO;
import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.enums.GeneroPojo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Livros", description = "API de gerenciamento de livros da biblioteca")
@RequestMapping("/livros")
public interface LivroResource {

    @PostMapping
    @Operation(summary = "Cria um novo livro")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Livro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "422", description = "Erro de negócio (ex: ISBN duplicado)")
    })
    ResponseEntity<LivroResponseDTO> criar(@RequestBody @Valid LivroRequestDTO dto);

    @GetMapping("/{id}")
    @Operation(summary = "Busca um livro por ID (com cache Redis)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livro encontrado"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    ResponseEntity<LivroResponseDTO> buscarPorId(@PathVariable String id);

    @GetMapping
    @Operation(summary = "Lista todos os livros com paginação")
    @ApiResponse(responseCode = "200", description = "Lista paginada de livros")
    ResponseEntity<Page<LivroResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) GeneroPojo genero);

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um livro existente (invalida cache)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado"),
            @ApiResponse(responseCode = "422", description = "Erro de negócio (ex: ISBN duplicado)")
    })
    ResponseEntity<LivroResponseDTO> atualizar(
            @PathVariable String id,
            @RequestBody @Valid LivroRequestDTO dto);

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um livro por ID (invalida cache)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Livro removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    ResponseEntity<Void> deletar(@PathVariable String id);
}
