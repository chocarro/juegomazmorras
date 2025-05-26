package com.mazmorras.model;

/**
 * Clase que representa a un enemigo en el juego Mazmorras.
 * Extiende de la clase Personaje y añade comportamiento específico de enemigos
 * como percepción del entorno y toma de decisiones para perseguir al jugador.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Enemigo extends Personaje {
    private int percepcion;
    private int posX;
    private int posY;

    /**
     * Constructor completo para crear un enemigo.
     * 
     * @param percepcion  Rango de percepción del enemigo (en celdas)
     * @param imagen      Ruta de la imagen representativa del enemigo
     * @param id          Identificador único del enemigo
     * @param salud       Salud actual del enemigo
     * @param ataque      Valor de ataque del enemigo
     * @param defensa     Valor de defensa del enemigo
     * @param velocidad   Valor de velocidad del enemigo
     * @param saludMaxima Salud máxima del enemigo
     * @param posX        Posición X inicial en el escenario
     * @param posY        Posición Y inicial en el escenario
     */
    public Enemigo(int percepcion, String imagen, int id, int salud, int ataque, int defensa, int velocidad,
            int saludMaxima, int posX, int posY) {
        super(imagen, id, salud, ataque, defensa, velocidad, saludMaxima);
        this.percepcion = percepcion;
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Obtiene la posición X actual del enemigo.
     * 
     * @return Posición X en el escenario
     */

    public int getPosX() {
        return posX;
    }

    /**
     * Establece la posición X del enemigo.
     * 
     * @param posX Nueva posición X
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * Obtiene la posición Y actual del enemigo.
     * 
     * @return Posición Y en el escenario
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Establece la posición Y del enemigo.
     * 
     * @param posY Nueva posición Y
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /**
     * Obtiene el rango de percepción del enemigo.
     * 
     * @return Rango de percepción en celdas
     */
    public int getPercepcion() {
        return this.percepcion;
    }

    /**
     * Establece el rango de percepción del enemigo.
     * 
     * @param percepcion Nuevo rango de percepción
     */
    public void setPercepcion(int percepcion) {
        this.percepcion = percepcion;
    }

    // METODOS

    /**
     * Decide la próxima acción del enemigo basada en su percepción del entorno.
     * Si el protagonista está dentro del rango de percepción, intenta acercarse o
     * atacar.
     * De lo contrario, realiza un movimiento aleatorio.
     * 
     * @return String con la dirección del movimiento ("W", "A", "S", "D") o cadena
     *         vacía si no puede moverse
     */
    public String decidirAccion() {
        GestorJuego gestor = Proveedor.getInstance().getGestorJuego();
        Protagonista prota = gestor.buscarProta();

        int[] posEnemigo = this.getPosicion();
        int[] posProta = prota.getPosicion();

        // Calcular distancia al protagonista
        int distanciaX = Math.abs(posEnemigo[0] - posProta[0]);
        int distanciaY = Math.abs(posEnemigo[1] - posProta[1]);

        // Si el prota está dentro del rango de percepción
        if (distanciaX <= this.getPercepcion() && distanciaY <= this.getPercepcion()) {
            // Intenta moverse hacia el prota o atacar
            return obtenerDireccionHaciaProta(posEnemigo, posProta);
        } else {
            // Movimiento aleatorio
            ArrayList<String> direcciones = new ArrayList<>(Arrays.asList("W", "A", "S", "D"));
            Random rand = new Random();

            // Intentar encontrar una dirección válida para moverse
            while (!direcciones.isEmpty()) {
                String direccion = direcciones.remove(rand.nextInt(direcciones.size()));
                String accion = this.comprobarAccion(posEnemigo, direccion);

                if (accion.equals("mover")) {
                    return direccion;
                }
            }
            return ""; // No puede moverse
        }
    }

    /**
     * Método auxiliar para determinar la mejor dirección para acercarse al
     * protagonista.
     * 
     * @param posEnemigo Posición actual del enemigo [x, y]
     * @param posProta   Posición actual del protagonista [x, y]
     * @return Dirección preferente ("W", "A", "S", "D")
     */

    private static String obtenerDireccionHaciaProta(int[] posEnemigo, int[] posProta) {
        int diffX = posProta[0] - posEnemigo[0];
        int diffY = posProta[1] - posEnemigo[1];

        if (Math.abs(diffX) > Math.abs(diffY)) {
            return diffX > 0 ? "S" : "W";
        } else {
            return diffY > 0 ? "D" : "A";
        }
    }

    /**
     * Método para recibir daño. Sobrescribe el método de la clase padre.
     * Registra el daño recibido y verifica si el enemigo ha sido derrotado.
     * 
     * @param cantidad Cantidad de daño a recibir
     */
    @Override
    public void recibirDanio(int cantidad) {
        int saludAnterior = this.salud;
        setSalud(this.salud - cantidad);
        System.out.println("Enemigo ID " + this.id + " recibe " + cantidad +
                " de daño. Salud: " + saludAnterior + " -> " + this.salud);

        if (this.salud <= 0) {
            System.out.println("Enemigo ID " + this.id + " ha muerto!");
        }
    }

    /**
     * Representación en String del enemigo.
     * 
     * @return String con todos los atributos relevantes del enemigo
     */
    @Override
    public String toString() {
        return "Enemigo{" +
                "id=" + getId() +
                ", salud=" + getSalud() + "/" + getSaludMaxima() +
                ", ataque=" + getAtaque() +
                ", defensa=" + getDefensa() +
                ", velocidad=" + getVelocidad() +
                ", percepcion=" + this.percepcion +
                ", posicion=" + java.util.Arrays.toString(getPosicion()) +
                "}";
    }
}