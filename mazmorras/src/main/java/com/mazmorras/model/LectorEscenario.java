package com.mazmorras.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class LectorEscenario {
    private String[][] escenario;
    int numFilas = 0;
    int numColumnas = 0;
    int posXZ = 0;
    int posYZ = 0;

    public LectorEscenario() {
    }

    public String[][] leerCSV(String nombremap) throws IOException {
 if (nombremap == null || nombremap.trim().isEmpty()) {
        nombremap = "escenario.csv"; // Valor por defecto
    }

     String rutaCompleta = "/mazmorras/data/" + nombremap;
    InputStream is = getClass().getResourceAsStream(rutaCompleta);
    
    if (is == null) {
        throw new IOException("Archivo no encontrado: " + rutaCompleta + 
                           "\n src/main/resources/mazmorras/data/");
    }
        
        LinkedList<String[]> filas = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(
                new FileReader("mazmorras/src/main/resources/mazmorras/data/escenario.csv"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.trim().split(",");
                filas.add(datos);
            }
        }

        numFilas = filas.size();
        numColumnas = filas.get(0).length;
        this.escenario = new String[numFilas][numColumnas];

        for (int i = 0; i < numFilas; i++) {
            if (filas.get(i).length != numColumnas) {
                throw new IOException("El archivo CSV tiene filas con distinto número de columnas.");
            } else {
                for (int j = 0; j < numColumnas; j++) {
                    this.escenario[i][j] = filas.get(i)[j];
                }
            }
        }

        return escenario;
    }


    public String[][] getEscenario() {
        return this.escenario;
    }

    public void setEscenario(String[][] escenario) {
        this.escenario = escenario;
    }

    public int getNumFilas() {
        return this.numFilas;
    }

    public void setNumFilas(int numFilas) {
        this.numFilas = numFilas;
    }

    public int getNumColumnas() {
        return this.numColumnas;
    }

    public void setNumColumnas(int numColumnas) {
        this.numColumnas = numColumnas;
    }

    public int getPosXZ() {
        return this.posXZ;
    }

    public void setPosXZ(int posXZ) {
        this.posXZ = posXZ;
    }

    public int getPosYZ() {
        return this.posYZ;
    }

    public void setPosYZ(int posYZ) {
        this.posYZ = posYZ;
    }

}
