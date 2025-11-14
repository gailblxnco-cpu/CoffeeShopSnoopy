package cafeteria.cafeteria;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class HistorialController {

    @FXML private ListView<PedidoCompleto> listaHistorial;
    @FXML private Button navCarritoButton;
    @FXML private Button navHistorialButton;

    private String nombreUsuario;

    public void setNombreUsuario(String nombre) {
        this.nombreUsuario = nombre;
    }

    @FXML
    public void initialize() {
        // Cargar los items del historial estático
        listaHistorial.getItems().setAll(HistorialPedidos.getPedidos());

        // Deshabilitar el botón de historial (ya estamos aquí)
        if (navHistorialButton != null) {
            navHistorialButton.setDisable(true);
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
            menuController.setNombreUsuario(nombreUsuario);

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
            controller.setNombreUsuario(this.nombreUsuario);

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
}