package io.github.derexxd.throwing_disc.item;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public enum ThrowingDiscTier {
    COPPER(5.0F, 190, 5, () -> new ItemStack(Items.IRON_INGOT)),
    IRON(6.0F, 250, 5, () -> new ItemStack(Items.IRON_INGOT)),
    GOLDEN(4.5F, 64, 5, () -> new ItemStack(Items.GOLD_INGOT)),
    DIAMOND(7.0F, 1561, 5, () -> new ItemStack(Items.DIAMOND)),
    NETHERITE(8.0F, 2031, 5, () -> new ItemStack(Items.DIAMOND)),
    CAPTAIN_AMERICA(10.0F, 3000, 10, () -> new ItemStack(Items.DIAMOND));

    private final float damage;
    private final int durability;
    private final int maxBounces;
    private final Supplier<ItemStack> repairStackSupplier;

    ThrowingDiscTier(float damage, int durability, int maxBounces, Supplier<ItemStack> repairStackSupplier) {
        this.damage = damage;
        this.durability = durability;
        this.maxBounces = maxBounces;
        this.repairStackSupplier = repairStackSupplier;
    }

    public float getDamage() {
        return this.damage;
    }

    public int getDurability() {
        return this.durability;
    }

    public int getMaxBounces() {
        return this.maxBounces;
    }

    public ItemStack getRepairItemStack() {
        return this.repairStackSupplier.get();
    }
}
