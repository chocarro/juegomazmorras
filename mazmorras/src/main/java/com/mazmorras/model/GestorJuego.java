package com.mazmorras.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.mazmorras.interfaces.Observer;

public class GestorJuego {
    private static GestorJuego instance;
    private Escenario escenario;
    private Protagonista protagonista;
    private ArrayList<Enemigo> enemigos;
    private boolean juegoActivo;
    private ArrayList<Observer> observers;

    // Constructor privado
    GestorJuego() {
        this.escenario = new Escenario();
        this.enemigos = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.juegoActivo = true;

        inicializarJuego();
    }

    // Métodos de inicialización
    private void inicializarJuego() {
        inicializarProtagonista();
        cargarEnemigosIniciales();
        actualizarEscenario();
    }

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

    // Sistema de observadores
    public void subscribe(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        System.out.println("Notificando a " + observers.size() + " observadores");
        observers.forEach(Observer::onChange);
    }

    // Lógica del juego mejorada
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
            
            // 4. Turno de enemigos (ordenados por velocidad)
            turnoEnemigos();
            
            // 5. Verificar estado del juego después del turno de enemigos
            verificarEstadoJuego();
        } else {
            System.out.println("El protagonista no pudo realizar la acción: " + direccion);
        }
        
        System.out.println("=== FIN DE TURNO ===");
        
        // 6. Actualizar escenario y notificar cambios finales
        actualizarEscenario();
        notifyObservers();
    }

    private boolean ejecutarAccionProtagonista(String direccion) {
        int[] posActual = protagonista.getPosicion();
        String accion = protagonista.comprobarAccion(posActual, direccion);
        
        int nuevaFila = posActual[0];
        int nuevaCol = posActual[1];
        
        // Calcular nueva posición
        switch (direccion.toUpperCase()) {
            case "W": nuevaFila--; break;
            case "A": nuevaCol--; break;
            case "S": nuevaFila++; break;
            case "D": nuevaCol++; break;
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

    private boolean moverProtagonista(int nuevaFila, int nuevaCol) {
        // Actualizar posición del protagonista
        protagonista.setPosicion(new int[]{nuevaFila, nuevaCol});
        
        System.out.println("Protagonista movido a: [" + nuevaFila + "," + nuevaCol + "]");
        return true;
    }

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

    private void turnoEnemigos() {
        if (enemigos.isEmpty()) {
            System.out.println("No hay enemigos para procesar");
            return;
        }
        
        // Crear lista de enemigos ordenada por velocidad (mayor velocidad primero)
        ArrayList<Enemigo> enemigosOrdenados = new ArrayList<>(enemigos);
        enemigosOrdenados.sort((e1, e2) -> Integer.compare(e2.getVelocidad(), e1.getVelocidad()));
        
        System.out.println("Procesando turno de " + enemigosOrdenados.size() + " enemigos");
        
        for (Enemigo enemigo : enemigosOrdenados) {
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
                    // Intentar moverse
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

    private void moverEnemigo(Enemigo enemigo, String direccion) {
        int[] posActual = enemigo.getPosicion();
        String accion = enemigo.comprobarAccion(posActual, direccion);
        
        if (accion.equals("mover")) {
            int nuevaFila = posActual[0];
            int nuevaCol = posActual[1];
            
            switch (direccion.toUpperCase()) {
                case "W": nuevaFila--; break;
                case "A": nuevaCol--; break;
                case "S": nuevaFila++; break;
                case "D": nuevaCol++; break;
            }
            
            // Actualizar posición del enemigo
            enemigo.setPosicion(new int[]{nuevaFila, nuevaCol});
            
            System.out.println("Enemigo ID " + enemigo.getId() + " movido de " + 
                             Arrays.toString(posActual) + " a [" + nuevaFila + "," + nuevaCol + "]");
        }
    }

    private boolean enemigoEsAdyacente(Enemigo enemigo) {
        int[] posEnemigo = enemigo.getPosicion();
        int[] posProta = protagonista.getPosicion();
        
        int distanciaFila = Math.abs(posEnemigo[0] - posProta[0]);
        int distanciaCol = Math.abs(posEnemigo[1] - posProta[1]);
        
        // Adyacente significa distancia máxima de 1 en cualquier dirección
        // pero no en la misma posición
        boolean esAdyacente = distanciaFila <= 1 && distanciaCol <= 1 && 
                             (distanciaFila + distanciaCol) > 0;
        
        if (esAdyacente) {
            System.out.println("Enemigo ID " + enemigo.getId() + " es adyacente al protagonista");
        }
        
        return esAdyacente;
    }

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

    private void eliminarEnemigosMuertos() {
        Iterator<Enemigo> iterator = enemigos.iterator();
        while (iterator.hasNext()) {
            Enemigo enemigo = iterator.next();
            if (enemigo.getSalud() <= 0) {
                iterator.remove();
                System.out.println("Enemigo ID " + enemigo.getId() + " eliminado (murió)");
            }
        }
    }

    private void verificarEstadoJuego() {
        if (protagonista.getSalud() <= 0) {
            juegoActivo = false;
            System.out.println("¡DERROTA! El protagonista ha muerto");
        } else if (enemigos.isEmpty()) {
            juegoActivo = false;
            System.out.println("¡VICTORIA! Todos los enemigos han sido derrotados");
        }
    }

    private void actualizarEscenario() {
        // Limpiar el escenario de personajes, manteniendo solo paredes ("P") y suelo ("0")
        String[][] matriz = this.escenario.getEscenario();
        
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                // Solo limpiar si no es pared
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
        for (Enemigo enemigo : enemigos) {
            if (enemigo.getSalud() > 0) {
                int[] pos = enemigo.getPosicion();
                if (pos[0] >= 0 && pos[0] < matriz.length && 
                    pos[1] >= 0 && pos[1] < matriz[0].length) {
                    matriz[pos[0]][pos[1]] = String.valueOf(enemigo.getId());
                }
            }
        }
        
        System.out.println("Escenario actualizado - Protagonista: " + Arrays.toString(protagonista.getPosicion()) + 
                          ", Enemigos vivos: " + enemigos.size());
    }

    // Búsquedas
    public Enemigo buscarEnemigoEnPosicion(int fila, int columna) {
        return enemigos.stream()
                .filter(e -> e.getPosicion()[0] == fila && e.getPosicion()[1] == columna && e.getSalud() > 0)
                .findFirst()
                .orElse(null);
    }

    public Protagonista buscarProta() {
        return this.protagonista;
    }

    // Gestión de enemigos
    public void eliminarEnemigo(Enemigo enemigo) {
        if (enemigo != null) {
            enemigos.remove(enemigo);
            System.out.println("Enemigo ID " + enemigo.getId() + " eliminado manualmente");
        }
    }

    // Reinicio del juego
    public void reiniciarJuego() {
        System.out.println("Reiniciando juego...");
        this.escenario = new Escenario();
        this.enemigos.clear();
        this.protagonista = null;
        this.juegoActivo = true;

        inicializarJuego();
        notifyObservers();
        System.out.println("Juego reiniciado");
    }

    // Getters y Setters
    public Escenario getEscenario() {
        return escenario;
    }

    public Protagonista getProtagonista() {
        return protagonista;
    }

    public ArrayList<Enemigo> getEnemigos() {
        return new ArrayList<>(enemigos); // Devuelve copia para evitar modificaciones externas
    }

    public boolean isJuegoActivo() {
        return juegoActivo;
    }

    public boolean isVictoria() {
        return !juegoActivo && protagonista.getSalud() > 0;
    }

    public void setProtagonista(Protagonista prota) {
        this.protagonista = prota;
    }

    public void finDelJuego(boolean victoria) {
        juegoActivo = false;
        System.out.println("Juego terminado. " + (victoria ? "¡Has ganado!" : "Has perdido..."));
        notifyObservers();
    }

    // Métodos para UI
    public String getEstadoJuego() {
        if (juegoActivo)
            return "Juego en curso";
        return isVictoria() ? "¡Victoria!" : "¡Derrota!";
    }
}