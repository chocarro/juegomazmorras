package com.mazmorras.model;

public class Personaje {
    private String imagen;
    private int id;
    private int salud;
    private int ataque;
    private int defensa;
    private int velocidad;
    private int[] posicion;


    public Personaje(String imagen, int id, int salud, int ataque, int defensa, int velocidad) {
        this.imagen = imagen;
        this.id=id;
        this.salud = salud;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.posicion= new int[2];
    }


  

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalud() {
        return this.salud;
    }

    public void setSalud(int salud) {
        this.salud = salud;
    }

    public int getAtaque() {
        return this.ataque;
    }

    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    public int getDefensa() {
        return this.defensa;
    }

    public void setDefensa(int defensa) {
        this.defensa = defensa;
    }

    public int getVelocidad() {
        return this.velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public String getImagen() {
        return this.imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int[] getPosicion() {
        return this.posicion;
    }

    public void setPosicion(int[] posicion) {
        this.posicion = posicion;
    }


    @Override
    public String toString() {
        return "{" +
            " imagen='" + getImagen() + "'" +
            ", id='" + getId() + "'" +
            ", salud='" + getSalud() + "'" +
            ", ataque='" + getAtaque() + "'" +
            ", defensa='" + getDefensa() + "'" +
            ", velocidad='" + getVelocidad() + "'" +
            ", posicion='" + getPosicion() + "'" +
            "}";
    }


}
