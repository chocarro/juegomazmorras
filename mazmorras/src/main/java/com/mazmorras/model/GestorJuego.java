package com.mazmorras.model;

import java.util.ArrayList;
import com.mazmorras.interfaces.Observer;

public class GestorJuego {
    private static GestorJuego instance;
    private Escenario escenario;
    private Protagonista protagonista;
    ArrayList<Observer> observers;
    private ArrayList<Enemigo> enemigos;
    private boolean juegoActivo;
    private String nombre;
    public void setEscenario(Escenario escenario) {
        this.escenario = escenario;
    }
    public void setProtagonista(Protagonista protagonista) {
        this.protagonista = protagonista;
    }

    public ArrayList<Enemigo> getEnemigos() {
        return this.enemigos;
    }

    public void setEnemigos(ArrayList<Enemigo> enemigos) {
        this.enemigos = enemigos;
    }

    public boolean getJuegoActivo() {
        return this.juegoActivo;
    }

    public void setJuegoActivo(boolean juegoActivo) {
        this.juegoActivo = juegoActivo;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    GestorJuego() {
        this.escenario = new Escenario();
        this.enemigos = new ArrayList<>();
        this.juegoActivo = true;
        this.observers = new ArrayList<>();

        inicializarJuego();
    }

    public static synchronized GestorJuego getInstance() {
        if (instance == null) {
            instance = new GestorJuego();
        }
        return instance;
    }

    public Protagonista buscarProta() {
        return this.protagonista;
    }

    // Getters
    public Escenario getEscenario() {
        return this.escenario;
    }

    public Protagonista getProtagonista() {
        return this.protagonista;
    }

    public ArrayList<Enemigo> getListaEnemigos() {
        return this.enemigos;
    }

    public boolean isJuegoActivo() {
        return this.juegoActivo;
    }

    public void setObservers(ArrayList<Observer> observers) {
        this.observers = observers;
    }

    public ArrayList<Observer> getObservers() {
        return this.observers;
    }

    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        observers.forEach(item -> item.onChange());
    }

    // METODOS//

    private void inicializarJuego() {
        // Inicializar protagonista
        this.protagonista = new Protagonista("/mazmorras/images/personaje.png", "protagonista", 1, 100, 100, 80, 90, 100, 100);
        this.protagonista.setPosicion(new int[] { 0, 0 });
        escenario.getEscenario()[0][0] = "0"; // Posición inicial del prota

        // Cargar enemigos
        LectorEnemigo lectorEnemigos = new LectorEnemigo();
        try {
            ArrayList<Personaje> personajes = lectorEnemigos.leerCSV();
            for (Personaje p : personajes) {
                Enemigo enemigo = (Enemigo) p;
                ArrayList<int[]> posiciones = escenario.generarPosiciones(1);
                int[] pos = posiciones.get(0);
                enemigo.setPosicion(pos);
                escenario.getEscenario()[pos[0]][pos[1]] = String.valueOf(enemigo.getId());
                this.enemigos.add(enemigo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void realizarAccionProtagonista(String tecla) {
        if (!juegoActivo)
            return;

        protagonista.accion(tecla);
        turnoEnemigos();
        verificarEstadoJuego();
        notifyObservers();
    }

    private void turnoEnemigos() {
        for (Enemigo enemigo : enemigos) {
            Enemigo.accion(enemigo);
        }
    }

    private void verificarEstadoJuego() {
        // Verificar si el prota murió
        if (protagonista.getSalud() <= 0) {
            juegoActivo = false;
            return;
        }

        // Verificar si quedan enemigos
        if (enemigos.isEmpty()) {
            juegoActivo = false;
        }
    }

    public Enemigo buscarEnemigoEnPosicion(int fila, int columna) {
        for (Enemigo enemigo : enemigos) {
            int[] pos = enemigo.getPosicion();
            if (pos[0] == fila && pos[1] == columna) {
                return enemigo;
            }
        }
        return null;
    }

    public void eliminarEnemigo(Enemigo enemigo) {
        int[] pos = enemigo.getPosicion();
        escenario.getEscenario()[pos[0]][pos[1]] = "0";
        enemigos.remove(enemigo);
    }

 /**
 * Reinicia el juego a su estado inicial
 */
public void reiniciarJuego() {
    // 1. Reiniciar el escenario
    try {
        this.escenario = new Escenario(); // Esto volverá a cargar el CSV original
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // 2. Reiniciar protagonista
    this.protagonista = new Protagonista("/mazmorras/images/personaje.png", "protagonista", 1, 100, 100, 80, 90, 100, 100);
    this.protagonista.setPosicion(new int[]{0, 0});
    escenario.getEscenario()[0][0] = "0"; // Posición inicial
    
    // 3. Reiniciar enemigos
    this.enemigos.clear();
    LectorEnemigo lectorEnemigos = new LectorEnemigo();
    try {
        ArrayList<Personaje> personajes = lectorEnemigos.leerCSV();
        for (Personaje p : personajes) {
            Enemigo enemigo = (Enemigo) p;
            ArrayList<int[]> posiciones = escenario.generarPosiciones(1);
            int[] pos = posiciones.get(0);
            enemigo.setPosicion(pos);
            escenario.getEscenario()[pos[0]][pos[1]] = String.valueOf(enemigo.getId());
            this.enemigos.add(enemigo);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    this.juegoActivo = true;
    
    notifyObservers();
}

/**
 * Sale al menú principal cerrando la partida actual
 */
public void salirAlMenu() {
    // 1. Limpiar recursos
    this.protagonista = null;
    this.enemigos.clear();
    
    // 2. Notificar al coordinador de vistas
    try {
        Proveedor.getInstance().getCoordinadorVistas().mostrarMenuPrincipal();
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // 3. Opcional: Resetear la instancia para nueva partida
    instance = null;
}

}