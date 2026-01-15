# SkyblockUpdater

This project is a Java 21 microservice for my Hypixel-Skyblock mod. It periodically fetches auction data from the Hypixel API, stores active and ended auctions 
in a PostgreSQL database and produces Kafka events for other microservices. 

---

## Features

- Periodically fetch active/ended auctions and store sales data.
- Uses PostgreSQL as a persistent data store.
- Publishes Kafka events:
  - updater-newauction
  - updater-endedauction
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

### Access

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

## 🛑 Disclaimer

> This project is **not affiliated with, endorsed by, or associated with Hypixel Inc.**  
> "Hypixel" and any associated names are trademarks of Hypixel Inc.
>
> This is an independent, community-created project intended for educational or personal use only.
