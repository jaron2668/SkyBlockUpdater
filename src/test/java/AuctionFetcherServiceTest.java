import io.github.jaron2668.skyblockupdater.model.Auction;
import io.github.jaron2668.skyblockupdater.service.AuctionFetcherService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionFetcherServiceTest {

    @Test
    public void testFetchActiveAuctions_withMockJson() {
        String sampleJson = """
                {
                  "success": true,
                  "page": 0,
                  "totalPages": 1,
                  "totalAuctions": 31267,
                  "lastUpdated": 1571065561345,
                  "auctions": [
                    {
                      "uuid": "409a1e0f261a49849493278d6cd9305a",
                      "auctioneer": "347ef6c1daac45ed9d1fa02818cf0fb6",
                      "profile_id": "347ef6c1daac45ed9d1fa02818cf0fb6",
                      "coop": [
                        "347ef6c1daac45ed9d1fa02818cf0fb6"
                      ],
                      "start": 1573760802637,
                      "end": 1573761102637,
                      "item_name": "Azure Bluet",
                      "item_id": "azure_bluet",
                      "item_lore": "§f§lCOMMON",
                      "extra": "Azure Bluet Red Rose",
                      "category": "blocks",
                      "tier": "COMMON",
                      "starting_bid": 123456,
                      "last_updated": 1757028847193,
                      "bin": true,
                      "item_bytes": "H4sIAAAAAAAAAB2NQQqCQBhGv1ErHaKu0KoLtGtnarRIhTpA/OGfDIwZ4wxUF/IeHiyyto/3eBKIIJQEIDx4qsJaYJK07m6FhG+p9hEdVMV7TXU3Wh+JWaW6h6ZXhODYGg5/LeZDfxt6nZR5XhYhgoIaxmKE8dsZXu20YwuJZfa0hmJrjbo6y134f8pTll5O5TnbbgAP05Qaqhk+8AVIrd2eoAAAAA=="
                    }
                  ]
                }
                """;

        AuctionFetcherService fetcherService = new AuctionFetcherService() {
            @Override
            protected String fetchPageJson(int page) {
                return sampleJson;
            }
        };

        List<Auction> auctions = fetcherService.fetchActiveAuctions();

        assertNotNull(auctions);
        assertEquals(1, auctions.size());

        Auction auction = auctions.getFirst();
        assertEquals(123456, auction.getPrice());
        assertEquals("409a1e0f-261a-4984-9493-278d6cd9305a", auction.getId().toString());

        // Additional assertions to verify NBT parsed attributes
        assertNotNull(auction.getReforge());
        assertTrue(auction.getHotPotatoCount() >= 0);
    }

    @Test
    public void testFetchActiveAuctions_emptyAuctions() {
        String emptyAuctionsJson = """
        {
          "success": true,
          "page": 0,
          "totalPages": 0,
          "totalAuctions": 0,
          "lastUpdated": 0,
          "auctions": []
        }
        """;

        AuctionFetcherService fetcherService = new AuctionFetcherService() {
            @Override
            protected String fetchPageJson(int page) {
                return emptyAuctionsJson;
            }
        };

        List<Auction> auctions = fetcherService.fetchActiveAuctions();

        assertNotNull(auctions);
        assertTrue(auctions.isEmpty());
    }

    @Test
    public void testFetchActiveAuctions_filtersOutNonBinAuctions() {
        String jsonWithNonBin = """
        {
          "success": true,
          "page": 0,
          "totalPages": 0,
          "totalAuctions": 3,
          "lastUpdated": 0,
          "auctions": [
            {
              "uuid": "347ef6c1daac45ed9d1fa02818cf0fb6",
              "item_name": "Test Item",
              "item_id": "test_item",
              "starting_bid": 100,
              "bin": false
            },
            {
              "uuid": "409a1e0f261a49849493278d6cd9305a",
              "auctioneer": "347ef6c1daac45ed9d1fa02818cf0fb6",
              "profile_id": "347ef6c1daac45ed9d1fa02818cf0fb6",
              "coop": [
                "347ef6c1daac45ed9d1fa02818cf0fb6"
              ],
              "start": 1573760802637,
              "end": 1573761102637,
              "item_name": "Bin Item",
              "item_id": "some_bin_item",
              "item_lore": "§f§lCOMMON",
              "extra": "Azure Bluet Red Rose",
              "category": "blocks",
              "tier": "COMMON",
              "starting_bid": 200,
              "last_updated": 1757028847193,
              "bin": true,
              "item_bytes": {
                "type": 0,
                "data": "H4sIAAAAAAAAAB2NQQqCQBhGv1ErHaKu0KoLtGtnarRIhTpA/OGfDIwZ4wxUF/IeHiyyto/3eBKIIJQEIDx4qsJaYJK07m6FhG+p9hEdVMV7TXU3Wh+JWaW6h6ZXhODYGg5/LeZDfxt6nZR5XhYhgoIaxmKE8dsZXu20YwuJZfa0hmJrjbo6y134f8pTll5O5TnbbgAP05Qaqhk+8AVIrd2eoAAAAA=="
              }
            },
            {
              "uuid": "347ef6c1daac45ed9d1fa02818cf0fb6",
              "item_name": "Missing Bin Item",
              "item_id": "missing_bin_item",
              "starting_bid": 300
            }
          ]
        }
        """;

        AuctionFetcherService fetcherService = new AuctionFetcherService() {
            @Override
            protected String fetchPageJson(int page) {
                return jsonWithNonBin;
            }
        };

        List<Auction> auctions = fetcherService.fetchActiveAuctions();

        assertNotNull(auctions);
        // Only the one with bin = true should be included
        assertEquals(1, auctions.size());

        Auction auction = auctions.getFirst();
        assertEquals(200, auction.getPrice());
    }

    @Test
    public void testFetchActiveAuctions_malformedJsonStopsFetching() {
        String malformedJson = "{ this is not valid JSON }";

        AuctionFetcherService fetcherService = new AuctionFetcherService() {
            @Override
            protected String fetchPageJson(int page) {
                return malformedJson;
            }
        };
        Logger.getLogger("Test").info("Expect logging of a com.fasterxml.jackson.core.JsonParseException. You can ignore this error");
        List<Auction> auctions = fetcherService.fetchActiveAuctions();

        // Since parsing failed immediately, it should return empty list
        assertNotNull(auctions);
        assertTrue(auctions.isEmpty());
    }

    @Test
    public void testFetchActiveAuctions_multiplePages() {
        String page0 = """
        {
          "success": true,
          "page": 0,
          "totalPages": 2,
          "totalAuctions": 2,
          "lastUpdated": 0,
          "auctions": [
            {
              "uuid": "229a1e0f261a49849493273333d9305a",
              "auctioneer": "347ef6c1daac45ed9d1fa02818cf0fb6",
              "profile_id": "347ef6c1daac45ed9d1fa02818cf0fb6",
              "coop": [
                "347ef6c1daac45ed9d1fa02818cf0fb6"
              ],
              "start": 1573760802637,
              "end": 1573761102637,
              "item_name": "Azure Bluet",
              "item_id": "azure_bluet",
              "item_lore": "§f§lCOMMON",
              "extra": "Azure Bluet Red Rose",
              "category": "blocks",
              "tier": "COMMON",
              "starting_bid": 123456,
              "last_updated": 1757028847193,
              "bin": true,
              "item_bytes": {
                "type": 0,
                "data": "H4sIAAAAAAAAAB2NQQqCQBhGv1ErHaKu0KoLtGtnarRIhTpA/OGfDIwZ4wxUF/IeHiyyto/3eBKIIJQEIDx4qsJaYJK07m6FhG+p9hEdVMV7TXU3Wh+JWaW6h6ZXhODYGg5/LeZDfxt6nZR5XhYhgoIaxmKE8dsZXu20YwuJZfa0hmJrjbo6y134f8pTll5O5TnbbgAP05Qaqhk+8AVIrd2eoAAAAA=="
              }
            }
          ]
        }
        """;

        String page1 = """
        {
          "success": true,
          "page": 1,
          "totalPages": 2,
          "totalAuctions": 2,
          "lastUpdated": 12345,
          "auctions": [
            {
              "uuid": "409a1e0f261a49849493278d6cd9305a",
              "auctioneer": "347ef6c1daac45ed9d1fa02818cf0fb6",
              "profile_id": "347ef6c1daac45ed9d1fa02818cf0fb6",
              "coop": [
                "347ef6c1daac45ed9d1fa02818cf0fb6"
              ],
              "start": 1573760802637,
              "end": 1573761102637,
              "item_name": "Azure Bluet2",
              "item_id": "azure_bluet",
              "item_lore": "§f§lCOMMON",
              "extra": "Azure Bluet Red Rose",
              "category": "blocks",
              "tier": "COMMON",
              "starting_bid": 123456,
              "last_updated": 1757028847193,
              "bin": true,
              "item_bytes": {
                "type": 0,
                "data": "H4sIAAAAAAAAAB2NQQqCQBhGv1ErHaKu0KoLtGtnarRIhTpA/OGfDIwZ4wxUF/IeHiyyto/3eBKIIJQEIDx4qsJaYJK07m6FhG+p9hEdVMV7TXU3Wh+JWaW6h6ZXhODYGg5/LeZDfxt6nZR5XhYhgoIaxmKE8dsZXu20YwuJZfa0hmJrjbo6y134f8pTll5O5TnbbgAP05Qaqhk+8AVIrd2eoAAAAA=="
              }
            }
          ]
        }
        """;

        String pageError404 = """
                {
                    "success": false,
                    "cause": "Page not found"
                }
                """;

        AuctionFetcherService fetcherService = new AuctionFetcherService() {
            @Override
            protected String fetchPageJson(int page) {
                if (page == 0) return page0;
                else if (page == 1) return page1;
                else return pageError404;
            }
        };

        List<Auction> auctions = fetcherService.fetchActiveAuctions();

        assertNotNull(auctions);
        assertEquals(2, auctions.size());
    }
}
