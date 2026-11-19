public final class Ejercicio03 {
    interface Notificacion {
        String enviar(String mensaje);
    }

    static final class Correo implements Notificacion {
        @Override public String enviar(String mensaje) { return "Correo enviado: " + mensaje; }
    }

    static final class SMS implements Notificacion {
        @Override public String enviar(String mensaje) { return "SMS enviado: " + mensaje; }
    }

    abstract static class CreadorNotificacion {
        protected abstract Notificacion crearNotificacion();
        String notificar(String mensaje) { return crearNotificacion().enviar(mensaje); }
    }

    static final class CreadorCorreo extends CreadorNotificacion {
        @Override protected Notificacion crearNotificacion() { return new Correo(); }
    }

    static final class CreadorSMS extends CreadorNotificacion {
        @Override protected Notificacion crearNotificacion() { return new SMS(); }
    }

    public static void main(String[] args) {
        CreadorNotificacion creador = new CreadorCorreo();
        System.out.println(creador.notificar("La reserva está confirmada."));
    }
}
