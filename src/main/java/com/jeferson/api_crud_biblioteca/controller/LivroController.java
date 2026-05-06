package com.jeferson.api_crud_biblioteca.controller;

import com.jeferson.api_crud_biblioteca.domain.enums.GeneroPojo;
import com.jeferson.api_crud_biblioteca.dto.LivroRequestDTO;
import com.jeferson.api_crud_biblioteca.dto.LivroResponseDTO;
import com.jeferson.api_crud_biblioteca.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
@Tag(name = "Livros", description = "API de gerenciamento de livros da biblioteca")
public class LivroController {

    private final LivroService livroService;

    @PostMapping
    @Operation(summary = "Cria um novo livro")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Livro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "422", description = "Erro de negócio (ex: ISBN duplicado)")
    })
    public ResponseEntity<LivroResponseDTO> criar(@RequestBody @Valid LivroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(livroService.criar(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um livro por ID (com cache Redis)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livro encontrado"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<LivroResponseDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Lista todos os livros com paginação")
    @ApiResponse(responseCode = "200", description = "Lista paginada de livros")
    public ResponseEntity<Page<LivroResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) GeneroPojo genero) {
        return ResponseEntity.ok(livroService.listar(pagina, tamanho, genero));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um livro existente (invalida cache)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado"),
            @ApiResponse(responseCode = "422", description = "Erro de negócio (ex: ISBN duplicado)")
    })
    public ResponseEntity<LivroResponseDTO> atualizar(
            @PathVariable String id,
            @RequestBody @Valid LivroRequestDTO dto) {
        return ResponseEntity.ok(livroService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um livro por ID (invalida cache)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Livro removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
