# Microservicio 2 - Catalogo de Restaurantes

CloudEats - Spring Boot + MongoDB.

## Responsabilidad

Este microservicio administra el catalogo de restaurantes. Cada restaurante se almacena como un documento MongoDB con:

- datos del restaurante
- arreglo embebido de `platos`
- arreglo embebido de `resenas`

No utiliza JPA ni comparte tablas con los otros microservicios.

## Endpoints

### Restaurantes

`POST /api/v1/restaurantes`
Crea un restaurante.

`GET /api/v1/restaurantes`
Busca restaurantes. Puede recibir:

- `?nombre=`
- `?categoria=`

Ejemplo:

`GET /api/v1/restaurantes?nombre=pizza&categoria=italiana`

`GET /api/v1/restaurantes/{id}`
Obtiene un restaurante completo.

`GET /api/v1/restaurantes/{id}/menu`
Obtiene solamente el menu.

### Platos

`POST /api/v1/restaurantes/{id}/platos`
Agrega un plato al menu embebido.

`DELETE /api/v1/restaurantes/{restauranteId}/platos/{platoId}`
Elimina un plato del menu.

### Resenas

`POST /api/v1/restaurantes/{id}/resenas`
Agrega una resena embebida.

### Eliminacion

`DELETE /api/v1/restaurantes/{id}`
Desactiva logicamente el restaurante.

## Ejemplo de documento MongoDB

```json
{
  "_id": "67xxxxxxxxxxxxxxxxxxxxxx",
  "empresaId": 1,
  "nombre": "La Esquina Criolla",
  "descripcion": "Comida peruana",
  "categoria": "Criolla",
  "direccion": "Av. Principal 123",
  "activo": true,
  "platos": [
    {
      "id": "uuid-del-plato",
      "nombre": "Lomo Saltado",
      "descripcion": "Lomo de res con papas y arroz",
      "precio": 24.90,
      "categoria": "Platos de fondo",
      "disponible": true
    }
  ],
  "resenas": [
    {
      "id": "uuid-de-la-resena",
      "usuarioId": "15",
      "puntuacion": 5,
      "comentario": "Muy bueno",
      "fecha": "2026-09-11T12:00:00"
    }
  ]
}
```

## Ejecutar localmente

Primero inicia MongoDB en `localhost:27017` y luego:

```bash
./mvnw clean package
./mvnw spring-boot:run
```

En Windows:

```powershell
mvnw.cmd clean package
mvnw.cmd spring-boot:run
```

## Ejecutar con Docker

```bash
mvnw.cmd clean package
docker compose up --build
```

El microservicio queda disponible en `http://localhost:8082`.
