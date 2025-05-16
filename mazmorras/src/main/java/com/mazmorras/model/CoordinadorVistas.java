package com.mazmorras.model;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CoordinadorVistas {
    private Stage primaryStage;
    
    public void mostrarMenuPrincipal() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/mazmorras/views/main.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}