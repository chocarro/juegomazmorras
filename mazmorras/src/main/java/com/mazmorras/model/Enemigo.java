package com.mazmorras.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Enemigo extends Personaje{
  private int percepcion;
  

  public Enemigo (int percepcion,  String imagen,int id, int salud, int ataque, int defensa, int velocidad){
    super(imagen, id, salud, ataque, defensa, velocidad);
    this.percepcion=percepcion;
  }


    public int getPercepcion() {
        return this.percepcion;
    }

    public void setPercepcion(int percepcion) {
        this.percepcion = percepcion;
    }


public static void accion(Enemigo enemigo) {
    GestorJuego gestor = Proveedor.getInstance().getGestorJuego();
    String[][] escenario = gestor.getEscenario().getEscenario();
    Protagonista prota = gestor.buscarProta();
    Random rand = new Random();
    
    int[] posEnemigo = enemigo.getPosicion();
    int[] posProta = prota.getPosicion();
    
    // Calcular distancia al protagonista
    int distanciaX = Math.abs(posEnemigo[0] - posProta[0]);
    int distanciaY = Math.abs(posEnemigo[1] - posProta[1]);
    
    // Si el prota está dentro del rango de percepción
    if (distanciaX <= enemigo.getPercepcion() && distanciaY <= enemigo.getPercepcion()) {
        // Intenta moverse hacia el prota o atacar
        String direccion = obtenerDireccionHaciaProta(posEnemigo, posProta);
        String accion = enemigo.comprobarAccion(posEnemigo, direccion);
        
        if (accion.equals("atacar")) {
            enemigo.atacar(
                posEnemigo[0] + (direccion.equals("S") ? 1 : direccion.equals("W") ? -1 : 0),
                posEnemigo[1] + (direccion.equals("D") ? 1 : direccion.equals("A") ? -1 : 0),
                escenario
            );
        } else if (accion.equals("mover")) {
            enemigo.mover(
                posEnemigo[0] + (direccion.equals("S") ? 1 : direccion.equals("W") ? -1 : 0),
                posEnemigo[1] + (direccion.equals("D") ? 1 : direccion.equals("A") ? -1 : 0),
                escenario
            );
        }
    } else {
        
        // Movimiento aleatorio
        ArrayList<String> direcciones = new ArrayList<>(Arrays.asList("W", "A", "S", "D"));
        boolean accionRealizada = false;
        
        while (!accionRealizada && !direcciones.isEmpty()) {
            String direccion = direcciones.remove(rand.nextInt(direcciones.size()));
            String accion = enemigo.comprobarAccion(posEnemigo, direccion);
            
            if (accion.equals("mover")) {
                enemigo.mover(
                    posEnemigo[0] + (direccion.equals("S") ? 1 : direccion.equals("W") ? -1 : 0),
                    posEnemigo[1] + (direccion.equals("D") ? 1 : direccion.equals("A") ? -1 : 0),
                    escenario
                );
                accionRealizada = true;
            }
        }
    }
    gestor.notifyObservers();
}

/**
 * Método auxiliar para obtener la dirección hacia el protagonista
 */
private static String obtenerDireccionHaciaProta(int[] posEnemigo, int[] posProta) {
    int diffX = posProta[0] - posEnemigo[0];
    int diffY = posProta[1] - posEnemigo[1];
    
    if (Math.abs(diffX) > Math.abs(diffY)) {
        return diffX > 0 ? "S" : "W";
    } else {
        return diffY > 0 ? "D" : "A";
    }
}
    @Override
    public String toString() {
        return "{" + super.toString()+ this.percepcion +
            "}";
    }
}
