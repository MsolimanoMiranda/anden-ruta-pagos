package pe.anden.pagos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ApiPagosTest {
    @Autowired
    WebApplicationContext contexto;

    MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    void laMismaClaveNoCobraDosVeces() throws Exception {
        String contrato = Files.readString(Path.of("src/main/resources/static/openapi.yaml"));
        if (!contrato.contains("/cobros") || !contrato.contains("Idempotency-Key")) {
            throw new AssertionError("el contrato no declara el cobro idempotente");
        }
        String token = token();
        mvc.perform(post("/cobros").contentType("application/json").content("{\"comercio\":\"Ana\",\"centavos\":1500}"))
                .andExpect(status().isUnauthorized());
        String primero = cobrar(token, "puesto-1", 1500);
        String segundo = cobrar(token, "puesto-1", 9900);
        String id = primero.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");
        if (!segundo.contains(id)) {
            throw new AssertionError("la segunda llamada cambió el cobro: " + segundo);
        }
        org.hamcrest.MatcherAssert.assertThat(primero, org.hamcrest.Matchers.containsString("1500"));
    }

    private String cobrar(String token, String clave, long centavos) throws Exception {
        return mvc.perform(post("/cobros")
                        .header("Authorization", "Bearer " + token)
                        .header("Idempotency-Key", clave)
                        .contentType("application/json")
                        .content("{\"comercio\":\"Puesto del muelle\",\"centavos\":" + centavos + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.centavos").value(1500))
                .andReturn().getResponse().getContentAsString();
    }

    private String token() throws Exception {
        String cuerpo = mvc.perform(post("/auth/token")
                        .contentType("application/json")
                        .content("{\"clientId\":\"ruta\",\"clientSecret\":\"oficio\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return cuerpo.replaceAll(".*\"accessToken\":\"([^\"]+)\".*", "$1");
    }
}
