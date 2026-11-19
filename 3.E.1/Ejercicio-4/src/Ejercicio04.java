import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class Ejercicio04 {
    record Fila(String concepto, BigDecimal importe) {}
    record Reporte(String encabezado, List<Fila> filas) {
        Reporte {
            Objects.requireNonNull(encabezado, "El encabezado es obligatorio");
            filas = List.copyOf(filas);
        }
    }

    interface Exportador {
        String exportar(Reporte reporte);
    }

    static final class ExportadorPDF implements Exportador {
        @Override public String exportar(Reporte r) { return "PDF: " + r.encabezado() + " (" + r.filas().size() + " filas)"; }
    }

    static final class ExportadorCSV implements Exportador {
        @Override public String exportar(Reporte r) {
            return "CSV: " + r.encabezado() + System.lineSeparator()
                    + r.filas().stream().map(f -> f.concepto() + "," + f.importe()).reduce((a, b) -> a + System.lineSeparator() + b).orElse("");
        }
    }

    abstract static class CreadorReporte {
        protected abstract Exportador crearExportador();
        final String generar(Reporte reporte) {
            validar(reporte);
            return crearExportador().exportar(reporte);
        }
        private void validar(Reporte reporte) {
            Objects.requireNonNull(reporte, "El reporte es obligatorio");
            if (reporte.filas().isEmpty() || reporte.filas().stream().anyMatch(f -> f.importe() == null || f.importe().signum() < 0))
                throw new IllegalArgumentException("El reporte debe tener filas con importes no negativos");
        }
    }

    static final class CreadorPDF extends CreadorReporte {
        @Override protected Exportador crearExportador() { return new ExportadorPDF(); }
    }

    static final class CreadorCSV extends CreadorReporte {
        @Override protected Exportador crearExportador() { return new ExportadorCSV(); }
    }

    public static void main(String[] args) {
        Reporte reporte = new Reporte("Ventas de noviembre",
                List.of(new Fila("Venta", new BigDecimal("125.50"))));
        System.out.println(new CreadorCSV().generar(reporte));
    }
}
