package com.mazmorras.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LectorEnemigo {
private ArrayList<Personaje> oponente;
private HashMap<String,String> imagenes;

public LectorEnemigo(){
    this.oponente=new ArrayList<>();
    this.imagenes.put("goblin","/mazmorras/images/goblin.png");
    this.imagenes.put("calavera","/mazmorras/images/calavera.png");

}


public ArrayList<Personaje> leerCSV() throws Exception {
        this.oponente.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("mazmorras/src/main/resources/mazmorras/data/enemigos.csv"))) {
            String linea;
            if ((linea = br.readLine()) == null)
                throw new Exception("Texto vacío");
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                String rutaImagen = imagenes.get(datos[1]);
                this.oponente.add(new Enemigo(Integer.parseInt(datos[0]),rutaImagen,
                        Integer.parseInt(datos[2]), Integer.parseInt(datos[3]),
                        Integer.parseInt(datos[4]), Integer.parseInt(datos[5]), Integer.parseInt(datos[6])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(Personaje p: oponente){
            System.out.println(p.toString());
        }
        return this.oponente;
    }


}
