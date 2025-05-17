package com.mazmorras.model;

public class Proveedor {
    private static Proveedor instance;
    private GestorJuego gestorJuego;
    
    private Proveedor(){
        gestorJuego = new GestorJuego();
    }

    public static Proveedor getInstance(){
        if (instance==null) {
            instance = new Proveedor();
        }
        return instance;
    }

    public GestorJuego getGestorJuego(){
        return this.gestorJuego;
    }

    public void setGestorJuego(GestorJuego gestorJuego){
        this.gestorJuego = gestorJuego;
    }
    
 
}
