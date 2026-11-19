import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class Ejercicio10 {
    record SolicitudTitulo(String jurisdiccion, String propietario, String documentoIdentidad,
                           String identificadorInmueble, String registro) {}
    record Titulo(String jurisdiccion, String documento, byte[] firma) {
        Titulo { firma = firma.clone(); }
        @Override public byte[] firma() { return firma.clone(); }
    }

    interface EmisorTitulo { Titulo emitir(SolicitudTitulo solicitud); }
    static final class EmisorJurisdiccion implements EmisorTitulo {
        private final String jurisdiccion;
        private final KeyPair claves;
        EmisorJurisdiccion(String jurisdiccion, KeyPair claves) {
            this.jurisdiccion = jurisdiccion;
            this.claves = claves;
        }
        @Override public Titulo emitir(SolicitudTitulo s) {
            validar(s);
            String documento = "<titulo jurisdiccion=\"" + jurisdiccion + "\" inmueble=\""
                    + escapar(s.identificadorInmueble()) + "\"><propietario>" + escapar(s.propietario())
                    + "</propietario><registro>" + escapar(s.registro()) + "</registro></titulo>";
            try {
                Signature firmador = Signature.getInstance("SHA256withRSA");
                firmador.initSign(claves.getPrivate());
                firmador.update(documento.getBytes(StandardCharsets.UTF_8));
                return new Titulo(jurisdiccion, documento, firmador.sign());
            } catch (GeneralSecurityException e) {
                throw new IllegalStateException("No se pudo firmar el título", e);
            }
        }
        private void validar(SolicitudTitulo s) {
            Objects.requireNonNull(s, "La solicitud es obligatoria");
            if (!jurisdiccion.equalsIgnoreCase(s.jurisdiccion()) || vacio(s.propietario())
                    || vacio(s.documentoIdentidad()) || vacio(s.identificadorInmueble()) || vacio(s.registro()))
                throw new IllegalArgumentException("Faltan datos legales o la jurisdicción no coincide");
        }
        private boolean vacio(String valor) { return valor == null || valor.isBlank(); }
        private String escapar(String valor) {
            return valor.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                    .replace("\"", "&quot;").replace("'", "&apos;");
        }
    }

    static final class FabricaTitulos {
        private final Map<String, EmisorTitulo> emisores;
        FabricaTitulos(Map<String, EmisorTitulo> emisores) {
            this.emisores = emisores.entrySet().stream().collect(java.util.stream.Collectors.toUnmodifiableMap(
                    e -> e.getKey().toUpperCase(Locale.ROOT), Map.Entry::getValue));
        }
        Titulo crear(SolicitudTitulo solicitud) {
            Objects.requireNonNull(solicitud, "La solicitud es obligatoria");
            if (solicitud.jurisdiccion() == null || solicitud.jurisdiccion().isBlank())
                throw new IllegalArgumentException("La jurisdicción es obligatoria");
            EmisorTitulo emisor = emisores.get(solicitud.jurisdiccion().toUpperCase(Locale.ROOT));
            if (emisor == null) throw new IllegalArgumentException("Jurisdicción no configurada");
            return emisor.emitir(solicitud);
        }
    }

    public static void main(String[] args) throws GeneralSecurityException {
        KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
        generador.initialize(2048);
        KeyPair clavesDemo = generador.generateKeyPair();
        FabricaTitulos fabrica = new FabricaTitulos(Map.of("AR", new EmisorJurisdiccion("AR", clavesDemo)));
        Titulo titulo = fabrica.crear(new SolicitudTitulo("AR", "Persona Demo", "DNI-123", "INM-456", "REG-789"));
        System.out.println(titulo.jurisdiccion() + ": firma generada (" + titulo.firma().length + " bytes)");
    }
}
