package com.comoencasa_backend.converter;

import com.comoencasa_backend.model.Usuario.Rol;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RolConverter implements AttributeConverter<Rol, String> {

    @Override
    public String convertToDatabaseColumn(Rol rol) {
        if (rol == null) {
            return null;
        }
        return rol.getDbValue();
    }

    @Override
    public Rol convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        return Rol.fromDbValue(dbValue);
    }
}