public final class Ejercicio07 {
    record Resolucion(int ancho, int alto) {
        Resolucion {
            if (ancho <= 0 || alto <= 0) throw new IllegalArgumentException("Resolución inválida");
        }
    }
    interface Boton {
        String renderizar();
        String pulsar();
    }
    interface Etiqueta { String renderizar(); }

    static final class BotonEscritorio implements Boton {
        private final Resolucion resolucion;
        BotonEscritorio(Resolucion r) { resolucion = r; }
        @Override public String renderizar() { return "Botón escritorio " + resolucion.ancho() + "x" + resolucion.alto(); }
        @Override public String pulsar() { return "Click de escritorio"; }
    }
    static final class EtiquetaEscritorio implements Etiqueta {
        private final Resolucion resolucion;
        EtiquetaEscritorio(Resolucion r) { resolucion = r; }
        @Override public String renderizar() { return "Etiqueta escritorio " + resolucion.ancho() + "x" + resolucion.alto(); }
    }
    static final class BotonMovil implements Boton {
        private final Resolucion resolucion;
        BotonMovil(Resolucion r) { resolucion = r; }
        @Override public String renderizar() { return "Botón móvil " + resolucion.ancho() + "x" + resolucion.alto(); }
        @Override public String pulsar() { return "Toque móvil"; }
    }
    static final class EtiquetaMovil implements Etiqueta {
        private final Resolucion resolucion;
        EtiquetaMovil(Resolucion r) { resolucion = r; }
        @Override public String renderizar() { return "Etiqueta móvil " + resolucion.ancho() + "x" + resolucion.alto(); }
    }

    abstract static class CreadorInterfaz {
        protected final Resolucion resolucion;
        CreadorInterfaz(Resolucion resolucion) { this.resolucion = resolucion; }
        protected abstract Boton crearBoton();
        protected abstract Etiqueta crearEtiqueta();
        final String renderizarPantalla() {
            Boton boton = crearBoton();
            Etiqueta etiqueta = crearEtiqueta();
            return etiqueta.renderizar() + " | " + boton.renderizar() + " | " + boton.pulsar();
        }
    }
    static final class CreadorEscritorio extends CreadorInterfaz {
        CreadorEscritorio(Resolucion r) { super(r); }
        @Override protected Boton crearBoton() { return new BotonEscritorio(resolucion); }
        @Override protected Etiqueta crearEtiqueta() { return new EtiquetaEscritorio(resolucion); }
    }
    static final class CreadorMovil extends CreadorInterfaz {
        CreadorMovil(Resolucion r) { super(r); }
        @Override protected Boton crearBoton() { return new BotonMovil(resolucion); }
        @Override protected Etiqueta crearEtiqueta() { return new EtiquetaMovil(resolucion); }
    }

    public static void main(String[] args) {
        System.out.println(new CreadorMovil(new Resolucion(390, 844)).renderizarPantalla());
    }
}
