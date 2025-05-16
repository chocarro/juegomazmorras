package com.mazmorras.controllers;

import com.mazmorras.SceneID;
import com.mazmorras.SceneManager;
import com.mazmorras.model.Protagonista;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class mainController {
  @FXML
  VBox vbox;

  @FXML
  Text text;

  @FXML
  GridPane gridPane;

  @FXML
  Label labelnombre;

  @FXML
  TextField campoNombre;

  @FXML
  Label labelsalud;

  @FXML
  TextField campoSalud;

  @FXML
  Label labelataque;

  @FXML
  TextField campoAtaque;

  @FXML
  Label labeldefensa;

  @FXML
  TextField campoDefensa;

  @FXML
  Label labelvelocidad;

  @FXML
  TextField campoVelocidad;

  @FXML
  Button comenzarAventuraBtn;

  @FXML
  public void initialize() {
    // Configurar valores por defecto
    campoSalud.setText("100");
    campoAtaque.setText("10");
    campoDefensa.setText("5");
    campoVelocidad.setText("5");

    comenzarAventuraBtn.setOnAction(event -> {
      crearPersonaje();
      SceneManager sm = SceneManager.getInstance();
   // Configura las escenas con identificadores
        sm.setScene(SceneID.JUEGO, "juego");        
        // Carga la escena principal
        sm.loadScene(SceneID.JUEGO);

    });
    
    
  }

  private void crearPersonaje() {
    try {
      String nombre = campoNombre.getText();
      int salud = Integer.parseInt(campoSalud.getText());
      int ataque = Integer.parseInt(campoAtaque.getText());
      int defensa = Integer.parseInt(campoDefensa.getText());
      int velocidad = Integer.parseInt(campoVelocidad.getText());

      Protagonista prota = new Protagonista( nombre,  "/mazmorras/images/personaje.png",  99,  salud,  ataque,  defensa,  velocidad,
             20,  100);

      // Aquí puedes guardar estos datos o crear un objeto "Personaje"
      System.out.println("Nombre: " + nombre);
      System.out.println("Salud: " + salud);
      System.out.println("Ataque: " + ataque);
      System.out.println("Defensa: " + defensa);
      System.out.println("Velocidad: " + velocidad);

      // Cambiar a la siguiente escena del juego
 

    } catch (NumberFormatException ex) {
      System.err.println("Error al convertir los valores numéricos. Verifica los campos.");
    }
  }



}