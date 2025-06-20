package com.comoencasa_backend.controller;

import com.comoencasa_backend.model.Categoria;
import com.comoencasa_backend.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests TDD para CategoriaController
 * Implementa cobertura completa y casos edge siguiendo patrones TDD
 * 
 * @author Como En Casa Team
 * @version 1.0
 */
@WebMvcTest(CategoriaController.class)
@DisplayName("CategoriaController TDD Tests")
class CategoriaControllerTDDTest {

     @Autowired
     private MockMvc mockMvc;
     @MockBean
     private CategoriaRepository categoriaRepository;

     private List<Categoria> categoriasMock;
     private Categoria categoriaTortas;
     private Categoria categoriaEventos;
     private Categoria categoriaPostres;

     @BeforeEach
     void setUp() {
          // Datos de prueba que reflejan la estructura real de la base de datos
          categoriaTortas = new Categoria(1L, "Tortas", "Tortas para ocasiones especiales");
          categoriaEventos = new Categoria(2L, "Eventos", "Productos para eventos y celebraciones");
          categoriaPostres = new Categoria(3L, "Postres", "Postres variados y dulces");

          categoriasMock = Arrays.asList(categoriaTortas, categoriaEventos, categoriaPostres);
     }

     @Nested
     @DisplayName("Tests para GET /api/categorias")
     class GetAllCategoriasTests {

          @Test
          @DisplayName("Debe retornar todas las categorías ordenadas por nombre")
          void debeRetornarTodasLasCategoriasOrdenadasPorNombre() throws Exception {
               // Given
               when(categoriaRepository.findAllOrderByNombre()).thenReturn(categoriasMock);

               // When & Then
               mockMvc.perform(get("/api/categorias")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$.length()").value(3))
                         .andExpect(jsonPath("$[0].id").value(1))
                         .andExpect(jsonPath("$[0].nombre").value("Tortas"))
                         .andExpect(jsonPath("$[1].id").value(2))
                         .andExpect(jsonPath("$[1].nombre").value("Eventos"))
                         .andExpect(jsonPath("$[2].id").value(3))
                         .andExpect(jsonPath("$[2].nombre").value("Postres"));
          }

          @Test
          @DisplayName("Debe manejar lista vacía de categorías")
          void debeMAnejarListaVaciaDeCategoras() throws Exception {
               // Given
               when(categoriaRepository.findAllOrderByNombre()).thenReturn(Arrays.asList());

               // When & Then
               mockMvc.perform(get("/api/categorias")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$.length()").value(0));
          }

          @Test
          @DisplayName("Debe manejar error del repositorio")
          void debeMAnejarErrorDelRepositorio() throws Exception {
               // Given
               when(categoriaRepository.findAllOrderByNombre()).thenThrow(new RuntimeException("Error de BD"));

               // When & Then
               mockMvc.perform(get("/api/categorias")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isInternalServerError());
          }
     }

     @Nested
     @DisplayName("Tests para GET /api/categorias/{id}")
     class GetCategoriaByIdTests {

          @Test
          @DisplayName("Debe retornar categoría cuando existe el ID")
          void debeRetornarCategoriaCandiDatoExiste() throws Exception {
               // Given
               when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTortas));

               // When & Then
               mockMvc.perform(get("/api/categorias/1")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$.id").value(1))
                         .andExpect(jsonPath("$.nombre").value("Tortas"))
                         .andExpect(jsonPath("$.descripcion").value("Tortas para ocasiones especiales"));
          }

          @Test
          @DisplayName("Debe retornar 404 cuando no existe el ID")
          void debeRetornar404CuandoNoExisteElId() throws Exception {
               // Given
               when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

               // When & Then
               mockMvc.perform(get("/api/categorias/99")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isNotFound());
          }

          @Test
          @DisplayName("Debe manejar error del repositorio por ID")
          void debeManejarErrorDelRepositorioPorId() throws Exception {
               // Given
               when(categoriaRepository.findById(any())).thenThrow(new RuntimeException("Error de BD"));

               // When & Then
               mockMvc.perform(get("/api/categorias/1")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isInternalServerError());
          }
     }

     @Nested
     @DisplayName("Tests para GET /api/categorias/buscar")
     class GetCategoriaByNombreTests {

          @Test
          @DisplayName("Debe encontrar categoría por nombre exacto")
          void debeEncontrarCategoriaPorNombreExacto() throws Exception {
               // Given
               when(categoriaRepository.findByNombreIgnoreCase("Tortas")).thenReturn(Optional.of(categoriaTortas));

               // When & Then
               mockMvc.perform(get("/api/categorias/buscar")
                         .param("nombre", "Tortas")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$.id").value(1))
                         .andExpect(jsonPath("$.nombre").value("Tortas"));
          }

          @Test
          @DisplayName("Debe encontrar categoría ignorando mayúsculas y minúsculas")
          void debeEncontrarCategoriaIgnorandoMayusculasYMinusculas() throws Exception {
               // Given
               when(categoriaRepository.findByNombreIgnoreCase("TORTAS")).thenReturn(Optional.of(categoriaTortas));

               // When & Then
               mockMvc.perform(get("/api/categorias/buscar")
                         .param("nombre", "TORTAS")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.id").value(1))
                         .andExpect(jsonPath("$.nombre").value("Tortas"));
          }

          @Test
          @DisplayName("Debe retornar 404 cuando no existe el nombre")
          void debeRetornar404CuandoNoExisteElNombre() throws Exception {
               // Given
               when(categoriaRepository.findByNombreIgnoreCase("NoExiste")).thenReturn(Optional.empty());

               // When & Then
               mockMvc.perform(get("/api/categorias/buscar")
                         .param("nombre", "NoExiste")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isNotFound());
          }
     }

     @Nested
     @DisplayName("Tests para GET /api/categorias/existe")
     class ExisteCategoriaByNombreTests {

          @Test
          @DisplayName("Debe retornar true cuando la categoría existe")
          void debeRetornarTrueCuandoLaCategoriaExiste() throws Exception {
               // Given
               when(categoriaRepository.findByNombreIgnoreCase("Tortas")).thenReturn(Optional.of(categoriaTortas));

               // When & Then
               mockMvc.perform(get("/api/categorias/existe")
                         .param("nombre", "Tortas")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(content().string("true"));
          }

          @Test
          @DisplayName("Debe retornar false cuando la categoría no existe")
          void debeRetornarFalseCuandoLaCategoriaNikrExiste() throws Exception {
               // Given
               when(categoriaRepository.findByNombreIgnoreCase("NoExiste")).thenReturn(Optional.empty());

               // When & Then
               mockMvc.perform(get("/api/categorias/existe")
                         .param("nombre", "NoExiste")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(content().string("false"));
          }

          @Test
          @DisplayName("Debe manejar error en verificación de existencia")
          void debeManejarErrorEnVerificacionDeExistencia() throws Exception {
               // Given
               when(categoriaRepository.findByNombreIgnoreCase(anyString()))
                         .thenThrow(new RuntimeException("Error de BD"));

               // When & Then
               mockMvc.perform(get("/api/categorias/existe")
                         .param("nombre", "Tortas")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isInternalServerError());
          }
     }

     @Nested
     @DisplayName("Tests de Casos Edge y Validaciones")
     class CasosEdgeYValidacionesTests {

          @Test
          @DisplayName("Debe manejar parámetros vacíos en búsqueda por nombre")
          void debeManejarParametrosVaciosEnBusquedaPorNombre() throws Exception {
               mockMvc.perform(get("/api/categorias/buscar")
                         .param("nombre", "")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isBadRequest());
          }

          @Test
          @DisplayName("Debe manejar caracteres especiales en nombres de categoría")
          void debeManejarCaracteresEspecialesEnNombresDeCategoria() throws Exception {
               // Given
               String nombreEspecial = "Tortas & Pasteles";
               Categoria categoriaEspecial = new Categoria(4L, nombreEspecial, "Categoría con caracteres especiales");
               when(categoriaRepository.findByNombreIgnoreCase(nombreEspecial))
                         .thenReturn(Optional.of(categoriaEspecial));

               // When & Then
               mockMvc.perform(get("/api/categorias/buscar")
                         .param("nombre", nombreEspecial)
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.nombre").value(nombreEspecial));
          }
     }
}
