package com.mazmorras.model;

public class Protagonista extends Personaje {

    public Protagonista(String imagen, int id, int salud, int ataque, int defensa, int velocidad) {
        super(imagen, id, salud, ataque, defensa, velocidad);
    }

    @Override
    public String toString() {
        return "{" + super.toString()+
            "}";
    }



}
