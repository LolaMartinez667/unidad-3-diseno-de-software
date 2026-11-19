import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class Ejercicio08 {
    interface Compresor { byte[] comprimir(byte[] contenido); }
    static final class CompresorZIP implements Compresor {
        @Override public byte[] comprimir(byte[] contenido) {
            try (ByteArrayOutputStream salida = new ByteArrayOutputStream();
                 ZipOutputStream zip = new ZipOutputStream(salida)) {
                zip.putNextEntry(new ZipEntry("contenido.txt"));
                zip.write(contenido);
                zip.closeEntry();
                zip.finish();
                return salida.toByteArray();
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo crear el archivo ZIP", e);
            }
        }
    }
    static final class CompresorTAR implements Compresor {
        @Override public byte[] comprimir(byte[] contenido) {
            try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
                byte[] encabezado = new byte[512];
                escribirTexto(encabezado, 0, 100, "contenido.txt");
                escribirOctal(encabezado, 100, 8, 0644);
                escribirOctal(encabezado, 108, 8, 0);
                escribirOctal(encabezado, 116, 8, 0);
                escribirOctal(encabezado, 124, 12, contenido.length);
                escribirOctal(encabezado, 136, 12, 0);
                for (int i = 148; i < 156; i++) encabezado[i] = (byte) ' ';
                encabezado[156] = '0';
                escribirTexto(encabezado, 257, 6, "ustar");
                encabezado[262] = 0;
                escribirTexto(encabezado, 263, 2, "00");
                int suma = 0;
                for (byte valor : encabezado) suma += valor & 0xff;
                escribirOctal(encabezado, 148, 8, suma);
                salida.write(encabezado);
                salida.write(contenido);
                int relleno = (512 - contenido.length % 512) % 512;
                salida.write(new byte[relleno]);
                salida.write(new byte[1024]);
                return salida.toByteArray();
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo crear el archivo TAR", e);
            }
        }
        private void escribirTexto(byte[] destino, int inicio, int longitud, String valor) {
            byte[] bytes = valor.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
            System.arraycopy(bytes, 0, destino, inicio, Math.min(bytes.length, longitud));
        }
        private void escribirOctal(byte[] destino, int inicio, int longitud, long valor) {
            String octal = String.format(java.util.Locale.ROOT, "%0" + (longitud - 1) + "o", valor);
            escribirTexto(destino, inicio, longitud - 1, octal);
            destino[inicio + longitud - 1] = 0;
        }
    }
    static final class CompresorGZ implements Compresor {
        @Override public byte[] comprimir(byte[] contenido) {
            try (ByteArrayOutputStream salida = new ByteArrayOutputStream();
                 GZIPOutputStream gzip = new GZIPOutputStream(salida)) {
                gzip.write(contenido);
                gzip.finish();
                return salida.toByteArray();
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo crear el archivo GZ", e);
            }
        }
    }

    abstract static class CreadorCompresor {
        protected abstract Compresor crearCompresor();
        final byte[] procesar(byte[] contenido) { return crearCompresor().comprimir(contenido.clone()); }
    }
    static final class CreadorZIP extends CreadorCompresor {
        @Override protected Compresor crearCompresor() { return new CompresorZIP(); }
    }
    static final class CreadorTAR extends CreadorCompresor {
        @Override protected Compresor crearCompresor() { return new CompresorTAR(); }
    }
    static final class CreadorGZ extends CreadorCompresor {
        @Override protected Compresor crearCompresor() { return new CompresorGZ(); }
    }

    static final class RegistroCreadores {
        private final Map<String, CreadorCompresor> creadores = Map.of(
                "zip", new CreadorZIP(), "tar", new CreadorTAR(), "gz", new CreadorGZ());
        byte[] procesar(String formato, byte[] contenido) {
            CreadorCompresor creador = creadores.get(Objects.requireNonNull(formato).toLowerCase(Locale.ROOT));
            if (creador == null) throw new IllegalArgumentException("Formato no soportado: " + formato);
            Objects.requireNonNull(contenido, "El contenido es obligatorio");
            return creador.procesar(contenido);
        }
    }

    public static void main(String[] args) {
        byte[] contenido = "datos de ejemplo".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] comprimido = new RegistroCreadores().procesar("gz", contenido);
        System.out.println("GZ: " + contenido.length + " -> " + comprimido.length + " bytes");
    }
}
