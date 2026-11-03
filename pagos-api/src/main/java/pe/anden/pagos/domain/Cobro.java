package pe.anden.pagos.domain;

public final class Cobro {
    private final String id;
    private final String claveIdempotencia;
    private final String comercio;
    private final long centavos;

    private Cobro(String id, String claveIdempotencia, String comercio, long centavos) {
        this.id = id;
        this.claveIdempotencia = claveIdempotencia;
        this.comercio = comercio;
        this.centavos = centavos;
    }

    public static Cobro aceptar(String id, String claveIdempotencia, String comercio, long centavos) {
        if (claveIdempotencia == null || claveIdempotencia.isBlank()) {
            throw new ReglaDeNegocio("Falta la clave de idempotencia");
        }
        if (comercio == null || comercio.isBlank()) {
            throw new ReglaDeNegocio("El cobro necesita un comercio");
        }
        if (centavos <= 0) {
            throw new ReglaDeNegocio("El monto tiene que ser mayor que cero");
        }
        return new Cobro(id, claveIdempotencia, comercio.trim(), centavos);
    }

    public String id() { return id; }
    public String claveIdempotencia() { return claveIdempotencia; }
    public String comercio() { return comercio; }
    public long centavos() { return centavos; }
}
