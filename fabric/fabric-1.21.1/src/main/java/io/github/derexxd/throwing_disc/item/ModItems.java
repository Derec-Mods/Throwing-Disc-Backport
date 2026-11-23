package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.ThrowingDiscBackport;
import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class ModItems {
    public static final Item COPPER_THROWING_DISC = register("copper_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.COPPER, new Item.Properties().stacksTo(1).durability(ThrowingDiscTier.COPPER.getDurability())));

    public static final Item IRON_THROWING_DISC = register("iron_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.IRON, new Item.Properties().stacksTo(1).durability(ThrowingDiscTier.IRON.getDurability())));

    public static final Item GOLDEN_THROWING_DISC = register("golden_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.GOLDEN, new Item.Properties().stacksTo(1).durability(ThrowingDiscTier.GOLDEN.getDurability())));

    public static final Item DIAMOND_THROWING_DISC = register("diamond_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.DIAMOND, new Item.Properties().stacksTo(1).durability(ThrowingDiscTier.DIAMOND.getDurability())));

    public static final Item NETHERITE_THROWING_DISC = register("netherite_throwing_disc",
            new ThrowingDiscItem(ThrowingDiscTier.NETHERITE, new Item.Properties().stacksTo(1).durability(ThrowingDiscTier.NETHERITE.getDurability()).fireResistant()));

    public static final Item CAPTAIN_AMERICA_SHIELD = register("captain_america_shield",
            new ThrowingDiscItem(ThrowingDiscTier.CAPTAIN_AMERICA, new Item.Properties().stacksTo(1).durability(ThrowingDiscTier.CAPTAIN_AMERICA.getDurability()).fireResistant().rarity(Rarity.EPIC)));

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(ThrowingDiscBackport.MODID, name), item);
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

    public static void registerDispenserBehaviors() {
        DefaultDispenseItemBehavior behavior = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Direction facing = source.state().getValue(DispenserBlock.FACING);
                Level level = source.level();
                Vec3 pos = source.center().add(
                        facing.getStepX() * 0.7,
                        facing.getStepY() * 0.7,
                        facing.getStepZ() * 0.7
                );
                ThrowingDiscTier tier = ThrowingDiscTier.IRON;
                if (stack.getItem() instanceof ThrowingDiscItem discItem) {
                    tier = discItem.getTier();
                }
                ThrowingDiscEntity disc = new ThrowingDiscEntity(level, pos.x, pos.y, pos.z, stack.copyWithCount(1), tier);
                disc.setDeltaMovement(
                        facing.getStepX() * 1.5,
                        facing.getStepY() * 1.5,
                        facing.getStepZ() * 1.5
                );
                disc.setDispensed(true);
                level.addFreshEntity(disc);
                stack.shrink(1);
                return stack;
            }

            @Override
            protected void playSound(BlockSource source) {
                source.level().levelEvent(1000, source.pos(), 0);
            }
        };

        DispenserBlock.registerBehavior(COPPER_THROWING_DISC, behavior);
        DispenserBlock.registerBehavior(IRON_THROWING_DISC, behavior);
        DispenserBlock.registerBehavior(GOLDEN_THROWING_DISC, behavior);
        DispenserBlock.registerBehavior(DIAMOND_THROWING_DISC, behavior);
        DispenserBlock.registerBehavior(NETHERITE_THROWING_DISC, behavior);
        DispenserBlock.registerBehavior(CAPTAIN_AMERICA_SHIELD, behavior);
    }
}
