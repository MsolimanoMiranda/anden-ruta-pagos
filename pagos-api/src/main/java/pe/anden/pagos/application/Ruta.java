package pe.anden.pagos.application;

import pe.anden.pagos.domain.Cobro;

import java.util.UUID;

public class Ruta {
    private final CobroRepositorio cobros;

    public Ruta(CobroRepositorio cobros) {
        this.cobros = cobros;
    }

    public Cobro cobrar(String claveIdempotencia, String comercio, long centavos) {
        return cobros.porClave(claveIdempotencia).orElseGet(() -> {
            Cobro cobro = Cobro.aceptar(UUID.randomUUID().toString(), claveIdempotencia, comercio, centavos);
            cobros.guardar(cobro);
            return cobros.porClave(claveIdempotencia).orElse(cobro);
        });
    }
}
