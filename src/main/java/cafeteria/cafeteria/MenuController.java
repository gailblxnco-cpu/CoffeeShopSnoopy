package cafeteria.cafeteria;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MenuController {

    // --- Variables de FXML (DEBEN tener @FXML) ---
    @FXML private Label saludoLabel;
    @FXML private Button carritoButton;

    // --- NUEVAS VARIABLES AÑADIDAS ---
    @FXML private Button navCarritoButton;
    @FXML private Button navHistorialButton;
    // --- FIN DE NUEVAS VARIABLES ---

    // --- Variables de lógica (NO necesitan @FXML) ---
    private String nombreUsuarioActual;
    private int usuarioIdActual;
    private Map<String, Double> precios = new HashMap<>();

    @FXML
    public void initialize() {
        cargarPreciosDesdeDB();
        actualizarBotonCarrito();
    }

    private void cargarPreciosDesdeDB() {
        String sql = "SELECT nombre, precio_base FROM Productos";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            precios.clear();
            while (rs.next()) {
                precios.put(rs.getString("nombre"), rs.getDouble("precio_base"));
            }
            System.out.println("Precios cargados desde la BD.");

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarError("Error al cargar los productos desde la base de datos.");
        }
    }

    public void setUsuario(int usuarioId, String nombre) {
        this.usuarioIdActual = usuarioId;
        this.nombreUsuarioActual = nombre;
        if (saludoLabel != null) {
            saludoLabel.setText("Hola, " + nombre + "!");
        }
        actualizarBotonCarrito();
    }

    private void actualizarBotonCarrito() {
        if (carritoButton != null) {
            int cantidad = Carrito.getCantidadItems();
            carritoButton.setText(String.format("Ver Carrito (%d)", cantidad));
        }
    }

    // --- MÉTODOS DE EVENTOS (DEBEN tener @FXML) ---

    @FXML
    private void handleVerCarrito(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("carrito-view.fxml"));
            Parent carritoView = loader.load();
            CarritoController controller = loader.getController();
            controller.setUsuario(usuarioIdActual, nombreUsuarioActual);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(carritoView, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir el carrito.");
        }
    }

    // --- ¡¡NUEVO MÉTODO AÑADIDO!! ---
    /**
     * Maneja el clic en el botón de Historial (🧾)
     */
    @FXML
    private void handleVerAHistorial(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("historial-view.fxml")); // Asegúrate que tu FXML se llame así
            Parent historialView = loader.load();

            HistorialController controller = loader.getController();
            controller.setUsuario(usuarioIdActual, nombreUsuarioActual); // Pasamos los datos del usuario

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(historialView, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar el historial.");
        }
    }
    // --- FIN DE NUEVO MÉTODO ---


    private void seleccionarBebida(ActionEvent event, String nombreBebida) {
        if (!precios.containsKey(nombreBebida)) {
            mostrarError("El producto '" + nombreBebida + "' no está disponible.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("personalizacion-view.fxml"));
            Parent personalizarView = loader.load();
            PersonalizarController controller = loader.getController();
            controller.setDatosBebida(nombreBebida, precios.get(nombreBebida));
            controller.setUsuario(usuarioIdActual, nombreUsuarioActual);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(personalizarView, 400, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar la vista de personalización");
        }
    }

    // Todos estos SÍ estaban correctos y tienen @FXML
    @FXML private void seleccionarChocolateCaliente(ActionEvent event) { seleccionarBebida(event, "Chocolate caliente"); }
    @FXML private void seleccionarCapuccino(ActionEvent event) { seleccionarBebida(event, "Capuccino"); }
    @FXML private void seleccionarLatte(ActionEvent event) { seleccionarBebida(event, "Latte"); }
    @FXML private void seleccionarMacchiato(ActionEvent event) { seleccionarBebida(event, "Macchiato"); }
    @FXML private void seleccionarMocha(ActionEvent event) { seleccionarBebida(event, "Mocha"); }
    @FXML private void seleccionarAmericano(ActionEvent event) { seleccionarBebida(event, "Americano"); }
    @FXML private void seleccionarFlatWhite(ActionEvent event) { seleccionarBebida(event, "Flat White"); }

    @FXML
    private void handleRegresarAction(ActionEvent event) {
        Carrito.limpiarCarrito();
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("login-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginView, 400, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}