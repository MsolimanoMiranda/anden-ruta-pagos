package pe.anden.pagos.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Cableado {
    @Bean
    CobroRepositorio cobros() {
        return new CobroMemoria();
    }

    @Bean
    Ruta ruta(CobroRepositorio cobros) {
        return new Ruta(cobros);
    }
}
