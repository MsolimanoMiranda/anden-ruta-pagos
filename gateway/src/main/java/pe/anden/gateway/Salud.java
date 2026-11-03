package pe.anden.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class Salud {
    @GetMapping("/salud")
    Map<String, String> salud() {
        return Map.of("servicio", "gateway");
    }
}
