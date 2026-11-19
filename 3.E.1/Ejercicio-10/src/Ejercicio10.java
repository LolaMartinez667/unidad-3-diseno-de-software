import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class Ejercicio10 {
    record SolicitudFiscal(String pais, String contribuyente, String identificadorFiscal, String detalle) {}
    record ComprobanteFiscal(String pais, String xml, byte[] firma) {
        ComprobanteFiscal { firma = firma.clone(); }
        @Override public byte[] firma() { return firma.clone(); }
    }

    interface EmisorFiscal { ComprobanteFiscal emitir(SolicitudFiscal solicitud); }
    static final class EmisorPais implements EmisorFiscal {
        private final String pais;
        private final KeyPair claves;
        EmisorPais(String pais, KeyPair claves) { this.pais = pais; this.claves = claves; }
        @Override public ComprobanteFiscal emitir(SolicitudFiscal s) {
            validar(s);
            String xml = "<comprobante pais=\"" + pais + "\" id=\"" + escapar(s.identificadorFiscal())
                    + "\"><contribuyente>" + escapar(s.contribuyente()) + "</contribuyente><detalle>"
                    + escapar(s.detalle()) + "</detalle></comprobante>";
            try {
                Signature firmador = Signature.getInstance("SHA256withRSA");
                firmador.initSign(claves.getPrivate());
                firmador.update(xml.getBytes(StandardCharsets.UTF_8));
                return new ComprobanteFiscal(pais, xml, firmador.sign());
            } catch (GeneralSecurityException e) {
                throw new IllegalStateException("No se pudo firmar el comprobante", e);
            }
        }
        private void validar(SolicitudFiscal s) {
            Objects.requireNonNull(s, "La solicitud es obligatoria");
            if (!pais.equalsIgnoreCase(s.pais()) || vacio(s.contribuyente()) || vacio(s.identificadorFiscal()) || vacio(s.detalle()))
                throw new IllegalArgumentException("Faltan datos fiscales obligatorios o la jurisdicción no coincide");
        }
        private boolean vacio(String valor) { return valor == null || valor.isBlank(); }
        private String escapar(String valor) {
            return valor.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                    .replace("\"", "&quot;").replace("'", "&apos;");
        }
    }

    static final class FabricaFiscal {
        private final Map<String, EmisorFiscal> emisores;
        FabricaFiscal(Map<String, EmisorFiscal> emisores) {
            this.emisores = emisores.entrySet().stream().collect(java.util.stream.Collectors.toUnmodifiableMap(
                    e -> e.getKey().toUpperCase(Locale.ROOT), Map.Entry::getValue));
        }
        ComprobanteFiscal crear(SolicitudFiscal s) {
            Objects.requireNonNull(s, "La solicitud es obligatoria");
            if (s.pais() == null || s.pais().isBlank()) throw new IllegalArgumentException("La jurisdicción es obligatoria");
            EmisorFiscal emisor = emisores.get(s.pais().toUpperCase(Locale.ROOT));
            if (emisor == null) throw new IllegalArgumentException("Jurisdicción no configurada");
            return emisor.emitir(s);
        }
    }

    public static void main(String[] args) throws GeneralSecurityException {
        KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
        generador.initialize(2048);
        KeyPair clavesDemo = generador.generateKeyPair();
        FabricaFiscal fabrica = new FabricaFiscal(Map.of("AR", new EmisorPais("AR", clavesDemo)));
        ComprobanteFiscal comprobante = fabrica.crear(new SolicitudFiscal("AR", "Comercio Demo", "30-12345678-9", "Servicio"));
        System.out.println(comprobante.pais() + ": firma generada (" + comprobante.firma().length + " bytes)");
    }
}
