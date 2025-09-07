import io.github.jaron2668.skyblocksharedmodels.AuctionActive;
import io.github.jaron2668.skyblocksharedmodels.ToolItem;
import io.github.jaron2668.skyblockupdater.service.AuctionFetcherService;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

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
                      "item_bytes": "H4sIAAAAAAAA/1VVz28bRRR+rt3EdlsKLRJCKmiAFqVKYxwnsUkPSK7jxIa0RbGbFCG0mt2d2KPs7qx2ZlPnyJUzlRCiN6QgLtw4cYrE/4HyhyC+2bUdY6288+Z978e89+bbKlGFCrJKRIVrdE36hXcLdL2j0sgUqlQ0fFShkoi8MdlfgW68iNxE8BPuBqJQpEpP+mI34CMN7b9VWvaljgN+BqN9lYgydhl9cHHe2uEhH4nH7OLcW23UG7UmVmJldaP+kBpQD0wiopEZ54CN7a3a+hyAxfYKjJp25a+srm8+pPdg00mkYYt+W/UH9KFFfwvc5ZvvsfpuLv78oxWRzx6ki/Mg/x+oNGBdbkTCDqF/ZOFPeCSYOmbtxIwTFStfs8P+TNkZ8zCWKmIvqWbFQPBTcWVrc5IeDxYtUlfqkB3SBoSdhMuI9ef4buQj9AAVsxnMbboT4aUGfumLTIpFItEEsWC5KxPB2joWnmH9/txyT/LIsK9kEGQOaQt7/TDmgYxGi7h9YcbYNGcLUfeVMhns0PZkez/1Tha0Aw8njUaLlRqMeRJHQtsC9emx3QmlTfsqznCc2iMGKvEXfA0TGQdiDV2XJ/ZQ9BE2D21lk6xWc+ChiFSoUgRA5+6h6T2B4qLZW/XLN79i0UJp2FiaGrXslOVavtrYqj+wWj8bD2YUhMY/f71mL5AN960qVK7Gq0ab+MezZztzcb6xurl1+dMfrKNClxt2JLWvQqgZHwGgzdQR3cH7/85qSPH+xXmz7Upb2MdsiPF5xewcY9IO+nu9Ievs9ztf0SewyJVnKk1YjweuSPxHLMvWdoAHAX0GkIhEKIVmyEwazWJuxkCJvJuownr9gU0tFIEQtDI/bw3ZR9qOkMRw2il+pZmnsuS3GxNawytLGiMU8ogzHvmZW+uzMWEzXe6NPoaMaYngSbA4EacSHakB9flKyCdsvTl5SHchPbWuOghjr+NGo456vINIRxJ2ifDZExWlmj61tU4wpTrrlL3nHi4rm1HAtKn0ftYWr8MN99ALi24F4lQEts6rs0t8wi/OE5ZLT78Z9vodNjh6frDDrgBlKj3joaAmtua5TGtu77k91WCMTV/YXjYvf/l98aEq3e5OTMLbBuPq4lrqIt1OOG76mZPGo4T7wrIf2PDtsTJOrAw3yvEshWL7dpVKIxHqMpW/bA++7h44dSrt9p91sdF5/vRJezjbqNBbaRQo70T4jg6U0ZY9ry2YzfFlujlbOvBNSzmEluiON+Unx8vm15nE7dMjPfjh7z8RL1S+PJaobfnVtA5FujU9gpPVFhGvl+1ngO62X3ad57vOsNd1Br2D7s5Od6dKN+2nAL0LBfpXpLKcUgvsikW644I6HXXs8Dl1QrFUpFIALsmXZW9Kkbm45GUMauMWaTnIGQhSqUjXtSUTrJcB02fxWEW54m4aGBmCtB0N+naEpe/cQUXPSCqXkS7IB9W0/JoHvHEM4nR4Rpx51lUx59fcf+V0xkS5ybLI6Xjqc2Qp1jnJKHZ6ptMpTeWIWyajN0dn9Jb7vGGuiDA3qugZe06P6GUfitxFedZHiFVggxldW1vCRKcpenRfeJsNt9lYXxPuMV/bXG/V19wW99fEdoM3/a3m8YbbLFEF1RLawCGm9PVv92q7dq6W8k+n/az/B5EqnO8FCAAA"
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

        List<AuctionActive> auctions = fetcherService.fetchActiveAuctions();

        assertNotNull(auctions);
        assertEquals(1, auctions.size());

        AuctionActive auction = auctions.getFirst();
        assertEquals(123456, auction.getPrice());
        assertEquals("409a1e0f-261a-4984-9493-278d6cd9305a", auction.getUuid().toString());

        // Additional assertions to verify NBT parsed attributes
        assertNotNull(((ToolItem)auction.getItem()).getReforge());
        assertTrue(((ToolItem)auction.getItem()).getHotPotatoCount() >= 0);
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

        List<AuctionActive> auctions = fetcherService.fetchActiveAuctions();

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
              "item_bytes": "H4sIAAAAAAAA/1VVz28bRRR+rt3EdlsKLRJCKmiAFqVKYxwnsUkPSK7jxIa0RbGbFCG0mt2d2KPs7qx2ZlPnyJUzlRCiN6QgLtw4cYrE/4HyhyC+2bUdY6288+Z978e89+bbKlGFCrJKRIVrdE36hXcLdL2j0sgUqlQ0fFShkoi8MdlfgW68iNxE8BPuBqJQpEpP+mI34CMN7b9VWvaljgN+BqN9lYgydhl9cHHe2uEhH4nH7OLcW23UG7UmVmJldaP+kBpQD0wiopEZ54CN7a3a+hyAxfYKjJp25a+srm8+pPdg00mkYYt+W/UH9KFFfwvc5ZvvsfpuLv78oxWRzx6ki/Mg/x+oNGBdbkTCDqF/ZOFPeCSYOmbtxIwTFStfs8P+TNkZ8zCWKmIvqWbFQPBTcWVrc5IeDxYtUlfqkB3SBoSdhMuI9ef4buQj9AAVsxnMbboT4aUGfumLTIpFItEEsWC5KxPB2joWnmH9/txyT/LIsK9kEGQOaQt7/TDmgYxGi7h9YcbYNGcLUfeVMhns0PZkez/1Tha0Aw8njUaLlRqMeRJHQtsC9emx3QmlTfsqznCc2iMGKvEXfA0TGQdiDV2XJ/ZQ9BE2D21lk6xWc+ChiFSoUgRA5+6h6T2B4qLZW/XLN79i0UJp2FiaGrXslOVavtrYqj+wWj8bD2YUhMY/f71mL5AN960qVK7Gq0ab+MezZztzcb6xurl1+dMfrKNClxt2JLWvQqgZHwGgzdQR3cH7/85qSPH+xXmz7Upb2MdsiPF5xewcY9IO+nu9Ievs9ztf0SewyJVnKk1YjweuSPxHLMvWdoAHAX0GkIhEKIVmyEwazWJuxkCJvJuownr9gU0tFIEQtDI/bw3ZR9qOkMRw2il+pZmnsuS3GxNawytLGiMU8ogzHvmZW+uzMWEzXe6NPoaMaYngSbA4EacSHakB9flKyCdsvTl5SHchPbWuOghjr+NGo456vINIRxJ2ifDZExWlmj61tU4wpTrrlL3nHi4rm1HAtKn0ftYWr8MN99ALi24F4lQEts6rs0t8wi/OE5ZLT78Z9vodNjh6frDDrgBlKj3joaAmtua5TGtu77k91WCMTV/YXjYvf/l98aEq3e5OTMLbBuPq4lrqIt1OOG76mZPGo4T7wrIf2PDtsTJOrAw3yvEshWL7dpVKIxHqMpW/bA++7h44dSrt9p91sdF5/vRJezjbqNBbaRQo70T4jg6U0ZY9ry2YzfFlujlbOvBNSzmEluiON+Unx8vm15nE7dMjPfjh7z8RL1S+PJaobfnVtA5FujU9gpPVFhGvl+1ngO62X3ad57vOsNd1Br2D7s5Od6dKN+2nAL0LBfpXpLKcUgvsikW644I6HXXs8Dl1QrFUpFIALsmXZW9Kkbm45GUMauMWaTnIGQhSqUjXtSUTrJcB02fxWEW54m4aGBmCtB0N+naEpe/cQUXPSCqXkS7IB9W0/JoHvHEM4nR4Rpx51lUx59fcf+V0xkS5ybLI6Xjqc2Qp1jnJKHZ6ptMpTeWIWyajN0dn9Jb7vGGuiDA3qugZe06P6GUfitxFedZHiFVggxldW1vCRKcpenRfeJsNt9lYXxPuMV/bXG/V19wW99fEdoM3/a3m8YbbLFEF1RLawCGm9PVv92q7dq6W8k+n/az/B5EqnO8FCAAA"
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

        List<AuctionActive> auctions = fetcherService.fetchActiveAuctions();

        assertNotNull(auctions);
        // Only the one with bin = true should be included
        assertEquals(1, auctions.size());

        AuctionActive auction = auctions.getFirst();
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
        LoggerFactory.getLogger(AuctionFetcherServiceTest.class).info("Expect logging of a com.fasterxml.jackson.core.JsonParseException. You can ignore this error");
        List<AuctionActive> auctions = fetcherService.fetchActiveAuctions();

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
              "item_bytes": "H4sIAAAAAAAA/1VVz28bRRR+rt3EdlsKLRJCKmiAFqVKYxwnsUkPSK7jxIa0RbGbFCG0mt2d2KPs7qx2ZlPnyJUzlRCiN6QgLtw4cYrE/4HyhyC+2bUdY6288+Z978e89+bbKlGFCrJKRIVrdE36hXcLdL2j0sgUqlQ0fFShkoi8MdlfgW68iNxE8BPuBqJQpEpP+mI34CMN7b9VWvaljgN+BqN9lYgydhl9cHHe2uEhH4nH7OLcW23UG7UmVmJldaP+kBpQD0wiopEZ54CN7a3a+hyAxfYKjJp25a+srm8+pPdg00mkYYt+W/UH9KFFfwvc5ZvvsfpuLv78oxWRzx6ki/Mg/x+oNGBdbkTCDqF/ZOFPeCSYOmbtxIwTFStfs8P+TNkZ8zCWKmIvqWbFQPBTcWVrc5IeDxYtUlfqkB3SBoSdhMuI9ef4buQj9AAVsxnMbboT4aUGfumLTIpFItEEsWC5KxPB2joWnmH9/txyT/LIsK9kEGQOaQt7/TDmgYxGi7h9YcbYNGcLUfeVMhns0PZkez/1Tha0Aw8njUaLlRqMeRJHQtsC9emx3QmlTfsqznCc2iMGKvEXfA0TGQdiDV2XJ/ZQ9BE2D21lk6xWc+ChiFSoUgRA5+6h6T2B4qLZW/XLN79i0UJp2FiaGrXslOVavtrYqj+wWj8bD2YUhMY/f71mL5AN960qVK7Gq0ab+MezZztzcb6xurl1+dMfrKNClxt2JLWvQqgZHwGgzdQR3cH7/85qSPH+xXmz7Upb2MdsiPF5xewcY9IO+nu9Ievs9ztf0SewyJVnKk1YjweuSPxHLMvWdoAHAX0GkIhEKIVmyEwazWJuxkCJvJuownr9gU0tFIEQtDI/bw3ZR9qOkMRw2il+pZmnsuS3GxNawytLGiMU8ogzHvmZW+uzMWEzXe6NPoaMaYngSbA4EacSHakB9flKyCdsvTl5SHchPbWuOghjr+NGo456vINIRxJ2ifDZExWlmj61tU4wpTrrlL3nHi4rm1HAtKn0ftYWr8MN99ALi24F4lQEts6rs0t8wi/OE5ZLT78Z9vodNjh6frDDrgBlKj3joaAmtua5TGtu77k91WCMTV/YXjYvf/l98aEq3e5OTMLbBuPq4lrqIt1OOG76mZPGo4T7wrIf2PDtsTJOrAw3yvEshWL7dpVKIxHqMpW/bA++7h44dSrt9p91sdF5/vRJezjbqNBbaRQo70T4jg6U0ZY9ry2YzfFlujlbOvBNSzmEluiON+Unx8vm15nE7dMjPfjh7z8RL1S+PJaobfnVtA5FujU9gpPVFhGvl+1ngO62X3ad57vOsNd1Br2D7s5Od6dKN+2nAL0LBfpXpLKcUgvsikW644I6HXXs8Dl1QrFUpFIALsmXZW9Kkbm45GUMauMWaTnIGQhSqUjXtSUTrJcB02fxWEW54m4aGBmCtB0N+naEpe/cQUXPSCqXkS7IB9W0/JoHvHEM4nR4Rpx51lUx59fcf+V0xkS5ybLI6Xjqc2Qp1jnJKHZ6ptMpTeWIWyajN0dn9Jb7vGGuiDA3qugZe06P6GUfitxFedZHiFVggxldW1vCRKcpenRfeJsNt9lYXxPuMV/bXG/V19wW99fEdoM3/a3m8YbbLFEF1RLawCGm9PVv92q7dq6W8k+n/az/B5EqnO8FCAAA"
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
              "item_bytes": "H4sIAAAAAAAA/1VVz28bRRR+rt3EdlsKLRJCKmiAFqVKYxwnsUkPSK7jxIa0RbGbFCG0mt2d2KPs7qx2ZlPnyJUzlRCiN6QgLtw4cYrE/4HyhyC+2bUdY6288+Z978e89+bbKlGFCrJKRIVrdE36hXcLdL2j0sgUqlQ0fFShkoi8MdlfgW68iNxE8BPuBqJQpEpP+mI34CMN7b9VWvaljgN+BqN9lYgydhl9cHHe2uEhH4nH7OLcW23UG7UmVmJldaP+kBpQD0wiopEZ54CN7a3a+hyAxfYKjJp25a+srm8+pPdg00mkYYt+W/UH9KFFfwvc5ZvvsfpuLv78oxWRzx6ki/Mg/x+oNGBdbkTCDqF/ZOFPeCSYOmbtxIwTFStfs8P+TNkZ8zCWKmIvqWbFQPBTcWVrc5IeDxYtUlfqkB3SBoSdhMuI9ef4buQj9AAVsxnMbboT4aUGfumLTIpFItEEsWC5KxPB2joWnmH9/txyT/LIsK9kEGQOaQt7/TDmgYxGi7h9YcbYNGcLUfeVMhns0PZkez/1Tha0Aw8njUaLlRqMeRJHQtsC9emx3QmlTfsqznCc2iMGKvEXfA0TGQdiDV2XJ/ZQ9BE2D21lk6xWc+ChiFSoUgRA5+6h6T2B4qLZW/XLN79i0UJp2FiaGrXslOVavtrYqj+wWj8bD2YUhMY/f71mL5AN960qVK7Gq0ab+MezZztzcb6xurl1+dMfrKNClxt2JLWvQqgZHwGgzdQR3cH7/85qSPH+xXmz7Upb2MdsiPF5xewcY9IO+nu9Ievs9ztf0SewyJVnKk1YjweuSPxHLMvWdoAHAX0GkIhEKIVmyEwazWJuxkCJvJuownr9gU0tFIEQtDI/bw3ZR9qOkMRw2il+pZmnsuS3GxNawytLGiMU8ogzHvmZW+uzMWEzXe6NPoaMaYngSbA4EacSHakB9flKyCdsvTl5SHchPbWuOghjr+NGo456vINIRxJ2ifDZExWlmj61tU4wpTrrlL3nHi4rm1HAtKn0ftYWr8MN99ALi24F4lQEts6rs0t8wi/OE5ZLT78Z9vodNjh6frDDrgBlKj3joaAmtua5TGtu77k91WCMTV/YXjYvf/l98aEq3e5OTMLbBuPq4lrqIt1OOG76mZPGo4T7wrIf2PDtsTJOrAw3yvEshWL7dpVKIxHqMpW/bA++7h44dSrt9p91sdF5/vRJezjbqNBbaRQo70T4jg6U0ZY9ry2YzfFlujlbOvBNSzmEluiON+Unx8vm15nE7dMjPfjh7z8RL1S+PJaobfnVtA5FujU9gpPVFhGvl+1ngO62X3ad57vOsNd1Br2D7s5Od6dKN+2nAL0LBfpXpLKcUgvsikW644I6HXXs8Dl1QrFUpFIALsmXZW9Kkbm45GUMauMWaTnIGQhSqUjXtSUTrJcB02fxWEW54m4aGBmCtB0N+naEpe/cQUXPSCqXkS7IB9W0/JoHvHEM4nR4Rpx51lUx59fcf+V0xkS5ybLI6Xjqc2Qp1jnJKHZ6ptMpTeWIWyajN0dn9Jb7vGGuiDA3qugZe06P6GUfitxFedZHiFVggxldW1vCRKcpenRfeJsNt9lYXxPuMV/bXG/V19wW99fEdoM3/a3m8YbbLFEF1RLawCGm9PVv92q7dq6W8k+n/az/B5EqnO8FCAAA"
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

        List<AuctionActive> auctions = fetcherService.fetchActiveAuctions();

        assertNotNull(auctions);
        assertEquals(2, auctions.size());
    }

    // TODO test auction ended
}
