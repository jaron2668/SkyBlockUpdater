package io.github.jaron2668.skyblockupdater.util;

import io.github.jaron2668.skyblockupdater.model.Auction;
import io.github.jaron2668.skyblockupdater.model.Enchantment;
import io.github.jaron2668.skyblockupdater.model.Enchantment.EnchantmentType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AttributeParserTest {

    @Test
    public void testParseHypixelUuid_valid() {
        String uuidStr = "409a1e0f-261a-4984-9493-278d6cd9305a";
        UUID uuid = AttributeParser.parseHypixelUuid(uuidStr.replace("-", ""));
        assertEquals(UUID.fromString(uuidStr), uuid);
    }

    @Test
    public void testParseAttributes_validItemBytes_parsesCorrectly() {

        Auction auction = new Auction();
        auction.setId(UUID.randomUUID());
        // For detailed info see end of the class
        String base64Nbt = "H4sIAAAAAAAA/1VVz28bRRR+rt3EdlsKLRJCKmiAFqVKYxwnsUkPSK7jxIa0RbGbFCG0mt2d2KPs7qx2ZlPnyJUzlRCiN6QgLtw4cYrE/4HyhyC+2bUdY6288+Z978e89+bbKlGFCrJKRIVrdE36hXcLdL2j0sgUqlQ0fFShkoi8MdlfgW68iNxE8BPuBqJQpEpP+mI34CMN7b9VWvaljgN+BqN9lYgydhl9cHHe2uEhH4nH7OLcW23UG7UmVmJldaP+kBpQD0wiopEZ54CN7a3a+hyAxfYKjJp25a+srm8+pPdg00mkYYt+W/UH9KFFfwvc5ZvvsfpuLv78oxWRzx6ki/Mg/x+oNGBdbkTCDqF/ZOFPeCSYOmbtxIwTFStfs8P+TNkZ8zCWKmIvqWbFQPBTcWVrc5IeDxYtUlfqkB3SBoSdhMuI9ef4buQj9AAVsxnMbboT4aUGfumLTIpFItEEsWC5KxPB2joWnmH9/txyT/LIsK9kEGQOaQt7/TDmgYxGi7h9YcbYNGcLUfeVMhns0PZkez/1Tha0Aw8njUaLlRqMeRJHQtsC9emx3QmlTfsqznCc2iMGKvEXfA0TGQdiDV2XJ/ZQ9BE2D21lk6xWc+ChiFSoUgRA5+6h6T2B4qLZW/XLN79i0UJp2FiaGrXslOVavtrYqj+wWj8bD2YUhMY/f71mL5AN960qVK7Gq0ab+MezZztzcb6xurl1+dMfrKNClxt2JLWvQqgZHwGgzdQR3cH7/85qSPH+xXmz7Upb2MdsiPF5xewcY9IO+nu9Ievs9ztf0SewyJVnKk1YjweuSPxHLMvWdoAHAX0GkIhEKIVmyEwazWJuxkCJvJuownr9gU0tFIEQtDI/bw3ZR9qOkMRw2il+pZmnsuS3GxNawytLGiMU8ogzHvmZW+uzMWEzXe6NPoaMaYngSbA4EacSHakB9flKyCdsvTl5SHchPbWuOghjr+NGo456vINIRxJ2ifDZExWlmj61tU4wpTrrlL3nHi4rm1HAtKn0ftYWr8MN99ALi24F4lQEts6rs0t8wi/OE5ZLT78Z9vodNjh6frDDrgBlKj3joaAmtua5TGtu77k91WCMTV/YXjYvf/l98aEq3e5OTMLbBuPq4lrqIt1OOG76mZPGo4T7wrIf2PDtsTJOrAw3yvEshWL7dpVKIxHqMpW/bA++7h44dSrt9p91sdF5/vRJezjbqNBbaRQo70T4jg6U0ZY9ry2YzfFlujlbOvBNSzmEluiON+Unx8vm15nE7dMjPfjh7z8RL1S+PJaobfnVtA5FujU9gpPVFhGvl+1ngO62X3ad57vOsNd1Br2D7s5Od6dKN+2nAL0LBfpXpLKcUgvsikW644I6HXXs8Dl1QrFUpFIALsmXZW9Kkbm45GUMauMWaTnIGQhSqUjXtSUTrJcB02fxWEW54m4aGBmCtB0N+naEpe/cQUXPSCqXkS7IB9W0/JoHvHEM4nR4Rpx51lUx59fcf+V0xkS5ybLI6Xjqc2Qp1jnJKHZ6ptMpTeWIWyajN0dn9Jb7vGGuiDA3qugZe06P6GUfitxFedZHiFVggxldW1vCRKcpenRfeJsNt9lYXxPuMV/bXG/V19wW99fEdoM3/a3m8YbbLFEF1RLawCGm9PVv92q7dq6W8k+n/az/B5EqnO8FCAAA";
        auction.setItemBytes(base64Nbt);
        AttributeParser.parseAttributes(auction);


        assertEquals("withered", auction.getReforge());

        List<Enchantment> enchants = auction.getImportantEnchantments();
        assertNotNull(enchants);
        // enchantments that should be extracted
        // bane 6
        // looting 4
        // smite 7
        // syphon 4
        // soul eater 5
        // scavenger 5
        // ender slayer 6
        // experience 4
        // vampirism 6
        // giant killer 6
        // sharpness 7
        // champion 10
        // lethality 6
        assertEquals(13, enchants.size());



        Enchantment bane = enchants.get(0);
        assertEquals(EnchantmentType.BANE_OF_ARTHROPODS, bane.type());
        assertEquals(6, bane.level());

        Enchantment champ = enchants.get(11);
        assertEquals(EnchantmentType.CHAMPION, champ.type());
        assertEquals(10, champ.level());

        assertEquals(5, auction.getUpgradeLevel());
        assertEquals(15, auction.getHotPotatoCount());
        assertEquals(1, auction.getRarityUpgrades());
        assertEquals(0, auction.getArtOfPeaceCount());
        assertEquals(0, auction.getArtOfWarCount());

        // second auction

        Auction auction2 = new Auction();
        auction2.setId(UUID.randomUUID());
        String base64Nbt2 = "H4sIAAAAAAAA/21WW28bRRQ+rtP40pSWqg9cpDJCFFpMgp3EccKbYztpRNJWsZu2QtVqPDveHXl3ZzU7duNH+A08oKo855GfgJQn/gJPSPkZPCDO7OzaDiKKsnPuZ87lm1QBKlAQVQAo3IAbwi18WoCbHTmJdKEKRU29CqzwiPlgfgpw60U0VJyO6TDghSJUngiXHwTUS1D6TxVKrkjigM7Q6FgqXkbul/DJ5UWrS0Pq8e/I5QWrbW3hhz+qbdUfw7co62vFI0/7Vrq51ZiL8bDzQ635Br97j2qb9cZj+BwNOkposvC4V2tsbj/Eg4s62w8fwz3UOeBKMqFn1mkz5fVjzl3DGNV2NlpNuI+8E+oJRg5ElAqGtR14kEa9vNi9evcznt78h8QrHWKoy4vA/u34IuSKkqOjI0zlG5PPPo04kSPSVtpXMpZuQs4W0o5Pw1jIiLyChiEDTqccFeZyvJ1gNLhmMxmKJEQO7CHVFVOBEQ7FSC+H7SoqInKWk73I5Yr0sRv4QV+wa5jnnE30crjeecyVwBbzhemBUJy0k5izNAC0kHcoaKTJ9yIIrLtc91DRKdZ5yeNRGNNARB45gzqSx1z7SF9TOZZSpxpzxoSN0ySbSPQZViTy0ji5Qt+nKo54cq2U/VCkd7Fl6YcywCtf8zugQ5owuVymgT8xlQmkcuemAyXigK/jJIrxUh3OTKeUrfycxyMZyonJA9YMLZhISRyMdRwo/D3EPiR27rIxRaKFZSYDGpr0Aj7lwQZ8ZdU7MhY8IdrnJNFUJ2SkZEhmcqIIZVpMOZRSc72BITasTY+qyAxmbatJmLThWqldKKNEY6gxdioxFl+b9eM0MCoUl6huVqXl2ryoZ3LVyNm8ev87fITfk5n2ZSC9dAZRM5TD1E8ju1m6JvWN5tX734z4ugeybA01ZGYeyEgqwinzUXFrnydaUDUz9lpgsnhZeISEb1ZhEgWSjbmbmvyf21sLtyaxD7ELLwWWT6HNvowmCXxpclU4sPbSBlHY1a8/khxqsn7Ax2k1WYdqymQ4TKuYNQdM9nbNx/TywmRiqJPXgydHHdJ/+ey0SxYKZVh5SkMOD5B19e4dmefTpdylAc7HfkBdDlW40zvXirY1jtoQNzEpwh1FceVnziT2FOoYKEVovetL7cQSB0I6zOAxsu9UYcXjYVKGcufZyX574NSh9Lx3etDrDCrwQV44JwmkTgz43lhSzE+NMqzlRwe9wcqzp69fLZj1ZebcaB4HVuEeyyDMMUWj2jmP28///Kw2+/sntAilK0bYVCi/zYqAl6FKO3LkvKVqfplC2bw3cL8/aJ+e9rpOt93rto9f9J32q14V1syjgw0MOTaxCGWRgQoa3izCSoB4gccSSliGl5a8mRhMsGf0gYuO1TAQaFmVJMcWpFeLUOVz+LOeb40Q+xyaYh9yishxU7h1PIRby1nzDBY64xQLrd/brqIeFgSxRee+y9MMKix5W6cg4yQpyNhoJW3xKYukF9CUp5vDnqVXWfoQWIeVIEdWS98b4rtjqkzn7461Kk0tRtmYqyx9cbL7J3PQzKTJLPZllKUXWJy21N1JoEVINXeYffNs1pVpjpLWZYnbNyYrQj4rSFYB12QywaZ/wTnbq+80m+utBuPr23TI14e7u3S92dpu7bHd+jbf3FyBCsZDqEAHOC6//EH/ujQjvWox1fxD8i/TY2n6vwgAAA==";
        auction2.setItemBytes(base64Nbt2);

        AttributeParser.parseAttributes(auction2);
        assertEquals("withered", auction2.getReforge());

        List<Enchantment> enchants2 = auction2.getImportantEnchantments();
        assertNotNull(enchants2);

        assertEquals(26, enchants2.size());

        Enchantment ench = enchants2.getFirst();
        assertEquals(EnchantmentType.LUCK, ench.type());
        assertEquals(7, ench.level());

        assertEquals(0, auction2.getUpgradeLevel());
        assertEquals(15, auction2.getHotPotatoCount());
        assertEquals(1, auction2.getRarityUpgrades());
        assertEquals(0, auction2.getArtOfPeaceCount());
        assertEquals(1, auction2.getArtOfWarCount());


    }

    @Test
    public void testDecodeItemBytes_invalidRootTag_throwsIOException() {
        String invalidRootTagBase64 = "H4sIAAAAAAAA/2NgYGBgYmBg4GBgYGBgYAAA4kAmnswAAAA="; // NBT with root tag as IntTag (not CompoundTag)

        Exception exception = assertThrows(Exception.class, () -> {
            // Using reflection because decodeItemBytes is private
            java.lang.reflect.Method method = AttributeParser.class.getDeclaredMethod("decodeItemBytes", String.class);
            method.setAccessible(true);
            method.invoke(null, invalidRootTagBase64);
        });
        // exception should be caused by IOException: Root tag is not a CompoundTag
        assertTrue(exception.getCause().getMessage().contains("Root tag is not a CompoundTag"));
    }


    /* Test data (Base64 encoded gzip-compressed NBT):
        base64Nbt corresponds to:
        {
            "type": "CompoundTag",
                "value": {
            "i": {
                "type": "ListTag",
                        "value": {
                    "type": "CompoundTag",
                            "list": [
                    {
                        "id": { "type": "ShortTag", "value": 276 },
                        "Count": { "type": "ByteTag", "value": 1 },
                        "tag": {
                        "type": "CompoundTag",
                                "value": {
                            "ench": { "type": "ListTag", "value": { "type": "EndTag", "list": [] } },
                            "Unbreakable": { "type": "ByteTag", "value": 1 },
                            "ExtraAttributes": {
                                "type": "CompoundTag",
                                        "value": {
                                    "rarity_upgrades": { "type": "IntTag", "value": 1 },
                                    "hot_potato_count": { "type": "IntTag", "value": 15 },
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
                                    },
                                    "champion_combat_xp": {
                                        "type": "DoubleTag",
                                                "value": 2.3426869221379958e7
                                    },
                                    "modifier": { "type": "StringTag", "value": "withered" },
                                    "upgrade_level": { "type": "IntTag", "value": 5 },
                                    "id": { "type": "StringTag", "value": "AXE_OF_THE_SHREDDED" },
                                    "enchantments": {
                                        "type": "CompoundTag",
                                                "value": {
                                            "impaling": { "type": "IntTag", "value": 3 },
                                            "bane_of_arthropods": { "type": "IntTag", "value": 6 },
                                            "luck": { "type": "IntTag", "value": 6 },
                                            "critical": { "type": "IntTag", "value": 6 },
                                            "cleave": { "type": "IntTag", "value": 5 },
                                            "looting": { "type": "IntTag", "value": 4 },
                                            "smite": { "type": "IntTag", "value": 7 },
                                            "syphon": { "type": "IntTag", "value": 4 },
                                            "ultimate_soul_eater": { "type": "IntTag", "value": 5 },
                                            "scavenger": { "type": "IntTag", "value": 5 },
                                            "ender_slayer": { "type": "IntTag", "value": 6 },
                                            "fire_aspect": { "type": "IntTag", "value": 3 },
                                            "experience": { "type": "IntTag", "value": 4 },
                                            "vampirism": { "type": "IntTag", "value": 6 },
                                            "execute": { "type": "IntTag", "value": 5 },
                                            "giant_killer": { "type": "IntTag", "value": 6 },
                                            "venomous": { "type": "IntTag", "value": 5 },
                                            "triple_strike": { "type": "IntTag", "value": 4 },
                                            "thunderlord": { "type": "IntTag", "value": 6 },
                                            "sharpness": { "type": "IntTag", "value": 7 },
                                            "cubism": { "type": "IntTag", "value": 5 },
                                            "champion": { "type": "IntTag", "value": 10 },
                                            "lethality": { "type": "IntTag", "value": 6 }
                                        }
                                    },
                                    "uuid": {
                                        "type": "StringTag",
                                                "value": "ec42b621-ebfa-4170-b7ad-e92a6d56f3b6"
                                    },
                                    "timestamp": { "type": "LongTag", "value": 1742282174022 }
                                }
                            },
                            "HideFlags": { "type": "IntTag", "value": 254 },
                            "display": {
                                "type": "CompoundTag",
                                        "value": {
                                    "Lore": {
                                        "type": "ListTag",
                                                "value": {
                                            "type": "StringTag",
                                                    "list": [
                                            "§7Damage: §c+202.6 §e(+30)",
                                                    "§7Strength: §c+395.1 §e(+30) §9(+206) §d(+14)",
                                                    "§7Crit Damage: §c+70%",
                                                    " §9[§d❁§9] §9[§d⚔§9]",
                                                    "",
                                                    "§d§l§d§lSoul Eater V§9, §9Bane of Arthropods VI§9, §9Champion X",
                                                    "§9Cleave V§9, §9Critical VI§9, §9Cubism V",
                                                    "§9Drain IV§9, §9Ender Slayer VI§9, §9Execute V",
                                                    "§9Experience IV§9, §9Fire Aspect III§9, §9Giant Killer VI",
                                                    "§9Impaling III§9, §9Lethality VI§9, §9Looting IV",
                                                    "§9Luck VI§9, §9Scavenger V§9, §9Sharpness VII",
                                                    "§9Smite VII§9, §9Thunderlord VI§9, §9Triple-Strike IV",
                                                    "§9Vampirism VI§9, §9Venomous V",
                                                    "",
                                                    "§7Heal §c50❤ §7per hit.",
                                                    "§7Deal §a+250% §7damage to §2༕ Undead §7mobs§7.",
                                                    "§7§7Gain §3+45☯ Combat Wisdom§7 against §2༕",
                                                    "§2Undead §7mobs.",
                                                    "",
                                                    "§6Ability: Throw  §e§lRIGHT CLICK",
                                                    "§7Throw your Halberd, damaging all",
                                                    "§7enemies in its path, dealing §c10%§7 melee",
                                                    "§7damage. Consecutive throws cost §92x",
                                                    "§9§7more mana and deal §c2x §7more damage",
                                                    "§7than the previous. §8(max 16x)",
                                                    "§8Mana Cost: §320",
                                                    "",
                                                    "§9Withered Bonus",
                                                    "§7Grants §a+1 §c❁ Strength §7per",
                                                    "§7§cCatacombs §7level.",
                                                    "",
                                                    "§d§l§ka§r §d§lMYTHIC SWORD §d§l§ka"
                                                ]
                                        }
                                    },
                                    "Name": {
                                        "type": "StringTag",
                                                "value": "§dWithered Halberd of the Shredded §6✪✪✪✪✪"
                                    }
                                }
                            }
                        }
                    },
                        "Damage": { "type": "ShortTag", "value": 0 }
                    }
                ]
                }
            }
        }
        }




        base64Nbt2 corresponds to:
        {
    "type": "CompoundTag",
    "value": {
        "i": {
            "type": "ListTag",
            "value": {
                "type": "CompoundTag",
                "list": [
                    {
                        "id": { "type": "ShortTag", "value": 283 },
                        "Count": { "type": "ByteTag", "value": 1 },
                        "tag": {
                            "type": "CompoundTag",
                            "value": {
                                "ench": { "type": "ListTag", "value": { "type": "EndTag", "list": [] } },
                                "Unbreakable": { "type": "ByteTag", "value": 1 },
                                "ExtraAttributes": {
                                    "type": "CompoundTag",
                                    "value": {
                                        "rarity_upgrades": { "type": "IntTag", "value": 1 },
                                        "hot_potato_count": { "type": "IntTag", "value": 15 },
                                        "gems": {
                                            "type": "CompoundTag",
                                            "value": {
                                                "COMBAT_0": { "type": "StringTag", "value": "PERFECT" },
                                                "unlocked_slots": {
                                                    "type": "ListTag",
                                                    "value": {
                                                        "type": "StringTag",
                                                        "list": ["COMBAT_0", "COMBAT_1"]
                                                    }
                                                },
                                                "COMBAT_1_gem": { "type": "StringTag", "value": "ONYX" },
                                                "COMBAT_0_gem": { "type": "StringTag", "value": "ONYX" },
                                                "COMBAT_1": { "type": "StringTag", "value": "PERFECT" }
                                            }
                                        },
                                        "champion_combat_xp": {
                                            "type": "DoubleTag",
                                            "value": 4416636.67931998
                                        },
                                        "modifier": { "type": "StringTag", "value": "withered" },
                                        "art_of_war_count": { "type": "IntTag", "value": 1 },
                                        "id": { "type": "StringTag", "value": "STARRED_DAEDALUS_AXE" },
                                        "enchantments": {
                                            "type": "CompoundTag",
                                            "value": {
                                                "impaling": { "type": "IntTag", "value": 5 },
                                                "luck": { "type": "IntTag", "value": 7 },
                                                "critical": { "type": "IntTag", "value": 7 },
                                                "smite": { "type": "IntTag", "value": 7 },
                                                "ender_slayer": { "type": "IntTag", "value": 7 },
                                                "scavenger": { "type": "IntTag", "value": 6 },
                                                "experience": { "type": "IntTag", "value": 5 },
                                                "fire_aspect": { "type": "IntTag", "value": 3 },
                                                "divine_gift": { "type": "IntTag", "value": 3 },
                                                "giant_killer": { "type": "IntTag", "value": 7 },
                                                "dragon_hunter": { "type": "IntTag", "value": 6 },
                                                "venomous": { "type": "IntTag", "value": 6 },
                                                "triple_strike": { "type": "IntTag", "value": 5 },
                                                "tabasco": { "type": "IntTag", "value": 3 },
                                                "thunderlord": { "type": "IntTag", "value": 7 },
                                                "sharpness": { "type": "IntTag", "value": 7 },
                                                "cubism": { "type": "IntTag", "value": 6 },
                                                "lethality": { "type": "IntTag", "value": 6 },
                                                "bane_of_arthropods": { "type": "IntTag", "value": 7 },
                                                "vicious": { "type": "IntTag", "value": 5 },
                                                "cleave": { "type": "IntTag", "value": 6 },
                                                "smoldering": { "type": "IntTag", "value": 5 },
                                                "syphon": { "type": "IntTag", "value": 5 },
                                                "looting": { "type": "IntTag", "value": 5 },
                                                "ultimate_chimera": { "type": "IntTag", "value": 3 },
                                                "vampirism": { "type": "IntTag", "value": 6 },
                                                "execute": { "type": "IntTag", "value": 6 },
                                                "champion": { "type": "IntTag", "value": 10 }
                                            }
                                        },
                                        "uuid": {
                                            "type": "StringTag",
                                            "value": "eec90655-71ce-4abe-b88a-57479c804e22"
                                        },
                                        "timestamp": { "type": "LongTag", "value": 1755708514242 }
                                    }
                                },
                                "HideFlags": { "type": "IntTag", "value": 254 },
                                "display": {
                                    "type": "CompoundTag",
                                    "value": {
                                        "Lore": {
                                            "type": "ListTag",
                                            "value": {
                                                "type": "StringTag",
                                                "list": [
                                                    "§7Damage: §c+33 §e(+30)",
                                                    "§7Strength: §c+231 §e(+30) §6[+5] §9(+201)",
                                                    "§7Crit Damage: §9+124% §d(+24%)",
                                                    "§7Ferocity: §c+5",
                                                    "§7Speed: §f+6.75",
                                                    "§7Magic Find: §b+6",
                                                    " §6[§8⚔§6] §6[§8⚔§6]",
                                                    "",
                                                    "§d§l§d§lChimera III§9, §9Bane of Arthropods VII§9, §9Champion X",
                                                    "§9Cleave VI§9, §9Critical VII§9, §9Cubism VI",
                                                    "§9Divine Gift III§9, §9Drain V§9, §9Ender Slayer VII",
                                                    "§9Execute VI§9, §9Experience V§9, §9Fire Aspect III",
                                                    "§9Giant Killer VII§9, §9Gravity VI§9, §9Impaling V",
                                                    "§9Lethality VI§9, §9Looting V§9, §9Luck VII",
                                                    "§9Scavenger VI§9, §9Sharpness VII§9, §9Smite VII",
                                                    "§9Smoldering V§9, §9Tabasco III§9, §9Thunderlord VII",
                                                    "§9Triple-Strike V§9, §9Vampirism VI§9, §9Venomous VI",
                                                    "§9Vicious V",
                                                    "",
                                                    "§7§7Gains §c+5 Damage §7per Taming level.",
                                                    "§7§7Copies the stats from your active",
                                                    "§7pet.",
                                                    "",
                                                    "§7§7Earn §6+35 coins §7from monster kills.",
                                                    "",
                                                    "§7Deals §a+200% §7damage against §2✿",
                                                    "§2Mythological §7mobs.",
                                                    "",
                                                    "§7Gain §b+0.5✯ §7against §2✿ Mythological",
                                                    "§2§7mobs for each §3Bestiary §7tier you",
                                                    "§7have unlocked for §2✿ Mythological",
                                                    "§2§7mobs.",
                                                    "",
                                                    "§9Withered Bonus",
                                                    "§7Grants §a+1 §c❁ Strength §7per",
                                                    "§7§cCatacombs §7level.",
                                                    "",
                                                    "§d§l§ka§r §d§lMYTHIC SWORD §d§l§ka"
                                                ]
                                            }
                                        },
                                        "Name": {
                                            "type": "StringTag",
                                            "value": "§d⚚ Withered Daedalus Blade"
                                        }
                                    }
                                }
                            }
                        },
                        "Damage": { "type": "ShortTag", "value": 0 }
                    }
                ]
            }
        }
    }
}

     */
}
