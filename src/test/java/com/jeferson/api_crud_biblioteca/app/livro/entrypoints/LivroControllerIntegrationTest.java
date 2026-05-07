package com.jeferson.api_crud_biblioteca.app.livro.entrypoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jeferson.api_crud_biblioteca.app.livro.entrypoints.dto.LivroRequestDTO;
import com.jeferson.api_crud_biblioteca.app.livro.providers.LivroRepository;
import com.jeferson.api_crud_biblioteca.domain.livro.entity.enums.GeneroPojo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class LivroControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroResource livroResource;

    @Autowired
    private LivroRepository livroRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        livroRepository.deleteAll();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private LivroRequestDTO livroValido() {
        return new LivroRequestDTO(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008,
                GeneroPojo.TECNOLOGIA,
                true
        );
    }

    // ========== POST /livros ==========

    @Test
    void criarLivro_sucesso_retorna201ComLivro() throws Exception {
        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.titulo").value("Clean Code"))
                .andExpect(jsonPath("$.isbn").value("978-0132350884"))
                .andExpect(jsonPath("$.genero").value("TECNOLOGIA"));
    }

    @Test
    void criarLivro_tituloVazio_retorna400() throws Exception {
        LivroRequestDTO dto = new LivroRequestDTO("", "Autor", "isbn-001", 2020, GeneroPojo.FANTASIA, true);

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DADOS_INVALIDOS"));
    }

    @Test
    void criarLivro_isbnDuplicado_retorna422() throws Exception {
        mockMvc.perform(post("/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(livroValido())));

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroValido())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("ISBN_DUPLICADO"));
    }

    @Test
    void criarLivro_anoFuturo_retorna422() throws Exception {
        LivroRequestDTO dto = new LivroRequestDTO("Futuro", "Autor", "isbn-002", 9999, GeneroPojo.ROMANCE, true);

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("ANO_INVALIDO"));
    }

    // ========== GET /livros/{id} ==========

    @Test
    void buscarPorId_encontrado_retorna200() throws Exception {
        String response = mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroValido())))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/livros/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Clean Code"));
    }

    @Test
    void buscarPorId_naoEncontrado_retorna404() throws Exception {
        mockMvc.perform(get("/livros/id-inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("LIVRO_NAO_ENCONTRADO"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // ========== GET /livros ==========

    @Test
    void listar_semFiltro_retorna200ComPaginacao() throws Exception {
        mockMvc.perform(post("/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(livroValido())));

        mockMvc.perform(get("/livros")
                        .param("pagina", "0")
                        .param("tamanho", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(greaterThan(0)))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void listar_comFiltroGenero_retorna200ComResultadosFiltrados() throws Exception {
        mockMvc.perform(post("/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(livroValido())));

        mockMvc.perform(get("/livros")
                        .param("genero", "TECNOLOGIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].genero").value("TECNOLOGIA"));
    }

    // ========== PUT /livros/{id} ==========

    @Test
    void atualizar_sucesso_retorna200ComDadosAtualizados() throws Exception {
        String response = mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroValido())))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        LivroRequestDTO atualizado = new LivroRequestDTO(
                "Clean Code - Edição Revisada",
                "Robert C. Martin",
                "978-0132350884",
                2008,
                GeneroPojo.TECNOLOGIA,
                false
        );

        mockMvc.perform(put("/livros/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Clean Code - Edição Revisada"))
                .andExpect(jsonPath("$.disponivel").value(false));
    }

    @Test
    void atualizar_naoEncontrado_retorna404() throws Exception {
        mockMvc.perform(put("/livros/id-inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroValido())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("LIVRO_NAO_ENCONTRADO"));
    }

    @Test
    void atualizar_dadosInvalidos_retorna400() throws Exception {
        String response = mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroValido())))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        LivroRequestDTO invalido = new LivroRequestDTO("", "", "", 2020, GeneroPojo.ROMANCE, true);

        mockMvc.perform(put("/livros/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DADOS_INVALIDOS"));
    }

    // ========== DELETE /livros/{id} ==========

    @Test
    void deletar_sucesso_retorna204() throws Exception {
        String response = mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroValido())))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/livros/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletar_naoEncontrado_retorna404() throws Exception {
        mockMvc.perform(delete("/livros/id-inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("LIVRO_NAO_ENCONTRADO"));
    }
}
