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

public class LoginController {

    @FXML
    private TextField usernameField; // Asegúrate que tu FXML use fx:id="usernameField"

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleIngresarAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", "Por favor, completa todos los campos.");
            return;
        }

        // --- LÓGICA DE BASE DE DATOS ---
        String sqlLogin = "SELECT usuario_id, nombre FROM Usuarios WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psLogin = conn.prepareStatement(sqlLogin)) {

            psLogin.setString(1, username);
            psLogin.setString(2, password); // Recuerda que esto no es seguro (debería ser hasheado)

            ResultSet rs = psLogin.executeQuery();

            if (rs.next()) {
                // ¡Usuario encontrado!
                int usuarioId = rs.getInt("usuario_id");
                String nombreUsuario = rs.getString("nombre");

                // Cargar el menú y pasarle los datos del usuario
                FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
                Parent menuView = loader.load();

                MenuController menuController = loader.getController();
                menuController.setUsuario(usuarioId, nombreUsuario); // <-- Pasamos ID y Nombre

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(menuView, 400, 600));
                stage.show();

            } else {
                // Usuario no encontrado
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Nombre de usuario o contraseña incorrectos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar a la base de datos.");
        }
    }

    @FXML
    private void handleRegistroLink(ActionEvent event) {
        try {
            Parent registerView = FXMLLoader.load(getClass().getResource("register-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(registerView, 400, 600));
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