package com.mazmorras.model;

import java.util.ArrayList;
import java.util.Arrays;

import com.mazmorras.interfaces.Observer;

public class GestorJuego {
    private static GestorJuego instance;
    private Escenario escenario;
    private Protagonista protagonista;
    private ArrayList<Enemigo> enemigos;
    private boolean juegoActivo;
    private ArrayList<Observer> observers;

    // Constructor privado
    GestorJuego() {
        this.escenario = new Escenario();
        this.enemigos = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.juegoActivo = true;

        inicializarJuego();
    }

    public Protagonista buscarProta() {
        return this.protagonista;
    }

    public static synchronized GestorJuego getInstance() {
        if (instance == null) {
            instance = new GestorJuego();
        }
        return instance;
    }

    // Métodos de inicialización
    private void inicializarJuego() {
        inicializarProtagonista();
        cargarEnemigosIniciales();
    }

    private void inicializarProtagonista() {
        this.protagonista = new Protagonista(
                "/mazmorras/images/personaje.png",
                "protagonista",
                1, // ID
                100, // salud
                100, // ataque
                80, // defensa
                90, // velocidad
                100, // porcentajeCritico
                100 // saludMaxima
        );
        this.protagonista.setPosicion(new int[] { 0, 0 });
        escenario.getEscenario()[0][0] = "0"; // Posición inicial
    }

    private void cargarEnemigosIniciales() {
        LectorEnemigo lectorEnemigos = new LectorEnemigo();
        try {
            for (Personaje p : lectorEnemigos.leerCSV()) {
                Enemigo enemigo = (Enemigo) p;
                ArrayList<int[]> posiciones = escenario.generarPosiciones(1);

                if (!posiciones.isEmpty()) {
                    int[] pos = posiciones.get(0);
                    enemigo.setPosicion(pos);
                    escenario.getEscenario()[pos[0]][pos[1]] = String.valueOf(enemigo.getId());
                    this.enemigos.add(enemigo);

                    System.out.println("Enemigo creado en posición: " + Arrays.toString(pos) +
                            " con imagen: " + enemigo.getImagen());
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando enemigos: " + e.getMessage());
            // Crear enemigos por defecto si falla la carga
            crearEnemigosPorDefecto();
        }
    }

    private void crearEnemigosPorDefecto() {
       Enemigo goblin = new Enemigo(
        3, // percepción
        "/mazmorras/images/goblin.png", // ruta imagen
        1, // id
        50, // vida (antes salud)
        10, // ataque
        5, // defensa
        2, // velocidad
        8, // fuerza (nuevo parámetro)
        4, // defensaEnemigo (nuevo parámetro)
        50 // vida (nuevo parámetro, mismo valor que el anterior)
        );

        ArrayList<int[]> posiciones = escenario.generarPosiciones(1);
        if (!posiciones.isEmpty()) {
            int[] pos = posiciones.get(0);
            goblin.setPosicion(pos);
            escenario.getEscenario()[pos[0]][pos[1]] = String.valueOf(goblin.getId());
            this.enemigos.add(goblin);
        }
    }

    // Sistema de observadores
    public void subscribe(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        observers.forEach(Observer::onChange);
    }

    // Lógica del juego
    public void realizarAccionProtagonista(String direccion) {
        if (!juegoActivo)
            return;

        protagonista.accion(direccion);
        turnoEnemigos();
        verificarEstadoJuego();
        notifyObservers();
    }

    private void turnoEnemigos() {
        enemigos.forEach(Enemigo::accion);
    }

    private void verificarEstadoJuego() {
        if (protagonista.getSalud() <= 0) {
            juegoActivo = false;
        } else if (enemigos.isEmpty()) {
            juegoActivo = false;
        }
    }

    // Búsquedas
    public Enemigo buscarEnemigoEnPosicion(int fila, int columna) {
        return enemigos.stream()
                .filter(e -> e.getPosicion()[0] == fila && e.getPosicion()[1] == columna)
                .findFirst()
                .orElse(null);
    }

    // Gestión de enemigos
    public void eliminarEnemigo(Enemigo enemigo) {
        if (enemigo != null) {
            int[] pos = enemigo.getPosicion();
            escenario.getEscenario()[pos[0]][pos[1]] = "0";
            enemigos.remove(enemigo);
        }
    }

    // Reinicio del juego
    public void reiniciarJuego() {
        this.escenario = new Escenario();
        this.enemigos.clear();
        this.juegoActivo = true;

        inicializarJuego();
        notifyObservers();
    }

    // Getters y Setters
    public Escenario getEscenario() {
        return escenario;
    }

    public Protagonista getProtagonista() {
        return protagonista;
    }

    public ArrayList<Enemigo> getEnemigos() {
        return new ArrayList<>(enemigos); // Devuelve copia para evitar modificaciones externas
    }

    public boolean isJuegoActivo() {
        return juegoActivo;
    }

    public boolean isVictoria() {
        return juegoActivo == false && protagonista.getSalud() > 0;
    }

    // Métodos para UI
    public String getEstadoJuego() {
        if (juegoActivo)
            return "Juego en curso";
        return isVictoria() ? "¡Victoria!" : "¡Derrota!";
    }

    public void setProtagonista(Protagonista prota) {
        this.protagonista = prota;
    }
}