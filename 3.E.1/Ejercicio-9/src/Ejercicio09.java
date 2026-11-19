import java.util.Map;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public final class Ejercicio09 {
    record Configuracion(String endpoint, String usuario, String credencial) {}
    record Estado(String protocolo, String estadoInicial) {}

    interface ConectorIoT extends AutoCloseable {
        Estado conectar();
        @Override void close();
    }
    interface FabricaConector {
        ConectorIoT crear(Configuracion configuracion);
    }

    static final class ConectorSimulado implements ConectorIoT {
        private final String protocolo;
        private final Configuracion configuracion;
        private final AtomicReference<String> estado = new AtomicReference<>("CREADO");
        ConectorSimulado(String protocolo, Configuracion configuracion) { this.protocolo = protocolo; this.configuracion = configuracion; }
        @Override public Estado conectar() {
            if (configuracion.endpoint().isBlank()) throw new IllegalStateException("Endpoint inválido");
            estado.set("CONECTADO_SIMULADO");
            return new Estado(protocolo, estado.get());
        }
        @Override public void close() { estado.set("CERRADO"); }
    }
    static final class FabricaProtocolo implements FabricaConector {
        private final String protocolo;
        FabricaProtocolo(String protocolo) { this.protocolo = protocolo; }
        @Override public ConectorIoT crear(Configuracion c) {
            validar(c);
            try { return new ConectorSimulado(protocolo, c); }
            catch (RuntimeException e) { throw new IllegalStateException("No se pudo inicializar " + protocolo, e); }
        }
        private void validar(Configuracion c) {
            Objects.requireNonNull(c, "La configuración es obligatoria");
            if (c.endpoint() == null || c.endpoint().isBlank() || c.usuario() == null || c.usuario().isBlank()
                    || c.credencial() == null || c.credencial().isBlank())
                throw new IllegalArgumentException("Endpoint, usuario y credencial son obligatorios");
        }
    }
    static final class Registro {
        private final Map<String, FabricaConector> fabricas = new ConcurrentHashMap<>();
        void registrar(String protocolo, FabricaConector fabrica) {
            if (protocolo == null || protocolo.isBlank()) throw new IllegalArgumentException("Protocolo obligatorio");
            if (fabricas.putIfAbsent(protocolo.toLowerCase(Locale.ROOT), Objects.requireNonNull(fabrica)) != null)
                throw new IllegalArgumentException("Ya existe una fábrica para " + protocolo);
        }
        ConectorIoT crear(String protocolo, Configuracion c) {
            FabricaConector fabrica = fabricas.get(Objects.requireNonNull(protocolo).toLowerCase(Locale.ROOT));
            if (fabrica == null) throw new IllegalArgumentException("Protocolo no registrado: " + protocolo);
            return fabrica.crear(c);
        }
    }

    public static void main(String[] args) {
        Registro registro = new Registro();
        registro.registrar("mqtt", new FabricaProtocolo("MQTT"));
        registro.registrar("http", new FabricaProtocolo("HTTP"));
        registro.registrar("coap", new FabricaProtocolo("CoAP"));
        try (ConectorIoT conector = registro.crear("mqtt", new Configuracion("mqtt://sensor", "operador", "demo"))) {
            System.out.println(conector.conectar());
        }
    }
}
