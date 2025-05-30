package com.mazmorras.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.mazmorras.interfaces.Observer;

/**
 * Clase encargada de gestionar el flujo del juego, incluyendo la lógica de
 * turnos,la interacción entre el protagonista y los enemigos, y el estado del
 * escenario.
 * También implementa el patrón Observador para actualizar vistas u otras capas
 * interesadas.
 */

public class GestorJuego {
    private static GestorJuego instance;
    private Escenario escenario;
    private Protagonista protagonista;
    private ArrayList<Enemigo> enemigos;
    private boolean juegoActivo;
    private ArrayList<Observer> observers;

    /**
     * Constructor privado que inicializa el juego, escenario, enemigos y
     * protagonista.
     */
    GestorJuego() {
        this.escenario = new Escenario();
        this.enemigos = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.juegoActivo = true;

        inicializarJuego();
    }

    /**
     * Inicializa los elementos principales del juego.
     */
    private void inicializarJuego() {
        inicializarProtagonista();
        cargarEnemigosIniciales();
        actualizarEscenario();
    }

    /**
     * Crea y posiciona al protagonista en el escenario.
     */

    private void inicializarProtagonista() {
        if (this.protagonista == null) {
            this.protagonista = new Protagonista(
                    "protagonista",
                    "/mazmorras/images/personaje.png",
                    1, // ID
                    100, // salud
                    100, // ataque
                    80, // defensa
                    90, // velocidad
                    100, // saludMaxima
                    15 // porcentajeCritico
            );

            this.protagonista.setPosicion(new int[] { 1, 1 });
        }
    }

    /**
     * Carga los enemigos iniciales desde un archivo CSV mediante
     * {@link LectorEnemigo}.
     */

    private void cargarEnemigosIniciales() {
        LectorEnemigo lectorEnemigos = new LectorEnemigo();
        try {
            for (Personaje p : lectorEnemigos.leerCSV()) {
                Enemigo enemigo = (Enemigo) p;
                int[] pos = { enemigo.getPosX(), enemigo.getPosY() };

                // Verificar si la posición es válida en el escenario
                if (pos[0] < 0 || pos[0] >= escenario.getEscenario().length ||
                        pos[1] < 0 || pos[1] >= escenario.getEscenario()[0].length) {
                    System.err.println("Posición inválida para enemigo: " + Arrays.toString(pos));
                    continue;
                }

                enemigo.setPosicion(pos);
                this.enemigos.add(enemigo);

                System.out.println("Enemigo cargado desde CSV en posición: " + Arrays.toString(pos) +
                        " con imagen: " + enemigo.getImagen());
            }
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO: No se pudieron cargar los enemigos desde el archivo CSV.");
            e.printStackTrace();
        }
    }

    /**
     * Suscribe un observador al sistema.
     * 
     * @param observer Objeto que implementa la interfaz {@link Observer}
     */
    public void subscribe(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Elimina un observador del sistema.
     * 
     * @param observer Observador a eliminar
     */
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifica a todos los observadores registrados.
     */
    public void notifyObservers() {
        System.out.println("Notificando a " + observers.size() + " observadores");
        observers.forEach(Observer::onChange);
    }

    /**
     * Ejecuta el turno del protagonista y luego el de los enemigos.
     * 
     * @param direccion Dirección del movimiento o acción del protagonista (W, A, S,
     *                  D)
     */
    public void realizarAccionProtagonista(String direccion) {
        if (!juegoActivo) {
            System.out.println("El juego no está activo");
            return;
        }

        System.out.println("=== INICIO DE TURNO ===");
        System.out.println("Acción del protagonista: " + direccion);

        // 1. Acción del protagonista (movimiento o ataque)
        boolean protagonistaActuo = ejecutarAccionProtagonista(direccion);

        if (protagonistaActuo) {
            // 2. Verificar si hay enemigos muertos y eliminarlos
            eliminarEnemigosMuertos();

            // 3. Verificar estado del juego después de la acción del protagonista
            verificarEstadoJuego();
            if (!juegoActivo) {
                System.out.println("Juego terminado después de acción del protagonista");
                actualizarEscenario();
                notifyObservers();
                return;
            }

            // 4. Solo ejecutar turno de enemigos si el protagonista ATACÓ, no si se movió
            // Esto evita que los enemigos ataquen inmediatamente después de cada movimiento
            String accion = getAccionParaDireccion(direccion);
            if (accion != null && accion.equals("atacar")) {
                System.out.println("Protagonista atacó, ejecutando turno de enemigos");
                turnoEnemigos();

                // 5. Verificar estado del juego después del turno de enemigos
                verificarEstadoJuego();
            } else {
                System.out.println("Protagonista se movió, enemigos no actúan este turno");
            }
        } else {
            System.out.println("El protagonista no pudo realizar la acción: " + direccion);
        }

        System.out.println("=== FIN DE TURNO ===");

        // 6. Actualizar escenario y notificar cambios finales
        actualizarEscenario();
        notifyObservers();
    }

    /**
     * Determina qué acción realizaría el protagonista en una dirección dada
     * 
     * @param direccion Dirección a evaluar
     * @return "mover", "atacar" o null si es inválida
     */
    private String getAccionParaDireccion(String direccion) {
        int[] posActual = protagonista.getPosicion();

        int nuevaFila = posActual[0];
        int nuevaCol = posActual[1];

        // Calcular nueva posición
        switch (direccion.toUpperCase()) {
            case "W":
                nuevaFila--;
                break;
            case "A":
                nuevaCol--;
                break;
            case "S":
                nuevaFila++;
                break;
            case "D":
                nuevaCol++;
                break;
            default:
                return null;
        }

        return protagonista.comprobarAccion(posActual, direccion);
    }

    /**
     * Ejecuta la acción del protagonista según la dirección.
     * 
     * @param direccion Dirección indicada
     * @return true si se pudo realizar la acción, false en caso contrario
     */
    private boolean ejecutarAccionProtagonista(String direccion) {
        int[] posActual = protagonista.getPosicion();
        String accion = protagonista.comprobarAccion(posActual, direccion);

        int nuevaFila = posActual[0];
        int nuevaCol = posActual[1];

        // Calcular nueva posición
        switch (direccion.toUpperCase()) {
            case "W":
                nuevaFila--;
                break;
            case "A":
                nuevaCol--;
                break;
            case "S":
                nuevaFila++;
                break;
            case "D":
                nuevaCol++;
                break;
            default:
                System.out.println("Dirección inválida: " + direccion);
                return false;
        }

        System.out.println("Posición actual: " + Arrays.toString(posActual) +
                ", Nueva posición: [" + nuevaFila + "," + nuevaCol + "]" +
                ", Acción: " + accion);

        if (accion.equals("mover")) {
            return moverProtagonista(nuevaFila, nuevaCol);
        } else if (accion.equals("atacar")) {
            return protagonistaAtaca(nuevaFila, nuevaCol);
        }

        return false;
    }

    /**
     * Mueve al protagonista a una nueva posición.
     * 
     * @param nuevaFila Fila destino
     * @param nuevaCol  Columna destino
     * @return true si el movimiento fue exitoso
     */
    private boolean moverProtagonista(int nuevaFila, int nuevaCol) {
        // Verificar que la nueva posición sea válida (dentro de límites y no sea pared)
        String[][] escenario = this.escenario.getEscenario();

        if (nuevaFila < 0 || nuevaFila >= escenario.length ||
                nuevaCol < 0 || nuevaCol >= escenario[0].length ||
                escenario[nuevaFila][nuevaCol].equals("P")) {
            System.out.println("Movimiento inválido a [" + nuevaFila + "," + nuevaCol + "]");
            return false;
        }

        // Verificar que no haya un enemigo en la posición destino
        Enemigo enemigoEnDestino = buscarEnemigoEnPosicion(nuevaFila, nuevaCol);
        if (enemigoEnDestino != null) {
            System.out.println("No se puede mover a [" + nuevaFila + "," + nuevaCol + "] - hay un enemigo");
            return false;
        }

        // Actualizar posición del protagonista
        protagonista.setPosicion(new int[] { nuevaFila, nuevaCol });

        System.out.println("Protagonista movido a: [" + nuevaFila + "," + nuevaCol + "]");
        return true;
    }

    /**
     * Realiza un ataque del protagonista hacia una posición.
     * 
     * @param fila Fila destino
     * @param col  Columna destino
     * @return true si se realizó el ataque
     */
    private boolean protagonistaAtaca(int nuevaFila, int nuevaCol) {
        Enemigo enemigo = buscarEnemigoEnPosicion(nuevaFila, nuevaCol);
        if (enemigo != null) {
            int danio = calcularDanio(protagonista, enemigo);
            enemigo.recibirDanio(danio);

            System.out.println("Protagonista ataca a enemigo ID " + enemigo.getId() +
                    " causando " + danio + " de daño. Salud restante: " + enemigo.getSalud());

            return true;
        } else {
            System.out.println("No hay enemigo en la posición [" + nuevaFila + "," + nuevaCol + "] para atacar");
        }
        return false;
    }

    /**
     * Ejecuta el turno de los enemigos en orden de velocidad descendente.
     */
    private void turnoEnemigos() {
        if (enemigos.isEmpty()) {
            System.out.println("No hay enemigos para procesar");
            return;
        }

        // Crear lista de enemigos vivos ordenada por velocidad (mayor velocidad
        // primero)
        ArrayList<Enemigo> enemigosVivos = new ArrayList<>();
        for (Enemigo enemigo : enemigos) {
            if (enemigo.getSalud() > 0) {
                enemigosVivos.add(enemigo);
            }
        }

        enemigosVivos.sort((e1, e2) -> Integer.compare(e2.getVelocidad(), e1.getVelocidad()));

        System.out.println("Procesando turno de " + enemigosVivos.size() + " enemigos vivos");

        for (Enemigo enemigo : enemigosVivos) {
            if (enemigo.getSalud() > 0 && juegoActivo) {
                System.out.println("Turno del enemigo ID " + enemigo.getId());

                // Verificar si puede atacar al protagonista (adyacente)
                if (enemigoEsAdyacente(enemigo)) {
                    // Atacar al protagonista
                    int danio = calcularDanio(enemigo, protagonista);
                    protagonista.recibirDanio(danio);

                    System.out.println("Enemigo ID " + enemigo.getId() + " ataca al protagonista " +
                            "causando " + danio + " de daño. Salud protagonista: " + protagonista.getSalud());

                    // Verificar si el protagonista murió
                    if (protagonista.getSalud() <= 0) {
                        System.out.println("¡El protagonista ha muerto!");
                        juegoActivo = false;
                        break;
                    }
                } else {
                    // Intentar moverse hacia el protagonista
                    String direccion = enemigo.decidirAccion();
                    if (!direccion.isEmpty()) {
                        moverEnemigo(enemigo, direccion);
                        System.out.println("Enemigo ID " + enemigo.getId() + " se mueve en dirección: " + direccion);
                    } else {
                        System.out.println("Enemigo ID " + enemigo.getId() + " no puede moverse");
                    }
                }
            }
        }
    }

    /**
     * Mueve al enemigo en una dirección específica.
     * 
     * @param enemigo   Enemigo a mover
     * @param direccion Dirección (W, A, S, D)
     */
    private void moverEnemigo(Enemigo enemigo, String direccion) {
        int[] posActual = enemigo.getPosicion();
        String accion = enemigo.comprobarAccion(posActual, direccion);

        if (accion.equals("mover")) {
            int nuevaFila = posActual[0];
            int nuevaCol = posActual[1];

            switch (direccion.toUpperCase()) {
                case "W":
                    nuevaFila--;
                    break;
                case "A":
                    nuevaCol--;
                    break;
                case "S":
                    nuevaFila++;
                    break;
                case "D":
                    nuevaCol++;
                    break;
            }

            // Verificar que la nueva posición sea válida
            String[][] escenario = this.escenario.getEscenario();

            if (nuevaFila >= 0 && nuevaFila < escenario.length &&
                    nuevaCol >= 0 && nuevaCol < escenario[0].length &&
                    !escenario[nuevaFila][nuevaCol].equals("P")) {

                // Verificar que no haya otro enemigo en esa posición
                Enemigo otroEnemigo = buscarEnemigoEnPosicion(nuevaFila, nuevaCol);
                if (otroEnemigo == null) {
                    // Actualizar posición del enemigo
                    enemigo.setPosicion(new int[] { nuevaFila, nuevaCol });

                    System.out.println("Enemigo ID " + enemigo.getId() + " movido de " +
                            Arrays.toString(posActual) + " a [" + nuevaFila + "," + nuevaCol + "]");
                } else {
                    System.out.println("Enemigo ID " + enemigo.getId() + " no puede moverse - posición ocupada");
                }
            } else {
                System.out.println("Enemigo ID " + enemigo.getId() + " no puede moverse - posición inválida");
            }
        }
    }

    /**
     * Verifica si un enemigo está adyacente al protagonista.
     * 
     * @param enemigo Enemigo a comprobar
     * @return true si está adyacente
     */
    private boolean enemigoEsAdyacente(Enemigo enemigo) {
        int[] posEnemigo = enemigo.getPosicion();
        int[] posProta = protagonista.getPosicion();

        int distanciaFila = Math.abs(posEnemigo[0] - posProta[0]);
        int distanciaCol = Math.abs(posEnemigo[1] - posProta[1]);

        // Un enemigo es adyacente si está a distancia 1 en fila O columna (no diagonal)
        // Y no están en la misma posición
        boolean esAdyacente = ((distanciaFila == 1 && distanciaCol == 0) ||
                (distanciaFila == 0 && distanciaCol == 1));

        if (esAdyacente) {
            System.out.println("Enemigo ID " + enemigo.getId() + " es adyacente al protagonista");
        }
        return esAdyacente;
    }

    /**
     * Calcula el daño entre un atacante y un defensor.
     * 
     * @param atacante Personaje atacante
     * @param defensor Personaje defensor
     * @return Cantidad de daño calculado
     */

    private int calcularDanio(Personaje atacante, Personaje defensor) {
        int ataqueBase = atacante.getAtaque();
        int defensa = defensor.getDefensa();

        // Fórmula básica de daño (mínimo 1 de daño)
        int danio = Math.max(1, ataqueBase - (defensa / 2));

        // Posibilidad de crítico solo para el protagonista
        if (atacante instanceof Protagonista) {
            Protagonista prota = (Protagonista) atacante;
            if (Math.random() * 100 < prota.getPorcentajeCritico()) {
                danio *= 2;
                System.out.println("¡CRÍTICO! Daño duplicado");
            }
        }

        return danio;
    }

    /**
     * Elimina enemigos cuya salud ha llegado a 0 o menos.
     */
    private void eliminarEnemigosMuertos() {
        Iterator<Enemigo> iterator = enemigos.iterator();
        int eliminados = 0;
        while (iterator.hasNext()) {
            Enemigo enemigo = iterator.next();
            if (enemigo.getSalud() <= 0) {
                iterator.remove();
                eliminados++;
                System.out.println("Enemigo ID " + enemigo.getId() + " eliminado (murió)");
            }
        }

        if (eliminados > 0) {
            System.out.println(
                    "Total de enemigos eliminados: " + eliminados + ". Enemigos restantes: " + enemigos.size());
        }
    }

    /**
     * Verifica si el juego ha terminado por victoria o derrota.
     */

    private void verificarEstadoJuego() {
        if (protagonista.getSalud() <= 0) {
            juegoActivo = false;
            System.out.println("¡DERROTA! El protagonista ha muerto");
        } else if (enemigos.isEmpty() || enemigos.stream().allMatch(e -> e.getSalud() <= 0)) {
            juegoActivo = false;
            System.out.println("¡VICTORIA! Todos los enemigos han sido derrotados");
        }
    }

    /**
     * Actualiza visualmente el estado del escenario con los personajes actuales.
     */
    private void actualizarEscenario() {
        String[][] matriz = this.escenario.getEscenario();

        // Limpiar posiciones no-pared
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if (!matriz[i][j].equals("P")) {
                    matriz[i][j] = "0"; // Suelo vacío
                }
            }
        }

        // Colocar protagonista si está vivo
        if (protagonista.getSalud() > 0) {
            int[] posProta = protagonista.getPosicion();
            if (posProta[0] >= 0 && posProta[0] < matriz.length &&
                    posProta[1] >= 0 && posProta[1] < matriz[0].length) {
                matriz[posProta[0]][posProta[1]] = "PROTA";
            }
        }

        // Colocar enemigos vivos
        int enemigosVivos = 0;
        for (Enemigo enemigo : enemigos) {
            if (enemigo.getSalud() > 0) {
                int[] pos = enemigo.getPosicion();
                if (pos[0] >= 0 && pos[0] < matriz.length &&
                        pos[1] >= 0 && pos[1] < matriz[0].length) {
                    matriz[pos[0]][pos[1]] = String.valueOf(enemigo.getId());
                    enemigosVivos++;
                }
            }
        }

        System.out.println("Escenario actualizado - Protagonista: " + Arrays.toString(protagonista.getPosicion()) +
                ", Enemigos vivos: " + enemigosVivos);
    }

    /**
     * Busca un enemigo en una posición específica.
     * 
     * @param fila    Fila buscada
     * @param columna Columna buscada
     * @return Enemigo en esa posición o null si no existe
     */
    public Enemigo buscarEnemigoEnPosicion(int fila, int columna) {
        return enemigos.stream()
                .filter(e -> e.getPosicion()[0] == fila && e.getPosicion()[1] == columna && e.getSalud() > 0)
                .findFirst()
                .orElse(null);
    }

    /**
     * @return El protagonista actual del juego
     */
    public Protagonista buscarProta() {
        return this.protagonista;
    }

    /**
     * Elimina un enemigo manualmente del juego.
     * 
     * @param enemigo Enemigo a eliminar
     */
    public void eliminarEnemigo(Enemigo enemigo) {
        if (enemigo != null) {
            enemigos.remove(enemigo);
            System.out.println("Enemigo ID " + enemigo.getId() + " eliminado manualmente");
        }
    }

    /**
     * Reinicia el juego a su estado inicial.
     */

    public void reiniciarJuego() {
        System.out.println("Reiniciando juego...");
        this.escenario = new Escenario();
        this.enemigos.clear();
        this.juegoActivo = true;

        // Reiniciar protagonista manteniendo la misma instancia pero resetando valores
        if (this.protagonista != null) {
            this.protagonista.setSalud(100);
            this.protagonista.setSaludMaxima(100);
            this.protagonista.setAtaque(10);
            this.protagonista.setDefensa(8);
            this.protagonista.setVelocidad(9);
            this.protagonista.setPosicion(new int[] { 1, 1 });
        } else {
            inicializarProtagonista();
        }

        cargarEnemigosIniciales();
        actualizarEscenario();
        notifyObservers();
        System.out.println("Juego reiniciado con valores iniciales");
    }

    // Getters y Setters

    /**
     * @return Escenario actual del juego
     */
    public Escenario getEscenario() {
        return escenario;
    }

    /**
     * @return Protagonista del juego
     */
    public Protagonista getProtagonista() {
        return protagonista;
    }

    /**
     * @return Lista de enemigos activos (copia defensiva)
     */
    public ArrayList<Enemigo> getEnemigos() {
        return new ArrayList<>(enemigos); // Devuelve copia para evitar modificaciones externas
    }

    /**
     * @return true si el juego sigue en curso
     */
    public boolean isJuegoActivo() {
        return juegoActivo;
    }

    /**
     * @return true si el juego terminó en victoria
     */
    public boolean isVictoria() {
        return !juegoActivo && protagonista.getSalud() > 0;
    }

    /**
     * Establece un nuevo protagonista.
     * 
     * @param prota Nuevo protagonista
     */
    public void setProtagonista(Protagonista prota) {
        this.protagonista = prota;
    }

    /**
     * Finaliza el juego con victoria o derrota.
     * 
     * @param victoria true si el jugador ha ganado
     */
    public void finDelJuego(boolean victoria) {
        juegoActivo = false;
        System.out.println("Juego terminado. " + (victoria ? "¡Has ganado!" : "Has perdido..."));
        notifyObservers();
    }

    /**
     * @return Cadena representando el estado actual del juego.
     */
    public String getEstadoJuego() {
        if (juegoActivo)
            return "Juego en curso";
        return isVictoria() ? "¡Victoria!" : "¡Derrota!";
    }
}