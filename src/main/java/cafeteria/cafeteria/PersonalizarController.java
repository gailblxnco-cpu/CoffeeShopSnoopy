package cafeteria.cafeteria;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PersonalizarController implements Initializable {

    @FXML private Label tituloLabel;
    @FXML private Label cantidadLabel;
    @FXML private Label totalLabel;
    @FXML private Button btnConfirmar;
    @FXML private Button btnMenos;
    @FXML private Button btnMas;

    // --- CORRECCIÓN 1: Eliminadas las referencias a radioHidalgo y radioUniversidad ---
    // @FXML private RadioButton radioHidalgo, radioUniversidad;
    @FXML private RadioButton radioRegular, radioGrande;
    @FXML private RadioButton radioEntera, radioDeslactosada;
    @FXML private RadioButton radioCaliente, radioFrio;

    // --- CORRECCIÓN 2: Añadidas las variables para la barra de navegación ---
    @FXML private Button navCarritoButton;
    @FXML private Button navHistorialButton;

    // --- CORRECCIÓN 1: Eliminado el ToggleGroup de sucursal ---
    // private final ToggleGroup sucursalToggleGroup = new ToggleGroup();
    private final ToggleGroup tamanoToggleGroup = new ToggleGroup();
    private final ToggleGroup lecheToggleGroup = new ToggleGroup();
    private final ToggleGroup estadoToggleGroup = new ToggleGroup();

    private int cantidad = 1;
    private double precioBase;
    private double precioTotal;
    private String nombreBebida;
    private int usuarioId;
    private String nombreUsuario;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarToggleGroups();
        configurarEventos();
    }

    public void setUsuario(int usuarioId, String nombre) {
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombre;
    }

    public void setDatosBebida(String nombreBebida, double precioBase) {
        this.nombreBebida = nombreBebida;
        this.precioBase = precioBase;
        this.precioTotal = precioBase;
        actualizarVista();
    }

    private void configurarToggleGroups() {
        // --- CORRECCIÓN 1: Eliminada la línea de sucursal ---
        // radioHidalgo.setToggleGroup(sucursalToggleGroup); radioUniversidad.setToggleGroup(sucursalToggleGroup); radioHidalgo.setSelected(true);

        radioRegular.setToggleGroup(tamanoToggleGroup); radioGrande.setToggleGroup(tamanoToggleGroup); radioRegular.setSelected(true);
        radioEntera.setToggleGroup(lecheToggleGroup); radioDeslactosada.setToggleGroup(lecheToggleGroup); radioEntera.setSelected(true);
        radioCaliente.setToggleGroup(estadoToggleGroup); radioFrio.setToggleGroup(estadoToggleGroup); radioCaliente.setSelected(true);
    }

    private void configurarEventos() {
        tamanoToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> actualizarTotal());
        lecheToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> actualizarTotal());
    }

    private void actualizarVista() {
        if (tituloLabel != null) tituloLabel.setText("Personalizar: " + nombreBebida);
        if (cantidadLabel != null) cantidadLabel.setText(String.valueOf(cantidad));
        if (totalLabel != null) totalLabel.setText(String.format("MXN %.2f", precioTotal));
    }

    @FXML private void incrementarCantidad(ActionEvent event) {
        cantidad++; actualizarVista(); actualizarTotal();
    }

    @FXML private void decrementarCantidad(ActionEvent event) {
        if (cantidad > 1) { cantidad--; actualizarVista(); actualizarTotal(); }
    }

    private void actualizarTotal() {
        double total = precioBase;
        if (radioGrande.isSelected()) total += 15.0;
        if (radioDeslactosada.isSelected()) total += 5.0;
        precioTotal = total * cantidad;
        if (totalLabel != null) totalLabel.setText(String.format("MXN %.2f", precioTotal));
    }


    @FXML
    private void handleAnadirAlCarrito(ActionEvent event) {
        try {
            RadioButton tamanoSel = (RadioButton) tamanoToggleGroup.getSelectedToggle();
            RadioButton lecheSel = (RadioButton) lecheToggleGroup.getSelectedToggle();
            RadioButton estadoSel = (RadioButton) estadoToggleGroup.getSelectedToggle();

            if (tamanoSel == null || lecheSel == null || estadoSel == null) {
                mostrarError("Por favor, completa todas las opciones.");
                return;
            }

            PedidoItem nuevoItem = new PedidoItem(
                    nombreBebida,
                    tamanoSel.getText(),
                    lecheSel.getText(),
                    estadoSel.getText(),
                    cantidad,
                    precioTotal
            );

            Carrito.agregarItem(nuevoItem);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent menuView = loader.load();
            MenuController menuController = loader.getController();
            menuController.setUsuario(this.usuarioId, this.nombreUsuario);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(menuView, 400, 600));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al añadir al carrito: " + e.getMessage());
        }
    }

    // --- CORRECCIÓN 2: Métodos de navegación superior añadidos ---

    @FXML
    private void handleIrACarrito(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("carrito-view.fxml"));
            Parent view = loader.load();
            CarritoController controller = loader.getController();
            controller.setUsuario(this.usuarioId, this.nombreUsuario); // Pass user data

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(view, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar el carrito.");
        }
    }

    @FXML
    private void handleIrAHistorial(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("historial-view.fxml"));
            Parent view = loader.load();
            HistorialController controller = loader.getController();
            controller.setUsuario(this.usuarioId, this.nombreUsuario); // Pass user data

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(view, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar el historial.");
        }
    }
    // --- FIN DE LA CORRECCIÓN ---

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}