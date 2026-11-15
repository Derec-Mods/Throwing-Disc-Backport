package io.github.derexxd.throwing_disc.entity;

import io.github.derexxd.throwing_disc.item.ModItems;
import io.github.derexxd.throwing_disc.item.ThrowingDiscTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ThrowingDiscEntity extends ThrowableItemProjectile {
    private ThrowingDiscTier tier = ThrowingDiscTier.IRON;
    private InteractionHand thrownHand = InteractionHand.MAIN_HAND;
    private int flightTicks = 0;
    private int bounces = 0;
    private boolean returning = false;
    private final Set<UUID> hitEntities = new HashSet<>();

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

    public boolean isReturning() {
        return this.returning;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.IRON_THROWING_DISC;
    }

    @Override
    protected float getGravity() {
        return this.returning ? 0.0F : 0.015F;
    }

    @Override
    public void tick() {
        super.tick();
        this.flightTicks++;

        if (!level().isClientSide()) {
            Entity owner = getOwner();

            if (owner == null || !owner.isAlive()) {
                if (this.returning) {
                    spawnAtLocation(getItem());
                    discard();
                    return;
                }
            }

            if (!this.returning) {
                if (this.flightTicks >= 25) {
                    this.returning = true;
                    this.hitEntities.clear();
                }
            } else if (owner != null) {
                setNoGravity(true);
                Vec3 target = owner.getEyePosition().subtract(0.0, 0.2, 0.0);
                Vec3 currentPos = position();
                Vec3 dir = target.subtract(currentPos);
                double dist = dir.length();

                if (dist < 1.3) {
                    if (owner instanceof Player player) {
                        ItemStack returnStack = getItem();
                        if (player.getItemInHand(this.thrownHand).isEmpty()) {
                            player.setItemInHand(this.thrownHand, returnStack);
                        } else if (!player.getInventory().add(returnStack)) {
                            player.drop(returnStack, false);
                        }
                        level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RETURN, SoundSource.PLAYERS, 0.6F, 1.0F);
                    }
                    discard();
                    return;
                }

                double speed = Math.min(1.5, 0.6 + (this.flightTicks * 0.025));
                Vec3 desiredVelocity = dir.normalize().scale(speed);
                Vec3 currentVelocity = getDeltaMovement();
                setDeltaMovement(currentVelocity.lerp(desiredVelocity, 0.28));
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity target = hitResult.getEntity();
        if (target == getOwner() && this.returning) {
            return;
        }

        if (this.hitEntities.contains(target.getUUID())) {
            return;
        }

        this.hitEntities.add(target.getUUID());
        Entity owner = getOwner();
        DamageSource damageSource = damageSources().thrown(this, owner);
        target.hurt(damageSource, this.tier.getDamage());
        playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level().getBlockState(pos);

        if (state.is(Blocks.DECORATED_POT)) {
            level().destroyBlock(pos, true, this);
            return;
        }

        Direction face = hitResult.getDirection();
        Direction.Axis axis = face.getAxis();
        Vec3 motion = getDeltaMovement();
        double bounciness = 0.85;

        double mx = motion.x;
        double my = motion.y;
        double mz = motion.z;

        if (axis == Direction.Axis.X) {
            mx = -mx * bounciness;
        } else if (axis == Direction.Axis.Y) {
            my = -my * bounciness;
        } else if (axis == Direction.Axis.Z) {
            mz = -mz * bounciness;
        }

        Vec3 normal = Vec3.atLowerCornerOf(face.getNormal());
        setPos(hitResult.getLocation().add(normal.scale(0.08)));
        setDeltaMovement(mx, my, mz);
        playSound(SoundEvents.TRIDENT_HIT_GROUND, 0.9F, 1.2F);

        this.bounces++;
        if (this.bounces >= this.tier.getMaxBounces()) {
            this.returning = true;
            this.hitEntities.clear();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("FlightTicks", this.flightTicks);
        compound.putInt("Bounces", this.bounces);
        compound.putBoolean("Returning", this.returning);
        compound.putString("Tier", this.tier.name());
        compound.putInt("ThrownHand", this.thrownHand.ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.flightTicks = compound.getInt("FlightTicks");
        this.bounces = compound.getInt("Bounces");
        this.returning = compound.getBoolean("Returning");
        if (compound.contains("Tier")) {
            try {
                this.tier = ThrowingDiscTier.valueOf(compound.getString("Tier"));
            } catch (Exception ignored) {
                this.tier = ThrowingDiscTier.IRON;
            }
        }
        if (compound.contains("ThrownHand")) {
            int handIdx = compound.getInt("ThrownHand");
            this.thrownHand = handIdx >= 0 && handIdx < InteractionHand.values().length ? InteractionHand.values()[handIdx] : InteractionHand.MAIN_HAND;
        }
    }
}
