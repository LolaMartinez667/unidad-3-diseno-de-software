import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class Ejercicio06 {
    record Comprobante(String medio, BigDecimal total, String referencia) {}

    interface PasarelaPago {
        Comprobante cobrar(BigDecimal monto);
        BigDecimal minimo();
    }

    static final class Tarjeta implements PasarelaPago {
        @Override public BigDecimal minimo() { return new BigDecimal("1.00"); }
        @Override public Comprobante cobrar(BigDecimal monto) { return new Comprobante("Tarjeta", monto.multiply(new BigDecimal("1.02")), "T-001"); }
    }

    static final class Transferencia implements PasarelaPago {
        @Override public BigDecimal minimo() { return new BigDecimal("5.00"); }
        @Override public Comprobante cobrar(BigDecimal monto) { return new Comprobante("Transferencia", monto, "B-001"); }
    }

    static final class Monedero implements PasarelaPago {
        @Override public BigDecimal minimo() { return new BigDecimal("0.50"); }
        @Override public Comprobante cobrar(BigDecimal monto) { return new Comprobante("Monedero", monto.multiply(new BigDecimal("1.01")), "W-001"); }
    }

    static final class FabricaPagos {
        private final Map<String, PasarelaPago> pasarelas = Map.of(
                "tarjeta", new Tarjeta(), "transferencia", new Transferencia(), "monedero", new Monedero());
        PasarelaPago crear(String medio, BigDecimal monto) {
            Objects.requireNonNull(monto, "El monto es obligatorio");
            if (monto.signum() <= 0) throw new IllegalArgumentException("El monto debe ser positivo");
            PasarelaPago pasarela = pasarelas.get(Objects.requireNonNull(medio).toLowerCase(Locale.ROOT));
            if (pasarela == null) throw new IllegalArgumentException("Medio de pago no soportado: " + medio);
            if (monto.compareTo(pasarela.minimo()) < 0) throw new IllegalArgumentException("El monto no alcanza el mínimo del medio seleccionado");
            return pasarela;
        }
    }

    public static void main(String[] args) {
        PasarelaPago pasarela = new FabricaPagos().crear("tarjeta", new BigDecimal("20.00"));
        System.out.println(pasarela.cobrar(new BigDecimal("20.00")));
    }
}
