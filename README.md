# 🏬 Sistema de Gestión de Franquicias (EPAM Challenge)

Solución integral y robusta desarrollada bajo los principios de **Clean Architecture (Arquitectura Hexagonal)** y principios **SOLID**, implementando una API REST en **Java 27** con **Spring Boot**, persistencia optimizada en **MongoDB**, y una interfaz moderna en **Angular (TypeScript)**.

---

## 🏛️ Arquitectura del Sistema (Clean Architecture)

El proyecto organiza el código separando estrictamente el dominio de cualquier infraestructura o framework tecnológico:

```
backend/src/main/java/com/epam/franquicias/
├── domain/                                  # CAPA DOMINIO (Reglas de negocio puras, POJOs sin dependencias de Spring/Mongo)
│   ├── model/
│   │   ├── Franchise.java                   # Aggregate Root: Invariantes y lógica de negocio
│   │   ├── Branch.java                      # Entidad Sucursal con catálogo de productos
│   │   ├── Product.java                     # Entidad Producto con validación de stock >= 0
│   │   └── BranchTopProduct.java            # Record/Value Object para el Requerimiento 7
│   ├── exception/                           # Excepciones semánticas de dominio (EntityNotFound, DuplicateEntity, InvalidStock)
│   └── port/out/
│       └── FranchiseRepositoryPort.java     # Puerto de salida (contrato para persistencia)
│
├── application/                             # CAPA APLICACIÓN (Casos de Uso / Interactors)
│   ├── port/in/                             # Puertos de entrada (Interfaces de Casos de Uso)
│   │   ├── CreateFranchiseUseCase.java
│   │   ├── AddBranchUseCase.java
│   │   ├── AddProductUseCase.java
│   │   ├── DeleteProductUseCase.java
│   │   ├── UpdateStockUseCase.java
│   │   ├── GetTopStockProductsUseCase.java  # Requerimiento 7
│   │   ├── GetFranchiseUseCase.java
│   │   └── UpdateNamesUseCase.java
│   └── service/                             # Servicios de aplicación que implementan los casos de uso
│       ├── CreateFranchiseService.java
│       ├── AddBranchService.java
│       ├── AddProductService.java
│       ├── DeleteProductService.java
│       ├── UpdateStockService.java
│       ├── GetTopStockProductsService.java
│       ├── GetFranchiseService.java
│       └── UpdateNamesService.java
│
└── infrastructure/                          # CAPA INFRAESTRUCTURA (Adaptadores externos y Frameworks)
    ├── adapter/in/web/                      # Inbound Adapter: REST API (Spring Web MVC)
    │   ├── FranchiseController.java         # Controlador REST y anotaciones OpenAPI/Swagger
    │   ├── dto/                             # Records para requests y responses
    │   ├── mapper/FranchiseWebMapper.java
    │   └── error/GlobalExceptionHandler.java# Manejador centralizado de errores y códigos HTTP
    ├── adapter/out/persistence/mongodb/     # Outbound Adapter: MongoDB
    │   ├── document/                        # Documentos Mongo con colecciones e índices
    │   ├── repository/                      # SpringDataMongoFranchiseRepository
    │   ├── mapper/FranchiseMongoMapper.java
    │   └── MongoFranchiseRepositoryAdapter.java # Implementa FranchiseRepositoryPort
    └── config/
        ├── BeanConfiguration.java           # Inyección de dependencias desacoplada (sin @Service en la app)
        ├── OpenApiConfig.java               # Configuración de Swagger UI
        └── MongoConfig.java
```

---

## 🎯 Principios SOLID Aplicados

1. **S - Single Responsibility Principle (SRP)**:
   Cada caso de uso (`CreateFranchiseService`, `UpdateStockService`, etc.) resuelve una única operación del sistema. El controlador solo atiende el protocolo HTTP y el repositorio solo atiende la persistencia.
2. **O - Open/Closed Principle (OCP)**:
   El núcleo de negocio está cerrado a modificaciones pero abierto a extensiones mediante puertos (`FranchiseRepositoryPort`, `GetTopStockProductsUseCase`). Si se sustituye MongoDB por PostgreSQL o DynamoDB, ninguna clase de dominio o aplicación se modifica.
3. **L - Liskov Substitution Principle (LSP)**:
   Cualquier implementación del puerto `FranchiseRepositoryPort` respeta íntegramente el contrato sin arrojar excepciones no contempladas.
4. **I - Interface Segregation Principle (ISP)**:
   Interfaces específicas por caso de uso (`AddBranchUseCase`, `UpdateStockUseCase`, `DeleteProductUseCase`) evitando interfaces monolíticas "fat".
5. **D - Dependency Inversion Principle (DIP)**:
   Los módulos de alto nivel (Dominio y Casos de Uso) no dependen de detalles de bajo nivel (MongoDB, Spring, HTTP). Los adaptadores de infraestructura dependen de las abstracciones definidas en el dominio.

---

## 📊 Requerimiento 7: Producto con Mayor Stock por Sucursal

El requerimiento solicita:
> *"Endpoint analítico que permita consultar cuál es el producto con mayor stock por cada sucursal para una franquicia puntual. Debe retornar un listado estructurado que indique claramente el producto y la sucursal a la que pertenece."*

### Implementación y Optimización
- **En el Dominio**: La entidad agregada `Franchise` cuenta con el método `getTopStockProductsByBranch()` que calcula deterministamente en memoria (utilizando `Stream.max(Comparator.comparingInt(Product::getStock))`) el producto líder por cada sucursal.
- **Formato Estructurado de Retorno**:
  ```json
  [
    {
      "branchId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
      "branchName": "Sucursal Centro",
      "topProduct": {
        "id": "a3b8c9d0-1e2f-3a4b-5c6d-7e8f9a0b1c2d",
        "name": "Capuchino Vainilla",
        "stock": 140
      }
    },
    {
      "branchId": "c4d5e6f7-8a9b-0c1d-2e3f-4a5b6c7d8e9f",
      "branchName": "Sucursal Norte",
      "topProduct": {
        "id": "e9f0a1b2-3c4d-5e6f-7a8b-9c0d1e2f3a4b",
        "name": "Café Americano",
        "stock": 85
      }
    }
  ]
  ```

---

## 🚀 Catálogo de Endpoints REST

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/v1/franchises` | Crear una nueva franquicia |
| `GET` | `/api/v1/franchises` | Listar todas las franquicias |
| `GET` | `/api/v1/franchises/{franchiseId}` | Obtener detalle de franquicia por ID |
| `POST` | `/api/v1/franchises/{franchiseId}/branches` | Agregar sucursal a una franquicia |
| `POST` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/products` | Agregar producto con stock a una sucursal |
| `DELETE` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}` | Eliminar producto de una sucursal |
| `PATCH` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` | Modificar stock de un producto |
| `GET` | `/api/v1/franchises/{franchiseId}/top-stock-products` | **Requerimiento 7**: Producto con mayor stock por sucursal |
| `PATCH` | `/api/v1/franchises/{franchiseId}/name` | Modificar nombre de una franquicia |
| `PATCH` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/name` | Modificar nombre de una sucursal |
| `PATCH` | `/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name` | Modificar nombre de un producto |

---

## 💻 Ejecución del Proyecto

### Opción A: Despliegue con Docker Compose (Recomendado)
Para iniciar MongoDB, Backend y Frontend con un solo comando:
```bash
docker compose up --build -d
```
- **Frontend**: [http://localhost:4200](http://localhost:4200)
- **Backend API**: [http://localhost:8080](http://localhost:8080)
- **Swagger / OpenAPI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **MongoDB**: `localhost:27017`

### Opción B: Ejecución Local

#### 1. Iniciar MongoDB
```bash
docker run -d -p 27017:27017 --name mongo-local mongo:7.0
```

#### 2. Backend (Spring Boot & Clean Architecture)
Puedes ejecutar tanto `backend/` como el módulo reestructurado `franquicia_backend/`:
```bash
# Opción franquicia_backend (Estructura Clean Architecture con Lombok y JUnit 5):
cd franquicia_backend
mvn clean test        # Ejecuta la suite de 26 pruebas JUnit 5
mvn spring-boot:run   # Inicia el backend en el puerto 8080

# O el módulo backend original:
cd backend
mvn clean test
mvn spring-boot:run
```

#### 3. Frontend (Angular 19 / TypeScript)
```bash
cd frontend
npm install
npm start             # Inicia el servidor de desarrollo en http://localhost:4200
```

---

## 🧪 Pruebas Automatizadas

El backend incluye pruebas exhaustivas que cubren:
- **Pruebas de Dominio**: Creación de franquicias, sucursales y productos, invariantes de stock no negativo, nombres duplicados, y cálculo del producto con mayor stock (`FranchiseTest`).
- **Pruebas de Aplicación**: Aislamiento de casos de uso y orquestación con puertos de repositorio (`FranchiseServiceTest`).
- **Pruebas de Controladores**: Verificación de contratos HTTP, códigos de respuesta (200, 201, 204, 400, 404, 409) y serialización JSON (`FranchiseControllerTest`).

Para ejecutar todas las pruebas:
```bash
cd backend
mvn test
```
Resultados:
```
Tests run: 23, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 🔄 Pipeline de Integración y Entrega Continua (CI/CD)

El repositorio cuenta con un pipeline automatizado en **GitHub Actions** (`.github/workflows/ci.yml`) que valida la integridad de cada cambio en ramas principales y Pull Requests:

1. **Backend CI (`backend-ci`)**:
   - Compilación con JDK 21 (Eclipse Temurin) y cache de artefactos Maven.
   - Ejecución de los 23 tests de Dominio, Aplicación y Controladores REST (`mvn -B clean verify`).
2. **Frontend CI (`frontend-ci`)**:
   - Entorno Node.js 22 con cache de dependencias NPM.
   - Instalación determinista (`npm ci`) y compilación del bundle de producción de Angular (`ng build --configuration production`).
3. **Docker Verification (`docker-verification`)**:
   - Validación estricta de la configuración `docker compose config`.
   - Compilación de las imágenes Docker de Backend y Frontend mediante Docker Buildx.

---

## ☁️ Despliegue en la Nube con Terraform (AWS ECS Fargate)

La infraestructura como código ubicada en la carpeta `terraform/` aprovisiona una arquitectura escalable, segura y altamente disponible en **Amazon Web Services**:

### Componentes de la Infraestructura
- **VPC Multi-AZ**: 2 subredes públicas (ALB, NAT Gateway) y 2 subredes privadas (servicios ECS).
- **Application Load Balancer (ALB)**: Punto de entrada público que enruta:
  - Tráfico a `/api/*`, `/swagger-ui*` y `/actuator/*` hacia el contenedor Backend (puerto 8080).
  - Tráfico por defecto `/*` hacia el frontend Angular en Nginx (puerto 80).
- **ECS Fargate**: Contenedores serverless para Backend (Spring Boot con healthcheck en `/actuator/health`) y Frontend (Angular Nginx).
- **Seguridad**: Grupos de seguridad que aíslan el Backend para aceptar tráfico únicamente desde el balanceador de carga.
- **Observabilidad**: Logs centralizados en AWS CloudWatch con métricas de Container Insights.

### Pasos para el Despliegue
1. Configurar credenciales de AWS:
   ```bash
   aws configure
   ```
2. Crear archivo de variables a partir de la plantilla:
   ```bash
   cd terraform
   cp terraform.tfvars.example terraform.tfvars
   # Editar terraform.tfvars con la URI de MongoDB y referencias de imagen en ECR
   ```
3. Inicializar y aplicar con Terraform:
   ```bash
   terraform init
   terraform plan
   terraform apply -auto-approve
   ```
4. Al finalizar, Terraform mostrará el `alb_dns_name` con el que se puede acceder inmediatamente a la plataforma.

