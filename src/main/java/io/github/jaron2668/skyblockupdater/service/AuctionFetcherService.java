package io.github.jaron2668.skyblockupdater.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaron2668.skyblocksharedmodels.AuctionActive;
import io.github.jaron2668.skyblocksharedmodels.AuctionEnded;
import io.github.jaron2668.skyblockupdater.util.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
public class AuctionFetcherService {

    private static final Logger LOG = LoggerFactory.getLogger(AuctionFetcherService.class);
    private static final String BASE_URL_ACTIVE;
    private static final String URL_ENDED;

    static {
        BASE_URL_ACTIVE = "https://api.hypixel.net/v2/skyblock/auctions?page=";
        URL_ENDED = "https://api.hypixel.net/v2/skyblock/auctions_ended";
    }

    private final HttpClient httpClient;


    private long lastUpdatedActive = -1;
    private long lastUpdatedEnded = -1;

    public AuctionFetcherService() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public List<AuctionActive> fetchActiveAuctions() {
        LOG.info("Fetching active auctions.");
        List<AuctionActive> auctions = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        int page = 0;
        boolean morePages = true;
        long newUpdatedTime = 0;

        int auctionsFetched = 0;

        while (morePages) {
            try {
                String json = fetchPageJson(page);
                JsonNode root = mapper.readTree(json);
                JsonNode auctionArray = root.get("auctions");

                // check if whole page did not change
                if (lastUpdatedActive >= root.get("lastUpdated").asLong()) {
                    int currentPage = root.get("page").asInt();
                    int totalPages = root.get("totalPages").asInt();
                    morePages = currentPage < totalPages - 1;
                    page++;
                    continue;
                }

                for (JsonNode node : auctionArray) {
                    if(!node.has("bin") || !node.get("bin").asBoolean())
                        continue;

                    // check if auction is not new
                    if(lastUpdatedActive >= node.get("last_updated").asLong())
                        continue;

                    AuctionActive auction = Parser.parseActiveAuction(node);
                    if (auction == null)
                        continue;

                    auctionsFetched++;
                    auctions.add(auction);
                }

                newUpdatedTime = Math.max(newUpdatedTime, root.get("lastUpdated").asLong());
                int currentPage = root.get("page").asInt();
                int totalPages = root.get("totalPages").asInt();
                morePages = currentPage < totalPages - 1;
                page++;
            } catch (Exception e) {
                LOG.error("Something went wrong while fetching page {}, aborting fetch.",page,e);
                morePages = false;
            }
        }

        LOG.info("Fetched {} pages, totaling {} new active auctions.", page, auctionsFetched);

        // set lastUpdated to max of page update times
        lastUpdatedActive = newUpdatedTime;

        return auctions;
    }

    protected String fetchPageJson(int page) throws Exception { // protected it can be mocked
        // URI uri = new URI(BASE_URL + page + "&key=" + API_KEY);
        URI uri = new URI(BASE_URL_ACTIVE + page); // seems like v2/skyblock/auctions doesn't need an api key to access
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch page " + page + ": " + response.statusCode());
        }

        return response.body();
    }

    public List<AuctionEnded> fetchEndedAuctions() {
        LOG.info("Fetching ended auctions.");
        List<AuctionEnded> auctions = new ArrayList<>();
        try {
            // Request ended auctions from hypixel api
            // URI uri = new URI(URL_ENDED + "&key=" + API_KEY);
            URI uri = new URI(URL_ENDED); // seems like v2/skyblock/auctions_ended doesn't need an api key to access
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch ended auctions: " + response.statusCode());
            }

            int auctionsFetched = 0;

            String json = response.body();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            // check if whole page did not change
            if (lastUpdatedEnded >= root.get("lastUpdated").asLong())
                return Collections.emptyList();

            JsonNode auctionArray = root.get("auctions");

            for (JsonNode node : auctionArray) {
                // check if auction is not new
                if(lastUpdatedEnded >= node.get("timestamp").asLong())
                    continue;
                AuctionEnded auction = Parser.parseEndedAuction(node);
                if (auction == null)
                    continue;

                auctionsFetched++;
                auctions.add(auction);
            }

            LOG.info("Fetched {} new ended auctions.", auctionsFetched);
            // update lastUpdated to current update time
            lastUpdatedEnded = root.get("lastUpdated").asLong();
        } catch (Exception e) {
            LOG.error("Something went wrong while fetching ended auctions.",e);
        }

        return auctions;
    }
}
