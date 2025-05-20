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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class juegoController implements Observer {
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


@FXML
public void initialize() {
    gestorJuego = Proveedor.getInstance().getGestorJuego();
    gestorJuego.subscribe(this);
    
    configurarControles();
    configurarFinJuegoUI();
    actualizarEscenario();
    
    Platform.runLater(() -> {
        anchorPane.requestFocus();
        System.out.println("Enfoque solicitado");
    });
}





private void configurarControles() {
    anchorPane.setFocusTraversable(true);
    anchorPane.requestFocus(); 
    
    anchorPane.setOnKeyPressed(event -> {
        System.out.println("Tecla presionada: " + event.getCode()); 
        
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
                    return;
            }
            
            // Forzar actualización visual
            Platform.runLater(() -> {
                actualizarEscenario();
                System.out.println("Escenario actualizado"); 
            });
        } else {
            System.err.println("Error: protagonista es null");
        }
    });

    // Click para recuperar foco si se pierde
    anchorPane.setOnMouseClicked(e -> {
        anchorPane.requestFocus();
        System.out.println("Foco recuperado manualmente"); 
    });
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
    
    gridPane2.getRowConstraints().clear();
    gridPane2.getColumnConstraints().clear();


    for (int i = 0; i < escenario.length; i++) {
    RowConstraints rc = new RowConstraints(40); // Mismo tamaño que gridPane
    rc.setValignment(VPos.CENTER);
    gridPane2.getRowConstraints().add(rc);
}

for (int j = 0; j < escenario[0].length; j++) {
    ColumnConstraints cc = new ColumnConstraints(40); // Mismo tamaño que gridPane
    cc.setHalignment(HPos.CENTER);
    gridPane2.getColumnConstraints().add(cc);
}

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
        ImageView imgProta = crearImagenPersonaje(prota);
        if (imgProta != null) {
            int[] pos = prota.getPosicion();
        
            gridPane2.add(imgProta, pos[1], pos[0]);
            System.out.println("Protagonista pintado en: " + pos[0] + "," + pos[1]);
        }
    }
    
    // Pintar enemigos
    gestorJuego.getEnemigos().forEach(enemigo -> {
        ImageView imgEnemigo = crearImagenPersonaje(enemigo);
        if (imgEnemigo != null) {
            int[] pos = enemigo.getPosicion();
            //GridPane.setRowIndex(imgEnemigo, pos[0]);
            //GridPane.setColumnIndex(imgEnemigo, pos[1]);
            //gridPane2.getChildren().add(imgEnemigo);
            gridPane2.add(imgEnemigo, pos[1], pos[0]);

        }
    });
}




private ImageView crearImagenPersonaje(Personaje personaje) {
    try {
        InputStream is = getClass().getResourceAsStream(personaje.getImagen());
        if (is == null) {
            System.err.println("Imagen no encontrada: " + personaje.getImagen());
            return null;
        }
        
        ImageView img = new ImageView(new Image(is));
        img.setFitWidth(40);
        img.setFitHeight(40);
        return img;
    } catch (Exception e) {
        System.err.println("Error cargando imagen: " + e.getMessage());
        return null;
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