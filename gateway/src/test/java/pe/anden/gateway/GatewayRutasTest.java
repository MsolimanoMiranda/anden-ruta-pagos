package pe.anden.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class GatewayRutasTest {
    @Autowired
    Environment entorno;

    @Test
    void elGatewayEnviaLosCobrosAlServicioDePagos() {
        assertEquals("cobros", entorno.getProperty("spring.cloud.gateway.server.webmvc.routes[0].id"));
        assertEquals("/cobros/**", entorno.getProperty("spring.cloud.gateway.server.webmvc.routes[0].predicates[0]").replace("Path=", ""));
    }
}
