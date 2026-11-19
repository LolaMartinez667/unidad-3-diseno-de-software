public final class Ejercicio07 {
    interface Boton {
        String renderizar();
        String pulsar();
    }
    interface CampoTexto {
        String renderizar();
        String escribir(String texto);
    }

    static final class BotonEscritorio implements Boton {
        @Override public String renderizar() { return "Botón de escritorio [oscuro]"; }
        @Override public String pulsar() { return "Click registrado en escritorio"; }
    }
    static final class CampoEscritorio implements CampoTexto {
        @Override public String renderizar() { return "Campo de escritorio [oscuro]"; }
        @Override public String escribir(String texto) { return "Texto de escritorio: " + texto; }
    }
    static final class BotonMovil implements Boton {
        @Override public String renderizar() { return "Botón móvil [minimalista]"; }
        @Override public String pulsar() { return "Toque registrado en móvil"; }
    }
    static final class CampoMovil implements CampoTexto {
        @Override public String renderizar() { return "Campo móvil [minimalista]"; }
        @Override public String escribir(String texto) { return "Texto móvil: " + texto; }
    }

    abstract static class CreadorUI {
        protected abstract Boton crearBoton();
        protected abstract CampoTexto crearCampoTexto();
        final String renderizarPantalla() {
            Boton boton = crearBoton();
            CampoTexto campo = crearCampoTexto();
            return boton.renderizar() + " | " + campo.renderizar() + " | "
                    + boton.pulsar() + " | " + campo.escribir("Hola");
        }
    }
    static final class CreadorEscritorio extends CreadorUI {
        @Override protected Boton crearBoton() { return new BotonEscritorio(); }
        @Override protected CampoTexto crearCampoTexto() { return new CampoEscritorio(); }
    }
    static final class CreadorMovil extends CreadorUI {
        @Override protected Boton crearBoton() { return new BotonMovil(); }
        @Override protected CampoTexto crearCampoTexto() { return new CampoMovil(); }
    }

    public static void main(String[] args) {
        System.out.println(new CreadorMovil().renderizarPantalla());
    }
}
