package com.mazmorras.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
}
