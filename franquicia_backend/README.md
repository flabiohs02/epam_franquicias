# 🏬 Franquicia Backend - API REST de Gestión de Franquicias

Servicio backend de alto rendimiento para el **Sistema de Gestión de Franquicias (EPAM Challenge)**, desarrollado bajo principios de **Clean Architecture (Arquitectura Hexagonal)** y principios **SOLID**, implementado en **Java 21** con **Spring Boot 3.4.3**, persistencia en **MongoDB**, documentación interactiva con **Swagger/OpenAPI**, **Lombok** para entidades y suite completa de pruebas unitarias e integración en **JUnit 5**.

---

## 🏛️ Arquitectura del Sistema (Clean Architecture)

El código se organiza desacoplando de manera estricta el dominio de negocio de los detalles de infraestructura, frameworks y persistencia:

```
franquicia_backend/src/main/java/com/epam/franquicias/
├── domain/                                  # 1. CAPA DOMINIO (Núcleo de Negocio Puro)
│   ├── model/                               # Entidades y Value Objects (POJOs sin dependencias)
│   │   ├── Franchise.java                   # Agregado Raíz: Invariantes y reglas de negocio
│   │   ├── Branch.java                      # Entidad Sucursal con catálogo de productos
│   │   ├── Product.java                     # Entidad Producto con validación stock >= 0
│   │   └── BranchTopProduct.java            # Record/Value Object para el Requerimiento 7
│   ├── exception/                           # Excepciones semánticas del dominio
│   │   ├── DomainException.java             # Base de excepciones de negocio
│   │   ├── EntityNotFoundException.java     # HTTP 404
│   │   ├── DuplicateEntityException.java    # HTTP 409
│   │   └── InvalidStockException.java       # HTTP 400
│   └── repository/                          # Puertos de salida (Interfaces)
│       └── FranchiseRepository.java         # Contrato para persistencia
│
├── application/                             # 2. CAPA APLICACIÓN (Casos de Uso)
│   ├── mapper/                              # Mapeo entre Dominio y DTOs
│   │   └── FranchiseWebMapper.java
│   └── usecase/                             # Puertos de entrada (Interfaces de Casos de Uso)
│       ├── CreateFranchiseUseCase.java
│       ├── AddBranchUseCase.java
│       ├── AddProductUseCase.java
│       ├── DeleteProductUseCase.java
│       ├── UpdateStockUseCase.java
│       ├── GetTopStockProductsUseCase.java  # Requerimiento 7
│       ├── GetFranchiseUseCase.java
│       ├── UpdateNamesUseCase.java
│       └── service/                         # Servicios que implementan los casos de uso
│           ├── CreateFranchiseServiceImpl.java
│           ├── AddBranchServiceImpl.java
│           ├── AddProductServiceImpl.java
│           ├── DeleteProductServiceImpl.java
│           ├── UpdateStockServiceImpl.java
│           ├── GetTopStockProductsServiceImpl.java
│           ├── GetFranchiseServiceImpl.java
│           └── UpdateNamesServiceImpl.java
│
├── infrastructure/                          # 3. CAPA INFRAESTRUCTURA (Adaptadores externos)
│   ├── config/                              # Configuraciones Spring
│   │   ├── BeanConfiguration.java           # Inyección de dependencias desacoplada
│   │   ├── OpenApiConfig.java               # Configuración OpenAPI 3.0 / Swagger
│   │   └── CorsConfig.java                  # Habilitación de CORS para frontends
│   └── persistence/                         # Persistencia en MongoDB
│       ├── entities/                        # Documentos Mongo con Lombok
│       │   ├── FranchiseDocument.java       # @Document con @Data, @Builder
│       │   ├── BranchDocument.java          # @Data, @Builder
│       │   └── ProductDocument.java         # @Data, @Builder
│       └── repositories/                    # Repositorio Spring Data y Adaptador
│           ├── SpringDataMongoFranchiseRepository.java
│           ├── FranchiseMongoMapper.java    # Mapper Dominio <-> Documentos Mongo
│           └── MongoFranchiseRepositoryAdapter.java # Implementa FranchiseRepository
│
└── presentation/                            # 4. CAPA PRESENTACIÓN (API REST)
    ├── controllers/
    │   └── FranchiseController.java         # Endpoints REST y anotaciones Swagger
    ├── exception/
    │   └── GlobalExceptionHandler.java      # Manejador centralizado de errores HTTP
    └── dto/                                 # Contratos inmutables basados en Java Records
        ├── request/                         # Request DTOs con validaciones Bean Validation
        │   ├── CreateFranchiseRequest.java  # record
        │   ├── AddBranchRequest.java        # record
        │   ├── AddProductRequest.java       # record
        │   ├── UpdateStockRequest.java      # record
        │   └── UpdateNameRequest.java       # record
        └── response/                        # Response DTOs
            ├── FranchiseResponse.java       # record
            ├── BranchResponse.java          # record
            ├── ProductResponse.java         # record
            ├── BranchTopProductResponse.java# record
            └── ErrorResponse.java           # record
```

---

## 🛠️ Stack Tecnológico

- **Java 21 (LTS)**: Records, Pattern Matching, Streams.
- **Spring Boot 3.4.3**: `spring-boot-starter-web`, `spring-boot-starter-validation`, `spring-boot-starter-actuator`.
- **Spring Data MongoDB**: Modelado NoSQL de agregados jerárquicos e índices únicos.
- **Lombok**: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` en entidades de persistencia.
- **Springdoc OpenAPI (Swagger UI) 2.8.5**: Documentación interactiva de la API.
- **JUnit 5 (Jupiter) & Mockito**: Suite de 26 pruebas unitarias e integración.
- **Docker & Docker Compose**: Contenedores multi-stage con comprobaciones de salud activas (`healthchecks`).

---

## 🚀 Catálogo de Endpoints REST

| Método | Ruta | Descripción | Código Éxito |
|---|---|---|---|
| `POST` | `/api/v1/franchises` | Registrar una nueva franquicia | `201 Created` |
| `GET` | `/api/v1/franchises` | Listar todas las franquicias registradas | `200 OK` |
| `GET` | `/api/v1/franchises/{franchiseId}` | Obtener detalle completo de una franquicia por ID | `200 OK` |
| `POST` | `/api/v1/franchises/{franchiseId}/branches` | Agregar una sucursal a la franquicia | `201 Created` |
| `POST` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/products` | Agregar un producto con stock a una sucursal | `201 Created` |
| `DELETE` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}` | Eliminar un producto de una sucursal | `204 No Content` |
| `PATCH` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` | Actualizar el stock disponible de un producto | `200 OK` |
| `GET` | `/api/v1/franchises/{franchiseId}/top-stock-products` | **Requerimiento 7**: Producto con mayor stock por sucursal | `200 OK` |
| `PATCH` | `/api/v1/franchises/{franchiseId}/name` | Modificar el nombre de una franquicia | `200 OK` |
| `PATCH` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/name` | Modificar el nombre de una sucursal | `200 OK` |
| `PATCH` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name` | Modificar el nombre de un producto | `200 OK` |

---

## 📊 Requerimiento 7: Producto con Mayor Stock por Sucursal

Endpoint analítico que identifica de forma determinista el producto líder en existencias para cada sucursal de una franquicia dada.

### Solicitud:
```http
GET /api/v1/franchises/f1234567-89ab-cdef-0123-456789abcdef/top-stock-products
```

### Respuesta Estructurada (`200 OK`):
```json
[
  {
    "branchId": "b1111111-2222-3333-4444-555555555555",
    "branchName": "Sucursal Norte",
    "topProduct": {
      "id": "p9999999-8888-7777-6666-555555555555",
      "name": "Café Colombiano Premium",
      "stock": 250
    }
  },
  {
    "branchId": "b2222222-3333-4444-5555-666666666666",
    "branchName": "Sucursal Centro",
    "topProduct": {
      "id": "p7777777-6666-5555-4444-333333333333",
      "name": "Espresso Doble",
      "stock": 180
    }
  },
  {
    "branchId": "b3333333-4444-5555-6666-777777777777",
    "branchName": "Sucursal Nueva (Sin inventario)",
    "topProduct": null
  }
]
```

---

## 💻 Ejecución del Proyecto

### Opción 1: Despliegue con Docker Compose (Recomendado)

Desde la carpeta `franquicia_backend/`:
```bash
docker compose up --build -d
```

Este comando levanta:
- **MongoDB 7.0**: En el puerto mapeado `27018:27017` con volumen persistente y healthcheck activo.
- **Backend API**: En el puerto mapeado `8781:8080` con arranque condicionado a que la base de datos esté lista (`service_healthy`).

Para verificar el estado de los contenedores:
```bash
docker compose ps
```

Acceso a servicios:

#### Entorno Producción (`138.199.212.52`):
- **API REST**: [http://138.199.212.52:8781/api/v1/franchises](http://138.199.212.52:8781/api/v1/franchises)
- **Swagger UI**: [http://138.199.212.52:8781/swagger-ui.html](http://138.199.212.52:8781/swagger-ui.html)
- **OpenAPI Docs**: [http://138.199.212.52:8781/api-docs](http://138.199.212.52:8781/api-docs)
- **Actuator Health**: [http://138.199.212.52:8781/actuator/health](http://138.199.212.52:8781/actuator/health)

#### Entorno Local (`localhost`):
- **API REST**: [http://localhost:8781/api/v1/franchises](http://localhost:8781/api/v1/franchises)
- **Swagger UI**: [http://localhost:8781/swagger-ui.html](http://localhost:8781/swagger-ui.html)
- **Actuator Health**: [http://localhost:8781/actuator/health](http://localhost:8781/actuator/health)

Detener el entorno:
```bash
docker compose down -v
```

---

### Opción 2: Ejecución Local

#### 1. Iniciar MongoDB localmente
```bash
docker run -d -p 27018:27017 --name mongo-local mongo:7.0
```

#### 2. Compilar y ejecutar con Maven
```bash
mvn clean test
mvn spring-boot:run
```

---

## 🧪 Pruebas Automatizadas con JUnit 5

El proyecto cuenta con una cobertura integral de **26 pruebas automatizadas** que validan todas las capas de la arquitectura:

1. **Pruebas de Dominio (`FranchiseTest.java` - 9 tests)**:
   - Invariantes de nombre obligatorio y no vacío.
   - Restricción de stock no negativo (`InvalidStockException`).
   - Unicidad de nombres de sucursales y productos por contexto (`DuplicateEntityException`).
   - Cálculo determinista del producto con mayor stock por sucursal (Requerimiento 7), incluyendo sucursales sin productos.
2. **Pruebas de Aplicación (`FranchiseServiceTest.java` - 8 tests)**:
   - Aislamiento y orquestación de cada caso de uso mediante mocks de `FranchiseRepository`.
   - Verificación de guardados y manejo de entidades no encontradas (`EntityNotFoundException`).
3. **Pruebas de Presentación (`FranchiseControllerTest.java` - 6 tests)**:
   - Pruebas web con `MockMvc` de todos los endpoints principales.
   - Validación de códigos HTTP (`200 OK`, `201 Created`, `204 No Content`, `Location Header`).
4. **Pruebas de Entidades Lombok y Persistencia (`FranchiseEntityLombokTest.java` - 3 tests)**:
   - Verificación de constructores, `@Builder`, `@Data` de Lombok en `ProductDocument`, `BranchDocument` y `FranchiseDocument`.
   - Mapeo bidireccional fiel entre el Dominio y los Documentos de MongoDB.

Para ejecutar todas las pruebas:
```bash
mvn test
```

Salida esperada:
```
[INFO] Running com.epam.franquicias.application.FranchiseServiceTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.epam.franquicias.domain.FranchiseTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.epam.franquicias.infrastructure.persistence.FranchiseEntityLombokTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.epam.franquicias.presentation.FranchiseControllerTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 26, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
