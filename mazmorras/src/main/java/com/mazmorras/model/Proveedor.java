package com.mazmorras.model;

/**
 * Clase singleton que actúa como proveedor central de dependencias del juego.

 * Actualmente, proporciona una instancia única de {@link GestorJuego} para
 * permitir el acceso global controlado.
 * 
 */
public class Proveedor {
    /**
     * Instancia única de la clase Proveedor (patrón Singleton).
     */
    private static Proveedor instance;
    /**
     * Instancia del gestor del juego que administra la lógica principal.
     */
    private GestorJuego gestorJuego;

    /**
     * Constructor privado para evitar instanciación externa (Singleton).
     * Inicializa el {@link GestorJuego}.
     */
    private Proveedor() {
        gestorJuego = new GestorJuego();
    }

    /**
     * Devuelve la instancia única de {@code Proveedor}. Si no existe, la crea.
     *
     * @return Instancia única de {@code Proveedor}.
     */
    public static Proveedor getInstance() {
        if (instance == null) {
            instance = new Proveedor();
        }
        return instance;
    }

    /**
     * Devuelve la instancia del {@link GestorJuego} actual.
     *
     * @return Instancia de {@code GestorJuego}.
     */
    public GestorJuego getGestorJuego() {
        return this.gestorJuego;
    }

    /**
     * Establece una nueva instancia de {@link GestorJuego}.
     *
     * @param gestorJuego Nueva instancia de {@code GestorJuego}.
     */
    public void setGestorJuego(GestorJuego gestorJuego) {
        this.gestorJuego = gestorJuego;
    }

}
