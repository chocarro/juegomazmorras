package com.mazmorras.controllers;

import com.mazmorras.SceneID;
import com.mazmorras.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class primeraController {
  @FXML
    AnchorPane anchorpane1;

    @FXML
    AnchorPane anchorpane2;

    @FXML
    public void initialize(){
        SceneManager sm = SceneManager.getInstance();

        Scene juego = sm.getScene(SceneID.JUEGO);
        AnchorPane.setBottomAnchor(juego.getRoot(), 0.0);
        AnchorPane.setTopAnchor(juego.getRoot(), 0.0);
        AnchorPane.setLeftAnchor(juego.getRoot(), 0.0);
        AnchorPane.setRightAnchor(juego.getRoot(), 0.0);
        anchorpane1.getChildren().setAll(juego.getRoot());

        Scene prota = sm.getScene(SceneID.PROTA);
        AnchorPane.setBottomAnchor(prota.getRoot(), 0.0);
        AnchorPane.setTopAnchor(prota.getRoot(), 0.0);
        AnchorPane.setLeftAnchor(prota.getRoot(), 0.0);
        AnchorPane.setRightAnchor(prota.getRoot(), 0.0);
        anchorpane2.getChildren().setAll(prota.getRoot());
    }
}
