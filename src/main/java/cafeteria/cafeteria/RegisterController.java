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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField nombreField;
    @FXML private TextField usernameField; // Asegúrate que tu FXML tenga este campo
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML
    private void handleRegisterAction(ActionEvent event) {
        String nombre = nombreField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (nombre.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error de Validación", "Por favor, completa todos los campos.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error de Validación", "Las contraseñas no coinciden.");
            return;
        }

        String sqlCheckUser = "SELECT COUNT(*) FROM Usuarios WHERE username = ?";
        String sqlInsertUser = "INSERT INTO Usuarios (nombre, username, password) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1. Verificar si el usuario ya existe
            PreparedStatement psCheck = conn.prepareStatement(sqlCheckUser);
            psCheck.setString(1, username);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Registro", "El nombre de usuario '" + username + "' ya existe.");
                return;
            }

            // 2. Si no existe, insertarlo
            PreparedStatement psInsert = conn.prepareStatement(sqlInsertUser);
            psInsert.setString(1, nombre);
            psInsert.setString(2, username);
            psInsert.setString(3, password);

            int rowsAffected = psInsert.executeUpdate();

            if (rowsAffected > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro Exitoso", "¡Cuenta creada! Ya puedes iniciar sesión.");
                handleVolverLogin(event); // Regresar al login
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo registrar el usuario.");
        }
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
        }
    }

    private void mostrarAlerta(Alert.AlertType type, String titulo, String mensaje) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}