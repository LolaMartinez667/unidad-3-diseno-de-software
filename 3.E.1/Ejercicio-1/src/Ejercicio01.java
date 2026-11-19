public final class Ejercicio01 {
    interface Documento {
        String abrir();
    }

    static final class TextoPlano implements Documento {
        @Override public String abrir() { return "Documento de texto plano abierto"; }
    }

    static final class DocumentoEnriquecido implements Documento {
        @Override public String abrir() { return "Documento enriquecido abierto con formato"; }
    }

    abstract static class CreadorDocumento {
        protected abstract Documento crearDocumento();
        String abrirDocumento() { return crearDocumento().abrir(); }
    }

    static final class CreadorTextoPlano extends CreadorDocumento {
        @Override protected Documento crearDocumento() { return new TextoPlano(); }
    }

    static final class CreadorEnriquecido extends CreadorDocumento {
        @Override protected Documento crearDocumento() { return new DocumentoEnriquecido(); }
    }

    public static void main(String[] args) {
        CreadorDocumento creador = new CreadorEnriquecido();
        System.out.println(creador.abrirDocumento());
    }
}
