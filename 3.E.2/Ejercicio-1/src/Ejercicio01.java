import java.util.Set;

public final class Ejercicio01 {
    interface Perfil {
        String rol();
        Set<String> permisos();
    }

    record Estudiante(String nombre) implements Perfil {
        @Override public String rol() { return "Estudiante"; }
        @Override public Set<String> permisos() { return Set.of("consultar-cursos", "entregar-tareas"); }
    }
    record Docente(String nombre) implements Perfil {
        @Override public String rol() { return "Docente"; }
        @Override public Set<String> permisos() { return Set.of("consultar-cursos", "calificar", "publicar-material"); }
    }

    abstract static class CreadorPerfil {
        protected abstract Perfil crearPerfil(String nombre);
        final Perfil registrar(String nombre) {
            if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio");
            return crearPerfil(nombre);
        }
    }
    static final class CreadorEstudiante extends CreadorPerfil {
        @Override protected Perfil crearPerfil(String nombre) { return new Estudiante(nombre); }
    }
    static final class CreadorDocente extends CreadorPerfil {
        @Override protected Perfil crearPerfil(String nombre) { return new Docente(nombre); }
    }

    public static void main(String[] args) {
        Perfil perfil = new CreadorEstudiante().registrar("Ana");
        System.out.println(perfil.rol() + ": " + perfil.permisos());
    }
}
