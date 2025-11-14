package cafeteria.cafeteria;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button; // Importado
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class CarritoController {

    @FXML private ListView<PedidoItem> listaCarrito;
    @FXML private Label subtotalLabel;

    // --- NUEVOS BOTONES DE NAVEGACIÓN ---
    @FXML private Button navCarritoButton;
    @FXML private Button navHistorialButton;

    private String nombreUsuario;

    public void setNombreUsuario(String nombre) {
        this.nombreUsuario = nombre;
    }

    @FXML
    public void initialize() {
        // Cargar los items del carrito estático
        cargarItemsCarrito();

        // Deshabilitar el botón de carrito (ya estamos aquí)
        if (navCarritoButton != null) {
            navCarritoButton.setDisable(true);
        }
    }

    private void cargarItemsCarrito() {
        listaCarrito.getItems().setAll(Carrito.getItems());
        subtotalLabel.setText(String.format("MXN %.2f", Carrito.getSubtotal()));
    }

    @FXML
    private void handleRemoverItem(ActionEvent event) {
        PedidoItem selectedItem = listaCarrito.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            mostrarAlerta("Error", "No has seleccionado ningún item para remover.");
            return;
        }
        Carrito.removerItem(selectedItem);
        cargarItemsCarrito();
    }

    @FXML
    private void handleVolverMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent menuView = loader.load();
            MenuController menuController = loader.getController();
            menuController.setNombreUsuario(nombreUsuario);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(menuView, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo regresar al menú.");
        }
    }

    @FXML
    private void handleConfirmarPedido(ActionEvent event) {
        if (Carrito.getItems().isEmpty()) {
            mostrarAlerta("Carrito Vacío", "No puedes confirmar un pedido sin items.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfirmarPedido-view.fxml"));
            Parent confirmarView = loader.load();
            ConfirmarPedidoController controller = loader.getController();
            controller.setNombreUsuario(nombreUsuario);
            controller.setDatosPedido(Carrito.getSubtotal(), Carrito.getItems());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(confirmarView, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la confirmación del pedido.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // --- MÉTODOS DE NAVEGACIÓN GLOBAL ---

    @FXML
    private void handleIrACarrito(ActionEvent event) {
        // No hacer nada, ya estamos en el carrito
    }

    @FXML
    private void handleIrAHistorial(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("historial-view.fxml"));
            Parent view = loader.load();
            HistorialController controller = loader.getController();
            controller.setNombreUsuario(this.nombreUsuario);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(view, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}