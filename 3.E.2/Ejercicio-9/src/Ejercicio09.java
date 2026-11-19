import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public final class Ejercicio09 {
    record Configuracion(String puerto, int direccion, String credencial) {}
    record Metrica(String protocolo, String estado, long lecturas) {}

    interface Sensor extends AutoCloseable {
        Metrica leer();
        @Override void close();
    }
    interface FabricaSensor { Sensor crear(Configuracion configuracion); }

    static final class SensorSimulado implements Sensor {
        private final String protocolo;
        private final AtomicReference<String> estado = new AtomicReference<>("INICIALIZADO");
        SensorSimulado(String protocolo) { this.protocolo = protocolo; }
        @Override public Metrica leer() {
            if ("CERRADO".equals(estado.get())) throw new IllegalStateException("El sensor está cerrado");
            estado.set("ACTIVO");
            return new Metrica(protocolo, estado.get(), 1);
        }
        @Override public void close() { estado.set("CERRADO"); }
    }
    static final class FabricaProtocolo implements FabricaSensor {
        private final String protocolo;
        FabricaProtocolo(String protocolo) { this.protocolo = protocolo; }
        @Override public Sensor crear(Configuracion c) {
            Objects.requireNonNull(c, "La configuración es obligatoria");
            if (c.puerto() == null || c.puerto().isBlank() || c.credencial() == null || c.credencial().isBlank()
                    || c.direccion() < 0 || c.direccion() > 65535)
                throw new IllegalArgumentException("Puerto, credencial o dirección de hardware inválidos");
            try { return new SensorSimulado(protocolo); }
            catch (RuntimeException e) { throw new IllegalStateException("Falló la inicialización de " + protocolo, e); }
        }
    }
    static final class Registro {
        private final Map<String, FabricaSensor> fabricas = new ConcurrentHashMap<>();
        void registrar(String protocolo, FabricaSensor fabrica) {
            if (protocolo == null || protocolo.isBlank()) throw new IllegalArgumentException("Protocolo obligatorio");
            if (fabricas.putIfAbsent(protocolo.toLowerCase(java.util.Locale.ROOT), Objects.requireNonNull(fabrica)) != null)
                throw new IllegalArgumentException("Protocolo ya registrado");
        }
        Sensor crear(String protocolo, Configuracion configuracion) {
            FabricaSensor fabrica = fabricas.get(Objects.requireNonNull(protocolo).toLowerCase(java.util.Locale.ROOT));
            if (fabrica == null) throw new IllegalArgumentException("Protocolo no registrado: " + protocolo);
            return fabrica.crear(configuracion);
        }
    }

    public static void main(String[] args) {
        Registro registro = new Registro();
        registro.registrar("modbus", new FabricaProtocolo("ModBus"));
        registro.registrar("i2c", new FabricaProtocolo("I2C"));
        registro.registrar("spi", new FabricaProtocolo("SPI"));
        try (Sensor sensor = registro.crear("i2c", new Configuracion("I2C-1", 42, "demo"))) {
            System.out.println(sensor.leer());
        }
    }
}
