import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class Ejercicio04 {
    record Linea(String descripcion, BigDecimal cantidad, BigDecimal precioUnitario) {}
    record Factura(String numero, List<Linea> lineas, BigDecimal impuesto) {
        Factura {
            Objects.requireNonNull(numero);
            lineas = List.copyOf(lineas);
        }
        BigDecimal subtotal() {
            return lineas.stream().map(l -> l.cantidad().multiply(l.precioUnitario()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        BigDecimal total() { return subtotal().multiply(BigDecimal.ONE.add(impuesto)); }
    }

    interface GeneradorFactura { String generar(Factura factura); }
    static final class GeneradorPDF implements GeneradorFactura {
        @Override public String generar(Factura f) { return "Factura PDF " + f.numero() + " | subtotal " + f.subtotal() + " | total " + f.total(); }
    }
    static final class GeneradorTicket implements GeneradorFactura {
        @Override public String generar(Factura f) { return "*** TICKET " + f.numero() + " ***\nSubtotal: " + f.subtotal() + "\nTotal: " + f.total(); }
    }

    abstract static class CreadorFactura {
        protected abstract GeneradorFactura crearGenerador();
        final String emitir(Factura factura) {
            validar(factura);
            return crearGenerador().generar(factura);
        }
        private void validar(Factura f) {
            Objects.requireNonNull(f, "La factura es obligatoria");
            if (f.lineas().isEmpty() || f.impuesto() == null || f.impuesto().signum() < 0)
                throw new IllegalArgumentException("La factura requiere líneas e impuesto no negativo");
            for (Linea linea : f.lineas()) {
                if (linea == null || linea.descripcion() == null || linea.descripcion().isBlank()
                        || linea.cantidad() == null || linea.cantidad().signum() <= 0
                        || linea.precioUnitario() == null || linea.precioUnitario().signum() < 0)
                    throw new IllegalArgumentException("Línea de factura inválida");
            }
        }
    }
    static final class CreadorPDF extends CreadorFactura {
        @Override protected GeneradorFactura crearGenerador() { return new GeneradorPDF(); }
    }
    static final class CreadorTicket extends CreadorFactura {
        @Override protected GeneradorFactura crearGenerador() { return new GeneradorTicket(); }
    }

    public static void main(String[] args) {
        Factura factura = new Factura("F-100", List.of(new Linea("Libro", BigDecimal.ONE, new BigDecimal("10.00"))), new BigDecimal("0.21"));
        System.out.println(new CreadorTicket().emitir(factura));
    }
}
