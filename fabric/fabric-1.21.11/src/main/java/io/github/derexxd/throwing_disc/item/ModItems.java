package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.ThrowingDiscBackport;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ModItems {
    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ThrowingDiscBackport.MODID, name));
    }

    public static final Item COPPER_THROWING_DISC = register("copper_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.COPPER, new Item.Properties().setId(itemKey("copper_throwing_disc")).stacksTo(1).durability(ThrowingDiscTier.COPPER.getDurability())));

    public static final Item IRON_THROWING_DISC = register("iron_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.IRON, new Item.Properties().setId(itemKey("iron_throwing_disc")).stacksTo(1).durability(ThrowingDiscTier.IRON.getDurability())));

    public static final Item GOLDEN_THROWING_DISC = register("golden_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.GOLDEN, new Item.Properties().setId(itemKey("golden_throwing_disc")).stacksTo(1).durability(ThrowingDiscTier.GOLDEN.getDurability())));

    public static final Item DIAMOND_THROWING_DISC = register("diamond_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.DIAMOND, new Item.Properties().setId(itemKey("diamond_throwing_disc")).stacksTo(1).durability(ThrowingDiscTier.DIAMOND.getDurability())));

    public static final Item NETHERITE_THROWING_DISC = register("netherite_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.NETHERITE, new Item.Properties().setId(itemKey("netherite_throwing_disc")).stacksTo(1).durability(ThrowingDiscTier.NETHERITE.getDurability()).fireResistant()));

    public static final Item CAPTAIN_AMERICA_SHIELD = register("captain_america_shield",
            new ThrowingDiscItem(ThrowingDiscTier.CAPTAIN_AMERICA, new Item.Properties().setId(itemKey("captain_america_shield")).stacksTo(1).durability(ThrowingDiscTier.CAPTAIN_AMERICA.getDurability()).fireResistant().rarity(Rarity.EPIC)));

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, itemKey(name), item);
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.accept(COPPER_THROWING_DISC);
            entries.accept(IRON_THROWING_DISC);
            entries.accept(GOLDEN_THROWING_DISC);
            entries.accept(DIAMOND_THROWING_DISC);
            entries.accept(NETHERITE_THROWING_DISC);
        });
    }
}
