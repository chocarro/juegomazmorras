package com.mazmorras.model;
/**
 * Clase abstracta que representa a un personaje dentro del juego, ya sea un protagonista o un enemigo.
 * Incluye atributos comunes como salud, ataque, defensa, posición, entre otros.
 * Implementa {@code Comparable} para permitir la comparación por velocidad.
 */
public abstract class Personaje implements Comparable{
    protected String imagen;
    protected int id;
    protected int salud;
    protected int ataque;
    protected int defensa;
    protected int velocidad;
    protected int saludMaxima;
    protected int[] posicion;
    protected int porcentajeCritico = 10; // Valor por defecto

    /**
     * Constructor base para instanciar un personaje.
     *
     * @param imagen        Ruta de la imagen del personaje.
     * @param id            Identificador único del personaje.
     * @param salud         Salud inicial del personaje.
     * @param ataque        Valor de ataque del personaje.
     * @param defensa       Valor de defensa del personaje.
     * @param velocidad     Velocidad del personaje.
     * @param saludMaxima   Salud máxima que puede alcanzar.
     */
    public Personaje(String imagen, int id, int salud, int ataque, int defensa, int velocidad, int saludMaxima) {
        this.imagen = imagen;
        this.id = id;
        this.salud = salud;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.saludMaxima = saludMaxima;
        this.posicion = new int[2];
    }

    // Getters y Setters 
    public int getSaludMaxima() {
        return this.saludMaxima;
    }

    public void setSaludMaxima(int saludMaxima) {
        this.saludMaxima = saludMaxima;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalud() {
        return this.salud;
    }

    /**
     * Establece la salud actual del personaje, evitando que sea menor a cero.
     *
     * @param salud Nueva salud a establecer.
     */
    public void setSalud(int salud) {
        this.salud = Math.max(0, salud); 
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

    public int getPorcentajeCritico() {
        return this.porcentajeCritico;
    }

    public void setPorcentajeCritico(int porcentajeCritico) {
        this.porcentajeCritico = porcentajeCritico;
    }

    // METODOS 

    /**
     * Aplica daño al personaje, reduciendo su salud.
     * Si la salud llega a cero, se considera que el personaje ha muerto.
     *
     * @param cantidad Cantidad de daño a recibir.
     */
    public void recibirDanio(int cantidad) {
        int saludAnterior = this.salud;
        setSalud(this.salud - cantidad);
        System.out.println(this.getClass().getSimpleName() + " ID " + this.id + 
                          " recibe " + cantidad + " de daño. Salud: " + saludAnterior + " -> " + this.salud);
        
        if (this.salud <= 0) {
            System.out.println(this.getClass().getSimpleName() + " ID " + this.id + " ha muerto!");
        }
    }

    /**
     * Determina la acción que puede realizar el personaje en base a su posición actual
     * y el movimiento solicitado.
     *
     * @param posicionActual Posición actual del personaje.
     * @param movimiento     Movimiento solicitado (W, A, S, D).
     * @return {@code "mover"}, {@code "atacar"} o {@code "bloqueado"} según la lógica del escenario.
     */
 public String comprobarAccion(int[] posicionActual, String direccion) {
    int nuevaFila = posicionActual[0];
    int nuevaCol = posicionActual[1];

    switch (direccion.toUpperCase()) {
        case "W": nuevaFila--; break;
        case "A": nuevaCol--; break;
        case "S": nuevaFila++; break;
        case "D": nuevaCol++; break;
        default: return null;
    }

    String[][] mapa = Proveedor.getInstance().getGestorJuego().getEscenario().getEscenario();

    if (nuevaFila < 0 || nuevaFila >= mapa.length || nuevaCol < 0 || nuevaCol >= mapa[0].length)
        return null;

    String tipoCasilla = mapa[nuevaFila][nuevaCol];

    if (tipoCasilla.equals("P")) {
        return "bloqueado";
    }

    Enemigo enemigo = Proveedor.getInstance().getGestorJuego().buscarEnemigoEnPosicion(nuevaFila, nuevaCol);
    if (enemigo != null) {
        return "atacar";
    }

    if (tipoCasilla.equals("0") || tipoCasilla.equals("T")) {
        return "mover";
    }

    return "bloqueado";
}

/**
 * Aplica una maldición al personaje, reduciendo su salud y salud máxima en 25%.
 */
public void aplicarMaldicion() {
    int reduccionSalud = (int) (this.salud * 0.25);
    int reduccionMaxima = (int) (this.saludMaxima * 0.25);
    this.salud -= reduccionSalud;
    this.saludMaxima -= reduccionMaxima;

    if (this.salud < 0) this.salud = 0;
    if (this.saludMaxima < 1) this.saludMaxima = 1;
}

    /**
     * Compara la velocidad entre dos personajes.
     * Se ordenan en orden descendente de velocidad.
     *
     * @param o Otro objeto de tipo {@code Personaje}.
     * @return Valor negativo si este personaje es más rápido, 0 si igual, positivo si es más lento.
     */

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Personaje)) {
            throw new IllegalArgumentException("Objeto no es de tipo Personaje");
        }
        Personaje otro = (Personaje) o;
        return Integer.compare(otro.velocidad, this.velocidad);
    }

    /**
     * Devuelve una representación en texto del personaje.
     *
     * @return Cadena con los atributos principales del personaje.
     */
    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", salud='" + getSalud() + "'" +
            ", ataque='" + getAtaque() + "'" +
            ", defensa='" + getDefensa() + "'" +
            ", velocidad='" + getVelocidad() + "'" +
            ", posicion='" + java.util.Arrays.toString(getPosicion()) + "'" +
            "}";
    }
}