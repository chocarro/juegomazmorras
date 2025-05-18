package com.mazmorras.controllers;

import java.io.InputStream;

import com.mazmorras.interfaces.Observer;
import com.mazmorras.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class juegoController implements Observer {
    @FXML private AnchorPane anchorPane;
    @FXML private GridPane gridPane;  // Para el escenario
    @FXML private GridPane gridPane2; // Para personajes
    @FXML private VBox endGamePanel;
    @FXML private Label lblResultado;
    @FXML private Button btnReiniciar;
    
    private GestorJuego gestorJuego;

    @FXML
    public void initialize() {
        gestorJuego = Proveedor.getInstance().getGestorJuego();
        gestorJuego.subscribe(this);
        
        configurarControles();
        configurarFinJuegoUI();
        actualizarEscenario();
        
        anchorPane.requestFocus();
    }

private void configurarControles() {
    anchorPane.setOnKeyPressed(event -> {
        Protagonista prota = gestorJuego.getProtagonista();
        if (prota != null) {
            switch (event.getCode()) {
    case W:
    case UP:
        prota.accion("W");
        break;
    case A:
    case LEFT:
        prota.accion("A");
        break;
    case S:
    case DOWN:
        prota.accion("S");
        break;
    case D:
    case RIGHT:
        prota.accion("D");
        break;
    default:
        break;
}
            actualizarEscenario();
        }
    });
    anchorPane.setFocusTraversable(true);
}


    private void configurarFinJuegoUI() {
        endGamePanel.setVisible(false);
        btnReiniciar.setOnAction(e -> gestorJuego.reiniciarJuego());
    }

    private void actualizarEscenario() {
        cargarEscenario();
        pintarPersonajes();
        checkFinJuego();
    }

private void cargarEscenario() {
    gridPane.getChildren().clear();
    String[][] escenario = gestorJuego.getEscenario().getEscenario();

    for (int i = 0; i < escenario.length; i++) {
        for (int j = 0; j < escenario[i].length; j++) {
            ImageView img = new ImageView();
            img.setFitWidth(40);
            img.setFitHeight(40);

            // Versión corregida (sin comillas y usando valores correctos)
            String ruta = escenario[i][j].equals("S") ? 
                gestorJuego.getEscenario().getSuelo() : 
                gestorJuego.getEscenario().getPared();

            try {
                InputStream is = getClass().getResourceAsStream(ruta);
                if (is != null) {
                    img.setImage(new Image(is));
                } else {
                    System.err.println("Imagen no encontrada: " + ruta);
                    // Opcional: Cargar imagen por defecto
                }
            } catch (Exception e) {
                System.err.println("Error cargando imagen: " + e.getMessage());
            }

            gridPane.add(img, j, i);
        }
    }
}




 private void pintarPersonajes() {
    gridPane2.getChildren().clear();
    
    // Pintar protagonista
    Protagonista prota = gestorJuego.getProtagonista();
    if (prota != null) {
        pintarPersonaje(prota);
    }
    
    // Pintar enemigos
    gestorJuego.getEnemigos().forEach(this::pintarPersonaje);
}

private void pintarPersonaje(Personaje personaje) {
    try {
        InputStream is = getClass().getResourceAsStream(personaje.getImagen());
        if (is == null) {
            System.err.println("Imagen no encontrada: " + personaje.getImagen());
            return;
        }
        
        ImageView img = new ImageView(new Image(is));
        img.setFitWidth(40);
        img.setFitHeight(40);
        gridPane2.add(img, personaje.getPosicion()[1], personaje.getPosicion()[0]);
    } catch (Exception e) {
        System.err.println("Error cargando imagen de " + personaje.getClass().getSimpleName() + ": " + e.getMessage());
    }
}

    private void checkFinJuego() {
        if (!gestorJuego.isJuegoActivo()) {
            endGamePanel.setVisible(true);
            lblResultado.setText(gestorJuego.isVictoria() ? "¡Has ganado!" : "¡Has perdido!");
        }
    }

    @Override
    public void onChange() {
        actualizarEscenario();
    }
}