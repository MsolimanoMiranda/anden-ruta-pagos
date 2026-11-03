package pe.anden.pagos.application;

import pe.anden.pagos.domain.Cobro;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public interface CobroRepositorio {
    Optional<Cobro> porClave(String clave);
    void guardar(Cobro cobro);
}

class CobroMemoria implements CobroRepositorio {
    private final Map<String, Cobro> cobros = new ConcurrentHashMap<>();

    @Override public Optional<Cobro> porClave(String clave) { return Optional.ofNullable(cobros.get(clave)); }
    @Override public void guardar(Cobro cobro) { cobros.putIfAbsent(cobro.claveIdempotencia(), cobro); }
}
