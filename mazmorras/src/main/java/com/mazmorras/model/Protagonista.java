package com.mazmorras.model;

import java.util.Random;
import java.util.function.Consumer;

public class Protagonista extends Personaje {

    private String imagen = "/mazmorras/images/personaje.png";
    private int getSaludMaxima = 100;
    private int porcentajeCritico;
    private int id;


    public Protagonista(String nombre, String imagen, int id, int salud, int ataque, int defensa, int velocidad,
            int porcentajeCritico, int getSaludMaxima) {
        super(imagen, id, salud, ataque, defensa, velocidad);
        this.posicion = new int[] { 0, 0 };
    }

    public int getId() {
        return this.id;
    }

    public String getImagen() {
        return this.imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGetSaludMaxima() {
        return this.getSaludMaxima;
    }

    public void setGetSaludMaxima(int getSaludMaxima) {
        this.getSaludMaxima = getSaludMaxima;
    }

    public int getPorcentajeCritico() {
        return this.porcentajeCritico;
    }

    public void setPorcentajeCritico(int porcentajeCritico) {
        this.porcentajeCritico = porcentajeCritico;
    }

    public String getNombre() {
        return this.imagen;
    }

    public void setNombre(String nombre) {
        this.imagen = nombre;
    }

    @Override
    public void atacar(int nuevaFila, int nuevaCol, String[][] escenario) {
        GestorJuego gestor = Proveedor.getInstance().getGestorJuego();
        Random r = new Random();

        Enemigo enemigo = gestor.buscarEnemigoEnPosicion(nuevaFila, nuevaCol);

        if (enemigo != null) {
            int vidaEnemigo = enemigo.getSalud();
            int defensaEnemigo = enemigo.getDefensa();
            int danioAEnemigo = this.ataque - defensaEnemigo;

            if (r.nextInt(100) < this.porcentajeCritico) {
                danioAEnemigo *= 2; // El daño se duplica si es crítico
            }
            if (danioAEnemigo > 0) {
                vidaEnemigo -= danioAEnemigo; // Reducimos la vida del enemigo
                enemigo.setSalud(vidaEnemigo); // Actualizamos su vida

                if (vidaEnemigo <= 0) {
                    gestor.eliminarEnemigo(enemigo);
                }

                gestor.notifyObservers();
            }
        }
    }

    public void accion(String tecla) {
        GestorJuego gestor = Proveedor.getInstance().getGestorJuego();
        String[][] escenario = gestor.getEscenario().getEscenario();
        int[] posActual = this.getPosicion();
        int nuevaFila = posActual[0];
        int nuevaColumna = posActual[1];

        // Determinar dirección
        switch (tecla.toUpperCase()) {
            case "W":
                nuevaFila--;
                break;
            case "A":
                nuevaColumna--;
                break;
            case "S":
                nuevaFila++;
                break;
            case "D":
                nuevaColumna++;
                break;
            default:
                return; // Tecla no válida
        }

        // Validar límites del escenario
        if (nuevaFila < 0 || nuevaFila >= escenario.length ||
                nuevaColumna < 0 || nuevaColumna >= escenario[0].length) {
            return; // No se puede mover/atacar fuera del escenario
        }

        // Decidir acción (mover o atacar)
        String contenidoCelda = escenario[nuevaFila][nuevaColumna];
        if (contenidoCelda.equals("0")) {
            // Mover (casilla vacía)
            this.mover(nuevaFila, nuevaColumna, escenario);
        } else if (contenidoCelda.matches("[1-9]+")) {
            // Atacar (ID de enemigo)
            this.atacar(nuevaFila, nuevaColumna, escenario);
        }
        // Otros casos (como paredes "P") se ignoran
    }

    @Override
    public String toString() {
        return "{" + super.toString() +
                "}";
    }


public void forEach(Consumer<Protagonista> action) {
    action.accept(this);
}





}
