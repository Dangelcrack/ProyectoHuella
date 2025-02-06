package com.github.dangelcrack.model.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class HabitoId implements Serializable {
    private Integer idUsuario;
    private Integer idActividad;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Integer idActividad) {
        this.idActividad = idActividad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HabitoId habitoId = (HabitoId) o;
        return Objects.equals(idUsuario, habitoId.idUsuario) &&
                Objects.equals(idActividad, habitoId.idActividad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idActividad);
    }
}
