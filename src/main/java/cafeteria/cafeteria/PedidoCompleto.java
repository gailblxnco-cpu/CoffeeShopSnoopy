package cafeteria.cafeteria;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Clase que representa un pedido completo (una Venta)
 * para mostrar en el historial. Coincide con la tabla 'Ventas'.
 */
public class PedidoCompleto {

    private final int ventaId;
    private final double total;
    private final Timestamp fechaHora;
    private final String estado;

    public PedidoCompleto(int ventaId, double total, Timestamp fechaHora, String estado) {
        this.ventaId = ventaId;
        this.total = total;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    // Getters
    public int getVentaId() { return ventaId; }
    public double getTotal() { return total; }
    public Timestamp getFechaHora() { return fechaHora; }
    public String getEstado() { return estado; }

    /**
     * Esto es lo que se mostrará en la lista del historial.
     */
    @Override
    public String toString() {
        // Formatear la fecha para que sea legible
        String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm").format(fechaHora);

        return String.format("Pedido #%d - %s\nTotal: MXN %.2f (%s)",
                ventaId, fechaFormateada, total, estado);
    }
}