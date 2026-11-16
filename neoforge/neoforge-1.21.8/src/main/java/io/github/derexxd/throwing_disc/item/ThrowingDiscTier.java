package io.github.derexxd.throwing_disc.item;

public enum ThrowingDiscTier {
    COPPER(5.0F, 120, 2),
    IRON(7.0F, 250, 3),
    GOLDEN(5.0F, 60, 4),
    DIAMOND(9.0F, 1000, 5),
    NETHERITE(11.0F, 1800, 6),
    CAPTAIN_AMERICA(10.0F, 3000, 10);

    private final float damage;
    private final int durability;
    private final int maxBounces;

    ThrowingDiscTier(float damage, int durability, int maxBounces) {
        this.damage = damage;
        this.durability = durability;
        this.maxBounces = maxBounces;
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
}
