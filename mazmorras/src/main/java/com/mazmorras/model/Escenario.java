package com.mazmorras.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Escenario {
    private String [][] escenario;
  LectorEscenario lector = new LectorEscenario();
    private final String suelo = "/mazmorras/images/suelo.png";
    private final String pared = "/mazmorras/images/pared.png";


    public Escenario(){
        this.lector=new LectorEscenario();
        try {
            this.escenario= lector.leerCSV("escenario.csv");
        } catch (IOException e) {
            System.err.println("Error cargando escenario: " + e.getMessage());
        // Crear escenario por defecto si falla la carga
        this.escenario = new String[][]{
            {"P","P","P"},
            {"P","S","P"},
            {"P","P","P"}
        };
            e.printStackTrace();
        }

    }

      public String getSuelo() {
        return this.suelo;
    }
     public String getPared() {
        return this.pared;
    }
    public String[][] getEscenario() {
        return this.escenario;
    }

    public void setEscenario(String[][] escenario) {
        this.escenario = escenario;
    }

    public LectorEscenario getLector() {
        return this.lector;
    }

    public void setLector(LectorEscenario lector) {
        this.lector = lector;
    }


 public void setEscenario(String nombremap) {
        if (nombremap.isEmpty())
            nombremap = "/mazmorras/data/escenario.csv";
        try {
            this.escenario = lector.leerCSV(nombremap);
         
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean moverPersonaje(int viejaFila, int viejaCol, int nuevaFila, int nuevaCol, String idPersonaje) {
    // Validar límites del escenario
    if (nuevaFila < 0 || nuevaFila >= escenario.length || 
        nuevaCol < 0 || nuevaCol >= escenario[0].length) {
        return false;
    }
    
    // Verificar si la nueva posición está vacía
    if (escenario[nuevaFila][nuevaCol].equals("0")) {
        // Liberar posición anterior
        escenario[viejaFila][viejaCol] = "0";
        // Ocupar nueva posición
        escenario[nuevaFila][nuevaCol] = idPersonaje;
        return true;
    }
    return false;
}



public ArrayList<int[]> generarPosiciones(int cantidad) {
    ArrayList<int[]> posiciones = new ArrayList<>();
    ArrayList<int[]> celdasVacias = new ArrayList<>();
    
    // 1. Identificar todas las celdas vacías disponibles
    for (int i = 0; i < escenario.length; i++) {
        for (int j = 0; j < escenario[i].length; j++) {
            if (escenario[i][j].equals("0")) {
                celdasVacias.add(new int[]{i, j});
            }
        }
    }
    
    // 2. Verificar si hay al menos una celda vacía
    if (celdasVacias.isEmpty()) {
        throw new IllegalStateException("No hay ninguna celda vacía en el escenario");
    }
    
    // 3. Si no hay suficientes celdas vacías, reutilizamos las existentes
    for (int i = 0; i < cantidad; i++) {
        int index = i % celdasVacias.size(); // Esto permite reutilización circular
        posiciones.add(celdasVacias.get(index));
    }
    
    // Opcional: Mezclar las posiciones si se están reutilizando
    if (cantidad > celdasVacias.size()) {
        Collections.shuffle(posiciones);
    }
    
    return posiciones;
}

}
