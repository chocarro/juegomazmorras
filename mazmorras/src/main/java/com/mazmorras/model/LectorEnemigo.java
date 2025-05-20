package com.mazmorras.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class LectorEnemigo {
    private ArrayList<Personaje> oponente;
    private HashMap<String, String> imagenes;

    public LectorEnemigo() {
        this.oponente = new ArrayList<>();
        this.imagenes = new HashMap<>();
        this.imagenes.put("goblin", "/mazmorras/images/goblin.png");
        this.imagenes.put("calavera", "/mazmorras/images/calavera.png");

    }

    public ArrayList<Personaje> leerCSV() throws Exception {
        this.oponente.clear();

        try (InputStream is = getClass().getResourceAsStream("/com/mazmorras/data/enemigos.csv");
     BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String linea;
            if ((linea = br.readLine()) == null)
                throw new Exception("Texto vacío");
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                String rutaImagen = imagenes.get(datos[1]);
                this.oponente.add(new Enemigo(
                        Integer.parseInt(datos[0]), // percepción
                        rutaImagen, // ruta imagen
                        Integer.parseInt(datos[2]), // id
                        Integer.parseInt(datos[3]), // vida
                        Integer.parseInt(datos[4]), // ataque
                        Integer.parseInt(datos[5]), // defensa
                        Integer.parseInt(datos[6]), // velocidad
                        Integer.parseInt(datos[7]), // fuerza (nuevo)
                        Integer.parseInt(datos[8]), // defensaEnemigo (nuevo)
                        Integer.parseInt(datos[9]) // vida (nuevo, o podrías repetir datos[3])
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Personaje p : oponente) {
            System.out.println(p.toString());
        }
        return this.oponente;
    }

    public ArrayList<Personaje> getOponente() {
        return this.oponente;
    }

    public void setOponente(ArrayList<Personaje> oponente) {
        this.oponente = oponente;
    }

    public HashMap<String, String> getImagenes() {
        return this.imagenes;
    }

    public void setImagenes(HashMap<String, String> imagenes) {
        this.imagenes = imagenes;
    }

}