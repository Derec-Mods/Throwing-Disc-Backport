package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.ThrowingDiscBackport;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final Item COPPER_THROWING_DISC = register("copper_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.COPPER, new Item.Properties().durability(ThrowingDiscTier.COPPER.getDurability())));

    public static final Item IRON_THROWING_DISC = register("iron_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.IRON, new Item.Properties().durability(ThrowingDiscTier.IRON.getDurability())));

    public static final Item GOLDEN_THROWING_DISC = register("golden_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.GOLDEN, new Item.Properties().durability(ThrowingDiscTier.GOLDEN.getDurability())));

    public static final Item DIAMOND_THROWING_DISC = register("diamond_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.DIAMOND, new Item.Properties().durability(ThrowingDiscTier.DIAMOND.getDurability())));

    public static final Item NETHERITE_THROWING_DISC = register("netherite_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.NETHERITE, new Item.Properties().durability(ThrowingDiscTier.NETHERITE.getDurability()).fireResistant()));

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(ThrowingDiscBackport.MODID, name), item);
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
