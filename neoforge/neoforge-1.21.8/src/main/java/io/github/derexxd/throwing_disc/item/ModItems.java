package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.ThrowingDiscNeoForge;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThrowingDiscNeoForge.MODID);

    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ThrowingDiscNeoForge.MODID, name));
    }

    public static final DeferredItem<Item> COPPER_THROWING_DISC = ITEMS.register("copper_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.COPPER, new Item.Properties().setId(itemKey("copper_throwing_disc")).stacksTo(1).durability(ThrowingDiscTier.COPPER.getDurability())));

    public static final DeferredItem<Item> IRON_THROWING_DISC = ITEMS.register("iron_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.IRON, new Item.Properties().setId(itemKey("iron_throwing_disc")).stacksTo(1).durability(ThrowingDiscTier.IRON.getDurability())));

    public static final DeferredItem<Item> GOLDEN_THROWING_DISC = ITEMS.register("golden_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.GOLDEN, new Item.Properties().setId(itemKey("golden_throwing_disc")).stacksTo(1).durability(ThrowingDiscTier.GOLDEN.getDurability())));

    public static final DeferredItem<Item> DIAMOND_THROWING_DISC = ITEMS.register("diamond_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.DIAMOND, new Item.Properties().setId(itemKey("diamond_throwing_disc")).stacksTo(1).durability(ThrowingDiscTier.DIAMOND.getDurability())));

    public static final DeferredItem<Item> NETHERITE_THROWING_DISC = ITEMS.register("netherite_throwing_disc",
            () -> new ThrowingDiscItem(ThrowingDiscTier.NETHERITE, new Item.Properties().setId(itemKey("netherite_throwing_disc")).stacksTo(1).durability(ThrowingDiscTier.NETHERITE.getDurability()).fireResistant()));

    public static final DeferredItem<Item> CAPTAIN_AMERICA_SHIELD = ITEMS.register("captain_america_shield",
            () -> new ThrowingDiscItem(ThrowingDiscTier.CAPTAIN_AMERICA, new Item.Properties().setId(itemKey("captain_america_shield")).stacksTo(1).durability(ThrowingDiscTier.CAPTAIN_AMERICA.getDurability()).fireResistant().rarity(Rarity.EPIC)));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(ModItems::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(COPPER_THROWING_DISC);
            event.accept(IRON_THROWING_DISC);
            event.accept(GOLDEN_THROWING_DISC);
            event.accept(DIAMOND_THROWING_DISC);
            event.accept(NETHERITE_THROWING_DISC);
        }
    }
}
