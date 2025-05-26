package com.mazmorras.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * Clase encargada de leer y cargar un escenario desde un archivo CSV.
 * El escenario se representa como una matriz de cadenas (String[][]),
 * donde cada celda contiene un símbolo del mapa.
 */
public class LectorEscenario {
    private String[][] escenario;
    int numFilas = 0;
    int numColumnas = 0;
    int posXZ = 0;
    int posYZ = 0;

    /**
     * Constructor por defecto.
     */
    public LectorEscenario() {
    }

    /**
     * Lee un archivo CSV y carga los datos en la matriz de escenario.
     * Si no se especifica nombre de archivo, se usa "escenario.csv" por defecto.
     *
     * @param nombremap Nombre del archivo CSV (ubicado en `/mazmorras/data/`).
     * @return Una matriz bidimensional con el contenido del archivo.
     * @throws IOException Si ocurre un error al leer el archivo o su formato es
     *                     inválido.
     */
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

        try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(is))) {
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

    /**
     * Obtiene la matriz del escenario cargado.
     *
     * @return Matriz bidimensional del escenario.
     */
    public String[][] getEscenario() {
        return this.escenario;
    }

    /**
     * Establece manualmente la matriz del escenario.
     *
     * @param escenario Nueva matriz del escenario.
     */
    public void setEscenario(String[][] escenario) {
        this.escenario = escenario;
    }

    /**
     * Obtiene el número de filas del escenario.
     *
     * @return Número de filas.
     */
    public int getNumFilas() {
        return this.numFilas;
    }

    /**
     * Establece el número de filas del escenario.
     *
     * @param numFilas Número de filas a establecer.
     */
    public void setNumFilas(int numFilas) {
        this.numFilas = numFilas;
    }

    /**
     * Obtiene el número de columnas del escenario.
     *
     * @return Número de columnas.
     */
    public int getNumColumnas() {
        return this.numColumnas;
    }

    /**
     * Establece el número de columnas del escenario.
     *
     * @param numColumnas Número de columnas a establecer.
     */

    public void setNumColumnas(int numColumnas) {
        this.numColumnas = numColumnas;
    }

    /**
     * Obtiene la posición X del jugador u objeto relacionado con el escenario.
     *
     * @return Coordenada X.
     */
    public int getPosXZ() {
        return this.posXZ;
    }

    /**
     * Establece la posición X del jugador u objeto.
     *
     * @param posXZ Nueva coordenada X.
     */
    public void setPosXZ(int posXZ) {
        this.posXZ = posXZ;
    }

    /**
     * Obtiene la posición Y del jugador u objeto relacionado con el escenario.
     *
     * @return Coordenada Y.
     */
    public int getPosYZ() {
        return this.posYZ;
    }

    /**
     * Establece la posición Y del jugador u objeto.
     *
     * @param posYZ Nueva coordenada Y.
     */
    public void setPosYZ(int posYZ) {
        this.posYZ = posYZ;
    }

}
