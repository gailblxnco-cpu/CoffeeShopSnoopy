package cafeteria.cafeteria;

/**
 * Clase que representa un solo item personalizado dentro del carrito.
 */
public class PedidoItem {

    private final String nombreBebida;
    private final String tamano;
    private final String leche;
    private final String estado;
    private final int cantidad;
    private final double precioTotalItem;

    public PedidoItem(String nombreBebida, String tamano, String leche, String estado, int cantidad, double precioTotalItem) {
        this.nombreBebida = nombreBebida;
        this.tamano = tamano;
        this.leche = leche;
        this.estado = estado;
        this.cantidad = cantidad;
        this.precioTotalItem = precioTotalItem;
    }

    // --- INICIO DE LA CORRECCIÓN ---
    // Se añaden los getters que faltaban para que
    // ConfirmarPedidoController pueda leerlos y guardarlos en la BD.

    public String getTamano() {
        return tamano;
    }

    public String getLeche() {
        return leche;
    }

    public String getEstado() {
        return estado;
    }

    public int getCantidad() {
        return cantidad;
    }

    // --- FIN DE LA CORRECCIÓN ---


    // Getters que ya tenías
    public String getNombreBebida() { return nombreBebida; }
    public double getPrecioTotalItem() { return precioTotalItem; }

    /**
     * Devuelve un resumen para mostrar en el ticket final.
     */
    public String getResumen() {
        return String.format("%dx %s (%s, %s, %s) - $%.2f",
                cantidad, nombreBebida, tamano, leche, estado, precioTotalItem);
    }

    /**
     * Esto es lo que se mostrará en la lista del carrito.
     */
    @Override
    public String toString() {
        return String.format("%dx %s - $%.2f", cantidad, nombreBebida, precioTotalItem);
    }
}