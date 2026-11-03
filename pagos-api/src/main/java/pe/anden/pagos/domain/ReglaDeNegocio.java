package pe.anden.pagos.domain;

public class ReglaDeNegocio extends RuntimeException {
    public ReglaDeNegocio(String mensaje) { super(mensaje); }
}
