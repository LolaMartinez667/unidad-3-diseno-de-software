public final class Ejercicio02 {
    interface Conexion {
        String probarConsulta();
    }

    static final class ConexionSQLite implements Conexion {
        private final String archivo;
        ConexionSQLite(String archivo) { this.archivo = archivo; }
        @Override public String probarConsulta() { return "SQLite listo: " + archivo; }
    }

    static final class ConexionPostgreSQL implements Conexion {
        private final String servidor;
        ConexionPostgreSQL(String servidor) { this.servidor = servidor; }
        @Override public String probarConsulta() { return "PostgreSQL listo: " + servidor; }
    }

    abstract static class CreadorConexion {
        protected abstract Conexion crearConexion();
        String ejecutarPrueba() { return crearConexion().probarConsulta(); }
    }

    static final class CreadorSQLite extends CreadorConexion {
        private final String archivo;
        CreadorSQLite(String archivo) { this.archivo = archivo; }
        @Override protected Conexion crearConexion() { return new ConexionSQLite(archivo); }
    }

    static final class CreadorPostgreSQL extends CreadorConexion {
        private final String servidor;
        CreadorPostgreSQL(String servidor) { this.servidor = servidor; }
        @Override protected Conexion crearConexion() { return new ConexionPostgreSQL(servidor); }
    }

    public static void main(String[] args) {
        CreadorConexion creador = new CreadorSQLite("app.db");
        System.out.println(creador.ejecutarPrueba());
    }
}
