package com.comoencasa_backend.controller;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ProductoController - Tests Adicionales de Cobertura")
class ProductoControllerCoberturaTDDTest {
     @Autowired
     private MockMvc mockMvc;

     @Autowired
     private ProductoRepository productoRepository;

     private Producto productoTest1;
     private Producto productoTest2;

     @BeforeEach
     void setUp() {
          // Limpiar base de datos
          productoRepository.deleteAll();

          // Crear productos de prueba
          productoTest1 = new Producto();
          productoTest1.setNombre("Torta Red Velvet");
          productoTest1.setDescripcion("Deliciosa torta red velvet con frosting de queso crema");
          productoTest1.setPrecioVenta(45.99);
          productoTest1.setCostoProduccion(25.00);
          productoTest1.setCategoriaId(1L);
          productoTest1.setDisponible(true);
          productoTest1.setCantidad(15);
          productoTest1.setImagenUrl("/images/red-velvet.jpg");

          productoTest2 = new Producto();
          productoTest2.setNombre("Brownie Chocolate");
          productoTest2.setDescripcion("Brownie casero con chocolate belga");
          productoTest2.setPrecioVenta(18.50);
          productoTest2.setCostoProduccion(8.00);
          productoTest2.setCategoriaId(2L);
          productoTest2.setDisponible(true);
          productoTest2.setCantidad(25);
          productoTest2.setImagenUrl("/images/brownie.jpg");
     }

     @Nested
     @DisplayName("GET /api/productos - Casos adicionales")
     class ListarProductosCasosAdicionales {

          @Test
          @DisplayName("GREEN: Debería retornar productos ordenados correctamente")
          void deberiaRetornarProductosOrdenadosCorrectamente() throws Exception {
               // Given
               productoRepository.save(productoTest1);
               productoRepository.save(productoTest2);

               // When & Then
               mockMvc.perform(get("/api/productos")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$", hasSize(2)))
                         .andExpect(jsonPath("$[*].nombre", hasItems("Torta Red Velvet", "Brownie Chocolate")))
                         .andExpect(jsonPath("$[*].disponible", everyItem(is(true))));
          }

          @Test
          @DisplayName("GREEN: Debería incluir todos los campos de producto")
          void deberiaIncluirTodosLosCamposDeProducto() throws Exception {
               // Given
               Producto saved = productoRepository.save(productoTest1);

               // When & Then
               mockMvc.perform(get("/api/productos")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$[0].id", is(saved.getId().intValue())))
                         .andExpect(jsonPath("$[0].nombre", is("Torta Red Velvet")))
                         .andExpect(jsonPath("$[0].descripcion",
                                   is("Deliciosa torta red velvet con frosting de queso crema")))
                         .andExpect(jsonPath("$[0].precioVenta", is(45.99)))
                         .andExpect(jsonPath("$[0].categoriaId", is(1)))
                         .andExpect(jsonPath("$[0].disponible", is(true)))
                         .andExpect(jsonPath("$[0].cantidad", is(15)))
                         .andExpect(jsonPath("$[0].imagenUrl", is("/images/red-velvet.jpg")));
          }

          @Test
          @DisplayName("GREEN: Debería manejar productos con campos nulos")
          void deberiaManejarProductosConCamposNulos() throws Exception {
               // Given - Producto con algunos campos opcionales nulos pero campos requeridos
               // completos
               Producto productoConNulos = new Producto();
               productoConNulos.setNombre("Producto Básico");
               productoConNulos.setPrecioVenta(10.00);
               productoConNulos.setCostoProduccion(5.00); // Campo requerido
               productoConNulos.setCategoriaId(1L); // Campo requerido
               productoConNulos.setDisponible(true);
               // descripcion e imagenUrl pueden ser nulos

               productoRepository.save(productoConNulos);

               // When & Then
               mockMvc.perform(get("/api/productos")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$", hasSize(1)))
                         .andExpect(jsonPath("$[0].nombre", is("Producto Básico")));
          }
     }

     @Nested
     @DisplayName("GET /api/productos/categoria/{categoriaId} - Casos adicionales")
     class ProductosPorCategoriaCasosAdicionales {

          @Test
          @DisplayName("GREEN: Debería filtrar productos por categoría correctamente")
          void deberiaFiltrarProductosPorCategoriaCorrectamente() throws Exception {
               // Given
               productoRepository.save(productoTest1); // categoriaId = 1
               productoRepository.save(productoTest2); // categoriaId = 2

               // When & Then - Filtrar por categoría 1
               mockMvc.perform(get("/api/productos/categoria/1")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$", hasSize(1)))
                         .andExpect(jsonPath("$[0].nombre", is("Torta Red Velvet")))
                         .andExpect(jsonPath("$[0].categoriaId", is(1)));
          }

          @Test
          @DisplayName("GREEN: Debería retornar array vacío para categoría sin productos")
          void deberiaRetornarArrayVacioParaCategoriaSinProductos() throws Exception {
               // Given
               productoRepository.save(productoTest1);

               // When & Then - Categoría que no existe
               mockMvc.perform(get("/api/productos/categoria/999")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$", hasSize(0)));
          }

          @Test
          @DisplayName("GREEN: Debería manejar múltiples productos de la misma categoría")
          void deberiaManejarMultiplesProductosDeLaMismaCategoria() throws Exception {
               // Given - Dos productos de la misma categoría
               productoTest2.setCategoriaId(1L); // Cambiar a misma categoría
               productoRepository.save(productoTest1);
               productoRepository.save(productoTest2);

               // When & Then
               mockMvc.perform(get("/api/productos/categoria/1")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$", hasSize(2)))
                         .andExpect(jsonPath("$[*].categoriaId", everyItem(is(1))));
          }

          @Test
          @DisplayName("RED: Debería manejar categoriaId igual a 0")
          void deberiaManejarCategoriaIdIgualACero() throws Exception {
               // When & Then
               mockMvc.perform(get("/api/productos/categoria/0")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$", hasSize(0)));
          }

          @Test
          @DisplayName("RED: Debería manejar categoriaId negativo")
          void deberiaManejarCategoriaIdNegativo() throws Exception {
               // When & Then
               mockMvc.perform(get("/api/productos/categoria/-1")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$", hasSize(0)));
          }
     }

     @Nested
     @DisplayName("GET /api/productos/{id} - Casos adicionales")
     class ProductoPorIdCasosAdicionales {

          @Test
          @DisplayName("GREEN: Debería retornar producto completo con todos los campos")
          void deberiaRetornarProductoCompletoConTodosLosCampos() throws Exception {
               // Given
               Producto saved = productoRepository.save(productoTest1);

               // When & Then
               mockMvc.perform(get("/api/productos/" + saved.getId())
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                         .andExpect(jsonPath("$.nombre", is("Torta Red Velvet")))
                         .andExpect(jsonPath("$.descripcion", containsString("red velvet")))
                         .andExpect(jsonPath("$.precioVenta", is(45.99)))
                         .andExpect(jsonPath("$.costoProduccion", is(25.00)))
                         .andExpect(jsonPath("$.categoriaId", is(1)))
                         .andExpect(jsonPath("$.disponible", is(true)))
                         .andExpect(jsonPath("$.cantidad", is(15)))
                         .andExpect(jsonPath("$.imagenUrl", is("/images/red-velvet.jpg")));
          }

          @Test
          @DisplayName("RED: Debería retornar 404 para ID que no existe")
          void deberiaRetornar404ParaIdQueNoExiste() throws Exception {
               // When & Then
               mockMvc.perform(get("/api/productos/99999")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isNotFound());
          }

          @Test
          @DisplayName("RED: Debería retornar 404 para ID igual a 0")
          void deberiaRetornar404ParaIdIgualACero() throws Exception {
               // When & Then
               mockMvc.perform(get("/api/productos/0")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isNotFound());
          }

          @Test
          @DisplayName("RED: Debería retornar 404 para ID negativo")
          void deberiaRetornar404ParaIdNegativo() throws Exception {
               // When & Then
               mockMvc.perform(get("/api/productos/-1")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isNotFound());
          }

          @Test
          @DisplayName("GREEN: Debería manejar producto con algunos campos nulos")
          void deberiaManejarProductoConAlgunosCamposNulos() throws Exception {
               // Given - Producto con descripción nula pero campos requeridos completos
               Producto productoMinimo = new Producto();
               productoMinimo.setNombre("Producto Mínimo");
               productoMinimo.setPrecioVenta(5.00);
               productoMinimo.setCostoProduccion(2.50); // Campo requerido
               productoMinimo.setCategoriaId(1L); // Campo requerido
               productoMinimo.setDisponible(true);
               // descripcion puede ser nula
               Producto saved = productoRepository.save(productoMinimo);

               // When & Then
               mockMvc.perform(get("/api/productos/" + saved.getId())
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.nombre", is("Producto Mínimo")))
                         .andExpect(jsonPath("$.precioVenta", is(5.00)));
          }
     }

     @Nested
     @DisplayName("Manejo de tipos de datos y validaciones")
     class ManejoTiposDatos {

          @Test
          @DisplayName("RED: Debería manejar parámetro de categoría no numérico")
          void deberiaManejarParametroCategoriaNoNumerico() throws Exception {
               // When & Then
               mockMvc.perform(get("/api/productos/categoria/abc")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isBadRequest());
          }

          @Test
          @DisplayName("RED: Debería manejar parámetro de ID no numérico")
          void deberiaManejarParametroIdNoNumerico() throws Exception {
               // When & Then
               mockMvc.perform(get("/api/productos/abc")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isBadRequest());
          }

          @Test
          @DisplayName("GREEN: Debería manejar IDs muy grandes")
          void deberiaManejarIdsMuyGrandes() throws Exception {
               // When & Then
               mockMvc.perform(get("/api/productos/999999999999")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isNotFound());
          }
     }

     @Nested
     @DisplayName("Headers y Content-Type")
     class HeadersContentType {

          @Test
          @DisplayName("GREEN: Debería retornar Content-Type correcto")
          void deberiaRetornarContentTypeCorrecto() throws Exception {
               // Given
               productoRepository.save(productoTest1);

               // When & Then
               mockMvc.perform(get("/api/productos")
                         .accept(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType(MediaType.APPLICATION_JSON));
          }

          @Test
          @DisplayName("GREEN: Debería manejar diferentes Accept headers")
          void deberiaManejarDiferentesAcceptHeaders() throws Exception {
               // Given
               productoRepository.save(productoTest1);

               // When & Then
               mockMvc.perform(get("/api/productos")
                         .accept(MediaType.ALL))
                         .andExpect(status().isOk());
          }
     }

     @Nested
     @DisplayName("Performance y datos grandes")
     class PerformanceDatosGrandes {
          @Test
          @DisplayName("GREEN: Debería manejar lista grande de productos")
          void deberiaManejarListaGrandeDeProductos() throws Exception {
               // Given - Crear múltiples productos
               for (int i = 1; i <= 50; i++) {
                    Producto producto = new Producto();
                    producto.setNombre("Producto " + i);
                    producto.setPrecioVenta(10.0 + i);
                    producto.setCostoProduccion(5.0 + i); // Campo requerido
                    producto.setDisponible(true);
                    producto.setCategoriaId((long) (i % 5 + 1)); // Categorías 1-5
                    productoRepository.save(producto);
               }

               // When & Then
               mockMvc.perform(get("/api/productos")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$", hasSize(50)));
          }

          @Test
          @DisplayName("GREEN: Debería manejar productos con nombres largos")
          void deberiaManejarProductosConNombresLargos() throws Exception {
               // Given
               Producto productoNombreLargo = new Producto();
               productoNombreLargo.setNombre(
                         "Este es un producto con un nombre extremadamente largo que podría causar problemas en algunos sistemas pero debería manejarse correctamente en nuestra API");
               productoNombreLargo.setPrecioVenta(25.00);
               productoNombreLargo.setCostoProduccion(15.00); // Campo requerido
               productoNombreLargo.setCategoriaId(1L); // Campo requerido
               productoNombreLargo.setDisponible(true);
               Producto saved = productoRepository.save(productoNombreLargo);

               // When & Then
               mockMvc.perform(get("/api/productos/" + saved.getId())
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.nombre", containsString("extremadamente largo")));
          }
     }

     @Nested
     @DisplayName("Estados de productos")
     class EstadosProductos {
          @Test
          @DisplayName("GREEN: Debería filtrar productos no disponibles en listado general")
          void deberiaFiltrarProductosNoDisponiblesEnListadoGeneral() throws Exception {
               // Given
               productoTest1.setDisponible(false);
               productoRepository.save(productoTest1);
               productoRepository.save(productoTest2);

               // When & Then
               mockMvc.perform(get("/api/productos")
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$", hasSize(1))) // Solo productos disponibles
                         .andExpect(jsonPath("$[0].disponible", is(true)))
                         .andExpect(jsonPath("$[0].nombre", is("Brownie Chocolate")));
          }

          @Test
          @DisplayName("GREEN: Debería manejar productos con cantidad 0")
          void deberiaManejarProductosConCantidadCero() throws Exception {
               // Given
               productoTest1.setCantidad(0);
               Producto saved = productoRepository.save(productoTest1);

               // When & Then
               mockMvc.perform(get("/api/productos/" + saved.getId())
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.cantidad", is(0)));
          }

          @Test
          @DisplayName("GREEN: Debería manejar productos sin precio de costo")
          void deberiaManejarProductosSinPrecioCosto() throws Exception {
               // Given - Establecer costo mínimo requerido
               productoTest1.setCostoProduccion(1.00); // Valor mínimo válido en lugar de null
               Producto saved = productoRepository.save(productoTest1);

               // When & Then
               mockMvc.perform(get("/api/productos/" + saved.getId())
                         .contentType(MediaType.APPLICATION_JSON))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.nombre", is("Torta Red Velvet")))
                         .andExpect(jsonPath("$.costoProduccion", is(1.00)));
          }
     }
}
