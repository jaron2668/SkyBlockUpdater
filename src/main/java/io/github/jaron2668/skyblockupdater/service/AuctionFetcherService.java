package io.github.jaron2668.skyblockupdater.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaron2668.skyblockupdater.model.Auction;
import io.github.jaron2668.skyblockupdater.util.AttributeParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AuctionFetcherService {

    private final String BASE_URL;
    {
        BASE_URL = "https://api.hypixel.net/v2/skyblock/auctions?page=";
    }

    private final String API_KEY;

    private final HttpClient httpClient;

    public AuctionFetcherService(@Value("${hypixel.api-key}") String apiKey) {
        API_KEY = apiKey;
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public List<Auction> fetchActiveAuctions() {
        List<Auction> auctions = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        int page = 0;
        boolean morePages = true;

        while (morePages) {
            try {
                String json = fetchPageJson(page);
                JsonNode root = mapper.readTree(json);
                JsonNode auctionArray = root.get("auctions");

                for (JsonNode node : auctionArray) {
                    if(!node.has("bin") || !node.get("bin").asBoolean())
                        continue;

                    auctions.add(createActiveAuction(node));
                }

                int currentPage = root.get("page").asInt();
                int totalPages = root.get("totalPages").asInt();
                morePages = currentPage < totalPages - 1;
                page++;
            } catch (Exception e) {
                e.printStackTrace();
                morePages = false;
            }
        }

        return auctions;
    }

    protected String fetchPageJson(int page) throws Exception { // protected it can be mocked
        // URI uri = new URI(BASE_URL + page + "&key=" + API_KEY);
        URI uri = new URI(BASE_URL + page); // seems like v2/skyblock/auctions doesn't need an api key to access
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

    private Auction createActiveAuction(JsonNode auctionJson) {
        Auction auction = new Auction();

        auction.setId(AttributeParser.parseHypixelUuid(auctionJson.get("uuid").asText()));
        auction.setItemId(auctionJson.get("item_id").asText());
        auction.setItemName(auctionJson.get("item_name").asText());
        auction.setItemBytes(auctionJson.get("item_bytes").asText());
        auction.setPrice(auctionJson.get("starting_bid").asLong());
        auction.setStartTime(Instant.ofEpochMilli(auctionJson.get("start").asLong()));
        auction.setEndTime(Instant.ofEpochMilli(auctionJson.get("end").asLong()));
        auction.setRarity(auctionJson.get("tier").asText());

        AttributeParser.parseAttributes(auction);

        return auction;
    }
}
