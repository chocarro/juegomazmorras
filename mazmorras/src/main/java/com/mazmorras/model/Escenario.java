package com.mazmorras.model;

import java.io.IOException;

public class Escenario {
    private String [][] escenario;
    private LectorEscenario lector;

    public Escenario(){
        this.lector=new LectorEscenario();
        try {
            this.escenario= lector.leerCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }

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



}
