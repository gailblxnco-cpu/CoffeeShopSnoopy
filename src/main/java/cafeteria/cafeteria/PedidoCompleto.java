package cafeteria.cafeteria;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Representa un pedido completo que ha sido confirmado y enviado.
 * Se usará en el historial.
 */
public class PedidoCompleto {

    private final List<PedidoItem> items;
    private final double total;
    private final String direccion;
    private final String timestamp;
    private String estado; // E.g., "En preparación", "En camino", "Entregado"

    public PedidoCompleto(List<PedidoItem> items, double total, String direccion) {
        this.items = items;
        this.total = total;
        this.direccion = direccion;
        // Formatear la fecha y hora actual
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.estado = "En preparación"; // Estado inicial
    }

    // Getters (si los necesitas)
    public List<PedidoItem> getItems() { return items; }
    public double getTotal() { return total; }
    public String getDireccion() { return direccion; }
    public String getTimestamp() { return timestamp; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    /**
     * Esto es lo que se mostrará en la lista del Historial.
     */
    @Override
    public String toString() {
        return String.format("[%s] - %s - $%.2f (%d items)",
                timestamp, estado, total, items.size());
    }
}