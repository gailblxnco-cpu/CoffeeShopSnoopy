package cafeteria.cafeteria;

import javafx.event.ActionEvent; // <-- IMPORTANTE
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException; // <-- IMPORTANTE

public class LoginController {

    @FXML
    private TextField nombreField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleIngresarAction() {
        System.out.println("✅ Botón Ingresar presionado");

        String nombre = nombreField.getText().trim();
        String password = passwordField.getText();

        // Validación básica
        if (nombre.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Por favor, completa todos los campos.");
            return;
        }

        try {
            // Cargar la vista del menú
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent menuView = loader.load();

            // Obtener el controlador del menú
            MenuController menuController = loader.getController();

            // Pasarle el nombre de usuario
            menuController.setNombreUsuario(nombre);

            Stage stage = (Stage) nombreField.getScene().getWindow();
            stage.setScene(new Scene(menuView, 400, 600));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el menú. Error: " + e.getMessage());
        }
    }

    // --- INICIO DE LA CORRECCIÓN ---
    /**
     * Maneja el clic en el enlace "Regístrate aquí",
     * y navega a la vista de registro.
     */
    @FXML
    private void handleRegistroLink(ActionEvent event) {
        try {
            Parent registerView = FXMLLoader.load(getClass().getResource("register-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(registerView, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de registro.");
        }
    }
    // --- FIN DE LA CORRECCIÓN ---


    private void mostrarAlerta(String titulo, String mensaje) {
        // Cambiado a WARNING para que no sea un error rojo
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}