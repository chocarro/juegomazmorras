package com.mazmorras.controllers;

/**
 * Controlador principal para la pantalla de inicio del juego Mazmorras.
 * Gestiona la creación del personaje protagonista y la transición a la pantalla de juego.
 */

import com.mazmorras.SceneID;
import com.mazmorras.SceneManager;
import com.mazmorras.model.Protagonista;
import com.mazmorras.model.Proveedor;
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
  private Label mensajeError;

  /**
   * Método de inicialización llamado automáticamente por JavaFX.
   * Configura los valores por defecto para los atributos del personaje
   * y establece el evento para el botón de comenzar aventura.
   */
  @FXML
  public void initialize() {
    // Configurar valores por defecto
    campoSalud.setText("100");
    campoAtaque.setText("100");
    campoDefensa.setText("5");
    campoVelocidad.setText("5");

    comenzarAventuraBtn.setOnAction(event -> {
      if (validarCampos()) {
        crearPersonaje();
        cargarEscenaJuego();
      }
    });

  }

  /**
   * Valida los campos de entrada del formulario de creación de personaje.
   * 
   * @return true si todos los campos son válidos, false en caso contrario
   */
  private boolean validarCampos() {
    // Validación básica del nombre
    if (campoNombre.getText().trim().isEmpty()) {
      mostrarError("¡Debes ingresar un nombre!");
      return false;
    }

    // Validación de valores numéricos
    try {
      int salud = Integer.parseInt(campoSalud.getText());
      int ataque = Integer.parseInt(campoAtaque.getText());
      int defensa = Integer.parseInt(campoDefensa.getText());
      int velocidad = Integer.parseInt(campoVelocidad.getText());

      if (salud <= 0 || ataque <= 0 || defensa <= 0 || velocidad <= 0) {
        mostrarError("¡Los valores deben ser mayores a 0!");
        return false;
      }

    } catch (NumberFormatException e) {
      mostrarError("¡Valores numéricos inválidos!");
      return false;
    }

    mensajeError.setVisible(false);
    return true;
  }

  /**
   * Muestra un mensaje de error en la interfaz de usuario.
   * 
   * @param mensaje Texto del mensaje de error a mostrar
   */
  private void mostrarError(String mensaje) {
    mensajeError.setText(mensaje);
    mensajeError.setVisible(true);
  }

  /**
   * Crea un nuevo personaje protagonista con los valores ingresados en el
   * formulario
   * y lo guarda en el gestor del juego a través del Proveedor.
   */

  private void crearPersonaje() {
    String nombre = campoNombre.getText().trim();
    int salud = Integer.parseInt(campoSalud.getText());
    int ataque = Integer.parseInt(campoAtaque.getText());
    int defensa = Integer.parseInt(campoDefensa.getText());
    int velocidad = Integer.parseInt(campoVelocidad.getText());

    Protagonista prota = new Protagonista(
        nombre, // Nombre del protagonista
        "/mazmorras/images/personaje.png", // Ruta de imagen fija
        1, // ID
        salud, // Salud actual
        ataque, // Ataque
        defensa, // Defensa
        velocidad, // Velocidad
        salud, // Salud máxima (valor corregido para que sea igual a salud)
        10 // Porcentaje crítico (valor ejemplo)
    );

    // Guardar en el gestor del juego
    Proveedor.getInstance().getGestorJuego().setProtagonista(prota);
  }

  /**
   * Carga y muestra la escena principal del juego utilizando el SceneManager.
   */
  private void cargarEscenaJuego() {
    SceneManager sm = SceneManager.getInstance();
    sm.setScene(SceneID.JUEGO, "juego");
    sm.loadScene(SceneID.JUEGO);
  }

}