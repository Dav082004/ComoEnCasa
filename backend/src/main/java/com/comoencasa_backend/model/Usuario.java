package com.comoencasa_backend.model;

import com.comoencasa_backend.converter.RolConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String apellido;

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono = ""; // Valor por defecto

    @Column(name = "direccion", nullable = false, length = 200)
    private String direccion = ""; // Valor por defecto

    @Column(name = "tipo_documento")
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento = TipoDocumento.DNI;

    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;

    @Column(nullable = false)
    @Convert(converter = RolConverter.class)
    private Rol rol = Rol.CLIENTE;

    @Column(name = "activado", nullable = false)
    private Boolean activado = true;

    public enum TipoDocumento {
        DNI, RUC, CE
    }

    public enum Rol {
        CLIENTE("CLIENTE"),  // Valor que espera Java
        ADMIN("ADMIN");

        private final String dbValue;

        Rol(String dbValue) {
            this.dbValue = dbValue;
        }

        public String getDbValue() {
            return dbValue;
        }

        // Método para convertir desde la BD
        public static Rol fromDbValue(String dbValue) {
            for (Rol rol : values()) {
                if (rol.dbValue.equalsIgnoreCase(dbValue)) {
                    return rol;
                }
            }
            throw new IllegalArgumentException("Rol no válido: " + dbValue);
        }
    }
}