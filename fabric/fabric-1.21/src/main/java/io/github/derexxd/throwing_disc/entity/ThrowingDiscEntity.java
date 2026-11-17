package io.github.derexxd.throwing_disc.entity;

import io.github.derexxd.throwing_disc.item.ModItems;
import io.github.derexxd.throwing_disc.item.ThrowingDiscTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
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
    private static final Set<ThrowingDiscEntity> ACTIVE_DISCS = new HashSet<>();
    private ThrowingDiscTier tier = ThrowingDiscTier.IRON;
    private InteractionHand thrownHand = InteractionHand.MAIN_HAND;
    private UUID ownerUUID;
    private int flightTicks = 0;
    private int bounces = 0;
    private int lastBounceTick = -1;
    private int bouncesThisTick = 0;
    private boolean returning = false;
    private final Set<UUID> hitEntities = new HashSet<>();

    public ThrowingDiscEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public ThrowingDiscEntity(Level level, LivingEntity shooter, ItemStack stack, ThrowingDiscTier tier, InteractionHand hand) {
        super(ModEntities.THROWING_DISC, shooter, level);
        this.setItem(stack.copy());
        this.tier = tier;
        this.thrownHand = hand;
        this.ownerUUID = shooter.getUUID();
        this.setNoGravity(true);
        if (!level.isClientSide()) {
            ACTIVE_DISCS.add(this);
        }
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
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public void tick() {
        super.tick();
        this.flightTicks++;

        if (!level().isClientSide()) {
            if (this.ownerUUID != null) {
                ACTIVE_DISCS.add(this);
            }
            Entity owner = getOwner();

            if (owner == null || !owner.isAlive()) {
                if (this.flightTicks > 600) {
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
                        if (!player.getAbilities().instabuild) {
                            ItemStack returnStack = getItem();
                            if (player.getItemInHand(this.thrownHand).isEmpty()) {
                                player.setItemInHand(this.thrownHand, returnStack);
                            } else if (!player.getInventory().add(returnStack)) {
                                player.drop(returnStack, false);
                            }
                        }
                        level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RETURN, SoundSource.PLAYERS, 0.6F, 1.0F);
                    }
                    discard();
                    return;
                }

                double speed = Math.min(1.2, 0.5 + (this.flightTicks * 0.02));
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
        if (this.returning) {
            return;
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level().getBlockState(pos);

        if (state.is(Blocks.DECORATED_POT)) {
            level().destroyBlock(pos, true, this);
            return;
        }

        Direction face = hitResult.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(face.getNormal());
        Vec3 motion = getDeltaMovement();

        double dot = motion.dot(normal);
        if (dot >= 0.0) {
            return;
        }

        if (this.tickCount == this.lastBounceTick) {
            this.bouncesThisTick++;
            if (this.bouncesThisTick > 2) {
                this.returning = true;
                this.hitEntities.clear();
                return;
            }
        } else {
            this.lastBounceTick = this.tickCount;
            this.bouncesThisTick = 1;
        }

        Vec3 reflected = motion.subtract(normal.scale(2.0 * dot));
        if (reflected.length() > 1.25) {
            reflected = reflected.normalize().scale(1.25);
        }

        setPos(hitResult.getLocation().add(normal.scale(0.28)));
        setDeltaMovement(reflected);
        playSound(SoundEvents.TRIDENT_HIT_GROUND, 0.9F, 1.2F);

        this.bounces++;
        if (this.bounces >= this.tier.getMaxBounces()) {
            this.returning = true;
            this.hitEntities.clear();
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        ACTIVE_DISCS.remove(this);
        super.remove(reason);
    }

    public static void recoverForPlayer(ServerPlayer player) {
        UUID playerId = player.getUUID();
        for (ThrowingDiscEntity disc : Set.copyOf(ACTIVE_DISCS)) {
            if (!disc.isRemoved() && playerId.equals(disc.ownerUUID)) {
                if (!player.getAbilities().instabuild) {
                    ItemStack returnStack = disc.getItem();
                    if (!player.getInventory().add(returnStack)) {
                        player.drop(returnStack, false);
                    }
                }
                disc.discard();
            }
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
        if (this.ownerUUID != null) {
            compound.putUUID("OwnerUUID", this.ownerUUID);
        }
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
        if (compound.hasUUID("OwnerUUID")) {
            this.ownerUUID = compound.getUUID("OwnerUUID");
        }
    }
}
