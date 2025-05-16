package com.mazmorras.controllers;

import com.mazmorras.model.*;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class juegoController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    GridPane gridPane;

    @FXML
    GridPane gridPane2;


    private GestorJuego gestorJuego;

    @FXML
    public void initialize() {
        gestorJuego = Proveedor.getInstance().getGestorJuego();
        
        // Configurar escenario
        cargarEscenario();
      
    }

    private void cargarEscenario() {
        gridPane.getChildren().clear();
        String[][] escenario = gestorJuego.getEscenario().getEscenario();
        
        for (int i = 0; i < escenario.length; i++) {
            for (int j = 0; j < escenario[i].length; j++) {
                ImageView imagenCelda = new ImageView();
                imagenCelda.setFitWidth(40);
                imagenCelda.setFitHeight(40);
                
                switch (escenario[i][j]) {
                    case "S": // Suelo
                        imagenCelda.setImage(new Image(getClass().getResourceAsStream("/images/suelo.png")));
                        break;
                         default: // Pared
                        imagenCelda.setImage(new Image(getClass().getResourceAsStream("/images/pared.png")));
                        break;
                
                }
                
                gridPane.add(imagenCelda, j, i);
            }
        }
    }

    

}