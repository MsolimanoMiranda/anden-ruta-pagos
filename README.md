# Ruta de pagos

Dos servicios. `pagos-api` cobra con OAuth2 (JWT de demostración) y una clave de idempotencia: la segunda llamada con la misma clave devuelve el primer cobro. `gateway` es Spring Cloud Gateway en el servidor WebMVC y reenvía `/cobros/**` hacia pagos.

Spring Boot 4.0.9 y Spring Cloud 2025.1.4. El contrato OpenAPI vive en `pagos-api/src/main/resources/static/openapi.yaml` y Swagger UI en `/swagger`.

Cliente de demostración: `ruta` / `oficio`. No hay un proveedor de identidad real: el token se firma en el propio servicio para poder probar el resource server.

## Correr

```bash
cd projects/spring-ruta-pagos
mvn test
```

Puertos: gateway `8080`, pagos `8081`.

```bash
mvn -pl pagos-api spring-boot:run
mvn -pl gateway spring-boot:run
```

`docker compose up --build` levanta los dos después de `mvn -DskipTests package`.
