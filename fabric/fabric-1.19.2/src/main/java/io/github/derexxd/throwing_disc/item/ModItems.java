package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.ThrowingDiscBackport;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ModItems {
    public static final Item COPPER_THROWING_DISC = register("copper_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.COPPER, new Item.Properties().durability(ThrowingDiscTier.COPPER.getDurability()).tab(CreativeModeTab.TAB_COMBAT)));

    public static final Item IRON_THROWING_DISC = register("iron_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.IRON, new Item.Properties().durability(ThrowingDiscTier.IRON.getDurability()).tab(CreativeModeTab.TAB_COMBAT)));

    public static final Item GOLDEN_THROWING_DISC = register("golden_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.GOLDEN, new Item.Properties().durability(ThrowingDiscTier.GOLDEN.getDurability()).tab(CreativeModeTab.TAB_COMBAT)));

    public static final Item DIAMOND_THROWING_DISC = register("diamond_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.DIAMOND, new Item.Properties().durability(ThrowingDiscTier.DIAMOND.getDurability()).tab(CreativeModeTab.TAB_COMBAT)));

    public static final Item NETHERITE_THROWING_DISC = register("netherite_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.NETHERITE, new Item.Properties().durability(ThrowingDiscTier.NETHERITE.getDurability()).fireResistant().rarity(Rarity.EPIC).tab(CreativeModeTab.TAB_COMBAT)));

    public static final Item CAPTAIN_AMERICA_SHIELD = register("captain_america_shield",
            new ThrowingDiscItem(ThrowingDiscTier.CAPTAIN_AMERICA, new Item.Properties().durability(ThrowingDiscTier.CAPTAIN_AMERICA.getDurability()).fireResistant().rarity(Rarity.EPIC)));

    private static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, new ResourceLocation(ThrowingDiscBackport.MODID, name), item);
    }

    public static void register() {
    }
}
