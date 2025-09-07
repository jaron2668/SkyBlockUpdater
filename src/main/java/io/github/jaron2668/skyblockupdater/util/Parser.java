package io.github.jaron2668.skyblockupdater.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaron2668.skyblockupdater.model.*;
import io.github.jaron2668.skyblockupdater.model.Enchantment.EnchantmentType;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.tag.*;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class Parser {

    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);

    /**
     * A map of enchantments considered important.
     * <p>
     * The key is the NBT name of the enchantment (as a {@link String}),
     * and the value is an {@link Enchantment} object containing the enchantment type
     * and the minimum level for it to be considered important.
     */
    private static final HashMap<String, Enchantment> IMPORTANT_ENCHANTMENTS = new HashMap<>();

    static {
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ABSORB.getNbtName(), new Enchantment(EnchantmentType.ABSORB, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ANGLER.getNbtName(), new Enchantment(EnchantmentType.ANGLER, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.BANE_OF_ARTHROPODS.getNbtName(), new Enchantment(EnchantmentType.BANE_OF_ARTHROPODS, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.BIG_BRAIN.getNbtName(), new Enchantment(EnchantmentType.BIG_BRAIN, 3));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.BLAST_PROTECTION.getNbtName(), new Enchantment(EnchantmentType.BLAST_PROTECTION, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.BLESSING.getNbtName(), new Enchantment(EnchantmentType.BLESSING, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CASTER.getNbtName(), new Enchantment(EnchantmentType.CASTER, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CAYENNE.getNbtName(), new Enchantment(EnchantmentType.CAYENNE, 4));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CHAMPION.getNbtName(), new Enchantment(EnchantmentType.CHAMPION, 1));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CHANCE.getNbtName(), new Enchantment(EnchantmentType.CHANCE,4)); level 4 is just a few k
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CHANCE.getNbtName(), new Enchantment(EnchantmentType.CHANCE, 5));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CHARM.getNbtName(), new Enchantment(EnchantmentType.CHARM,1)); early levels very cheap (
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CHARM.getNbtName(), new Enchantment(EnchantmentType.CHARM, 3));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CLEAVE.getNbtName(), new Enchantment(EnchantmentType.CLEAVE, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.COMPACT.getNbtName(), new Enchantment(EnchantmentType.COMPACT, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CORRUPTION.getNbtName(), new Enchantment(EnchantmentType.CORRUPTION, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.COUNTER_STRIKE.getNbtName(), new Enchantment(EnchantmentType.COUNTER_STRIKE, 3));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CRITICAL.getNbtName(), new Enchantment(EnchantmentType.CRITICAL,6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CRITICAL.getNbtName(), new Enchantment(EnchantmentType.CRITICAL, 7));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CUBISM.getNbtName(), new Enchantment(EnchantmentType.CUBISM, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CULTIVATING.getNbtName(), new Enchantment(EnchantmentType.CULTIVATING, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.DEDICATION.getNbtName(), new Enchantment(EnchantmentType.DEDICATION, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.DELICATE.getNbtName(), new Enchantment(EnchantmentType.DELICATE, 5));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.DIVINE_GIFT.getNbtName(), new Enchantment(EnchantmentType.DIVINE_GIFT, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.DRAGON_HUNTER.getNbtName(), new Enchantment(EnchantmentType.DRAGON_HUNTER, 3));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.DRAIN.getNbtName(), new Enchantment(EnchantmentType.DRAIN, 4));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ENDER_SLAYER.getNbtName(), new Enchantment(EnchantmentType.ENDER_SLAYER, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.EXECUTE.getNbtName(), new Enchantment(EnchantmentType.EXECUTE, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.EXPERIENCE.getNbtName(), new Enchantment(EnchantmentType.EXPERIENCE, 4));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.EXPERTISE.getNbtName(), new Enchantment(EnchantmentType.EXPERTISE, 1));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FEATHER_FALLING.getNbtName(), new Enchantment(EnchantmentType.FEATHER_FALLING,6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FEATHER_FALLING.getNbtName(), new Enchantment(EnchantmentType.FEATHER_FALLING, 10));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FEROCIOUS_MANA.getNbtName(), new Enchantment(EnchantmentType.FEROCIOUS_MANA, 1));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FIRE_ASPECT.getNbtName(), new Enchantment(EnchantmentType.FIRE_ASPECT,3));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FIRE_PROTECTION.getNbtName(), new Enchantment(EnchantmentType.FIRE_PROTECTION,6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FIRE_PROTECTION.getNbtName(), new Enchantment(EnchantmentType.FIRE_PROTECTION, 7));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FIRST_STRIKE.getNbtName(), new Enchantment(EnchantmentType.FIRST_STRIKE, 5));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FOREST_PLEDGE.getNbtName(), new Enchantment(EnchantmentType.FOREST_PLEDGE, 3));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FORTUNE.getNbtName(), new Enchantment(EnchantmentType.FORTUNE, 4));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FRAIL.getNbtName(), new Enchantment(EnchantmentType.FRAIL,6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.GIANT_KILLER.getNbtName(), new Enchantment(EnchantmentType.GIANT_KILLER, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.GRAVITY.getNbtName(), new Enchantment(EnchantmentType.GRAVITY, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.GREEN_THUMB.getNbtName(), new Enchantment(EnchantmentType.GREEN_THUMB, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.GROWTH.getNbtName(), new Enchantment(EnchantmentType.GROWTH, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.HARDENED_MANA.getNbtName(), new Enchantment(EnchantmentType.HARDENED_MANA, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.HARVESTING.getNbtName(), new Enchantment(EnchantmentType.HARVESTING, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.HECATOMB.getNbtName(), new Enchantment(EnchantmentType.HECATOMB, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ICE_COLD.getNbtName(), new Enchantment(EnchantmentType.ICE_COLD, 1));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.INFINITE_QUIVER.getNbtName(), new Enchantment(EnchantmentType.INFINITE_QUIVER,6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.INFINITE_QUIVER.getNbtName(), new Enchantment(EnchantmentType.INFINITE_QUIVER, 10));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LAPIDARY.getNbtName(), new Enchantment(EnchantmentType.LAPIDARY, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LETHALITY.getNbtName(), new Enchantment(EnchantmentType.LETHALITY, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LIFE_STEAL.getNbtName(), new Enchantment(EnchantmentType.LIFE_STEAL, 4));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LOOTING.getNbtName(), new Enchantment(EnchantmentType.LOOTING, 4));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LUCK.getNbtName(), new Enchantment(EnchantmentType.LUCK,6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LUCK.getNbtName(), new Enchantment(EnchantmentType.LUCK, 7));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LUCK_OF_THE_SEA.getNbtName(), new Enchantment(EnchantmentType.LUCK_OF_THE_SEA, 6));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LURE.getNbtName(), new Enchantment(EnchantmentType.LURE,6));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.MAGNET.getNbtName(), new Enchantment(EnchantmentType.MAGNET,6));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.MANA_STEAL.getNbtName(), new Enchantment(EnchantmentType.MANA_STEAL,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.MANA_STEAL.getNbtName(), new Enchantment(EnchantmentType.MANA_STEAL, 3));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.MANA_VAMPIRE.getNbtName(), new Enchantment(EnchantmentType.MANA_VAMPIRE, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.OVERLOAD.getNbtName(), new Enchantment(EnchantmentType.OVERLOAD, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PALEONTOLOGIST.getNbtName(), new Enchantment(EnchantmentType.PALEONTOLOGIST, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PESTERMINATOR.getNbtName(), new Enchantment(EnchantmentType.PESTERMINATOR, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PISCARY.getNbtName(), new Enchantment(EnchantmentType.PISCARY, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.POWER.getNbtName(), new Enchantment(EnchantmentType.POWER, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PRISMATIC.getNbtName(), new Enchantment(EnchantmentType.PRISMATIC, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PROJECTILE_PROTECTION.getNbtName(), new Enchantment(EnchantmentType.PROJECTILE_PROTECTION, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PROSECUTE.getNbtName(), new Enchantment(EnchantmentType.PROSECUTE, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PROSPERITY.getNbtName(), new Enchantment(EnchantmentType.PROSPERITY, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PROTECTION.getNbtName(), new Enchantment(EnchantmentType.PROTECTION, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.QUANTUM.getNbtName(), new Enchantment(EnchantmentType.QUANTUM, 3));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.QUICK_BITE.getNbtName(), new Enchantment(EnchantmentType.QUICK_BITE, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.REFLECTION.getNbtName(), new Enchantment(EnchantmentType.REFLECTION, 1));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.REJUVENATE.getNbtName(), new Enchantment(EnchantmentType.REJUVENATE,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.REJUVENATE.getNbtName(), new Enchantment(EnchantmentType.REJUVENATE, 5));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.REPLENISH.getNbtName(), new Enchantment(EnchantmentType.REPLENISH, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.RESPIRATION.getNbtName(), new Enchantment(EnchantmentType.RESPIRATION, 4));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.RESPITE.getNbtName(), new Enchantment(EnchantmentType.RESPITE,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.RESPITE.getNbtName(), new Enchantment(EnchantmentType.RESPITE, 4));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SCAVENGER.getNbtName(), new Enchantment(EnchantmentType.SCAVENGER,4));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SCAVENGER.getNbtName(), new Enchantment(EnchantmentType.SCAVENGER, 5));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SCUBA.getNbtName(), new Enchantment(EnchantmentType.SCUBA, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SHARPNESS.getNbtName(), new Enchantment(EnchantmentType.SHARPNESS, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SMALL_BRAIN.getNbtName(), new Enchantment(EnchantmentType.SMALL_BRAIN, 3));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SMARTY_PANTS.getNbtName(), new Enchantment(EnchantmentType.SMARTY_PANTS, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SMITE.getNbtName(), new Enchantment(EnchantmentType.SMITE, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SMOLDERING.getNbtName(), new Enchantment(EnchantmentType.SMOLDERING, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SNIPE.getNbtName(), new Enchantment(EnchantmentType.SNIPE, 4));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SPIKED_HOOK.getNbtName(), new Enchantment(EnchantmentType.SPIKED_HOOK,6));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.STEALTH.getNbtName(), new Enchantment(EnchantmentType.STEALTH,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.STRONG_MANA.getNbtName(), new Enchantment(EnchantmentType.STRONG_MANA, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SUGAR_RUSH.getNbtName(), new Enchantment(EnchantmentType.SUGAR_RUSH, 1));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SUNDER.getNbtName(), new Enchantment(EnchantmentType.SUNDER,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SUNDER.getNbtName(), new Enchantment(EnchantmentType.SUNDER, 5));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SYPHON.getNbtName(), new Enchantment(EnchantmentType.SYPHON, 4));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TABASCO.getNbtName(), new Enchantment(EnchantmentType.TABASCO, 2));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.THUNDERBOLT.getNbtName(), new Enchantment(EnchantmentType.THUNDERBOLT,6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.THUNDERBOLT.getNbtName(), new Enchantment(EnchantmentType.THUNDERBOLT, 7));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.THUNDERLORD.getNbtName(), new Enchantment(EnchantmentType.THUNDERLORD,6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.THUNDERLORD.getNbtName(), new Enchantment(EnchantmentType.THUNDERLORD, 7));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TIDAL.getNbtName(), new Enchantment(EnchantmentType.TIDAL, 1));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TITAN_KILLER.getNbtName(), new Enchantment(EnchantmentType.TITAN_KILLER,6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TITAN_KILLER.getNbtName(), new Enchantment(EnchantmentType.TITAN_KILLER, 7));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TOXOPHILITE.getNbtName(), new Enchantment(EnchantmentType.TOXOPHILITE, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TRANSYLVANIAN.getNbtName(), new Enchantment(EnchantmentType.TRANSYLVANIAN, 4));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TRIPLE_STRIKE.getNbtName(), new Enchantment(EnchantmentType.TRIPLE_STRIKE, 5));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TRUE_PROTECTION.getNbtName(), new Enchantment(EnchantmentType.TRUE_PROTECTION, 1));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_CACTI.getNbtName(), new Enchantment(EnchantmentType.TURBO_CACTI,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_CACTI.getNbtName(), new Enchantment(EnchantmentType.TURBO_CACTI, 5));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_CANE.getNbtName(), new Enchantment(EnchantmentType.TURBO_CANE,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_CANE.getNbtName(), new Enchantment(EnchantmentType.TURBO_CANE, 5));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_CARROT.getNbtName(), new Enchantment(EnchantmentType.TURBO_CARROT,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_CARROT.getNbtName(), new Enchantment(EnchantmentType.TURBO_CARROT, 5));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_COCOA.getNbtName(), new Enchantment(EnchantmentType.TURBO_COCOA,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_COCOA.getNbtName(), new Enchantment(EnchantmentType.TURBO_COCOA, 5));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_MELON.getNbtName(), new Enchantment(EnchantmentType.TURBO_MELON,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_MELON.getNbtName(), new Enchantment(EnchantmentType.TURBO_MELON, 5));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_MUSHROOMS.getNbtName(), new Enchantment(EnchantmentType.TURBO_MUSHROOMS,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_MUSHROOMS.getNbtName(), new Enchantment(EnchantmentType.TURBO_MUSHROOMS, 5));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_POTATO.getNbtName(), new Enchantment(EnchantmentType.TURBO_POTATO,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_POTATO.getNbtName(), new Enchantment(EnchantmentType.TURBO_POTATO, 5));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_PUMPKIN.getNbtName(), new Enchantment(EnchantmentType.TURBO_PUMPKIN,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_PUMPKIN.getNbtName(), new Enchantment(EnchantmentType.TURBO_PUMPKIN, 5));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_WARTS.getNbtName(), new Enchantment(EnchantmentType.TURBO_WARTS,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_WARTS.getNbtName(), new Enchantment(EnchantmentType.TURBO_WARTS, 5));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_WHEAT.getNbtName(), new Enchantment(EnchantmentType.TURBO_WHEAT,1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_WHEAT.getNbtName(), new Enchantment(EnchantmentType.TURBO_WHEAT, 5));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.VAMPIRISM.getNbtName(), new Enchantment(EnchantmentType.VAMPIRISM, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.VENOMOUS.getNbtName(), new Enchantment(EnchantmentType.VENOMOUS, 6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.VICIOUS.getNbtName(), new Enchantment(EnchantmentType.VICIOUS, 3));
        //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.WOODSPLITTER.getNbtName(), new Enchantment(EnchantmentType.WOODSPLITTER,6));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_BANK.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_BANK, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_BOBBIN_TIME.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_BOBBIN_TIME, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_CHIMERA.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_CHIMERA, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_COMBO.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_COMBO, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_DUPLEX.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_DUPLEX, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_FATAL_TEMPO.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_FATAL_TEMPO, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_FIRST_IMPRESSION.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_FIRST_IMPRESSION, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_FLASH.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_FLASH, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_FLOWSTATE.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_FLOWSTATE, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_HABANERO_TACTICS.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_HABANERO_TACTICS, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_INFERNO.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_INFERNO, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_LAST_STAND.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_LAST_STAND, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_LEGION.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_LEGION, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_MISSILE.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_MISSILE, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_NO_PAIN_NO_GAIN.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_NO_PAIN_NO_GAIN, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_ONE_FOR_ALL.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_ONE_FOR_ALL, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_REFRIGERATE.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_REFRIGERATE, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_REND.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_REND, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_SOUL_EATER.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_SOUL_EATER, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_SWARM.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_SWARM, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_THE_ONE.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_THE_ONE, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_ULTIMATE_JERRY.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_ULTIMATE_JERRY, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_ULTIMATE_WISE.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_ULTIMATE_WISE, 1));
        IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_WISDOM.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_WISDOM, 1));
    }

    /**
     * Parses a JSON representation of an active auction and converts it into an {@link AuctionActive} object.
     * <p>
     * The method extracts auction details such as UUID, start and end times, starting price,
     * and the item being auctioned from the provided JSON node.
     *
     * @param auctionJson the JSON node containing the active auction data
     * @return an {@link AuctionActive} object populated with data parsed from the JSON or null if unable to parse item
     */
    public static AuctionActive parseActiveAuction(JsonNode auctionJson) {
        AuctionActive auction = new AuctionActive();

        auction.setUuid(Parser.parseHypixelUuid(auctionJson.get("uuid").asText()));
        auction.setStartTime(Instant.ofEpochMilli(auctionJson.get("start").asLong()));
        auction.setEndTime(Instant.ofEpochMilli(auctionJson.get("end").asLong()));
        auction.setPrice(auctionJson.get("starting_bid").asLong());

        Item item = createItemFromBytes(auctionJson.get("item_bytes").asText());
        if (item == null) {
            LOG.error("Couldn't create item for active auction from json: {}", auctionJson);
            return null;
        }

        item.setRarity(auctionJson.get("tier").asText());


        auction.setItem(item);
        auction.setItemId(item.getItemId());

        return auction;
    }

    /**
     * Parses a JSON representation of an ended auction and converts it into an {@link AuctionEnded} object.
     * <p>
     * The method extracts auction details such as UUID, end time, final price,
     * and the item sold from the provided JSON node.
     * Note that the rarity of the item is not included in the API response for ended auctions.
     *
     * @param auctionJson the JSON node containing the ended auction data
     * @return an {@link AuctionEnded} object populated with data parsed from the JSON or null if unable to parse item
     */
    public static AuctionEnded parseEndedAuction(JsonNode auctionJson) {
        AuctionEnded auction = new AuctionEnded();

        auction.setUuid(Parser.parseHypixelUuid(auctionJson.get("auction_id").asText()));
        auction.setTimeEnded(Instant.ofEpochMilli(auctionJson.get("timestamp").asLong()));
        auction.setPrice(auctionJson.get("price").asLong());

        Item item = createItemFromBytes(auctionJson.get("item_bytes").asText());
        if (item == null) {
            LOG.error("Couldn't create item for ended auction from json: {}", auctionJson);
            return null;
        }
        //item.setRarity(auctionJson.get("tier").asText()); rarity not it api result
        if (item.getRarity() == null)
            item.setRarity("");


        auction.setItem(item);
        auction.setItemId(item.getItemId());

        auction.setWasBin(auctionJson.has("bin") && auctionJson.get("bin").asBoolean());
        auction.setWasBought(auctionJson.has("buyer") && !auctionJson.get("buyer").asText().isBlank());

        return auction;
    }


    /**
     * Creates a {@link Item} or subtypes with attributes:
     * {@link Item#setItemId(String)}, {@link Item#setEnchantments(List)},
     * {@link Item#setGemstones(List)}, {@link ToolItem#setUpgradeLevel(int)},
     * {@link ToolItem#setReforge(String)}, {@link ToolItem#setHotPotatoCount(int)},
     * {@link ToolItem#setRarityUpgrades(int)}, {@link WeaponItem#setArtOfWarCount(int)},
     * {@link ArmorItem#setArtOfPeaceCount(int)}, {@link PetItem#setLevel(int)},
     * {@link PetItem#setCandyCount(int)}, {@link PetItem#setPetItem(String)} based on the (base64 + gzip) item bytes.
     *
     * @param itemBytes The item bytes to use for the creation
     * @return {@link Item} (or subtype) with said attributes or null if something fails
     */
    public static Item createItemFromBytes(String itemBytes) {
        try {
            CompoundTag rootTag = decodeItemBytes(itemBytes);
            ListTag<Tag<?>> nbtList = (ListTag<Tag<?>>) rootTag.getListTag("i");
            if (nbtList == null || nbtList.size() == 0) {
                LOG.warn("NBT list is empty for parsed bytes. Returning null.");
                return null;
            }

            List<String> processedExtraAttributes = new ArrayList<>();

            CompoundTag itemTag = (CompoundTag) nbtList.get(0);
            CompoundTag tag = itemTag.getCompoundTag("tag");
            CompoundTag extra = tag.getCompoundTag("ExtraAttributes");

            Item item;
            // Determine Type
            if (extra.containsKey("art_of_war_count")) {
                item = new WeaponItem();
            } else if (extra.containsKey("art_of_peace_count")) {
                item = new ArmorItem();
            } else if (extra.containsKey("upgrade_level") || extra.containsKey("modifier") || extra.containsKey("hot_potato_count") || extra.containsKey("rarity_upgrades")) {
                item = new ToolItem();
            } else if (extra.containsKey("petInfo")) {
                item = new PetItem();
            } else {
                item = new Item();
            }


            // UUID
            try {
                item.setUuid(UUID.fromString(extra.getString("uuid")));
            } catch (IllegalArgumentException e) {
                LOG.debug("Attributes don't contain a uuid. This happens for some consumables/stackable items. Creating a new one and pray it doesn't exist for this item type in db.");
                item.setUuid(UUID.randomUUID());
            }
            processedExtraAttributes.add("uuid");
            // ItemId
            item.setItemId(extra.getString("id"));
            processedExtraAttributes.add("id");
            // Item Bytes
            item.setItemBytes(itemBytes);

            // Enchantments
            CompoundTag enchants = extra.getCompoundTag("enchantments");
            processedExtraAttributes.add("enchantments");
            List<Enchantment> enchantments = new ArrayList<>();
            if (enchants != null) {
                for (Map.Entry<String, Tag<?>> entry : enchants.entrySet()) {
                    if (IMPORTANT_ENCHANTMENTS.containsKey(entry.getKey())) {
                        Enchantment ench = IMPORTANT_ENCHANTMENTS.get(entry.getKey());
                        EnchantmentType type = ench.type();
                        if (((IntTag) entry.getValue()).asInt() < ench.level())
                            continue;

                        enchantments.add(new Enchantment(type, enchants.getInt(entry.getKey())));
                    }
                }
            }
            item.setEnchantments(enchantments);


            // TODO breaks because gems sometimes look like this - hopefully fixed now
            /*
                "gems": {
                                            "type": "CompoundTag",
                                            "value": {
                                                "JASPER_0": { "type": "StringTag", "value": "FINE" },
                                                "COMBAT_0": { "type": "StringTag", "value": "FINE" },
                                                "unlocked_slots": {
                                                    "type": "ListTag",
                                                    "value": {
                                                        "type": "StringTag",
                                                        "list": ["JASPER_0", "COMBAT_0"]
                                                    }
                                                },
                                                "COMBAT_0_gem": { "type": "StringTag", "value": "JASPER" }
                                            }
                                        }
             */ // and sometimes like this - thank you hypixel
            /*
            "gems":{
                                            "type":"CompoundTag", "value":{
                                                "JASPER_0":{
                                                    "type":"CompoundTag", "value":{
                                                        "uuid":{
                                                            "type":"StringTag", "value":
                                                            "11b428d7-642b-4d09-94fb-184f3fd5e4db"
                                                        },"quality":{
                                                            "type":"StringTag", "value":"FLAWLESS"
                                                        }
                                                    }
                                                },"unlocked_slots":{
                                                    "type":"ListTag", "value":{
                                                        "type":"StringTag", "list":["JASPER_0"]}
                                                }
                                            }
            */
            // Gemstones
            List<GemstoneSlot> gemstones = new ArrayList<>();
            CompoundTag gems = extra.getCompoundTag("gems");
            processedExtraAttributes.add("gems");
            if (gems != null) {
                ListTag<StringTag> unlockedSlots = (ListTag<StringTag>) gems.getListTag("unlocked_slots");
                if (unlockedSlots != null) {
                    for (int i = 0; i < unlockedSlots.size(); i++) { // cursed shit because gems with purity >= FLAWLESS have an uuid which changes the structure and gem structure already sucks to process
                        String slotName = unlockedSlots.get(i).getValue();
                        String gemType;
                        if (gems.containsKey(slotName + "_gem")) {
                            gemType = gems.getString(slotName + "_gem");
                        } else {
                            gemType = slotName.split("_")[0];
                        }
                        String gemPurity = "";
                        Tag<?> slotTag = gems.get(slotName);
                        if (slotTag instanceof StringTag stag) {
                            gemPurity = stag.getValue();

                        } else if (slotTag instanceof CompoundTag ctag) {
                            gemPurity = ctag.getString("quality");
                        }
                        gemstones.add(new GemstoneSlot(slotName, gemPurity, gemType));
                    }
                }
            }
            item.setGemstones(gemstones);

            if (item instanceof ToolItem toolItem) {
                // Stars
                toolItem.setUpgradeLevel(extra.getInt("upgrade_level"));
                processedExtraAttributes.add("upgrade_level");
                // Reforge
                toolItem.setReforge(extra.getString("modifier"));
                processedExtraAttributes.add("modifier");
                // HPB/FPB
                toolItem.setHotPotatoCount(extra.getInt("hot_potato_count"));
                processedExtraAttributes.add("hot_potato_count");
                // Recombobulated
                toolItem.setRarityUpgrades(extra.getInt("rarity_upgrades"));
                processedExtraAttributes.add("rarity_upgrades");

                if (toolItem instanceof WeaponItem weaponItem) {
                    // Art of War
                    weaponItem.setArtOfWarCount(extra.getInt("art_of_war_count"));
                    processedExtraAttributes.add("art_of_war_count");
                } else if (toolItem instanceof ArmorItem armorItem) {
                    // Art of Peace
                    armorItem.setArtOfPeaceCount(extra.getInt("art_of_peace_count"));
                    processedExtraAttributes.add("art_of_peace_count");
                }
            } else if (item instanceof PetItem petItem) {
                String petInfo = extra.getStringTag("petInfo").getValue();
                processedExtraAttributes.add("petInfo");
                ObjectMapper mapper = new ObjectMapper();
                JsonNode petData = mapper.readTree(petInfo);

                String type = petData.path("type").asText();
                String rarity = petData.path("tier").asText();
                int candyUsed = petData.path("candyUsed").asInt();
                String heldItem = petData.path("heldItem").asText("");
                long exp = petData.path("exp").asLong();

                // Change ItemId to something more precise than just "PET" using this extra data (why would you give all pets just the id PET in the first place hypixel?)
                petItem.setItemId("PET_" + type);
                // When dealing with pets we can actually extract the rarity from here in case it was an ended auction (which won't show item rarities)
                petItem.setRarity(rarity);
                // Pet Level
                petItem.setLevel(calculatePetLevel(petItem.getItemId(), petItem.getRarity(), exp));
                // Pet Candy
                petItem.setCandyCount(candyUsed);
                // Current Pet Item
                petItem.setPetItem(heldItem);
            }

            // Special stuff - just save it to a string, might be useful to determine if item was exotic
            StringBuilder remainingTagDump = new StringBuilder();
            for (Map.Entry<String, Tag<?>> entry : extra.entrySet()) {
                String key = entry.getKey();

                if (processedExtraAttributes.contains(key))
                    continue;

                remainingTagDump.append(key)
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n");
            }

            item.setRemainingTagDump(remainingTagDump.toString());


            return item;
        } catch (Exception e) {
            LOG.error("Failed to parse item attributes. Returning null.", e);
            return null;
        }
    }

    private static final long[] expForLevel;
    private static final Map<String, Integer> rarityOffset;

    static {
        // exp needed for level-up, e.g. lvl2->3 takes expForLevel[2] exp. Just pray these values are correct
        expForLevel = new long[]{
                100, 110, 120, 130, 145, 160, 175, 190, 210, 230, 250, 275, 300, 330, 360, 400,
                440, 490, 540, 600, 660, 730, 800, 880, 960, 1050, 1150, 1260, 1380, 1510, 1650,
                1800, 1960, 2130, 2310, 2500, 2700, 2920, 3160, 3420, 3700, 4000, 4350, 4750,
                5200, 5700, 6300, 7000, 7800, 8700, 9700, 10800, 12000, 13300, 14700, 16200, 17800,
                19500, 21300, 23200, 25200, 27400, 29800, 32400, 35200, 38200, 41400, 44800, 48400,
                52200, 56200, 60400, 64800, 69400, 74200, 79200, 84700, 90700, 97200, 104200, 111700,
                119700, 128200, 137200, 146700, 156700, 167700, 179700, 192700, 206700, 221700, 237700,
                254700, 272700, 291700, 311700, 333700, 357700, 383700, 411700, 441700, 476700, 516700,
                561700, 611700, 666700, 726700, 791700, 861700, 936700, 1016700, 1101700, 1191700,
                1286700, 1386700, 1496700, 1616700, 1746700, 1886700
        };
        rarityOffset = Map.of(
                "common", 0,
                "uncommon", 6,
                "rare", 11,
                "epic", 16,
                "legendary", 20,
                "mythic", 20
        );
    }

    /**
     * Calculates the pet level based on the pet's ID, rarity, and accumulated experience points (EXP).
     * <p>
     * The leveling system works as follows:
     * <ul>
     *   <li>Levels 1 to 100 require varying amounts of EXP, determined by the pet's rarity offset.</li>
     *   <li>For pets other than the Golden Dragon ("PET_GOLDEN_DRAGON"), the level caps at 100.</li>
     *   <li>The Golden Dragon pet can level beyond 100, up to a maximum of 200.</li>
     *   <li>Level 101 requires a fixed amount of 5555 EXP.</li>
     *   <li>Levels 102 to 200 require a flat EXP cost equivalent to level 100's EXP requirement.</li>
     * </ul>
     *
     * @param petId  the unique identifier of the pet (e.g., "PET_GOLDEN_DRAGON")
     * @param rarity the rarity tier of the pet (case-insensitive), used to determine EXP offsets
     * @param exp    the total accumulated experience points for the pet
     * @return the current level of the pet based on the EXP provided, capped at 100 for non-Golden Dragon pets and 200 for the Golden Dragon
     */
    private static int calculatePetLevel(String petId, String rarity, long exp) {
        int offset = rarityOffset.getOrDefault(rarity.toLowerCase(), 0);
        int level = 1;
        long accumulated = 0;

        // Handle levels 1–100
        for (int i = 0; i < 99; i++) { // 99 level-ups between level 1 and 100
            long xpNeeded = expForLevel[offset + i];
            if (exp < accumulated + xpNeeded) {
                return level;
            }
            accumulated += xpNeeded;
            level++;
        }

        // If not gdrag, cap at 100 (I hope there don't exist another pet which I don't know)
        if (!petId.equals("PET_GOLDEN_DRAGON")) {
            return 100;
        }

        // Handle levels 101–200
        // Level 101: 5555 XP
        if (exp < accumulated + 5555) {
            return 100;
        }

        accumulated += 5555;
        level++;

        // Levels 102–200: flat XP cost same as level 100 (i.e. expForLevel[offset + 98])
        long flatXP = expForLevel[offset + 98];

        while (level < 200 && exp >= accumulated + flatXP) {
            accumulated += flatXP;
            level++;
        }

        return level;
    }

    /**
     * Decodes the item bytes and returns a {@link CompoundTag}
     *
     * @param base64 Base64 encoded item bytes
     * @return Root {@link CompoundTag} of the decoded bytes
     * @throws IOException if root tag is not a {@link CompoundTag}
     */
    private static CompoundTag decodeItemBytes(String base64) throws IOException {
        // Unescape any Unicode sequences first (sometimes breaks if I don't do this, don't really know what hypixel does with its encoding)
        String cleanBase64 = StringEscapeUtils.unescapeJava(base64).trim();

        // Decode Base64
        byte[] compressed = Base64.getDecoder().decode(cleanBase64);

        // Decompress GZIP
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
             NBTInputStream nis = new NBTInputStream(gis)) {
            Tag<?> tag = nis.readTag(Tag.DEFAULT_MAX_DEPTH).getTag();
            if (!(tag instanceof CompoundTag)) {
                throw new IOException("Root tag is not a CompoundTag");
            }
            return (CompoundTag) tag;
        }
    }

    /**
     * Transforms a Hypixel UUID (UUID String without dashes) into a {@link UUID}
     *
     * @param hypixelUuid the uuid to transform
     * @return the corresponding {@link UUID}
     */
    public static UUID parseHypixelUuid(String hypixelUuid) {
        return UUID.fromString(
                hypixelUuid.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                        "$1-$2-$3-$4-$5"
                )
        );
    }
}
