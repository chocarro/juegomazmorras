package com.mazmorras.model;

import java.util.function.Consumer;

/**
 * Representa al protagonista del juego. Hereda de {@link Personaje} y añade
 * lógica específica para el control del jugador.
 */

public class Protagonista extends Personaje {
    /**
     * Constructor del protagonista del juego.
     *
     * @param nombre            Nombre del personaje (almacenado en el atributo
     *                          {@code imagen}).
     * @param imagen            Imagen del personaje (usada como nombre en este
     *                          caso).
     * @param id                Identificador único.
     * @param salud             Salud inicial.
     * @param ataque            Valor de ataque.
     * @param defensa           Valor de defensa.
     * @param velocidad         Velocidad (influye en el turno).
     * @param saludMaxima       Salud máxima alcanzable.
     * @param porcentajeCritico Probabilidad de golpe crítico (en porcentaje).
     */

    public Protagonista(String nombre, String imagen, int id, int salud, int ataque, int defensa, int velocidad,
            int saludMaxima, int porcentajeCritico) {
        super(imagen, id, salud, ataque, defensa, velocidad, saludMaxima);
        this.porcentajeCritico = porcentajeCritico;
    }

    // Getters y Setters
    /**
     * Obtiene el nombre del protagonista.
     * Nota: En esta implementación, el nombre se almacena en el atributo
     * {@code imagen}.
     *
     * @return El nombre del protagonista.
     */
    public String getNombre() {
        return this.imagen;
    }

    /**
     * Establece el nombre del protagonista.
     * Nota: Se almacena en el atributo {@code imagen}.
     *
     * @param nombre Nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.imagen = nombre;
    }

    // METODOS
    /**
     * Ejecuta una acción en función de la tecla presionada.
     * Verifica si el movimiento es válido según el escenario y si se puede mover o
     * atacar.
     *
     * @param tecla Tecla de movimiento: W (arriba), A (izquierda), S (abajo), D
     *              (derecha).
     * @return {@code true} si se puede realizar la acción (mover o atacar),
     *         {@code false} si está bloqueado.
     */
    public boolean accion(String tecla) {
        int[] posActual = this.getPosicion();
        String accion = this.comprobarAccion(posActual, tecla);

        System.out.println("Protagonista - Acción: " + tecla + ", Resultado: " + accion);

        if (accion.equals("mover") || accion.equals("atacar")) {
            return true;
        }
        return false;
    }

    /**
     * Representación textual del protagonista, extendiendo la de la clase base.
     *
     * @return Cadena con información del protagonista.
     */
    @Override
    public String toString() {
        return "Protagonista{" + super.toString() +
                ", porcentajeCritico=" + porcentajeCritico +
                "}";
    }

    /**
     * Ejecuta una acción sobre esta instancia del protagonista, permitiendo
     * expresiones lambda.
     *
     * @param action Acción a aplicar sobre el protagonista.
     */
    public void forEach(Consumer<Protagonista> action) {
        action.accept(this);
    }
}