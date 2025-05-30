package com.mazmorras.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase encargada de leer los datos de enemigos desde un archivo CSV
 * y construir una lista de objetos {@link Enemigo}.
 * También gestiona las rutas de imagen asociadas a cada tipo de enemigo.
 */
public class LectorEnemigo {
    private ArrayList<Personaje> oponente;
    private HashMap<String, String> imagenes;

    /**
     * Constructor que inicializa las listas de oponentes y las rutas de imágenes.
     */
    public LectorEnemigo() {
        this.oponente = new ArrayList<>();
        this.imagenes = new HashMap<>();
        this.imagenes.put("goblin", "/mazmorras/images/goblin.png");
        this.imagenes.put("calavera", "/mazmorras/images/calavera.png");

    }

    /**
     * Lee los datos de enemigos desde el archivo `enemigos.csv` ubicado en los
     * recursos,
     * parsea cada línea y crea objetos {@link Enemigo} que se agregan a la lista de
     * oponentes.
     *
     * @return Lista de enemigos leídos desde el archivo.
     * @throws Exception si el archivo está vacío o no puede leerse correctamente.
     */
    public ArrayList<Personaje> leerCSV() throws Exception {
        this.oponente.clear();

        try (InputStream is = getClass().getResourceAsStream("/mazmorras/data/enemigos.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String linea;
            if ((linea = br.readLine()) == null)
                throw new Exception("Archivo vacío");

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length < 10) {
                    System.err.println("Línea inválida: " + linea);
                    continue;
                }

                String rutaImagen = imagenes.get(datos[1].trim());
                if (rutaImagen == null) {
                    System.err.println("Tipo de enemigo no reconocido: " + datos[1]);
                    continue;
                }

                // Asegurar que saludMaxima sea igual a salud inicial
                int salud = Integer.parseInt(datos[3].trim());
                int saludMaxima = Integer.parseInt(datos[7].trim());

                if (saludMaxima != salud) {
                    System.out.println("Ajustando saludMaxima para que coincida con salud inicial");
                    saludMaxima = salud;
                }

                this.oponente.add(new Enemigo(
                        Integer.parseInt(datos[0].trim()), // percepción
                        rutaImagen, // ruta imagen
                        Integer.parseInt(datos[2].trim()), // id
                        salud, // salud
                        Integer.parseInt(datos[4].trim()), // ataque
                        Integer.parseInt(datos[5].trim()), // defensa
                        Integer.parseInt(datos[6].trim()), // velocidad
                        saludMaxima, // saludMaxima (igual a salud)
                        Integer.parseInt(datos[8].trim()), // posX
                        Integer.parseInt(datos[9].trim()) // posY
                ));
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo CSV: " + e.getMessage());
            throw e;
        } catch (NumberFormatException e) {
            System.err.println("Error en formato numérico: " + e.getMessage());
            throw e;
        }

        for (Personaje p : oponente) {
            System.out.println(p.toString());
        }
        return this.oponente;
    }

    /**
     * Obtiene la lista de personajes enemigos.
     *
     * @return Lista de oponentes.
     */
    public ArrayList<Personaje> getOponente() {
        return this.oponente;
    }

    /**
     * Establece la lista de personajes enemigos.
     *
     * @param oponente Lista nueva de oponentes.
     */
    public void setOponente(ArrayList<Personaje> oponente) {
        this.oponente = oponente;
    }

    /**
     * Obtiene el mapa de tipos de enemigo a rutas de imágenes.
     *
     * @return Mapa de imágenes.
     */
    public HashMap<String, String> getImagenes() {
        return this.imagenes;
    }

    /**
     * Establece el mapa de rutas de imágenes.
     *
     * @param imagenes Mapa nuevo de imágenes.
     */
    public void setImagenes(HashMap<String, String> imagenes) {
        this.imagenes = imagenes;
    }

}