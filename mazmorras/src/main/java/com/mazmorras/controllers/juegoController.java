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

            String ruta = escenario[i][j].equals("S") ? "\"/mazmorras/images/suelo.png\"" : "/mazmorras/images/pared.png";
            InputStream is = getClass().getResourceAsStream(ruta);

            if (is != null) {
                img.setImage(new Image(is));
            } else {
                System.err.println("⚠️ Imagen no encontrada: " + ruta);
            }

            gridPane.add(img, j, i);
        }
    }
}


    private void pintarPersonajes() {
        gridPane2.getChildren().clear();
        gestorJuego.getProtagonista().forEach(p -> {
            ImageView img = new ImageView(new Image(getClass().getResourceAsStream(p.getImagen())));
            img.setFitWidth(40);
            img.setFitHeight(40);
            gridPane2.add(img, p.getPosicion()[1], p.getPosicion()[0]);
        });
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