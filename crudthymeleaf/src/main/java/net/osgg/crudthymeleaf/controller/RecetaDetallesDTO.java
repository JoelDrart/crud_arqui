package net.osgg.crudthymeleaf.controller;

import java.util.UUID;

public class RecetaDetallesDTO {
    private Long id;
    private String nombre;
    private String foto;
    private String preparacion;
    private String dificultad;
    private String autor;
    private String telefono;
    private String correo;
    private String ingredientes;
    private String enlace;
    private String idVideo;

    // Constructor
    public RecetaDetallesDTO(Long id, String nombre, String foto, String preparacion,
                             String dificultad, String autor, String telefono, String correo, String ingredientes,
                             String enlace, String idVideo) {
        this.id = id;
        this.nombre = nombre;
        this.foto = foto;
        this.preparacion = preparacion;
        this.dificultad = dificultad;
        this.autor = autor;
        this.telefono = telefono;
        this.correo = correo;
        this.ingredientes = ingredientes;
        this.enlace = enlace;
        this.idVideo = idVideo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getPreparacion() {
        return preparacion;
    }

    public void setPreparacion(String preparacion) {
        this.preparacion = preparacion;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public String getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(String idVideo) {
        this.idVideo = idVideo;
    }
}