package com.mazmorras.model;

public abstract class Personaje  implements Comparable{
    protected  String imagen;
    protected int id;
    protected int salud;
    protected int ataque;
    protected int defensa;
    protected int velocidad;
    protected int[] posicion;


    public Personaje(String imagen, int id, int salud, int ataque, int defensa, int velocidad) {
        this.imagen = imagen;
        this.id=id;
        this.salud = salud;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.posicion= new int[2];
    }

      // Getters y Setters 

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalud() {
        return this.salud;
    }

    public void setSalud(int salud) {
        this.salud = salud;
    }

    public int getAtaque() {
        return this.ataque;
    }

    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    public int getDefensa() {
        return this.defensa;
    }

    public void setDefensa(int defensa) {
        this.defensa = defensa;
    }

    public int getVelocidad() {
        return this.velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public String getImagen() {
        return this.imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int[] getPosicion() {
        return this.posicion;
    }

    public void setPosicion(int[] posicion) {
        this.posicion = posicion;
    }


      // METODOS 

    public void mover(int nuevaFila, int nuevaCol, String[][] escenario) {
        GestorJuego gestor = Proveedor.getInstance().getGestorJuego();
        int[] pos = this.getPosicion();
        escenario[pos[0]][pos[1]] = "s";
        this.setPosicion(new int[] { nuevaFila, nuevaCol });
        escenario[nuevaFila][nuevaCol] = "" + this.id;
        gestor.notifyObservers();
    }

    public void atacar(int nuevaFila, int nuevaCol, String[][] escenario) {
    }

    public void danio(int cantidad) {
        setSalud(getSalud() - cantidad);
    }

    public String comprobarAccion(int[] posicionActual, String movimiento) {
        GestorJuego gestor = Proveedor.getInstance().getGestorJuego();
        String[][] escenario = gestor.getEscenario().getEscenario();

        int nuevaFila = posicionActual[0];
        int nuevaColumna = posicionActual[1];

        // Calcular nueva posición
        switch (movimiento.toUpperCase()) {
            case "W":
                nuevaFila--;
                break;
            case "A":
                nuevaColumna--;
                break;
            case "S":
                nuevaFila++;
                break;
            case "D":
                nuevaColumna++;
                break;
            default:
                return "bloqueado";
        }

        // Validar límites del escenario
        if (nuevaFila < 0 || nuevaFila >= escenario.length ||
                nuevaColumna < 0 || nuevaColumna >= escenario[0].length) {
            return "bloqueado";
        }

        String contenidoCelda = escenario[nuevaFila][nuevaColumna];

        // Lógica para protagonista
        if (this instanceof Protagonista) {
            if (contenidoCelda.equals("0")) {
                return "mover";
            } else if (contenidoCelda.matches("[1-9]+")) {
                return "atacar";
            }
        }
        // Lógica para enemigos
        else if (this instanceof Enemigo) {
            Protagonista prota = gestor.buscarProta();
            int[] posProta = prota.getPosicion();

            // Si la celda es el protagonista
            if (nuevaFila == posProta[0] && nuevaColumna == posProta[1]) {
                return "atacar";
            }
            // Si la celda está vacía o es transitable
            else if (contenidoCelda.equals("0") || contenidoCelda.equals("C")) { // "C" para cofres
                return "mover";
            }
        }

        return "bloqueado";
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Personaje)) {
            throw new IllegalArgumentException("Objeto no es de tipo Personaje");
        }
        Personaje otro = (Personaje) o;
        return Integer.compare(otro.velocidad, this.velocidad);
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", salud='" + getSalud() + "'" +
            ", ataque='" + getAtaque() + "'" +
            ", defensa='" + getDefensa() + "'" +
            ", velocidad='" + getVelocidad() + "'" +
            ", posicion='" + getPosicion() + "'" +
            "}";
    }
   

}
