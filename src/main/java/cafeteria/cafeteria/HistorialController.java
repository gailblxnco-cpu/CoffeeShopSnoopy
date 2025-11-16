package cafeteria.cafeteria;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class HistorialController {

    @FXML private ListView<PedidoCompleto> listaHistorial;
    @FXML private Button navCarritoButton;
    @FXML private Button navHistorialButton;

    private String nombreUsuario;
    private int usuarioId;

    // Lista observable para la ListView
    private final ObservableList<PedidoCompleto> historialPedidos = FXCollections.observableArrayList();

    /**
     * Recibe el ID y el Nombre del usuario desde la vista anterior.
     */
    public void setUsuario(int usuarioId, String nombre) {
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombre;

        // --- ¡ACCIÓN CLAVE! ---
        // Ahora que tenemos el ID, cargamos el historial
        cargarHistorialDesdeDB(this.usuarioId);
    }

    @FXML
    public void initialize() {
        // Asignar la lista al ListView
        listaHistorial.setItems(historialPedidos);

        // Deshabilitar el botón de historial (ya estamos aquí)
        if (navHistorialButton != null) {
            navHistorialButton.setDisable(true);
        }
    }

    /**
     * Consulta la base de datos y llena la lista del historial.
     */
    private void cargarHistorialDesdeDB(int idUsuario) {
        // Limpiar la lista anterior
        historialPedidos.clear();

        String sql = "SELECT venta_id, total, fecha_hora, estado FROM Ventas WHERE usuario_id = ? ORDER BY fecha_hora DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int ventaId = rs.getInt("venta_id");
                double total = rs.getDouble("total");
                Timestamp fecha = rs.getTimestamp("fecha_hora");
                String estado = rs.getString("estado");

                // Crear el objeto y añadirlo a la lista observable
                historialPedidos.add(new PedidoCompleto(ventaId, total, fecha, estado));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de BD", "No se pudo cargar el historial de pedidos.");
        }
    }

    /**
     * Maneja el botón "Volver al Menú"
     */
    @FXML
    private void handleVolverMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent menuView = loader.load();

            MenuController menuController = loader.getController();
            menuController.setUsuario(this.usuarioId, this.nombreUsuario);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(menuView, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- MÉTODOS DE NAVEGACIÓN GLOBAL ---

    @FXML
    private void handleIrACarrito(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("carrito-view.fxml"));
            Parent view = loader.load();

            CarritoController controller = loader.getController();
            controller.setUsuario(this.usuarioId, this.nombreUsuario);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(view, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleIrAHistorial(ActionEvent event) {
        // No hacer nada, ya estamos en historial
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}