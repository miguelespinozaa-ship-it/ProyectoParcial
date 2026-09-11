# orders-service

Microservicio de Pedidos - Proyecto Parcial CS2032 Cloud Computing UTEC 2026-2

---

## Estructura

```
orders-service/
├── main.js                                # API REST completo (todo en un archivo, estilo profe)
├── Dockerfile                             # Imagen base node:20-slim
├── package.json                           # express, cors, pg, faker
├── db.txt                                 # Script SQL para Adminer
├── seed.js                                # Insercion masiva de 20,000 pedidos
├── orders-api.yaml                        # Documentacion OpenAPI (subir al repo catalog)
├── orders.postman_collection.json         # Coleccion de Postman
├── plantilla_crear_mv_bd_orders.yaml      # CFN: crea MV Bases de Datos con puertos 8004-8006
├── plantilla_crear_mv_app.yaml            # CFN: crea MV Pruebas 1/2 con puerto 8000
└── README.md
```

## Endpoints

| Metodo | Path | Descripcion |
|--------|------|-------------|
| GET | `/` | Echo test para health check del ALB |
| GET | `/orders` | Ultimos 100 pedidos |
| GET | `/orders/:id` | Pedido con sus items |
| GET | `/orders/user/:userId` | Historial de un usuario |
| GET | `/orders/restaurant/:restaurantId` | Pedidos de un restaurante |
| POST | `/orders` | Crea pedido (consume micro de Restaurantes) |
| PUT | `/orders/:id` | Cambia estado del pedido |
| DELETE | `/orders/:id` | Elimina pedido |

## Base de datos (PostgreSQL 16)

**2 tablas relacionadas** (`orders` <- 1:N -> `order_items`)

Password: `utec`. Usuario: `root`. DB: `bd_api_orders`. Puerto expuesto en MV BD: `8004`.

## Comandos Docker

```bash
# En MV Bases de Datos (una sola vez):
docker network create red_bd
docker volume create pg_orders_data
docker run -d --rm --name pg_orders_c \
  --network red_bd \
  -e POSTGRES_USER=root -e POSTGRES_PASSWORD=utec -e POSTGRES_DB=bd_api_orders \
  -p 8004:5432 \
  -v pg_orders_data:/var/lib/postgresql/data \
  postgres:16

# En MV Desarrollo / MV Pruebas 1 / MV Pruebas 2:
docker build -t orders-service .
docker run -d --rm --name orders-service_c -p 8000:8000 orders-service
docker logs orders-service_c
```

## Antes de correr

Reemplazar en `main.js` la constante `host_name` con la IP privada real de la MV Bases de Datos.
