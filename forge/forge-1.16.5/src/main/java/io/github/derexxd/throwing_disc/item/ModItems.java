package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.ThrowingDiscForge;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ThrowingDiscForge.MODID);

    public static final RegistryObject<Item> COPPER_THROWING_DISC = ITEMS.register("copper_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.COPPER, new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(ThrowingDiscTier.COPPER.getDurability())));

    public static final RegistryObject<Item> IRON_THROWING_DISC = ITEMS.register("iron_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.IRON, new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(ThrowingDiscTier.IRON.getDurability())));

    public static final RegistryObject<Item> GOLDEN_THROWING_DISC = ITEMS.register("golden_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.GOLDEN, new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(ThrowingDiscTier.GOLDEN.getDurability())));

    public static final RegistryObject<Item> DIAMOND_THROWING_DISC = ITEMS.register("diamond_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.DIAMOND, new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(ThrowingDiscTier.DIAMOND.getDurability())));

    public static final RegistryObject<Item> NETHERITE_THROWING_DISC = ITEMS.register("netherite_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.NETHERITE, new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(ThrowingDiscTier.NETHERITE.getDurability()).fireResistant()));

    public static final RegistryObject<Item> CAPTAIN_AMERICA_SHIELD = ITEMS.register("captain_america_shield",
            () -> new ThrowingDiscItem(ThrowingDiscTier.CAPTAIN_AMERICA, new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(ThrowingDiscTier.CAPTAIN_AMERICA.getDurability()).fireResistant().rarity(Rarity.EPIC)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
