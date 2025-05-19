package com.mazmorras.controllers;

import com.mazmorras.interfaces.Observer;
import com.mazmorras.model.GestorJuego;
import com.mazmorras.model.Protagonista;
import com.mazmorras.model.Proveedor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class protaController implements Observer { 
    @FXML
    AnchorPane anchorPaneProta;
    @FXML
    GridPane gridPaneProta;
    @FXML
    ImageView imageProta;

    @FXML
    Label nombre;
    @FXML
    Label salud;
    @FXML
    Label danio;
    @FXML
    Label defensa;
    @FXML
    Label velocidad;
    @FXML
    Label critico;
    @FXML
    Label posicion;

    @FXML
    public void initialize() {
        GestorJuego gestor = Proveedor.getInstance().getGestorJuego();
        gestor.subscribe(this);

        actualizarDatosProtagonista();
    }

    private void actualizarDatosProtagonista() {
        Protagonista prota = Proveedor.getInstance().getGestorJuego().getProtagonista();

        if (prota != null) {
            imageProta.setImage(new Image(getClass().getResourceAsStream(prota.getImagen())));
            imageProta.setFitHeight(128);
            imageProta.setFitWidth(128);

            nombre.setText(prota.getNombre());
            salud.setText(prota.getSalud() + "/" + prota.getGetSaludMaxima());
            danio.setText(String.valueOf(prota.getAtaque()));
            defensa.setText(String.valueOf(prota.getDefensa()));
            velocidad.setText(String.valueOf(prota.getVelocidad()));
            critico.setText(prota.getPorcentajeCritico() + "%");
            posicion.setText("Fila: " + prota.getPosicion()[0] + ", Col: " + prota.getPosicion()[1]);
        }
    }

    @Override
    public void onChange() {
        actualizarDatosProtagonista();
    }
}