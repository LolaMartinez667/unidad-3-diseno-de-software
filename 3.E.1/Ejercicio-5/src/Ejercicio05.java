import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public final class Ejercicio05 {
    record Personaje(String clase, int vida, int ataque, int defensa, String arma) {}

    abstract static class CreadorPersonaje {
        protected abstract Personaje crearPersonaje();
        final Personaje crear() { return crearPersonaje(); }
    }

    static final class CreadorGuerrero extends CreadorPersonaje {
        @Override protected Personaje crearPersonaje() { return new Personaje("Guerrero", 150, 18, 14, "Espada"); }
    }

    static final class CreadorMago extends CreadorPersonaje {
        @Override protected Personaje crearPersonaje() { return new Personaje("Mago", 90, 28, 5, "Bastón"); }
    }

    static final class CreadorArquero extends CreadorPersonaje {
        @Override protected Personaje crearPersonaje() { return new Personaje("Arquero", 110, 20, 8, "Arco"); }
    }

    static final class RegistroCreadores {
        private final Map<String, Supplier<CreadorPersonaje>> creadores = Map.of(
                "guerrero", CreadorGuerrero::new,
                "mago", CreadorMago::new,
                "arquero", CreadorArquero::new);
        Personaje crear(String tipo) {
            Supplier<CreadorPersonaje> proveedor = creadores.get(tipo == null ? "" : tipo.toLowerCase(Locale.ROOT));
            if (proveedor == null) throw new IllegalArgumentException("Clase de personaje no soportada: " + tipo);
            return proveedor.get().crear();
        }
    }

    public static void main(String[] args) {
        System.out.println(new RegistroCreadores().crear("mago"));
    }
}
