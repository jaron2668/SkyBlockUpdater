# Hypixel Updater

This project is a Java 21 microservice for my Hypixel Skyblock mod. It periodically fetches auction data from the Hypixel API, stores active and ended auctions in a PostgreSQL database

---

## Features

- Periodically fetch active/ended auctions and store sales data
- Uses PostgreSQL as a persistent data store
- Uses X and Y for Hypixel API communication and JSON parsing

---

## Technologies & Dependencies

- Java 21
- Maven
- Spring Boot (Web, JDBC)
- PostgreSQL JDBC Driver
- jackson-databind
- Kafka/Redpanda
- Docker & Docker Compose for containerized deployment

---

## Prerequisites

- Docker & Docker Compose installed

---

## Setup

### 1. Clone the repository

    git clone https://github.com/jaron2668/SkyblockUpdater.git
    cd SkyblockUpdater

### 2. Create a `.env` file

Create a `.env` file in the root directory with the following content, replacing the placeholders with your credentials:

    POSTGRES_DB=hypixel_db
    POSTGRES_USER=hypixel_user
    POSTGRES_PASSWORD=supersecret

> No quotes needed unless your values contain spaces or special characters.

### 3. Build and start services

Run the following command to build and start the backend and PostgreSQL containers:

    docker compose up --build

This will:

- Start a PostgreSQL database with the specified credentials.
- Build the backend app Docker image.
- Run the backend container, connecting it to the database and injecting the required environment variables.

### 4. Access the backend API

The backend will be accessible at:

`http://localhost:8080`

The database will be accessible at:

`http://localhost:5432` (if you uncomment the line in the Dockerfile)


---

## Usage

- The backend periodically fetches auctions every 5 minutes (ended auctions) and every 1 minute (current BIN auctions).
- Produces Kafka events for other microservices to consume
---

## Configuration

- Environment variables injected via `.env` file and passed through Docker Compose:
    - `POSTGRES_DB` — PostgreSQL database name
    - `POSTGRES_USER` — PostgreSQL username
    - `POSTGRES_PASSWORD` — PostgreSQL password
- Database connection configured via Spring Boot datasource environment variables.

---

## Notes

- By default, the backend port 8080 is bound only to localhost for security. To expose it to external networks, modify the `docker-compose.yml` ports section.
- Logs are output to the Docker container logs — use `docker logs hypixel-updater` to view.

---

## License

This project is **(open/closed) source** and distributed as [**Enter License Here when i know**](LICENSE.txt).  
Third-party dependencies are included as per their [**respective licenses**](THIRD-PARTY-LICENSES.txt).

---

## Contact

For questions or issues, [contact me](mailto:jaron2668@gmail.com).
