package borme.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class ConstitucionEmpresaId implements Serializable {
    private String numeroAsiento;
    private LocalDate fechaConstitucion;

    // Constructor vacío
    public ConstitucionEmpresaId() {}

    // Constructor con campos
    public ConstitucionEmpresaId(String numeroAsiento, LocalDate fechaConstitucion) {
        this.numeroAsiento = numeroAsiento;
        this.fechaConstitucion = fechaConstitucion;
    }

    // Getters, Setters, equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstitucionEmpresaId that = (ConstitucionEmpresaId) o;
        return Objects.equals(numeroAsiento, that.numeroAsiento) &&
                Objects.equals(fechaConstitucion, that.fechaConstitucion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroAsiento, fechaConstitucion);
    }
}