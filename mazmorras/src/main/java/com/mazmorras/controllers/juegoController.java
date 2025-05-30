package com.mazmorras.controllers;

/**
 * Controlador principal para la interfaz de usuario del juego Mazmorras.
 * Gestiona la interacción del usuario, actualiza la vista y coordina
 * las acciones con el modelo del juego a través del {@link GestorJuego}.
 * Implementa el patrón Observer para recibir actualizaciones del modelo.
 */

import java.io.InputStream;
import com.mazmorras.interfaces.Observer;
import com.mazmorras.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class juegoController implements Observer {

    @FXML
    private VBox statsPanel;
    @FXML
    private VBox enemiesPanel;
    @FXML
    private Label lblSaludProta;
    @FXML
    private Label lblAtaqueProta;
    @FXML
    private Label lblDefensaProta;
    @FXML
    private Label lblVelocidadProta;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private GridPane gridPane; // Para el escenario
    @FXML
    private GridPane gridPane2; // Para personajes
    @FXML
    private VBox endGamePanel;
    @FXML
    private Label lblResultado;
    @FXML
    private Button btnReiniciar;
    private GestorJuego gestorJuego;
    private boolean procesandoTurno = false; // Para evitar múltiples procesamientos simultáneos

    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     * Configura los componentes iniciales, suscribe el controlador como observador,
     * carga el escenario y configura los controles de teclado.
     */

    @FXML
    public void initialize() {
        try {
            gestorJuego = Proveedor.getInstance().getGestorJuego();
            gestorJuego.subscribe(this);

            configurarFinJuegoUI();
            cargarEscenario();

            // Validar y corregir posición del protagonista antes de actualizar vista
            validarPosicionProtagonista();
            actualizarVista(); // Actualizar todo al inicio

            // Configurar controles de teclado
            Platform.runLater(() -> {
                if (anchorPane.getScene() != null) {
                    configurarEventosTeclado();
                } else {
                    anchorPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                        if (newScene != null) {
                            configurarEventosTeclado();
                        }
                    });
                }
            });

            System.out.println("Controller inicializado correctamente");
        } catch (Exception e) {
            System.err.println("Error en initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida y corrige la posición del protagonista en el escenario.
     * Si la posición actual es inválida (fuera de límites o en pared),
     * busca una posición válida alternativa.
     */

    private void validarPosicionProtagonista() {
        Protagonista prota = gestorJuego.getProtagonista();
        if (prota != null) {
            int[] pos = prota.getPosicion();
            String[][] escenario = gestorJuego.getEscenario().getEscenario();

            System.out.println("Validando posición del protagonista: [" + pos[0] + "," + pos[1] + "]");

            // Verificar si la posición actual es válida (no es pared y está dentro de
            // límites)
            if (pos[0] < 0 || pos[0] >= escenario.length ||
                    pos[1] < 0 || pos[1] >= escenario[0].length ||
                    escenario[pos[0]][pos[1]].equals("P")) {

                System.err.println("Posición inválida del protagonista: [" + pos[0] + "," + pos[1] + "]");

                // Buscar la primera posición válida (que no sea pared)
                boolean posicionEncontrada = false;
                for (int i = 1; i < escenario.length - 1 && !posicionEncontrada; i++) {
                    for (int j = 1; j < escenario[i].length - 1 && !posicionEncontrada; j++) {
                        if (!escenario[i][j].equals("P")) {
                            // Verificar que no haya un enemigo en esta posición
                            Enemigo enemigoEnPosicion = gestorJuego.buscarEnemigoEnPosicion(i, j);
                            if (enemigoEnPosicion == null) {
                                prota.setPosicion(new int[] { i, j });
                                System.out.println("Protagonista reposicionado a: [" + i + "," + j + "]");
                                posicionEncontrada = true;
                            }
                        }
                    }
                }

                if (!posicionEncontrada) {
                    System.err.println("ERROR CRÍTICO: No se encontró posición válida para el protagonista");
                    // Como último recurso, colocar en [1,1] aunque sea pared
                    prota.setPosicion(new int[] { 1, 1 });
                }
            } else {
                System.out.println("Posición del protagonista válida: [" + pos[0] + "," + pos[1] + "]");
            }
        }
    }

    /**
     * Configura los eventos de teclado para controlar al protagonista.
     * Las teclas WASD y flechas direccionales son válidas.
     */

    private void configurarEventosTeclado() {
        anchorPane.getScene().setOnKeyPressed(event -> {
            if (!procesandoTurno && gestorJuego.isJuegoActivo()) {
                configurarControles(event.getCode());
            }
        });

        // Asegurar que el anchorPane pueda recibir focus para eventos de teclado
        anchorPane.setFocusTraversable(true);
        anchorPane.requestFocus();
    }

    /**
     * Procesa la tecla presionada y ejecuta la acción correspondiente.
     * 
     * @param keyCode Código de la tecla presionada
     */

    private void configurarControles(KeyCode keyCode) {
        if (procesandoTurno || !gestorJuego.isJuegoActivo()) {
            return;
        }

        System.out.println("Tecla presionada: " + keyCode);

        String direccion = "";
        switch (keyCode) {
            case W:
            case UP:
                direccion = "W";
                break;
            case A:
            case LEFT:
                direccion = "A";
                break;
            case S:
            case DOWN:
                direccion = "S";
                break;
            case D:
            case RIGHT:
                direccion = "D";
                break;
            default:
                return;
        }

        procesandoTurno = true;

        try {
            // Ejecutar la acción del protagonista y todos los turnos subsecuentes
            gestorJuego.realizarAccionProtagonista(direccion);
            System.out.println("Turno completado");
        } catch (Exception e) {
            System.err.println("Error procesando turno: " + e.getMessage());
            e.printStackTrace();
        } finally {
            procesandoTurno = false;
        }
    }

    /**
     * Configura la interfaz de usuario para el fin del juego.
     * Incluye el panel de resultados y el botón de reinicio.
     */
    private void configurarFinJuegoUI() {
        endGamePanel.setVisible(false);
        btnReiniciar.setOnAction(e -> {
            endGamePanel.setVisible(false);
            gestorJuego.reiniciarJuego();
            // Validar posición después del reinicio
            validarPosicionProtagonista();
            Platform.runLater(() -> anchorPane.requestFocus()); // Restaurar focus después de reinicio
        });
    }

    /**
     * Actualiza todos los componentes de la vista.
     * Se ejecuta en el hilo de JavaFX mediante Platform.runLater().
     */

    private void actualizarVista() {
        Platform.runLater(() -> {
            try {
                actualizarEstadisticas();
                actualizarEnemigosCercanos(); // Añadir esta línea
                pintarPersonajes();
                checkFinJuego();
                System.out.println("Vista actualizada correctamente");
            } catch (Exception e) {
                System.err.println("Error actualizando vista: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Actualiza las estadísticas del protagonista en la UI.
     * Muestra salud, ataque, defensa y velocidad.
     */
    private void actualizarEstadisticas() {
        Protagonista prota = gestorJuego.getProtagonista();
        if (prota != null) {
            lblSaludProta.setText(String.format("Salud: %d/%d", prota.getSalud(), prota.getSaludMaxima()));
            lblAtaqueProta.setText(String.format("Ataque: %d", prota.getAtaque()));
            lblDefensaProta.setText(String.format("Defensa: %d", prota.getDefensa()));
            lblVelocidadProta.setText(String.format("Velocidad: %d", prota.getVelocidad()));
        }
    }

    /**
     * Actualiza el panel de enemigos cercanos.
     * Muestra información de enemigos a distancia <= 5 del protagonista.
     * CORREGIDO: Ahora calcula correctamente la distancia y actualiza la
     * información en tiempo real.
     */
    private void actualizarEnemigosCercanos() {
        Platform.runLater(() -> {
            try {
                enemiesPanel.getChildren().clear();

                Label titulo = new Label("ENEMIGOS CERCANOS");
                titulo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
                enemiesPanel.getChildren().add(titulo);

                Protagonista prota = gestorJuego.getProtagonista();
                if (prota == null) {
                    Label error = new Label("Error: No se encontró protagonista");
                    error.setStyle("-fx-text-fill: red; -fx-font-style: italic;");
                    enemiesPanel.getChildren().add(error);
                    return;
                }

                int[] posProta = prota.getPosicion();
                int enemigosEncontrados = 0;
                java.util.ArrayList<Enemigo> enemigosActuales = gestorJuego.getEnemigos();

                for (Enemigo enemigo : enemigosActuales) {
                    if (enemigo.getSalud() <= 0)
                        continue;

                    int[] posEnemigo = enemigo.getPosicion();
                    int distanciaManhattan = Math.abs(posProta[0] - posEnemigo[0])
                            + Math.abs(posProta[1] - posEnemigo[1]);
                    int distanciaChebyshev = Math.max(Math.abs(posProta[0] - posEnemigo[0]),
                            Math.abs(posProta[1] - posEnemigo[1]));

                    if (distanciaManhattan <= 5) {
                        HBox hbox = new HBox(5);
                        hbox.setStyle("-fx-alignment: center-left; -fx-padding: 2;");

                        ImageView img = crearImagenPersonaje(enemigo);
                        if (img != null) {
                            img.setFitWidth(16);
                            img.setFitHeight(16);
                            hbox.getChildren().add(img);
                        }

                        boolean esAdyacente = (distanciaChebyshev == 1);
                        String estadoAdyacente = esAdyacente ? " [PELIGRO!]" : "";

                        Label lbl = new Label(String.format("ID:%d HP:%d/%d Dist:%d%s",
                                enemigo.getId(), enemigo.getSalud(), enemigo.getSaludMaxima(),
                                distanciaManhattan, estadoAdyacente));

                        if (esAdyacente) {
                            lbl.setStyle("-fx-text-fill: #ff6666; -fx-font-size: 10px; -fx-font-weight: bold;");
                        } else {
                            lbl.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
                        }

                        hbox.getChildren().add(lbl);
                        enemiesPanel.getChildren().add(hbox);
                        enemigosEncontrados++;
                    }
                }

                if (enemigosEncontrados == 0) {
                    Label noEnemigos = new Label("No hay enemigos cerca");
                    noEnemigos.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-font-size: 10px;");
                    enemiesPanel.getChildren().add(noEnemigos);
                }

                // Añadir información adicional
                Label estadoJuego = new Label("Estado: " + gestorJuego.getEstadoJuego());
                estadoJuego.setStyle("-fx-text-fill: lightgray; -fx-font-size: 9px; -fx-font-style: italic;");
                enemiesPanel.getChildren().add(estadoJuego);

                long enemigosVivos = enemigosActuales.stream().filter(e -> e.getSalud() > 0).count();
                Label totalEnemigos = new Label("Enemigos restantes: " + enemigosVivos);
                totalEnemigos.setStyle("-fx-text-fill: lightblue; -fx-font-size: 9px;");
                enemiesPanel.getChildren().add(totalEnemigos);

            } catch (Exception e) {
                System.err.println("Error actualizando enemigos cercanos: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Carga y muestra el escenario del juego en el GridPane.
     * Configura las dimensiones y añade las imágenes de paredes y suelo.
     */
    private void cargarEscenario() {
        try {
            gridPane.getChildren().clear();
            gridPane.getRowConstraints().clear();
            gridPane.getColumnConstraints().clear();

            String[][] escenario = gestorJuego.getEscenario().getEscenario();

            // Configurar constraints para el GridPane del escenario
            for (int i = 0; i < escenario.length; i++) {
                RowConstraints rc = new RowConstraints(40);
                rc.setValignment(VPos.CENTER);
                gridPane.getRowConstraints().add(rc);
            }

            for (int j = 0; j < escenario[0].length; j++) {
                ColumnConstraints cc = new ColumnConstraints(40);
                cc.setHalignment(HPos.CENTER);
                gridPane.getColumnConstraints().add(cc);
            }

            // Configurar constraints para el GridPane de personajes (debe coincidir)
            gridPane2.getRowConstraints().clear();
            gridPane2.getColumnConstraints().clear();

            for (int i = 0; i < escenario.length; i++) {
                RowConstraints rc = new RowConstraints(40);
                rc.setValignment(VPos.CENTER);
                gridPane2.getRowConstraints().add(rc);
            }

            for (int j = 0; j < escenario[0].length; j++) {
                ColumnConstraints cc = new ColumnConstraints(40);
                cc.setHalignment(HPos.CENTER);
                gridPane2.getColumnConstraints().add(cc);
            }

            // Cargar imágenes del escenario
            for (int i = 0; i < escenario.length; i++) {
                for (int j = 0; j < escenario[i].length; j++) {
                    ImageView img = new ImageView();
                    img.setFitWidth(40);
                    img.setFitHeight(40);

                    String ruta = escenario[i][j].equals("P") ? gestorJuego.getEscenario().getPared()
                            : gestorJuego.getEscenario().getSuelo();

                    try {
                        InputStream is = getClass().getResourceAsStream(ruta);
                        if (is != null) {
                            img.setImage(new Image(is));
                        } else {
                            System.err.println("Imagen no encontrada: " + ruta);
                            // Crear imagen de placeholder si no se encuentra
                            img.setStyle(
                                    "-fx-background-color: " + (escenario[i][j].equals("P") ? "brown" : "lightgray"));
                        }
                    } catch (Exception e) {
                        System.err.println("Error cargando imagen: " + e.getMessage());
                        img.setStyle("-fx-background-color: red"); // Color de error
                    }

                    gridPane.add(img, j, i);
                }
            }

            System.out.println("Escenario cargado: " + escenario.length + "x" + escenario[0].length);
        } catch (Exception e) {
            System.err.println("Error cargando escenario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Dibuja todos los personajes (protagonista y enemigos) en el GridPane.
     * Solo muestra personajes con salud > 0.
     * CORREGIDO: Mejor validación de posiciones y manejo de errores.
     */
    private void pintarPersonajes() {
        try {
            gridPane2.getChildren().clear();

            // Validar posición del protagonista antes de pintarlo
            validarPosicionProtagonista();

            // Pintar protagonista
            Protagonista prota = gestorJuego.getProtagonista();
            if (prota != null && prota.getSalud() > 0) {
                ImageView imgProta = crearImagenPersonaje(prota);
                if (imgProta != null) {
                    int[] pos = prota.getPosicion();

                    // Verificar que la posición esté dentro de los límites
                    if (pos[0] >= 0 && pos[0] < gridPane2.getRowConstraints().size() &&
                            pos[1] >= 0 && pos[1] < gridPane2.getColumnConstraints().size()) {

                        // Verificar que no sea una pared
                        String[][] escenario = gestorJuego.getEscenario().getEscenario();
                        if (!escenario[pos[0]][pos[1]].equals("P")) {
                            gridPane2.add(imgProta, pos[1], pos[0]);
                            System.out.println("Protagonista pintado en: [" + pos[0] + "," + pos[1] + "]");
                        } else {
                            System.err.println(
                                    "ERROR: Protagonista en posición de pared [" + pos[0] + "," + pos[1]
                                            + "], reposicionando...");
                            // Reposicionar el protagonista
                            validarPosicionProtagonista();
                            pos = prota.getPosicion();
                            if (pos[0] >= 0 && pos[0] < gridPane2.getRowConstraints().size() &&
                                    pos[1] >= 0 && pos[1] < gridPane2.getColumnConstraints().size()) {
                                gridPane2.add(imgProta, pos[1], pos[0]);
                                System.out.println(
                                        "Protagonista reposicionado y pintado en: [" + pos[0] + "," + pos[1] + "]");
                            }
                        }
                    } else {
                        System.err
                                .println("Posición del protagonista fuera de límites: [" + pos[0] + "," + pos[1]
                                        + "], reposicionando...");
                        validarPosicionProtagonista();
                        pos = prota.getPosicion();
                        if (pos[0] >= 0 && pos[0] < gridPane2.getRowConstraints().size() &&
                                pos[1] >= 0 && pos[1] < gridPane2.getColumnConstraints().size()) {
                            gridPane2.add(imgProta, pos[1], pos[0]);
                            System.out.println("Protagonista reposicionado después de estar fuera de límites: ["
                                    + pos[0] + "," + pos[1] + "]");
                        }
                    }
                }
            } else if (prota != null) {
                System.out.println("Protagonista está muerto, no se pinta");
            }

            // Pintar enemigos vivos
            int enemigosVivos = 0;
            java.util.ArrayList<Enemigo> enemigosActuales = gestorJuego.getEnemigos();

            for (Enemigo enemigo : enemigosActuales) {
                if (enemigo.getSalud() > 0) {
                    ImageView imgEnemigo = crearImagenPersonaje(enemigo);
                    if (imgEnemigo != null) {
                        int[] pos = enemigo.getPosicion();

                        // Verificar que la posición esté dentro de los límites
                        if (pos[0] >= 0 && pos[0] < gridPane2.getRowConstraints().size() &&
                                pos[1] >= 0 && pos[1] < gridPane2.getColumnConstraints().size()) {

                            gridPane2.add(imgEnemigo, pos[1], pos[0]);
                            enemigosVivos++;
                            System.out.println(
                                    "Enemigo ID " + enemigo.getId() + " pintado en: [" + pos[0] + "," + pos[1] + "]");
                        } else {
                            System.err.println("Posición del enemigo " + enemigo.getId() +
                                    " fuera de límites: [" + pos[0] + "," + pos[1] + "]");
                        }
                    }
                } else {
                    System.out.println("Enemigo ID " + enemigo.getId() + " está muerto, no se pinta");
                }
            }

            System.out.println("Personajes pintados - Enemigos vivos: " + enemigosVivos + " de "
                    + enemigosActuales.size() + " totales");
        } catch (Exception e) {
            System.err.println("Error pintando personajes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea un ImageView para representar visualmente un personaje.
     * 
     * @param personaje Personaje a representar (Protagonista o Enemigo)
     * @return ImageView con la imagen del personaje o un placeholder si falla la
     *         carga
     */
    private ImageView crearImagenPersonaje(Personaje personaje) {
        try {
            String rutaImagen = personaje.getImagen();
            InputStream is = getClass().getResourceAsStream(rutaImagen);

            if (is == null) {
                System.err.println("Imagen no encontrada: " + rutaImagen + " para " +
                        (personaje instanceof Protagonista ? "protagonista" : "enemigo ID " + personaje.getId()));
                return crearImagenPlaceholder(personaje);
            }

            ImageView img = new ImageView(new Image(is));
            img.setFitWidth(40);
            img.setFitHeight(40);
            img.setPreserveRatio(true);
            img.setSmooth(true);

            return img;
        } catch (Exception e) {
            System.err.println("Error cargando imagen de personaje: " + e.getMessage());
            return crearImagenPlaceholder(personaje);
        }
    }

    /**
     * Crea un ImageView de reemplazo cuando falla la carga de la imagen original.
     * 
     * @param personaje Personaje a representar
     * @return ImageView con un placeholder coloreado según el tipo de personaje
     */
    private ImageView crearImagenPlaceholder(Personaje personaje) {
        ImageView img = new ImageView();
        img.setFitWidth(40);
        img.setFitHeight(40);

        // Crear un placeholder visual simple
        String color = personaje instanceof Protagonista ? "blue" : "red";
        img.setStyle("-fx-background-color: " + color + "; -fx-border-color: black; -fx-border-width: 1;");

        return img;
    }

    /**
     * Verifica el estado del juego y muestra el panel de fin de juego si
     * corresponde.
     * Muestra mensaje de victoria o derrota según el resultado.
     */
    private void checkFinJuego() {
        if (!gestorJuego.isJuegoActivo()) {
            endGamePanel.setVisible(true);
            lblResultado.setText(gestorJuego.isVictoria() ? "¡Has ganado!" : "¡Has perdido!");
            lblResultado.setStyle(
                    gestorJuego.isVictoria() ? "-fx-text-fill: green; -fx-font-size: 18px; -fx-font-weight: bold;"
                            : "-fx-text-fill: red; -fx-font-size: 18px; -fx-font-weight: bold;");

            System.out.println("Fin del juego detectado: " + (gestorJuego.isVictoria() ? "Victoria" : "Derrota"));
        } else {
            endGamePanel.setVisible(false);
        }
    }

    /**
     * Método callback del patrón Observer.
     * Se ejecuta cuando el modelo notifica cambios y actualiza la vista.
     */
    @Override
    public void onChange() {
        System.out.println("Observer notificado - Actualizando vista");
        actualizarVista();
    }
}