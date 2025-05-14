package com.mazmorras;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * La clase <code>SceneManager</code> es responsable de gestionar las escenas de la aplicación JavaFX.
 * Permite cargar, almacenar, cambiar y eliminar escenas. Además, gestiona la asignación de hojas de estilo (CSS)
 * para cada escena y se asegura de que la escena actual se cargue en la ventana principal (Stage).
 */
public class SceneManager {
    // Instancia única (singleton) de SceneManager
    private static SceneManager instance;

    private Stage stage; // La ventana principal de la aplicación
    private URL styles; // Ruta a la hoja de estilo CSS que se aplica a las escenas
    private HashMap<SceneID, Scene> scenes; // Mapa para almacenar las escenas según su identificador

    /**
     * Constructor privado de <code>SceneManager</code>. 
     * Inicializa el mapa de escenas vacío.
     */
    private SceneManager() {
        scenes = new HashMap<>();
    }

    /**
     * Método estático para obtener la instancia única de <code>SceneManager</code> (patrón Singleton).
     * 
     * @return La instancia única de <code>SceneManager</code>.
     */
    public static SceneManager getInstance(){
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    /**
     * Inicializa el <code>SceneManager</code> con el <code>Stage</code> principal y la ruta de la hoja de estilo.
     * 
     * @param stage la ventana principal de la aplicación donde se mostrarán las escenas.
     * @param styles el nombre de la hoja de estilo CSS a aplicar a las escenas.
     */
    @SuppressWarnings("exports")
    public void init(Stage stage, String styles){
        this.stage = stage;
        this.styles = App.class.getResource("styles/" + styles + ".css"); // Ruta al archivo CSS
    }

    /**
     * Inicializa el <code>SceneManager</code> con el <code>Stage</code> principal y la ruta de la hoja de estilo.
     * 
     * @param stage la ventana principal de la aplicación donde se mostrarán las escenas.
     */
    @SuppressWarnings("exports")
    public void init(Stage stage){
        this.stage = stage;
    }

    /**
     * Establece una escena, cargando un archivo FXML y configurando su tamaño.
     * La escena también se asocia con la hoja de estilo definida previamente.
     * 
     * @param sceneID el identificador único de la escena.
     * @param fxml el nombre del archivo FXML que define la vista de la escena.
     * @param width el ancho de la escena.
     * @param height el alto de la escena.
     */
    public void setScene(SceneID sceneID, String fxml){
        // Obtener la pantalla principal
        Screen screen = Screen.getPrimary();

        // Obtener el tamaño de la pantalla
        double screenWidth = screen.getBounds().getWidth();
        double screenHeight = screen.getBounds().getHeight();
        try {
            // Carga el archivo FXML
            URL url = App.class.getResource("views/" + fxml + ".fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, screenWidth*0.7, screenHeight*0.7); // Crea la escena con el tamaño especificado
            if (styles!=null) scene.getStylesheets().add(styles.toExternalForm()); // Añade la hoja de estilo
            scenes.put(sceneID, scene); // Almacena la escena en el mapa con el identificador correspondiente
        } catch (IOException e) {
            e.printStackTrace(); // En caso de error al cargar el FXML
        }
    }

    /**
     * Elimina una escena previamente almacenada usando su identificador.
     * 
     * @param sceneID el identificador único de la escena que se desea eliminar.
     */
    public void removeScene(SceneID sceneID){
        scenes.remove(sceneID); // Elimina la escena del mapa
    }

    /**
     * Carga y muestra una escena previamente almacenada en el <code>SceneManager</code>.
     * 
     * @param sceneID el identificador único de la escena que se desea cargar.
     */
    public void loadScene(SceneID sceneID) {
        if (scenes.containsKey(sceneID)){
            stage.setScene(scenes.get(sceneID)); // Establece la escena en la ventana principal
            stage.show(); // Muestra la ventana con la nueva escena
        } else {
            System.err.println("La escena seleccionada no existe");
        }
    }

    @SuppressWarnings("exports")
    public Scene getScene(SceneID sceneID){
        if (scenes.containsKey(sceneID)){
            return scenes.get(sceneID);
        } else {
            System.err.println("La escena seleccionada no existe");
            return null;
        }
    }
}