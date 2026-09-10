# SkyBlock Updater

`SkyBlock Updater` is a Java 21 microservice for the Hypixel Skyblock backend. It periodically fetches auction data from the Hypixel API, stores active and ended auctions
in a PostgreSQL database and produces Kafka events for other microservices.

## Responsibilities

-   Fetch active BIN auctions and recently ended auctions.
-   Parse item data, including Minecraft NBT data.
-   Persist auction data in PostgreSQL.
-   Publish events for new and ended auctions.

The service fetches active BIN auctions roughly every three minutes. Ended auctions are checked roughly every 50 seconds because the Hypixel endpoint only exposes recently ended auctions for a short period.

## Events

-   `updater-newauction` contains an `AuctionActive` object as JSON. The initial full fetch on startup is stored without publishing this event. Publishing begins after that fetch has completed.
-   `updater-endedauction` contains the UUID of the ended auction.

## Requirements

-   Java 21
-   Maven
-   PostgreSQL
-   Kafka-compatible broker such as Redpanda

Docker Compose supplies PostgreSQL and Redpanda when the service is run as part of the complete backend stack.

## Build locally

Install the shared models artifact first, then build this service:

```bash
# Run in skyblock-shared-models
mvn clean install

# Run in skyblock-updater
mvn clean verify
```

## Run with Docker Compose

From the backend stack's root directory:

```bash
docker compose up --build updater
```

The container expects PostgreSQL at `postgres_db:5432` and Kafka at `redpanda:9092`, as configured in the root Compose file. For a complete local stack, start the root services together instead.

PostgreSQL is exposed on `127.0.0.1:5432` by default. The service logs are available with:

```bash
docker logs -f skyblock-updater
```

## License

See [LICENSE.txt](LICENSE.txt) and [THIRD-PARTY-LICENSES.txt](THIRD-PARTY-LICENSES.txt).

## Disclaimer

This project is not affiliated with, endorsed by, or associated with Hypixel Inc. "Hypixel" and related names are trademarks of Hypixel Inc. This is an independent community project intended for educational and personal use.
