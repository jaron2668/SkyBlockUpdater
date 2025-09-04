package io.github.jaron2668.skyblockupdater.util;

import io.github.jaron2668.skyblockupdater.model.Auction;
import io.github.jaron2668.skyblockupdater.model.Enchantment;
import io.github.jaron2668.skyblockupdater.model.Enchantment.EnchantmentType;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.Tag;
import org.apache.commons.text.StringEscapeUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class AttributeParser {

    /**
     * Map of enchantments to consider important
     * @key String nbt name of the enchantment type
     * @value {@link Enchantment} with the enchantment type and min level to be considered important
     */
    private static final HashMap<String, Enchantment> IMPORTANT_ENCHANTMENTS = new HashMap<>();
    static {
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ABSORB.getNbtName(), new Enchantment(EnchantmentType.ABSORB,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ANGLER.getNbtName(), new Enchantment(EnchantmentType.ANGLER,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.BANE_OF_ARTHROPODS.getNbtName(), new Enchantment(EnchantmentType.BANE_OF_ARTHROPODS,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.BIG_BRAIN.getNbtName(), new Enchantment(EnchantmentType.BIG_BRAIN,3));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.BLAST_PROTECTION.getNbtName(), new Enchantment(EnchantmentType.BLAST_PROTECTION,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.BLESSING.getNbtName(), new Enchantment(EnchantmentType.BLESSING,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CASTER.getNbtName(), new Enchantment(EnchantmentType.CASTER,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CAYENNE.getNbtName(), new Enchantment(EnchantmentType.CAYENNE,4));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CHAMPION.getNbtName(), new Enchantment(EnchantmentType.CHAMPION,1));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CHANCE.getNbtName(), new Enchantment(EnchantmentType.CHANCE,4)); level 4 is just a few k
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CHANCE.getNbtName(), new Enchantment(EnchantmentType.CHANCE,5));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CHARM.getNbtName(), new Enchantment(EnchantmentType.CHARM,1)); early levels very cheap (
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CHARM.getNbtName(), new Enchantment(EnchantmentType.CHARM,3));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CLEAVE.getNbtName(), new Enchantment(EnchantmentType.CLEAVE,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.COMPACT.getNbtName(), new Enchantment(EnchantmentType.COMPACT,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CORRUPTION.getNbtName(), new Enchantment(EnchantmentType.CORRUPTION,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.COUNTER_STRIKE.getNbtName(), new Enchantment(EnchantmentType.COUNTER_STRIKE,3));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CRITICAL.getNbtName(), new Enchantment(EnchantmentType.CRITICAL,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CRITICAL.getNbtName(), new Enchantment(EnchantmentType.CRITICAL,7));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CUBISM.getNbtName(), new Enchantment(EnchantmentType.CUBISM,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.CULTIVATING.getNbtName(), new Enchantment(EnchantmentType.CULTIVATING,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.DEDICATION.getNbtName(), new Enchantment(EnchantmentType.DEDICATION,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.DELICATE.getNbtName(), new Enchantment(EnchantmentType.DELICATE,5));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.DIVINE_GIFT.getNbtName(), new Enchantment(EnchantmentType.DIVINE_GIFT,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.DRAGON_HUNTER.getNbtName(), new Enchantment(EnchantmentType.DRAGON_HUNTER,3));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.DRAIN.getNbtName(), new Enchantment(EnchantmentType.DRAIN,4));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ENDER_SLAYER.getNbtName(), new Enchantment(EnchantmentType.ENDER_SLAYER,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.EXECUTE.getNbtName(), new Enchantment(EnchantmentType.EXECUTE,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.EXPERIENCE.getNbtName(), new Enchantment(EnchantmentType.EXPERIENCE,4));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.EXPERTISE.getNbtName(), new Enchantment(EnchantmentType.EXPERTISE,1));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FEATHER_FALLING.getNbtName(), new Enchantment(EnchantmentType.FEATHER_FALLING,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FEATHER_FALLING.getNbtName(), new Enchantment(EnchantmentType.FEATHER_FALLING,10));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FEROCIOUS_MANA.getNbtName(), new Enchantment(EnchantmentType.FEROCIOUS_MANA,1));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FIRE_ASPECT.getNbtName(), new Enchantment(EnchantmentType.FIRE_ASPECT,3));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FIRE_PROTECTION.getNbtName(), new Enchantment(EnchantmentType.FIRE_PROTECTION,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FIRE_PROTECTION.getNbtName(), new Enchantment(EnchantmentType.FIRE_PROTECTION,7));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FIRST_STRIKE.getNbtName(), new Enchantment(EnchantmentType.FIRST_STRIKE,5));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FOREST_PLEDGE.getNbtName(), new Enchantment(EnchantmentType.FOREST_PLEDGE,3));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FORTUNE.getNbtName(), new Enchantment(EnchantmentType.FORTUNE,4));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.FRAIL.getNbtName(), new Enchantment(EnchantmentType.FRAIL,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.GIANT_KILLER.getNbtName(), new Enchantment(EnchantmentType.GIANT_KILLER,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.GRAVITY.getNbtName(), new Enchantment(EnchantmentType.GRAVITY,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.GREEN_THUMB.getNbtName(), new Enchantment(EnchantmentType.GREEN_THUMB,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.GROWTH.getNbtName(), new Enchantment(EnchantmentType.GROWTH,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.HARDENED_MANA.getNbtName(), new Enchantment(EnchantmentType.HARDENED_MANA,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.HARVESTING.getNbtName(), new Enchantment(EnchantmentType.HARVESTING,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.HECATOMB.getNbtName(), new Enchantment(EnchantmentType.HECATOMB,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ICE_COLD.getNbtName(), new Enchantment(EnchantmentType.ICE_COLD,1));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.INFINITE_QUIVER.getNbtName(), new Enchantment(EnchantmentType.INFINITE_QUIVER,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.INFINITE_QUIVER.getNbtName(), new Enchantment(EnchantmentType.INFINITE_QUIVER,10));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LAPIDARY.getNbtName(), new Enchantment(EnchantmentType.LAPIDARY,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LETHALITY.getNbtName(), new Enchantment(EnchantmentType.LETHALITY,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LIFE_STEAL.getNbtName(), new Enchantment(EnchantmentType.LIFE_STEAL,4));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LOOTING.getNbtName(), new Enchantment(EnchantmentType.LOOTING,4));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LUCK.getNbtName(), new Enchantment(EnchantmentType.LUCK,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LUCK.getNbtName(), new Enchantment(EnchantmentType.LUCK,7));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LUCK_OF_THE_SEA.getNbtName(), new Enchantment(EnchantmentType.LUCK_OF_THE_SEA,6));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.LURE.getNbtName(), new Enchantment(EnchantmentType.LURE,6));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.MAGNET.getNbtName(), new Enchantment(EnchantmentType.MAGNET,6));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.MANA_STEAL.getNbtName(), new Enchantment(EnchantmentType.MANA_STEAL,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.MANA_STEAL.getNbtName(), new Enchantment(EnchantmentType.MANA_STEAL,3));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.MANA_VAMPIRE.getNbtName(), new Enchantment(EnchantmentType.MANA_VAMPIRE,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.OVERLOAD.getNbtName(), new Enchantment(EnchantmentType.OVERLOAD,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PALEONTOLOGIST.getNbtName(), new Enchantment(EnchantmentType.PALEONTOLOGIST,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PESTERMINATOR.getNbtName(), new Enchantment(EnchantmentType.PESTERMINATOR,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PISCARY.getNbtName(), new Enchantment(EnchantmentType.PISCARY,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.POWER.getNbtName(), new Enchantment(EnchantmentType.POWER,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PRISMATIC.getNbtName(), new Enchantment(EnchantmentType.PRISMATIC,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PROJECTILE_PROTECTION.getNbtName(), new Enchantment(EnchantmentType.PROJECTILE_PROTECTION,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PROSECUTE.getNbtName(), new Enchantment(EnchantmentType.PROSECUTE,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PROSPERITY.getNbtName(), new Enchantment(EnchantmentType.PROSPERITY,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.PROTECTION.getNbtName(), new Enchantment(EnchantmentType.PROTECTION,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.QUANTUM.getNbtName(), new Enchantment(EnchantmentType.QUANTUM,3));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.QUICK_BITE.getNbtName(), new Enchantment(EnchantmentType.QUICK_BITE,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.REFLECTION.getNbtName(), new Enchantment(EnchantmentType.REFLECTION,1));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.REJUVENATE.getNbtName(), new Enchantment(EnchantmentType.REJUVENATE,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.REJUVENATE.getNbtName(), new Enchantment(EnchantmentType.REJUVENATE,5));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.REPLENISH.getNbtName(), new Enchantment(EnchantmentType.REPLENISH,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.RESPIRATION.getNbtName(), new Enchantment(EnchantmentType.RESPIRATION,4));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.RESPITE.getNbtName(), new Enchantment(EnchantmentType.RESPITE,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.RESPITE.getNbtName(), new Enchantment(EnchantmentType.RESPITE,4));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SCAVENGER.getNbtName(), new Enchantment(EnchantmentType.SCAVENGER,4));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SCAVENGER.getNbtName(), new Enchantment(EnchantmentType.SCAVENGER,5));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SCUBA.getNbtName(), new Enchantment(EnchantmentType.SCUBA,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SHARPNESS.getNbtName(), new Enchantment(EnchantmentType.SHARPNESS,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SMALL_BRAIN.getNbtName(), new Enchantment(EnchantmentType.SMALL_BRAIN,3));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SMARTY_PANTS.getNbtName(), new Enchantment(EnchantmentType.SMARTY_PANTS,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SMITE.getNbtName(), new Enchantment(EnchantmentType.SMITE,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SMOLDERING.getNbtName(), new Enchantment(EnchantmentType.SMOLDERING,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SNIPE.getNbtName(), new Enchantment(EnchantmentType.SNIPE,4));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SPIKED_HOOK.getNbtName(), new Enchantment(EnchantmentType.SPIKED_HOOK,6));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.STEALTH.getNbtName(), new Enchantment(EnchantmentType.STEALTH,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.STRONG_MANA.getNbtName(), new Enchantment(EnchantmentType.STRONG_MANA,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SUGAR_RUSH.getNbtName(), new Enchantment(EnchantmentType.SUGAR_RUSH,1));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SUNDER.getNbtName(), new Enchantment(EnchantmentType.SUNDER,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SUNDER.getNbtName(), new Enchantment(EnchantmentType.SUNDER,5));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.SYPHON.getNbtName(), new Enchantment(EnchantmentType.SYPHON,4));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TABASCO.getNbtName(), new Enchantment(EnchantmentType.TABASCO,2));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.THUNDERBOLT.getNbtName(), new Enchantment(EnchantmentType.THUNDERBOLT,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.THUNDERBOLT.getNbtName(), new Enchantment(EnchantmentType.THUNDERBOLT,7));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.THUNDERLORD.getNbtName(), new Enchantment(EnchantmentType.THUNDERLORD,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.THUNDERLORD.getNbtName(), new Enchantment(EnchantmentType.THUNDERLORD,7));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TIDAL.getNbtName(), new Enchantment(EnchantmentType.TIDAL,1));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TITAN_KILLER.getNbtName(), new Enchantment(EnchantmentType.TITAN_KILLER,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TITAN_KILLER.getNbtName(), new Enchantment(EnchantmentType.TITAN_KILLER,7));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TOXOPHILITE.getNbtName(), new Enchantment(EnchantmentType.TOXOPHILITE,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TRANSYLVANIAN.getNbtName(), new Enchantment(EnchantmentType.TRANSYLVANIAN,4));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TRIPLE_STRIKE.getNbtName(), new Enchantment(EnchantmentType.TRIPLE_STRIKE,5));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TRUE_PROTECTION.getNbtName(), new Enchantment(EnchantmentType.TRUE_PROTECTION,1));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_CACTI.getNbtName(), new Enchantment(EnchantmentType.TURBO_CACTI,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_CACTI.getNbtName(), new Enchantment(EnchantmentType.TURBO_CACTI,5));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_CANE.getNbtName(), new Enchantment(EnchantmentType.TURBO_CANE,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_CANE.getNbtName(), new Enchantment(EnchantmentType.TURBO_CANE,5));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_CARROT.getNbtName(), new Enchantment(EnchantmentType.TURBO_CARROT,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_CARROT.getNbtName(), new Enchantment(EnchantmentType.TURBO_CARROT,5));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_COCOA.getNbtName(), new Enchantment(EnchantmentType.TURBO_COCOA,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_COCOA.getNbtName(), new Enchantment(EnchantmentType.TURBO_COCOA,5));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_MELON.getNbtName(), new Enchantment(EnchantmentType.TURBO_MELON,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_MELON.getNbtName(), new Enchantment(EnchantmentType.TURBO_MELON,5));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_MUSHROOMS.getNbtName(), new Enchantment(EnchantmentType.TURBO_MUSHROOMS,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_MUSHROOMS.getNbtName(), new Enchantment(EnchantmentType.TURBO_MUSHROOMS,5));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_POTATO.getNbtName(), new Enchantment(EnchantmentType.TURBO_POTATO,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_POTATO.getNbtName(), new Enchantment(EnchantmentType.TURBO_POTATO,5));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_PUMPKIN.getNbtName(), new Enchantment(EnchantmentType.TURBO_PUMPKIN,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_PUMPKIN.getNbtName(), new Enchantment(EnchantmentType.TURBO_PUMPKIN,5));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_WARTS.getNbtName(), new Enchantment(EnchantmentType.TURBO_WARTS,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_WARTS.getNbtName(), new Enchantment(EnchantmentType.TURBO_WARTS,5));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_WHEAT.getNbtName(), new Enchantment(EnchantmentType.TURBO_WHEAT,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.TURBO_WHEAT.getNbtName(), new Enchantment(EnchantmentType.TURBO_WHEAT,5));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.VAMPIRISM.getNbtName(), new Enchantment(EnchantmentType.VAMPIRISM,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.VENOMOUS.getNbtName(), new Enchantment(EnchantmentType.VENOMOUS,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.VICIOUS.getNbtName(), new Enchantment(EnchantmentType.VICIOUS,3));
                //IMPORTANT_ENCHANTMENTS.put(EnchantmentType.WOODSPLITTER.getNbtName(), new Enchantment(EnchantmentType.WOODSPLITTER,6));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_BANK.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_BANK,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_BOBBIN_TIME.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_BOBBIN_TIME,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_CHIMERA.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_CHIMERA,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_COMBO.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_COMBO,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_DUPLEX.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_DUPLEX,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_FATAL_TEMPO.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_FATAL_TEMPO,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_FIRST_IMPRESSION.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_FIRST_IMPRESSION,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_FLASH.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_FLASH,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_FLOWSTATE.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_FLOWSTATE,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_HABANERO_TACTICS.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_HABANERO_TACTICS,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_INFERNO.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_INFERNO,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_LAST_STAND.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_LAST_STAND,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_LEGION.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_LEGION,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_MISSILE.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_MISSILE,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_NO_PAIN_NO_GAIN.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_NO_PAIN_NO_GAIN,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_ONE_FOR_ALL.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_ONE_FOR_ALL,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_REFRIGERATE.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_REFRIGERATE,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_REND.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_REND,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_SOUL_EATER.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_SOUL_EATER,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_SWARM.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_SWARM,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_THE_ONE.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_THE_ONE,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_ULTIMATE_JERRY.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_ULTIMATE_JERRY,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_ULTIMATE_WISE.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_ULTIMATE_WISE,1));
                IMPORTANT_ENCHANTMENTS.put(EnchantmentType.ULTIMATE_WISDOM.getNbtName(), new Enchantment(EnchantmentType.ULTIMATE_WISDOM,1));
    }

    /**
     * Sets various item-related attributes of the given auction:
     * {@link Auction#setItemId(String)}, {@link Auction#setReforge(String)}, {@link Auction#setImportantEnchantments(List)},
     * {@link Auction#setUpgradeLevel(int)}, {@link Auction#setHotPotatoCount(int)},
     * {@link Auction#setRarityUpgrades(int)}, {@link Auction#setArtOfWarCount(int)},
     * and {@link Auction#setArtOfPeaceCount(int)}, based on the {@link Auction#getItemBytes()}.
     *
     * @param auction The auction to process — {@link Auction#getItemBytes()} must be set.
     */
    public static void parseAttributes(Auction auction) {
        try {
            CompoundTag root = decodeItemBytes(auction.getItemBytes());
            ListTag<Tag<?>> itemList = (ListTag<Tag<?>>) root.getListTag("i");
            if (itemList == null || itemList.size() == 0) {
                System.err.println("Item list is empty for auction: " + auction.getId());
                return;
            }

            CompoundTag item = (CompoundTag) itemList.get(0);
            CompoundTag tag = item.getCompoundTag("tag");
            CompoundTag extra = tag.getCompoundTag("ExtraAttributes");

            // Set item id
            auction.setItemId(extra.getString("id"));

            // Set reforge
            auction.setReforge(extra.getString("modifier"));

            // Enchantments
            CompoundTag enchants = extra.getCompoundTag("enchantments");
            List<Enchantment> enchantments = new ArrayList<>();
            if (enchants != null) {
                for (Map.Entry<String,Tag<?>> entry : enchants.entrySet()) {
                    System.out.println("testing: " + entry.getKey());
                    if (IMPORTANT_ENCHANTMENTS.containsKey(entry.getKey())) {
                        Enchantment ench = IMPORTANT_ENCHANTMENTS.get(entry.getKey());
                        EnchantmentType type = ench.type();
                        if (((IntTag)entry.getValue()).asInt() < ench.level())
                            continue;

                        enchantments.add(new Enchantment(type, enchants.getInt(entry.getKey())));
                    }
                }
            }
            auction.setImportantEnchantments(enchantments);

            // Stars
            auction.setUpgradeLevel(extra.getInt("upgrade_level"));

            // HPB / FPB
            auction.setHotPotatoCount(extra.getInt("hot_potato_count"));

            // Recombobulated
            auction.setRarityUpgrades(extra.getInt("rarity_upgrades"));

            // Art of War
            auction.setArtOfWarCount(extra.getInt("art_of_war_count"));

            // Art of Peace
            auction.setArtOfPeaceCount(extra.getInt("art_of_peace_count"));

        } catch (Exception e) {
            System.err.println("Failed to parse item_bytes for auction ID: " + auction.getId());
            e.printStackTrace();
        }
    }

    /**
     * Decodes the item bytes and returns a {@link CompoundTag}
     * @param base64 Base64 encoded item bytes
     * @return Root {@link CompoundTag} of the decoded bytes
     * @throws IOException if root tag is not a {@link CompoundTag}
     */
    private static CompoundTag decodeItemBytes(String base64) throws IOException {
        System.out.println("Decoding Base64: " + base64);
        // Unescape any unicode sequences first
        String cleanBase64 = StringEscapeUtils.unescapeJava(base64).trim();
        System.out.println("Clean Base64: " + cleanBase64);

        byte[] compressed = Base64.getDecoder().decode(cleanBase64);
        System.out.println("Compressed: " + compressed.length + " bytes");

        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
             NBTInputStream nis = new NBTInputStream(gis)) {
            Tag<?> tag = nis.readTag(Tag.DEFAULT_MAX_DEPTH).getTag();
            if (!(tag instanceof CompoundTag)) {
                throw new IOException("Root tag is not a CompoundTag");
            }
            System.out.println("Decoded to: " + ((CompoundTag) tag).toString());
            return (CompoundTag) tag;
        }
    }

    /**
     * Transforms a Hypixel UUID (UUID String without dashes) into a {@link UUID}
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
