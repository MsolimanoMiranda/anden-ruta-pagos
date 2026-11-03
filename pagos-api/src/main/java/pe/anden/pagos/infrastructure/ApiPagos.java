package pe.anden.pagos.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.anden.pagos.application.Ruta;
import pe.anden.pagos.domain.Cobro;
import pe.anden.pagos.domain.ReglaDeNegocio;

import java.time.Instant;
import java.util.Map;

@RestController
class ApiPagos {
    private final Ruta ruta;
    private final JwtEncoder encoder;

    ApiPagos(Ruta ruta, JwtEncoder encoder) {
        this.ruta = ruta;
        this.encoder = encoder;
    }

    @GetMapping("/salud")
    Map<String, String> salud() {
        return Map.of("servicio", "pagos");
    }

    @PostMapping("/auth/token")
    TokenResponse token(@RequestBody TokenRequest pedido) {
        if (!"ruta".equals(pedido.clientId()) || !"oficio".equals(pedido.clientSecret())) {
            throw new ReglaDeNegocio("Cliente desconocido");
        }
        Instant ahora = Instant.now();
        String valor = encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                JwtClaimsSet.builder()
                        .issuer("anden-pagos")
                        .issuedAt(ahora)
                        .expiresAt(ahora.plusSeconds(3600))
                        .subject(pedido.clientId())
                        .claim("scope", "cobros")
                        .build())).getTokenValue();
        return new TokenResponse(valor, "Bearer", 3600);
    }

    @PostMapping("/cobros")
    @ResponseStatus(HttpStatus.CREATED)
    CobroResponse cobrar(@RequestHeader(value = "Idempotency-Key", required = false) String clave, @RequestBody CobroPedido pedido) {
        Cobro cobro = ruta.cobrar(clave, pedido.comercio(), pedido.centavos());
        return CobroResponse.de(cobro);
    }

    record TokenRequest(String clientId, String clientSecret) {}
    record TokenResponse(String accessToken, String tokenType, long expiresIn) {}
    record CobroPedido(String comercio, long centavos) {}
    record CobroResponse(String id, String comercio, long centavos) {
        static CobroResponse de(Cobro cobro) {
            return new CobroResponse(cobro.id(), cobro.comercio(), cobro.centavos());
        }
    }
}

@RestControllerAdvice
class ErroresApi {
    @ExceptionHandler(ReglaDeNegocio.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    Map<String, String> regla(ReglaDeNegocio error) {
        return Map.of("error", error.getMessage());
    }
}
