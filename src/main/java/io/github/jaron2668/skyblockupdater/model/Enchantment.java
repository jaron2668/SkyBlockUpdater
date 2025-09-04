package io.github.jaron2668.skyblockupdater.model;

public record Enchantment(EnchantmentType type, int level) {
    // There are probably more like impaling or dragon tracer but hopefully nothing important (because all others are obtainable from an enchanting table)
    public enum EnchantmentType { // TODO: make sure all nbt names are correct
        ABSORB("absorb"),                       // not verified
        ANGLER("angler"),
        BANE_OF_ARTHROPODS("bane_of_arthropods"),
        BIG_BRAIN("big_brain"),
        BLAST_PROTECTION("blast_protection"),   // not verified
        BLESSING("blessing"),
        CASTER("caster"),
        CAYENNE("cayenne"),                     // not verified
        CHAMPION("champion"),
        CHANCE("chance"),
        CHARM("charm"),
        CLEAVE("cleave"),
        COMPACT("compact"),
        CORRUPTION("corruption"),
        COUNTER_STRIKE("counter_strike"),
        CRITICAL("critical"),                   // not verified
        CUBISM("cubism"),
        CULTIVATING("cultivating"),
        DEDICATION("dedication"),
        DELICATE("delicate"),
        DIVINE_GIFT("divine_gift"),             // not verified
        DRAGON_HUNTER("dragon_hunter"),
        DRAIN("drain"),
        ENDER_SLAYER("ender_slayer"),
        EXECUTE("execute"),
        EXPERIENCE("experience"),
        EXPERTISE("expertise"),
        FEATHER_FALLING("feather_falling"),     // not verified
        FEROCIOUS_MANA("ferocious_mana"),
        FIRE_ASPECT("fire_aspect"),
        FIRE_PROTECTION("fire_protection"),
        FIRST_STRIKE("first_strike"),
        FOREST_PLEDGE("forest_pledge"),         // not verified
        FORTUNE("fortune"),
        FRAIL("frail"),
        GIANT_KILLER("giant_killer"),
        GRAVITY("gravity"),                     // not verified
        GREEN_THUMB("green_thumb"),
        GROWTH("growth"),
        HARDENED_MANA("hardened_mana"),
        HARVESTING("harvesting"),               // not verified
        HECATOMB("hecatomb"),                   // not verified
        ICE_COLD("ice_cold"),                   // not verified
        INFINITE_QUIVER("infinite_quiver"),
        LAPIDARY("lapidary"),                   // not verified
        LETHALITY("lethality"),
        LIFE_STEAL("life_steal"),
        LOOTING("looting"),
        LUCK("luck"),
        LUCK_OF_THE_SEA("luck_of_the_sea"),
        LURE("lure"),
        MAGNET("magnet"),
        MANA_STEAL("mana_steal"),
        MANA_VAMPIRE("mana_vampire"),
        OVERLOAD("overload"),
        PALEONTOLOGIST("paleontologist"),
        PESTERMINATOR("pesterminator"),
        PISCARY("piscary"),                     // not verified
        POWER("power"),
        PRISMATIC("prismatic"),
        PROJECTILE_PROTECTION("projectile_protection"), // not verified
        PROSECUTE("prosecute"),
        PROSPERITY("prosperity"),               // not verified
        PROTECTION("protection"),               // not verified
        QUANTUM("quantum"),
        QUICK_BITE("quick_bite"),               // not verified
        REFLECTION("reflection"),               // not verified
        REJUVENATE("rejuvenate"),               // not verified
        REPLENISH("replenish"),
        RESPIRATION("respiration"),             // not verified
        RESPITE("respite"),                     // not verified
        SCAVENGER("scavenger"),
        SCUBA("scuba"),                         // not verified
        SHARPNESS("sharpness"),
        SMALL_BRAIN("small_brain"),             // not verified
        SMARTY_PANTS("smarty_pants"),
        SMITE("smite"),
        SMOLDERING("smoldering"),
        SNIPE("snipe"),
        SPIKED_HOOK("spiked_hook"),
        STEALTH("stealth"),                     // not verified
        STRONG_MANA("strong_mana"),
        SUGAR_RUSH("sugar_rush"),               // not verified
        SUNDER("sunder"),
        SYPHON("syphon"),
        TABASCO("tabasco"),
        THUNDERBOLT("thunderbolt"),
        THUNDERLORD("thunderlord"),
        TIDAL("tidal"),                         // not verified
        TITAN_KILLER("titan_killer"),
        TOXOPHILITE("toxophilite"),
        TRANSYLVANIAN("transylvanian"),         // not verified
        TRIPLE_STRIKE("triple_strike"),
        TRUE_PROTECTION("true_protection"),     // not verified
        TURBO_CACTI("turbo_cacti"),             // not verified
        TURBO_CANE("turbo_cane"),               // not verified
        TURBO_CARROT("turbo_carrot"),           // not verified
        TURBO_COCOA("turbo_cocoa"),
        TURBO_MELON("turbo_melon"),             // not verified
        TURBO_MUSHROOMS("turbo_mushrooms"),     // not verified
        TURBO_POTATO("turbo_potato"),           // not verified
        TURBO_PUMPKIN("turbo_pumpkin"),         // not verified
        TURBO_WARTS("turbo_warts"),             // not verified
        TURBO_WHEAT("turbo_wheat"),             // not verified
        VAMPIRISM("vampirism"),
        VENOMOUS("venomous"),
        VICIOUS("vicious"),                     // not verified
        WOODSPLITTER("woodsplitter"),           // not verified
        ULTIMATE_BANK("ultimate_bank"),         // not verified
        ULTIMATE_BOBBIN_TIME("ultimate_bobbin_time"), // not verified
        ULTIMATE_CHIMERA("ultimate_chimera"),
        ULTIMATE_COMBO("ultimate_combo"),
        ULTIMATE_DUPLEX("ultimate_duplex"),
        ULTIMATE_FATAL_TEMPO("ultimate_fatal_tempo"),
        ULTIMATE_FIRST_IMPRESSION("ultimate_first_impression"), // not verified
        ULTIMATE_FLASH("ultimate_flash"),       // not verified
        ULTIMATE_FLOWSTATE("ultimate_flowstate"), // not verified
        ULTIMATE_HABANERO_TACTICS("ultimate_habanero_tactics"), // not verified
        ULTIMATE_INFERNO("ultimate_inferno"),
        ULTIMATE_LAST_STAND("ultimate_last_stand"), // not verified
        ULTIMATE_LEGION("ultimate_legion"),     // not verified
        ULTIMATE_MISSILE("ultimate_missile"),   // not verified
        ULTIMATE_NO_PAIN_NO_GAIN("ultimate_no_pain_no_gain"), // not verified
        ULTIMATE_ONE_FOR_ALL("ultimate_one_for_all"),
        ULTIMATE_REFRIGERATE("ultimate_refrigerate"), // not verified
        ULTIMATE_REND("ultimate_rend"),
        ULTIMATE_SOUL_EATER("ultimate_soul_eater"),
        ULTIMATE_SWARM("ultimate_swarm"),
        ULTIMATE_THE_ONE("ultimate_the_one"),   // not verified
        ULTIMATE_ULTIMATE_JERRY("ultimate_jerry"), // note verified
        ULTIMATE_ULTIMATE_WISE("ultimate_wise"),   // not verified
        ULTIMATE_WISDOM("ultimate_wisdom");     // not verified


        private final String nbtName;

        EnchantmentType(String nbtName) {
            this.nbtName = nbtName;
        }

        public String getNbtName() {
            return nbtName;
        }
    }

}
