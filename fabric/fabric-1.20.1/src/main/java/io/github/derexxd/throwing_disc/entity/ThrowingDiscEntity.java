package io.github.derexxd.throwing_disc.entity;

import io.github.derexxd.throwing_disc.item.ModItems;
import io.github.derexxd.throwing_disc.item.ThrowingDiscTier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrowingDiscEntity extends ThrowableItemProjectile {
    private ThrowingDiscTier tier = ThrowingDiscTier.IRON;
    private InteractionHand thrownHand = InteractionHand.MAIN_HAND;

    public ThrowingDiscEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ThrowingDiscEntity(Level level, LivingEntity shooter, ItemStack stack, ThrowingDiscTier tier, InteractionHand hand) {
        super(ModEntities.THROWING_DISC, shooter, level);
        this.setItem(stack.copy());
        this.tier = tier;
        this.thrownHand = hand;
    }

    public ThrowingDiscTier getTier() {
        return this.tier;
    }

    public InteractionHand getThrownHand() {
        return this.thrownHand;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.IRON_THROWING_DISC;
    }
}
