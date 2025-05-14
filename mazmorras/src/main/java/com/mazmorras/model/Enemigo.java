package com.mazmorras.model;

public class Enemigo extends Personaje{
  private int percepcion;

  public Enemigo (int percepcion, String imagen, int id, int salud, int ataque, int defensa, int velocidad){
    super(imagen, id, salud, ataque, defensa, velocidad);
    this.percepcion=percepcion;
  }

    public int getPercepcion() {
        return this.percepcion;
    }

    public void setPercepcion(int percepcion) {
        this.percepcion = percepcion;
    }


    @Override
    public String toString() {
        return "{" + super.toString()+ this.percepcion +
            "}";
    }
}
