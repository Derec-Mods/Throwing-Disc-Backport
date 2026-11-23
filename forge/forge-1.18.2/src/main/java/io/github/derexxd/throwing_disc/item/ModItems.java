package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.ThrowingDiscForge;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ThrowingDiscForge.MODID);

    public static final RegistryObject<Item> COPPER_THROWING_DISC = ITEMS.register("copper_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.COPPER, new Item.Properties().durability(ThrowingDiscTier.COPPER.getDurability()).tab(CreativeModeTab.TAB_COMBAT)));

    public static final RegistryObject<Item> IRON_THROWING_DISC = ITEMS.register("iron_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.IRON, new Item.Properties().durability(ThrowingDiscTier.IRON.getDurability()).tab(CreativeModeTab.TAB_COMBAT)));

    public static final RegistryObject<Item> GOLDEN_THROWING_DISC = ITEMS.register("golden_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.GOLDEN, new Item.Properties().durability(ThrowingDiscTier.GOLDEN.getDurability()).tab(CreativeModeTab.TAB_COMBAT)));

    public static final RegistryObject<Item> DIAMOND_THROWING_DISC = ITEMS.register("diamond_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.DIAMOND, new Item.Properties().durability(ThrowingDiscTier.DIAMOND.getDurability()).tab(CreativeModeTab.TAB_COMBAT)));

    public static final RegistryObject<Item> NETHERITE_THROWING_DISC = ITEMS.register("netherite_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.NETHERITE, new Item.Properties().durability(ThrowingDiscTier.NETHERITE.getDurability()).fireResistant().rarity(Rarity.EPIC).tab(CreativeModeTab.TAB_COMBAT)));

    public static final RegistryObject<Item> CAPTAIN_AMERICA_SHIELD = ITEMS.register("captain_america_shield",
            () -> new ThrowingDiscItem(ThrowingDiscTier.CAPTAIN_AMERICA, new Item.Properties().durability(ThrowingDiscTier.CAPTAIN_AMERICA.getDurability()).fireResistant().rarity(Rarity.EPIC)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
