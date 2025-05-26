package com.mazmorras.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * La clase {@code Escenario} representa el mapa del juego en forma de matriz de
 * Strings.
 * Administra la carga desde un archivo CSV, la representación gráfica del suelo
 * y paredes,
 * y permite acciones como mover personajes o generar posiciones aleatorias
 * vacías.
 */
public class Escenario {
    private String[][] escenario;
    LectorEscenario lector = new LectorEscenario();
    private final String suelo = "/mazmorras/images/suelo.png";
    private final String pared = "/mazmorras/images/pared.png";

    /**
     * Constructor por defecto. Inicializa el lector y carga el escenario desde el
     * archivo por defecto.
     */
    public Escenario() {
        this.lector = new LectorEscenario();
        try {
            this.escenario = lector.leerCSV("escenario.csv");
        } catch (IOException e) {
            System.err.println("Error cargando escenario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la ruta de la imagen del suelo.
     *
     * @return Ruta del archivo de imagen del suelo.
     */
    public String getSuelo() {
        return this.suelo;
    }

    /**
     * Obtiene la ruta de la imagen de la pared.
     *
     * @return Ruta del archivo de imagen de la pared.
     */
    public String getPared() {
        return this.pared;
    }

    /**
     * Obtiene la matriz actual del escenario.
     *
     * @return Matriz de Strings que representa el escenario.
     */
    public String[][] getEscenario() {
        return this.escenario;
    }

    /**
     * Establece una nueva matriz como escenario.
     *
     * @param escenario Matriz de Strings que representará el nuevo escenario.
     */
    public void setEscenario(String[][] escenario) {
        this.escenario = escenario;
    }

    /**
     * Obtiene el lector de escenarios.
     *
     * @return Instancia del {@code LectorEscenario}.
     */
    public LectorEscenario getLector() {
        return this.lector;
    }

    /**
     * Establece el lector de escenarios.
     *
     * @param lector Nuevo lector a utilizar.
     */
    public void setLector(LectorEscenario lector) {
        this.lector = lector;
    }

    /**
     * Carga un nuevo escenario desde el nombre del archivo proporcionado.
     * Si el nombre está vacío, se utiliza un archivo por defecto.
     *
     * @param nombremap Ruta del archivo CSV del escenario.
     */
    public void setEscenario(String nombremap) {
        if (nombremap.isEmpty())
            nombremap = "/mazmorras/data/escenario.csv";
        try {
            this.escenario = lector.leerCSV(nombremap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // METODOS

    /**
     * Mueve un personaje de una posición a otra dentro del escenario.
     *
     * @param viejaFila   Fila actual del personaje.
     * @param viejaCol    Columna actual del personaje.
     * @param nuevaFila   Nueva fila a la que se desea mover.
     * @param nuevaCol    Nueva columna a la que se desea mover.
     * @param idPersonaje Identificador del personaje.
     * @return {@code true} si el movimiento fue exitoso; {@code false} si la nueva
     *         posición está ocupada o fuera de límites.
     */
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

    /**
     * Genera una lista de posiciones vacías en el escenario.
     * Si hay menos celdas vacías que la cantidad solicitada, las reutiliza
     * circularmente.
     *
     * @param cantidad Número de posiciones a generar.
     * @return Lista de arreglos de 2 enteros representando coordenadas [fila,
     *         columna].
     * @throws IllegalStateException si no hay celdas vacías en el escenario.
     */

    public ArrayList<int[]> generarPosiciones(int cantidad) {
        ArrayList<int[]> posiciones = new ArrayList<>();
        ArrayList<int[]> celdasVacias = new ArrayList<>();

        // 1. Identificar todas las celdas vacías disponibles
        for (int i = 0; i < escenario.length; i++) {
            for (int j = 0; j < escenario[i].length; j++) {
                if (escenario[i][j].equals("0")) {
                    celdasVacias.add(new int[] { i, j });
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
