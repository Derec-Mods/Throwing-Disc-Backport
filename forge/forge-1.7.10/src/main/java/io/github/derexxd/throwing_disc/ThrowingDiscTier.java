package io.github.derexxd.throwing_disc;

public enum ThrowingDiscTier {
    COPPER(5.0F, 190, 5),
    IRON(6.0F, 250, 5),
    GOLDEN(4.5F, 64, 5),
    DIAMOND(7.0F, 1561, 5),
    NETHERITE(8.0F, 2031, 5),
    CAPTAIN_AMERICA(10.0F, 3000, 10);

    private float damage;
    private int durability;
    private int maxBounces;

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
