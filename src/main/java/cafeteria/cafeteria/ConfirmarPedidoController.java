package cafeteria.cafeteria;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button; // <-- AÑADIR IMPORT
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

public class ConfirmarPedidoController {

    @FXML private Label infoPedidoLabel;
    @FXML private TextArea direccionTextArea;

    // --- INICIO DE LA CORRECCIÓN ---
    // Variables para la barra de navegación superior
    @FXML private Button navCarritoButton;
    @FXML private Button navHistorialButton;
    // --- FIN DE LA CORRECCIÓN ---

    private List<PedidoItem> itemsDelPedido;
    private double total;
    private String nombreUsuario;
    private int usuarioId;

    public void setUsuario(int usuarioId, String nombre) {
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombre;
    }

    public void setDatosPedido(double total, List<PedidoItem> items) {
        this.itemsDelPedido = items;
        this.total = total;
        actualizarVista();
    }

    private void actualizarVista() {
        if (infoPedidoLabel != null) {
            int numItems = (itemsDelPedido != null) ? itemsDelPedido.size() : 0;
            infoPedidoLabel.setText(String.format("Estás por ordenar %d productos por un total de MXN %.2f.", numItems, total));
        }
    }

    @FXML
    private void confirmarPedidoFinal(ActionEvent event) {
        String direccion = direccionTextArea.getText().trim();
        if (direccion.isEmpty() || direccion.length() < 10) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", "Por favor, ingresa una dirección detallada.");
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // ... (El resto de tu lógica de BD para insertar Venta y DetalleVenta es correcta) ...
            // Paso A: Insertar en 'Ventas'
            String sqlVenta = "INSERT INTO Ventas (total, fecha_hora, direccion_entrega, estado, usuario_id) VALUES (?, ?, ?, ?, ?)";
            int ventaIdGenerada;

            try (PreparedStatement psVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setDouble(1, this.total);
                psVenta.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                psVenta.setString(3, direccion);
                psVenta.setString(4, "Pendiente");
                psVenta.setInt(5, this.usuarioId);
                psVenta.executeUpdate();

                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (rs.next()) {
                        ventaIdGenerada = rs.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la venta.");
                    }
                }
            }

            // Paso B: Insertar en 'DetalleVenta'
            String sqlDetalle = "INSERT INTO DetalleVenta (venta_id, nombre_bebida, tamano, leche, estado_temp, cantidad, precio_total_item) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {
                for (PedidoItem item : this.itemsDelPedido) {
                    psDetalle.setInt(1, ventaIdGenerada);
                    psDetalle.setString(2, item.getNombreBebida());
                    psDetalle.setString(3, item.getTamano());
                    psDetalle.setString(4, item.getLeche());
                    psDetalle.setString(5, item.getEstado());
                    psDetalle.setInt(6, item.getCantidad());
                    psDetalle.setDouble(7, item.getPrecioTotalItem());
                    psDetalle.addBatch();
                }
                psDetalle.executeBatch();
            }

            conn.commit(); // Confirmar transacción
            System.out.println("Venta #" + ventaIdGenerada + " guardada en la BD.");

            Carrito.limpiarCarrito(); // Limpiar carrito estático
            mostrarAlerta(Alert.AlertType.INFORMATION, "¡Pedido Confirmado!", "Tu pedido ha sido confirmado.\nID de Venta: " + ventaIdGenerada);
            regresarAlMenuPrincipal(event);

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            mostrarError("Error al guardar el pedido en la base de datos.");
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    /**
     * Este botón vuelve al Carrito (como dice el texto del FXML)
     */
    @FXML
    private void volverAPersonalizar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("carrito-view.fxml"));
            Parent carritoView = loader.load();

            CarritoController controller = loader.getController();
            controller.setUsuario(usuarioId, nombreUsuario); // Devolvemos ID y Nombre

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(carritoView, 400, 600));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al regresar al carrito");
        }
    }

    private void regresarAlMenuPrincipal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent menuView = loader.load();

            MenuController menuController = loader.getController();
            menuController.setUsuario(usuarioId, nombreUsuario); // Devolvemos ID y Nombre

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(menuView, 400, 600));
            stage.setTitle("Menú - Gemini Café Snoopy");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al regresar al menú principal");
        }
    }

    // --- INICIO DE LA CORRECCIÓN ---
    // MÉTODOS DE NAVEGACIÓN SUPERIOR AÑADIDOS

    @FXML
    private void handleIrACarrito(ActionEvent event) {
        // Llama al mismo método que el botón "Volver al Carrito"
        volverAPersonalizar(event);
    }

    @FXML
    private void handleIrAHistorial(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("historial-view.fxml"));
            Parent historialView = loader.load();

            HistorialController controller = loader.getController();
            controller.setUsuario(usuarioId, nombreUsuario); // Pasamos los datos del usuario

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(historialView, 400, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar el historial.");
        }
    }
    // --- FIN DE LA CORRECCIÓN ---


    private void mostrarAlerta(Alert.AlertType type, String titulo, String mensaje) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private void mostrarError(String mensaje) { mostrarAlerta(Alert.AlertType.ERROR, "Error", mensaje); }

    @FXML
    private void initialize() {
        if (direccionTextArea != null) {
            direccionTextArea.setWrapText(true);
        }
    }
}