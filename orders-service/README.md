# orders-service
 
Microservicio de Pedidos - Proyecto Parcial CS2032 Cloud Computing UTEC 2026-2
 
**Stack:** Node.js 20 + Express + PostgreSQL 16 + Docker
 
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
├── plantilla_crear_mv_app.yaml            # CFN: crea MV Pruebas con puerto 8000
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
 
- Password: `utec`
- Usuario: `root`
- DB: `bd_api_orders`
- Puerto expuesto en MV BD: `8004`
- Total de registros insertados: **20,003** (3 de ejemplo + 20,000 fake)
## Maquinas virtuales usadas
 
| MV | Rol | Puertos abiertos |
|----|-----|-----|
| MV Desarrollo | Donde codeo y corre el contenedor de la API | 22, 8000 |
| MV Base de Datos | Corre PostgreSQL + Adminer | 22, 8080, 8004 |
| MV Pruebas | (Por usar) Sera la replica del micro para balanceador | 22, 8000 |
 
Configuracion actual
- IP PRIVADA de MV Base de Datos: 172.31.94.208 (usada por main.js, tráfico interno VPC)
- IP PUBLICA (Elastic IP) de MV Base de Datos: 34.206.96.68 (usada por compañeros de otro Learner Lab)
- IP PUBLICA (Elastic IP) de MV Desarrollo: 98.84.250.185 (donde corre la API)
- Puerto Postgres expuesto: 8004
- Puerto Adminer: 8080
- Usuario BD: root
- Contraseña BD: utec
- Nombre BD: bd_api_orders
## Comandos Docker
 
### En MV Base de Datos (una sola vez)
 
```bash
docker network create red_bd
docker volume create pg_orders_data
 
docker run -d --rm --name pg_orders_c \
  --network red_bd \
  -e POSTGRES_USER=root -e POSTGRES_PASSWORD=utec -e POSTGRES_DB=bd_api_orders \
  -p 8004:5432 \
  -v pg_orders_data:/var/lib/postgresql/data \
  postgres:16
 
docker run -d --rm --name adminer_c --network red_bd -p 8080:8080 adminer
```
 
### En MV Desarrollo
 
```bash
cd /home/ubuntu/contenedores
git clone https://github.com/miguelespinozaa-ship-it/ProyectoParcial.git
cd ProyectoParcial/orders-service
docker build -t orders-service .
docker run -d --rm --name orders-service_c -p 8000:8000 orders-service
docker logs orders-service_c
```
 
## Insertar los 20,000 pedidos ficticios (una sola vez)
 
Desde MV Desarrollo, con el contenedor corriendo:
 
```bash
docker exec orders-service_c node seed.js
```
 
Esperar 1-2 minutos hasta ver `insertados 20000/20000`.
 
## API en produccion
 
- Base URL: `http://98.84.250.185:8000`
- Ejemplos:
  - `http://98.84.250.185:8000/` -> Health check
  - `http://98.84.250.185:8000/orders` -> Ultimos 100 pedidos
  - `http://98.84.250.185:8000/orders/1` -> Detalle del pedido 1
  - 
## Arquitectura actual (avance del 50%)
 
```
[Cliente / Navegador]
        |
        v HTTP publico
[MV Desarrollo: orders-service_c :8000]
        |
        v TCP privado (puerto 8004)
[MV Base de Datos: pg_orders_c :5432 (expuesto en 8004)]
        + Adminer en :8080
        + red_bd (red docker compartida)
```
 
## Arquitectura final 
 
```
[Cliente / Navegador]
        |
        v HTTPS
[AWS API Gateway]
        |
        v HTTP privado (VPC Link)
[ALB - Application Load Balancer]
        |
        v round-robin
[MV Desarrollo: orders-service_c :8000]  [MV Pruebas: orders-service_c :8000]
        |
        v TCP privado (puerto 8004)
[MV Base de Datos: pg_orders_c :5432]
```
 
## Estado de despliegue
 
- [x] Repo publico en GitHub
- [x] Docker corriendo en MV Desarrollo
- [x] PostgreSQL con 20,003 registros
- [x] API responde desde IP publica
- [ ] Replicado en MV Pruebas
- [ ] Application Load Balancer configurado
- [ ] API Gateway con HTTPS
- [ ] Documentacion Swagger en repo `catalog` compartido
- [ ] Diagrama E/R exportado
 
