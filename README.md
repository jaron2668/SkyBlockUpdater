# SkyblockUpdater

This project is a Java 21 microservice for my Hypixel-Skyblock mod. It periodically fetches auction data from the Hypixel API, stores active and ended auctions 
in a PostgreSQL database and produces Kafka events for other microservices. 

---

## Features

- Periodically fetch active/ended auctions and store sales data.
- Uses PostgreSQL as a persistent data store.
- Dispatches Kafka events for new or ended auctions ('updater-newauction' / 'updater-endedauction').

---

## Technologies & Dependencies

- Java 21
- Maven
- Spring Boot (JDBC)
- PostgreSQL JDBC Driver
- jackson-databind
- Querz-NBT
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

> This step is not needed but recommended to replace default credentials

Create a `.env` file in the root directory with the following content, replacing the placeholders with your credentials:

    POSTGRES_DB=skyblock_db
    POSTGRES_USER=skyblock_user
    POSTGRES_PASSWORD=supersecret

> No quotes needed unless your values contain spaces or special characters.
 

### 3. Build and start services

Run the following command to build and start the SkyblockUpdater, Kafka and PostgreSQL containers:

    docker compose up --build

This will:

- Start a Redpanda container for kafka.
- Start a PostgreSQL database with the specified credentials.
- Build the SkyblockUpdater Docker image.
- Start a skyblock-updater container (from the build Docker image), connecting it to the database and redpanda and injecting the required environment variables.

### 4. Access

The database will be accessible at:

`http://localhost:5432` (only on localhost)

Redpanda admin http api (if uncommented in docker-compose) will be accessible at:

`http://localhost:9644` (only on localhost)


---

## Usage

- The updater periodically fetches bin auctions every 3 minutes and ended auctions every 50 seconds (because hypixel api endpoint only shows auctions that ended in the last 60 sec).
- Produces Kafka events for other microservices to consume.
---

## Notes

- By default, all ports are only bound to localhost for security. To expose it to external networks, modify the `docker-compose.yml` ports section.
- Logs are output to the Docker container logs — use `docker logs skyblock-updater` to view.

---

## License

This project is **(open/closed) source** and distributed as [**Enter License Here when i know**](LICENSE.txt).  
Third-party dependencies are included as per their [**respective licenses**](THIRD-PARTY-LICENSES.txt).

---

## Contact

For questions or issues, [contact me](mailto:jaron2668@gmail.com).
