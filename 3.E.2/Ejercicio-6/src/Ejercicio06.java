import java.math.BigDecimal;
import java.util.Map;

public final class Ejercicio06 {
    record Pedido(double pesoKg, double distanciaKm, String destino) {}
    record Cotizacion(BigDecimal costo, int diasEstimados) {}

    interface Transporte {
        Cotizacion cotizar(Pedido pedido);
        boolean admite(String destino);
    }
    static final class CorreoPostal implements Transporte {
        @Override public boolean admite(String destino) { return destino != null && !destino.isBlank(); }
        @Override public Cotizacion cotizar(Pedido p) { return cotizacion(p, "3.00", "0.05", 5); }
    }
    static final class ExpresoPrivado implements Transporte {
        @Override public boolean admite(String destino) { return destino != null && destino.matches("[A-Z]{2}-[0-9]{4,6}"); }
        @Override public Cotizacion cotizar(Pedido p) { return cotizacion(p, "12.00", "0.12", 1); }
    }
    static final class RetiroSucursal implements Transporte {
        @Override public boolean admite(String destino) { return destino != null && destino.startsWith("SUC-"); }
        @Override public Cotizacion cotizar(Pedido p) { return new Cotizacion(new BigDecimal("0.00"), 2); }
    }

    abstract static class CreadorTransporte {
        protected abstract Transporte crearTransporte();
        final Transporte crear(Pedido pedido) {
            validar(pedido);
            Transporte transporte = crearTransporte();
            if (!transporte.admite(pedido.destino())) throw new IllegalArgumentException("Destino no válido para el transporte");
            return transporte;
        }
        private void validar(Pedido p) {
            if (p == null || !Double.isFinite(p.pesoKg()) || p.pesoKg() <= 0
                    || !Double.isFinite(p.distanciaKm()) || p.distanciaKm() < 0)
                throw new IllegalArgumentException("Peso y distancia inválidos");
        }
    }
    static final class CreadorCorreo extends CreadorTransporte {
        @Override protected Transporte crearTransporte() { return new CorreoPostal(); }
    }
    static final class CreadorExpreso extends CreadorTransporte {
        @Override protected Transporte crearTransporte() { return new ExpresoPrivado(); }
    }
    static final class CreadorRetiro extends CreadorTransporte {
        @Override protected Transporte crearTransporte() { return new RetiroSucursal(); }
    }

    static Cotizacion cotizacion(Pedido p, String base, String tarifaPeso, int dias) {
        BigDecimal total = new BigDecimal(base)
                .add(BigDecimal.valueOf(p.pesoKg()).multiply(new BigDecimal(tarifaPeso)))
                .add(BigDecimal.valueOf(p.distanciaKm()).multiply(new BigDecimal("0.01")));
        return new Cotizacion(total, dias);
    }

    public static void main(String[] args) {
        Pedido pedido = new Pedido(2.0, 100.0, "AR-12345");
        Transporte transporte = new CreadorExpreso().crear(pedido);
        System.out.println(transporte.cotizar(pedido));
    }
}
