import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Objects;

public final class Ejercicio03 {
    interface Alertador {
        void alertar(String mensaje);
    }
    static final class AlertaConsola implements Alertador {
        @Override public void alertar(String mensaje) { System.out.println("[ALERTA] " + mensaje); }
    }
    static final class AlertaArchivo implements Alertador {
        private final Writer destino;
        AlertaArchivo(Writer destino) { this.destino = Objects.requireNonNull(destino); }
        @Override public void alertar(String mensaje) {
            try { destino.write(mensaje + System.lineSeparator()); destino.flush(); }
            catch (IOException e) { throw new UncheckedIOException("No se pudo escribir la alerta", e); }
        }
    }

    abstract static class CreadorAlerta {
        protected abstract Alertador crearAlertador();
        final void enviar(String mensaje) {
            if (mensaje == null || mensaje.isBlank()) throw new IllegalArgumentException("El mensaje no puede estar vacío");
            crearAlertador().alertar(mensaje);
        }
    }
    static final class CreadorConsola extends CreadorAlerta {
        @Override protected Alertador crearAlertador() { return new AlertaConsola(); }
    }
    static final class CreadorArchivo extends CreadorAlerta {
        private final Writer destino;
        CreadorArchivo(Writer destino) { this.destino = destino; }
        @Override protected Alertador crearAlertador() { return new AlertaArchivo(destino); }
    }

    public static void main(String[] args) {
        StringWriter registro = new StringWriter();
        new CreadorArchivo(registro).enviar("Servidor disponible");
        System.out.print(registro);
    }
}
