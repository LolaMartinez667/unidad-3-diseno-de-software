import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Ejercicio08 {
    interface Importador { List<String> importar(String contenido); }

    static final class ImportadorCSV implements Importador {
        @Override public List<String> importar(String contenido) {
            return contenido.lines().filter(linea -> !linea.isBlank()).map(linea -> linea.split(",", -1).length + " columnas: " + linea).toList();
        }
    }
    static final class ImportadorXML implements Importador {
        private static final Pattern ETIQUETA = Pattern.compile("<([A-Za-z][A-Za-z0-9]*)>([^<>]*)</\\1>");
        @Override public List<String> importar(String contenido) {
            Matcher matcher = ETIQUETA.matcher(contenido);
            java.util.ArrayList<String> registros = new java.util.ArrayList<>();
            while (matcher.find()) registros.add(matcher.group(1) + "=" + matcher.group(2));
            if (registros.isEmpty() && !contenido.isBlank()) throw new IllegalArgumentException("XML no compatible con el formato plano de ejemplo");
            return List.copyOf(registros);
        }
    }
    static final class ImportadorJSON implements Importador {
        private static final Pattern CAMPO = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\"[^\"]*\"|-?\\d+(?:\\.\\d+)?|true|false|null)");
        @Override public List<String> importar(String contenido) {
            Matcher matcher = CAMPO.matcher(contenido);
            java.util.ArrayList<String> registros = new java.util.ArrayList<>();
            while (matcher.find()) registros.add(matcher.group(1) + "=" + matcher.group(2).replaceAll("^\"|\"$", ""));
            if (registros.isEmpty() && !contenido.trim().equals("{}")) throw new IllegalArgumentException("JSON no compatible con el formato plano de ejemplo");
            return List.copyOf(registros);
        }
    }

    abstract static class CreadorImportador {
        protected abstract Importador crearImportador();
        final List<String> procesar(String contenido) {
            if (contenido == null) throw new IllegalArgumentException("El contenido es obligatorio");
            return crearImportador().importar(contenido);
        }
    }
    static final class CreadorCSV extends CreadorImportador {
        @Override protected Importador crearImportador() { return new ImportadorCSV(); }
    }
    static final class CreadorXML extends CreadorImportador {
        @Override protected Importador crearImportador() { return new ImportadorXML(); }
    }
    static final class CreadorJSON extends CreadorImportador {
        @Override protected Importador crearImportador() { return new ImportadorJSON(); }
    }

    static final class Seleccionador {
        private final Map<String, CreadorImportador> creadores = Map.of(
                "csv", new CreadorCSV(), "xml", new CreadorXML(), "json", new CreadorJSON());
        List<String> importar(String formato, String contenido) {
            CreadorImportador creador = creadores.get(formato == null ? "" : formato.toLowerCase(java.util.Locale.ROOT));
            if (creador == null) throw new IllegalArgumentException("Formato de importación no soportado");
            return creador.procesar(contenido);
        }
    }

    public static void main(String[] args) {
        System.out.println(new Seleccionador().importar("json", "{\"nombre\":\"Ana\",\"activo\":true}"));
    }
}
