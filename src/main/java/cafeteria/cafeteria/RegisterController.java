package cafeteria.cafeteria;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField nombreField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void handleRegisterAction(ActionEvent event) {
        String nombre = nombreField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // 1. Validar que los campos no estén vacíos
        if (nombre.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            mostrarAlerta("Error de Validación", "Por favor, completa todos los campos.");
            return;
        }

        // 2. Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            mostrarAlerta("Error de Validación", "Las contraseñas no coinciden. Inténtalo de nuevo.");
            return;
        }

        // --- Lógica de registro ---
        // Aquí es donde guardarías el usuario en una base de datos.
        // Por ahora, simularemos un registro exitoso.
        System.out.println("Usuario registrado: " + nombre);

        // 3. Mostrar alerta de éxito
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro Exitoso");
        alert.setHeaderText(null);
        alert.setContentText("¡Cuenta creada exitosamente para '" + nombre + "'! \nAhora puedes iniciar sesión.");
        alert.showAndWait();

        // 4. Regresar al login automáticamente
        handleVolverLogin(event);
    }

    @FXML
    private void handleVolverLogin(ActionEvent event) {
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("login-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginView, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver al inicio de sesión.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}