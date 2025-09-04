package io.github.jaron2668.skyblockupdater.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.jaron2668.skyblockupdater.model.Auction;
import io.github.jaron2668.skyblockupdater.model.Enchantment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * TODO: finish
 */
public class AttributeParser {

    private static final Pattern ART_OF_WAR_PATTERN = Pattern.compile("(?i)^Art of War$");
    private static final Pattern ART_OF_PEACE_PATTERN = Pattern.compile("(?i)^Art of Peace$");
    private static final Pattern RECOMB_PATTERN = Pattern.compile("(?i)(RECOMBOBULATED)$");
    private static final Pattern BRACKET_STAT_PATTERN = Pattern.compile("\\(\\+?(\\d+)\\)");
    private static final String ENCHANT_REGEX = ".*(I{1,3}|IV|V|VI|VII|VIII|IX|X)$";

    public static void parseAttributes(Auction auction, JsonNode auctionJson) {
        List<String> lore = extractLore(auctionJson);
        String name = auction.getItemName();

        auction.setImportantEnchantments(extractEnchantments(lore));
        auction.setDungeonStars(countStars(name)); // maybe name instead have to see actual api responses later
        auction.setReforge(extractReforge(name));
        auction.setRarity(auctionJson.get("tier").asText());

        int[] counts = countPotatioBooks(lore);
        auction.setHpbCount(counts[0]);
        auction.setFpbCount(counts[1]);

        auction.setHasArtOfWar(hasArtOfWar(lore));
        auction.setHasArtOfPeace(hasArtOfPeace(lore));
        auction.setRecombobulated(isRecombobulated(lore));
    }

    @SuppressWarnings("unchecked") /*for empty list to string list cast*/
    private static List<String> extractLore(JsonNode auctionJson) {
        JsonNode loreArray = auctionJson.path("item_lore").path("lore");

        if(!loreArray.isArray()) {
            return (List<String>)Collections.EMPTY_LIST;
        }

        List<String> loreLines = new ArrayList<>();
        for (JsonNode elem : loreArray) {
            loreLines.add(elem.asText());
        }
        return loreLines;
    }

    private static List<Enchantment> extractEnchantments(List<String> lore) {
        List<Enchantment> enchantments = new ArrayList<>();
        for (String line : lore) {
            if(line.matches(ENCHANT_REGEX)) {
                // TODO: check if enchant is important and add to list
            }
        }
        return enchantments;
    }

    private static int countStars(String name) {
        int count = 0;
        for (char c : name.toCharArray()) {
            // TODO: actually count stars
        }
        return count;
    }

    private static int[] countPotatioBooks(List<String> lore) {
        int hpb = 0;
        int fpb = 0;
        //TODO: count

        return new int[]{hpb,fpb};
    }

    private static boolean hasArtOfWar(List<String> lore) {
        for (String line : lore) {
            if(ART_OF_WAR_PATTERN.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasArtOfPeace(List<String> lore) {
        for (String line : lore) {
            if(ART_OF_PEACE_PATTERN.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRecombobulated(List<String> lore) {
        for (String line : lore) {
            if(RECOMB_PATTERN.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }

    private static String extractReforge(String name) {
        if(name.contains(" ")) {
            return name.split(" ")[0];
        }
        return "";
    }

    public static UUID parseHypixelUuid(String hypixelUuid) {
        return UUID.fromString(
                hypixelUuid.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                        "$1-$2-$3-$4-$5"
                )
        );
    }

}
