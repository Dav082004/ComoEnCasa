package com.comoencasa_backend.testutil;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.model.Categoria;

/**
 * Factory para crear objetos de test siguiendo el patrón Test Data Builder
 * Facilita la creación de datos de prueba consistentes
 */
public class TestDataFactory {

    public static class ProductoTestBuilder {
        private Producto producto;

        public ProductoTestBuilder() {
            this.producto = new Producto();
            // Valores por defecto
            this.producto.setNombre("Producto Test");
            this.producto.setDescripcion("Descripción de producto test");
            this.producto.setPrecioVenta(100.0);
            this.producto.setCostoProduccion(50.0);
            this.producto.setDisponible(true);
            this.producto.setCantidad(10);
            this.producto.setCategoriaId(1L);
        }

        public ProductoTestBuilder conNombre(String nombre) {
            this.producto.setNombre(nombre);
            return this;
        }

        public ProductoTestBuilder conPrecio(Double precio) {
            this.producto.setPrecioVenta(precio);
            return this;
        }

        public ProductoTestBuilder conCosto(Double costo) {
            this.producto.setCostoProduccion(costo);
            return this;
        }

        public ProductoTestBuilder conCategoria(Long categoriaId) {
            this.producto.setCategoriaId(categoriaId);
            return this;
        }

        public ProductoTestBuilder noDisponible() {
            this.producto.setDisponible(false);
            return this;
        }

        public ProductoTestBuilder conCantidad(Integer cantidad) {
            this.producto.setCantidad(cantidad);
            return this;
        }

        public ProductoTestBuilder conId(Long id) {
            this.producto.setId(id);
            return this;
        }

        public ProductoTestBuilder conDescripcion(String descripcion) {
            this.producto.setDescripcion(descripcion);
            return this;
        }

        public ProductoTestBuilder conImagenUrl(String imagenUrl) {
            this.producto.setImagenUrl(imagenUrl);
            return this;
        }

        public ProductoTestBuilder conDisponible(Boolean disponible) {
            this.producto.setDisponible(disponible);
            return this;
        }

        public Producto build() {
            return this.producto;
        }
    }

    public static class UsuarioTestBuilder {
        private Usuario usuario;

        public UsuarioTestBuilder() {
            this.usuario = new Usuario();
            // Valores por defecto
            this.usuario.setNombre("Usuario");
            this.usuario.setApellido("Test");
            this.usuario.setEmail("test@test.com");
            this.usuario.setPassword("password123");
            this.usuario.setTelefono("123456789");
            this.usuario.setDireccion("Dirección Test");
            this.usuario.setTipoDocumento(Usuario.TipoDocumento.DNI);
            this.usuario.setNumeroDocumento("12345678");
            this.usuario.setRol(Usuario.Rol.CLIENTE);
            this.usuario.setActivado(true);
        }

        public UsuarioTestBuilder conNombre(String nombre) {
            this.usuario.setNombre(nombre);
            return this;
        }

        public UsuarioTestBuilder conApellido(String apellido) {
            this.usuario.setApellido(apellido);
            return this;
        }

        public UsuarioTestBuilder conNombreCompleto(String nombre, String apellido) {
            this.usuario.setNombre(nombre);
            this.usuario.setApellido(apellido);
            return this;
        }

        public UsuarioTestBuilder conNombreCompleto(String nombreCompleto) {
            String[] partes = nombreCompleto.split(" ", 2);
            this.usuario.setNombre(partes[0]);
            this.usuario.setApellido(partes.length > 1 ? partes[1] : "");
            return this;
        }

        public UsuarioTestBuilder conEmail(String email) {
            this.usuario.setEmail(email);
            return this;
        }

        public UsuarioTestBuilder conPassword(String password) {
            this.usuario.setPassword(password);
            return this;
        }

        public UsuarioTestBuilder conTelefono(String telefono) {
            this.usuario.setTelefono(telefono);
            return this;
        }

        public UsuarioTestBuilder conDireccion(String direccion) {
            this.usuario.setDireccion(direccion);
            return this;
        }

        public UsuarioTestBuilder conTipoDocumento(Usuario.TipoDocumento tipoDocumento) {
            this.usuario.setTipoDocumento(tipoDocumento);
            return this;
        }

        public UsuarioTestBuilder conNumeroDocumento(String numeroDocumento) {
            this.usuario.setNumeroDocumento(numeroDocumento);
            return this;
        }

        public UsuarioTestBuilder conRol(Usuario.Rol rol) {
            this.usuario.setRol(rol);
            return this;
        }

        public UsuarioTestBuilder inactivo() {
            this.usuario.setActivado(false);
            return this;
        }

        public UsuarioTestBuilder conId(Long id) {
            this.usuario.setId(id);
            return this;
        }

        public Usuario build() {
            return this.usuario;
        }
    }

    public static class CategoriaTestBuilder {
        private Categoria categoria;

        public CategoriaTestBuilder() {
            this.categoria = new Categoria();
            // Valores por defecto
            this.categoria.setNombre("Categoria Test");
            this.categoria.setDescripcion("Descripción de categoría test");
        }

        public CategoriaTestBuilder conNombre(String nombre) {
            this.categoria.setNombre(nombre);
            return this;
        }

        public CategoriaTestBuilder conDescripcion(String descripcion) {
            this.categoria.setDescripcion(descripcion);
            return this;
        }

        public CategoriaTestBuilder conId(Long id) {
            this.categoria.setId(id);
            return this;
        }

        public Categoria build() {
            return this.categoria;
        }
    }

    // Métodos estáticos para facilitar el uso
    public static ProductoTestBuilder unProducto() {
        return new ProductoTestBuilder();
    }

    public static UsuarioTestBuilder unUsuario() {
        return new UsuarioTestBuilder();
    }

    public static CategoriaTestBuilder unaCategoria() {
        return new CategoriaTestBuilder();
    }
}
