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

public class protaController implements Observer {  // Implementa Observer
    // Componentes básicos
    @FXML private AnchorPane anchorPaneProta;
    @FXML private GridPane gridPaneProta;
    @FXML private ImageView imageProta;
    
    // Labels de categorías
    @FXML private Label labelnombre;
    @FXML private Label labelsalud;
    @FXML private Label labeldanio;
    @FXML private Label labeldefensa;
    @FXML private Label labelvelocidad;
    @FXML private Label labelcritico;
    @FXML private Label labelposicion;
    
    // Labels de valores
    @FXML private Label nombre;
    @FXML private Label salud;
    @FXML private Label danio;
    @FXML private Label defensa;
    @FXML private Label velocidad;
    @FXML private Label critico;
    @FXML private Label posicion;

    @FXML
    public void initialize() {
        // Registra este controlador como observador
        GestorJuego gestor = Proveedor.getInstance().getGestorJuego();
        gestor.subscribe(this);
        
        // Carga los datos iniciales
        actualizarDatosProtagonista();
    }

    private void actualizarDatosProtagonista() {
        Protagonista prota = Proveedor.getInstance().getGestorJuego().getProtagonista();
        
        if (prota != null) {
            // Configurar imagen
            imageProta.setImage(new Image(getClass().getResourceAsStream(prota.getImagen())));
            imageProta.setFitHeight(128);
            imageProta.setFitWidth(128);
            
            // Actualizar stats
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
        // Se ejecuta cuando hay cambios en el modelo
        actualizarDatosProtagonista();
    }
}