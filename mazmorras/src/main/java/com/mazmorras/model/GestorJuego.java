package com.mazmorras.model;

import java.util.ArrayList;
import com.mazmorras.interfaces.Observer;

public class GestorJuego {
    // Patrón Singleton
    private static GestorJuego instance;
    
    // Componentes del juego
    private Escenario escenario;
    private Protagonista protagonista;
    private ArrayList<Enemigo> enemigos;
    
    // Estado del juego
    private boolean juegoActivo;
    private String nombre;
    
    // Sistema de observadores
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

    // Singleton
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
            1,       // ID
            100,     // salud
            100,     // ataque
            80,      // defensa
            90,      // velocidad
            100,     // porcentajeCritico
            100      // saludMaxima
        );
        this.protagonista.setPosicion(new int[]{0, 0});
        escenario.getEscenario()[0][0] = "0"; // Posición inicial
    }

    private void cargarEnemigosIniciales() {
        LectorEnemigo lectorEnemigos = new LectorEnemigo();
        try {
            for (Personaje p : lectorEnemigos.leerCSV()) {
                Enemigo enemigo = (Enemigo) p;
                ArrayList<int[]> posiciones = escenario.generarPosiciones(1);
                int[] pos = posiciones.get(0);
                enemigo.setPosicion(pos);
                escenario.getEscenario()[pos[0]][pos[1]] = String.valueOf(enemigo.getId());
                this.enemigos.add(enemigo);
            }
        } catch (Exception e) {
            System.err.println("Error cargando enemigos: " + e.getMessage());
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
        if (!juegoActivo) return;
        
        protagonista.accion(direccion);
        turnoEnemigos();
        verificarEstadoJuego();
        notifyObservers();
    }

    private void turnoEnemigos() {
        enemigos.forEach(Enemigo:: accion);
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

    // Getters y Setters mejorados
    public Escenario getEscenario() {
        return escenario;
    }

    public Protagonista getProtagonista() {
        return protagonista;
    }


    public void setProtagonista(Protagonista prota) {
            this.protagonista = prota;
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
        if (juegoActivo) return "Juego en curso";
        return isVictoria() ? "¡Victoria!" : "¡Derrota!";
    }


}