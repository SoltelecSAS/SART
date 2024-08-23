package com.soltelec.conexion_seriales.dtos;

public class Enum1DTO {
    private Integer id;
    private String descripcion;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Enum1DTO(Integer id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    
}
