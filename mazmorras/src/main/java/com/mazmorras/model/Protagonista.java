package com.mazmorras.model;

import java.util.function.Consumer;

public class Protagonista extends Personaje {

    public Protagonista(String nombre, String imagen, int id, int salud, int ataque, int defensa, int velocidad,
            int saludMaxima, int porcentajeCritico) {
        super(imagen, id, salud, ataque, defensa, velocidad, saludMaxima);
        this.porcentajeCritico = porcentajeCritico;
    }

    // Getters específicos del protagonista
    public String getNombre() {
        return this.imagen; // Parece que usas imagen como nombre
    }

    public void setNombre(String nombre) {
        this.imagen = nombre;
    }

    // METODOS

    /**
     * Método principal de acción del protagonista
     * Retorna true si se realizó alguna acción
     */
    public boolean accion(String tecla) {
        int[] posActual = this.getPosicion();
        String accion = this.comprobarAccion(posActual, tecla);
        
        System.out.println("Protagonista - Acción: " + tecla + ", Resultado: " + accion);
        
        if (accion.equals("mover") || accion.equals("atacar")) {
            return true; // La acción se procesará en GestorJuego
        }
        
        return false; // No se puede realizar la acción
    }

    @Override
    public String toString() {
        return "Protagonista{" + super.toString() +
                ", porcentajeCritico=" + porcentajeCritico +
                "}";
    }

    public void forEach(Consumer<Protagonista> action) {
        action.accept(this);
    }
}