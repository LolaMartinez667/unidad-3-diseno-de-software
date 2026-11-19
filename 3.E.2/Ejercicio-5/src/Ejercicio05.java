import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public final class Ejercicio05 {
    interface Enemigo {
        int salud();
        int dano();
        double velocidad();
        String tacticaInicial();
    }
    record Soldado(int salud, int dano, double velocidad) implements Enemigo {
        @Override public String tacticaInicial() { return "Patrullar en grupo"; }
    }
    record Drone(int salud, int dano, double velocidad) implements Enemigo {
        @Override public String tacticaInicial() { return "Sobrevolar y perseguir"; }
    }

    abstract static class CreadorEnemigo {
        protected abstract Enemigo crearEnemigo();
        final Enemigo crear() { return crearEnemigo(); }
    }
    static final class CreadorSoldado extends CreadorEnemigo {
        @Override protected Enemigo crearEnemigo() { return new Soldado(100, 15, 2.5); }
    }
    static final class CreadorDrone extends CreadorEnemigo {
        @Override protected Enemigo crearEnemigo() { return new Drone(60, 22, 5.0); }
    }

    static final class RegistroCreadores {
        private final Map<String, Supplier<CreadorEnemigo>> creadores = Map.of(
                "soldado", CreadorSoldado::new, "drone", CreadorDrone::new);
        Enemigo crear(String tipo) {
            Supplier<CreadorEnemigo> proveedor = creadores.get(tipo == null ? "" : tipo.toLowerCase(Locale.ROOT));
            if (proveedor == null) throw new IllegalArgumentException("Tipo de enemigo no soportado: " + tipo);
            return proveedor.get().crear();
        }
    }

    public static void main(String[] args) {
        Enemigo enemigo = new RegistroCreadores().crear("drone");
        System.out.println(enemigo.getClass().getSimpleName() + ": " + enemigo.tacticaInicial());
    }
}
