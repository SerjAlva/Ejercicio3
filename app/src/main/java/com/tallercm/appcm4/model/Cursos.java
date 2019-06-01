package com.tallercm.appcm4.model;

public class Cursos {
    long id;
    String nombre;
    String sede;
    String fechai;
    String fechaf;

    public Cursos(long id, String nombre, String sede, String fechai, String fechaf) {
        this.id = id;
        this.nombre = nombre;
        this.sede = sede;
        this.fechai=fechai;
        this.fechaf=fechaf;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String tipo) {
        this.sede = tipo;
    }

    public String getFechai() {
        return fechai;
    }

    public void setFechai(String fechai) {
        this.fechai = fechai;
    }

    public String getFechaf() {
        return fechaf;
    }

    public void setFechaf(String fechaf) {
        this.fechaf = fechaf;
    }
}
