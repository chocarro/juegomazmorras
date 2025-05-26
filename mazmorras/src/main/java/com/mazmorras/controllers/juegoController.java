package com.mazmorras.controllers;

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

    private void validarPosicionProtagonista() {
        Protagonista prota = gestorJuego.getProtagonista();
        if (prota != null) {
            int[] pos = prota.getPosicion();
            String[][] escenario = gestorJuego.getEscenario().getEscenario();
            
            System.out.println("Validando posición del protagonista: [" + pos[0] + "," + pos[1] + "]");
            
            // Verificar si la posición actual es válida (no es pared y está dentro de límites)
            if (pos[0] < 0 || pos[0] >= escenario.length || 
                pos[1] < 0 || pos[1] >= escenario[0].length || 
                escenario[pos[0]][pos[1]].equals("P")) {
                
                System.err.println("Posición inválida del protagonista: [" + pos[0] + "," + pos[1] + "]");
                
                // Buscar la primera posición válida (que no sea pared)
                boolean posicionEncontrada = false;
                for (int i = 1; i < escenario.length - 1 && !posicionEncontrada; i++) {
                    for (int j = 1; j < escenario[i].length - 1 && !posicionEncontrada; j++) {
                        if (!escenario[i][j].equals("P")) {
                            prota.setPosicion(new int[]{i, j});
                            System.out.println("Protagonista reposicionado a: [" + i + "," + j + "]");
                            posicionEncontrada = true;
                        }
                    }
                }
                
                if (!posicionEncontrada) {
                    System.err.println("ERROR CRÍTICO: No se encontró posición válida para el protagonista");
                    // Como último recurso, colocar en [1,1] aunque sea pared
                    prota.setPosicion(new int[]{1, 1});
                }
            } else {
                System.out.println("Posición del protagonista válida: [" + pos[0] + "," + pos[1] + "]");
            }
        }
    }

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

    private void configurarControles(KeyCode keyCode) {
        if (procesandoTurno || !gestorJuego.isJuegoActivo()) {
            return;
        }

        System.out.println("Tecla presionada: " + keyCode);
        
        String direccion = "";
        switch (keyCode) {
            case W: case UP:    direccion = "W"; break;
            case A: case LEFT:  direccion = "A"; break;
            case S: case DOWN:  direccion = "S"; break;
            case D: case RIGHT: direccion = "D"; break;
            default: return;
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

    private void actualizarVista() {
        Platform.runLater(() -> {
            try {
                actualizarEstadisticas();
                pintarPersonajes();
                checkFinJuego();
                System.out.println("Vista actualizada correctamente");
            } catch (Exception e) {
                System.err.println("Error actualizando vista: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void actualizarEstadisticas() {
        Protagonista prota = gestorJuego.getProtagonista();
        if (prota != null) {
            lblSaludProta.setText(String.format("Salud: %d/%d", prota.getSalud(), prota.getSaludMaxima()));
            lblAtaqueProta.setText(String.format("Ataque: %d", prota.getAtaque()));
            lblDefensaProta.setText(String.format("Defensa: %d", prota.getDefensa()));
            lblVelocidadProta.setText(String.format("Velocidad: %d", prota.getVelocidad()));
            
            System.out.println("Estadísticas actualizadas - Salud: " + prota.getSalud() + "/" + prota.getSaludMaxima());
        } else {
            System.err.println("Error: Protagonista es null al actualizar estadísticas");
        }

        actualizarEnemigosCercanos();
    }

    private void actualizarEnemigosCercanos() {
        enemiesPanel.getChildren().clear();
        
        Label titulo = new Label("ENEMIGOS CERCANOS");
        titulo.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        enemiesPanel.getChildren().add(titulo);

        Protagonista prota = gestorJuego.getProtagonista();
        if (prota == null) {
            return;
        }

        int[] posProta = prota.getPosicion();
        int enemigosEncontrados = 0;

        for (Enemigo enemigo : gestorJuego.getEnemigos()) {
            if (enemigo.getSalud() <= 0) continue; // Skip enemigos muertos
            
            int[] posEnemigo = enemigo.getPosicion();
            int distancia = Math.max(Math.abs(posProta[0] - posEnemigo[0]),
                    Math.abs(posProta[1] - posEnemigo[1]));

            // Mostrar enemigos a distancia <= 5
            if (distancia <= 5) {
                HBox hbox = new HBox(10);
                hbox.setStyle("-fx-alignment: center-left;");
                
                ImageView img = crearImagenPersonaje(enemigo);
                if (img != null) {
                    img.setFitWidth(20);
                    img.setFitHeight(20);
                    hbox.getChildren().add(img);
                }

                Label lbl = new Label(String.format("ID:%d HP:%d/%d Dist:%d",
                        enemigo.getId(), enemigo.getSalud(), enemigo.getSaludMaxima(), distancia));
                lbl.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");

                hbox.getChildren().add(lbl);
                enemiesPanel.getChildren().add(hbox);
                enemigosEncontrados++;
            }
        }
        
        if (enemigosEncontrados == 0) {
            Label noEnemigos = new Label("No hay enemigos cerca");
            noEnemigos.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            enemiesPanel.getChildren().add(noEnemigos);
        }
    }

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

                    String ruta = escenario[i][j].equals("P") ? 
                            gestorJuego.getEscenario().getPared() : 
                            gestorJuego.getEscenario().getSuelo();

                    try {
                        InputStream is = getClass().getResourceAsStream(ruta);
                        if (is != null) {
                            img.setImage(new Image(is));
                        } else {
                            System.err.println("Imagen no encontrada: " + ruta);
                            // Crear imagen de placeholder si no se encuentra
                            img.setStyle("-fx-background-color: " + (escenario[i][j].equals("P") ? "brown" : "lightgray"));
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
                            System.err.println("ERROR: Protagonista en posición de pared [" + pos[0] + "," + pos[1] + "]");
                            // Reposicionar el protagonista
                            validarPosicionProtagonista();
                            pos = prota.getPosicion();
                            gridPane2.add(imgProta, pos[1], pos[0]);
                            System.out.println("Protagonista reposicionado y pintado en: [" + pos[0] + "," + pos[1] + "]");
                        }
                    } else {
                        System.err.println("Posición del protagonista fuera de límites: [" + pos[0] + "," + pos[1] + "]");
                    }
                }
            }

            // Pintar enemigos vivos
            int enemigosVivos = 0;
            for (Enemigo enemigo : gestorJuego.getEnemigos()) {
                if (enemigo.getSalud() > 0) {
                    ImageView imgEnemigo = crearImagenPersonaje(enemigo);
                    if (imgEnemigo != null) {
                        int[] pos = enemigo.getPosicion();
                        
                        // Verificar que la posición esté dentro de los límites
                        if (pos[0] >= 0 && pos[0] < gridPane2.getRowConstraints().size() &&
                            pos[1] >= 0 && pos[1] < gridPane2.getColumnConstraints().size()) {
                            
                            gridPane2.add(imgEnemigo, pos[1], pos[0]);
                            enemigosVivos++;
                        } else {
                            System.err.println("Posición del enemigo " + enemigo.getId() + 
                                             " fuera de límites: [" + pos[0] + "," + pos[1] + "]");
                        }
                    }
                }
            }
            
            System.out.println("Personajes pintados - Enemigos vivos: " + enemigosVivos);
        } catch (Exception e) {
            System.err.println("Error pintando personajes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ImageView crearImagenPersonaje(Personaje personaje) {
        try {
            String rutaImagen = personaje.getImagen();
            InputStream is = getClass().getResourceAsStream(rutaImagen);
            
            if (is == null) {
                System.err.println("Imagen no encontrada: " + rutaImagen);
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

    private ImageView crearImagenPlaceholder(Personaje personaje) {
        ImageView img = new ImageView();
        img.setFitWidth(40);
        img.setFitHeight(40);
        
        // Crear un placeholder visual simple
        String color = personaje instanceof Protagonista ? "blue" : "red";
        img.setStyle("-fx-background-color: " + color + "; -fx-border-color: black; -fx-border-width: 1;");
        
        return img;
    }

    private void checkFinJuego() {
        if (!gestorJuego.isJuegoActivo()) {
            endGamePanel.setVisible(true);
            lblResultado.setText(gestorJuego.isVictoria() ? "¡Has ganado!" : "¡Has perdido!");
            lblResultado.setStyle(gestorJuego.isVictoria() ? 
                "-fx-text-fill: green; -fx-font-size: 18px; -fx-font-weight: bold;" :
                "-fx-text-fill: red; -fx-font-size: 18px; -fx-font-weight: bold;");
            
            System.out.println("Fin del juego detectado: " + (gestorJuego.isVictoria() ? "Victoria" : "Derrota"));
        } else {
            endGamePanel.setVisible(false);
        }
    }

    @Override
    public void onChange() {
        System.out.println("Observer notificado - Actualizando vista");
        actualizarVista();
    }
}