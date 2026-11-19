import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Ejercicio02 {
    interface Almacenamiento {
        void guardar(String nombre, String contenido);
        String recuperar(String nombre);
    }

    static final class AlmacenLocal implements Almacenamiento {
        private final Map<String, String> archivos = new ConcurrentHashMap<>();
        @Override public void guardar(String nombre, String contenido) { archivos.put(nombre, contenido); }
        @Override public String recuperar(String nombre) { return archivos.get(nombre); }
    }
    static final class AlmacenNubeSimulado implements Almacenamiento {
        private final String bucket;
        private final Map<String, String> archivos = new ConcurrentHashMap<>();
        AlmacenNubeSimulado(String bucket) {
            if (bucket == null || bucket.isBlank()) throw new IllegalArgumentException("El bucket es obligatorio");
            this.bucket = bucket;
        }
        @Override public void guardar(String nombre, String contenido) { archivos.put(bucket + "/" + nombre, contenido); }
        @Override public String recuperar(String nombre) { return archivos.get(bucket + "/" + nombre); }
    }

    abstract static class CreadorAlmacenamiento {
        protected abstract Almacenamiento crearAlmacenamiento();
        final Almacenamiento crear() { return crearAlmacenamiento(); }
    }
    static final class CreadorLocal extends CreadorAlmacenamiento {
        @Override protected Almacenamiento crearAlmacenamiento() { return new AlmacenLocal(); }
    }
    static final class CreadorNube extends CreadorAlmacenamiento {
        private final String bucket;
        CreadorNube(String bucket) { this.bucket = bucket; }
        @Override protected Almacenamiento crearAlmacenamiento() { return new AlmacenNubeSimulado(bucket); }
    }

    public static void main(String[] args) {
        Almacenamiento almacenamiento = new CreadorLocal().crear();
        almacenamiento.guardar("foto.jpg", "contenido de ejemplo");
        System.out.println(almacenamiento.recuperar("foto.jpg"));
    }
}
