package io.github.derexxd.throwing_disc.entity;

import io.github.derexxd.throwing_disc.item.ModItems;
import io.github.derexxd.throwing_disc.item.ThrowingDiscTier;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ThrowingDiscEntity extends ProjectileItemEntity {
    private static final DataParameter<Boolean> ID_RETURNING = EntityDataManager.defineId(ThrowingDiscEntity.class, DataSerializers.BOOLEAN);
    private static final Set<ThrowingDiscEntity> ACTIVE_DISCS = new HashSet<>();
    private ThrowingDiscTier tier = ThrowingDiscTier.IRON;
    private Hand thrownHand = Hand.MAIN_HAND;
    private UUID ownerUUID;
    private int flightTicks = 0;
    private int bounces = 0;
    private final Set<UUID> hitEntities = new HashSet<>();

    public ThrowingDiscEntity(EntityType<? extends ProjectileItemEntity> entityType, World level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public ThrowingDiscEntity(World level, LivingEntity shooter, ItemStack stack, ThrowingDiscTier tier, Hand hand) {
        super(ModEntities.THROWING_DISC.get(), shooter, level);
        this.setItem(stack.copy());
        this.tier = tier;
        this.thrownHand = hand;
        this.ownerUUID = shooter.getUUID();
        this.setNoGravity(true);
        if (!level.isClientSide) {
            ACTIVE_DISCS.add(this);
        }
    }

    public ThrowingDiscTier getTier() {
        return this.tier;
    }

    public Hand getThrownHand() {
        return this.thrownHand;
    }

    public boolean isReturning() {
        return this.entityData.get(ID_RETURNING);
    }

    public void setReturning(boolean returning) {
        this.entityData.set(ID_RETURNING, returning);
        if (returning) {
            this.hitEntities.clear();
            this.noPhysics = true;
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.IRON_THROWING_DISC.get();
    }

    @Override
    protected float getGravity() {
        return 0.0F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_RETURNING, false);
    }

    @Override
    public void tick() {
        this.baseTick();
        this.flightTicks++;

        if (isReturning()) {
            this.noPhysics = true;

            if (!this.level.isClientSide) {
                if (this.ownerUUID != null) {
                    ACTIVE_DISCS.add(this);
                }
                Entity owner = getOwner();

                if (this.flightTicks > 240) {
                    recoverOrDrop(owner);
                    this.remove();
                    return;
                }

                if (owner instanceof PlayerEntity && owner.isAlive()) {
                    PlayerEntity player = (PlayerEntity) owner;
                    Vector3d target = new Vector3d(player.getX(), player.getEyeY() - 0.2, player.getZ());
                    Vector3d dir = target.subtract(position());

                    if (dir.length() < 1.3) {
                        if (!player.abilities.instabuild) {
                            ItemStack returnStack = getItem();
                            if (player.getItemInHand(this.thrownHand).isEmpty()) {
                                player.setItemInHand(this.thrownHand, returnStack);
                            } else if (!player.inventory.add(returnStack)) {
                                player.drop(returnStack, false);
                            }
                        }
                        this.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RETURN, SoundCategory.PLAYERS, 0.6F, 1.0F);
                        this.remove();
                        return;
                    }

                    Vector3d desiredVelocity = dir.normalize().scale(Math.min(1.2, 0.5 + (this.flightTicks * 0.02)));
                    Vector3d cur = getDeltaMovement();
                    setDeltaMovement(new Vector3d(
                            cur.x + (desiredVelocity.x - cur.x) * 0.28,
                            cur.y + (desiredVelocity.y - cur.y) * 0.28,
                            cur.z + (desiredVelocity.z - cur.z) * 0.28
                    ));
                } else {
                    spawnAtLocation(getItem());
                    this.remove();
                    return;
                }
            }

            setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z);
            return;
        }

        if (!this.level.isClientSide) {
            if (this.ownerUUID != null) {
                ACTIVE_DISCS.add(this);
            }
            if (this.flightTicks > 240) {
                recoverOrDrop(getOwner());
                this.remove();
                return;
            }
            if (this.flightTicks >= 25) {
                setReturning(true);
                return;
            }
        }

        Vector3d start = position();
        Vector3d motion = getDeltaMovement();
        Vector3d end = start.add(motion);

        BlockRayTraceResult blockHit = this.level.clip(new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
        Vector3d rayLimit = blockHit.getType() != RayTraceResult.Type.MISS ? blockHit.getLocation() : end;

        EntityRayTraceResult entityHit = ProjectileHelper.getEntityHitResult(
                this.level,
                this,
                start,
                rayLimit,
                getBoundingBox().expandTowards(motion).inflate(1.0),
                this::canHitEntity
        );

        if (entityHit != null) {
            onHitEntity(entityHit);
        }

        if (blockHit.getType() != RayTraceResult.Type.MISS) {
            onHitBlock(blockHit);
        } else {
            setPos(end.x, end.y, end.z);
            checkInsideBlocks();
        }
    }

    private void recoverOrDrop(Entity owner) {
        if (owner instanceof PlayerEntity && owner.isAlive()) {
            PlayerEntity player = (PlayerEntity) owner;
            if (!player.abilities.instabuild) {
                ItemStack returnStack = getItem();
                if (player.getItemInHand(this.thrownHand).isEmpty()) {
                    player.setItemInHand(this.thrownHand, returnStack);
                } else if (!player.inventory.add(returnStack)) {
                    player.drop(returnStack, false);
                }
            }
        } else {
            spawnAtLocation(getItem());
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity.isSpectator() || !entity.isAlive() || !entity.isPickable()) {
            return false;
        }
        Entity owner = getOwner();
        if (entity == owner) {
            return false;
        }
        return !this.hitEntities.contains(entity.getUUID());
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult hitResult) {
        if (isReturning()) {
            return;
        }
        Entity target = hitResult.getEntity();
        if (target == getOwner() || this.hitEntities.contains(target.getUUID())) {
            return;
        }

        this.hitEntities.add(target.getUUID());
        Entity owner = getOwner();
        DamageSource damageSource = DamageSource.thrown(this, owner);
        target.hurt(damageSource, this.tier.getDamage());
        playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
        if (this.level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld) this.level;
            serverLevel.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(0.5), target.getZ(), 12, 0.25, 0.25, 0.25, 0.15);
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult hitResult) {
        if (isReturning()) {
            return;
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = this.level.getBlockState(pos);
        state.onProjectileHit(this.level, state, hitResult, this);

        Direction face = hitResult.getDirection();
        Vector3d normal = new Vector3d(face.getStepX(), face.getStepY(), face.getStepZ());
        Vector3d motion = getDeltaMovement();

        double dot = motion.dot(normal);
        if (dot < 0.0) {
            Vector3d reflected = motion.subtract(normal.scale(2.0 * dot));
            Vector3d hitPos = hitResult.getLocation().add(normal.scale(0.05));
            setPos(hitPos.x, hitPos.y, hitPos.z);
            setDeltaMovement(reflected);
            playSound(SoundEvents.TRIDENT_HIT_GROUND, 0.9F, 1.2F);

            this.bounces++;
            if (this.bounces >= this.tier.getMaxBounces()) {
                setReturning(true);
            }
        } else {
            Vector3d end = position().add(getDeltaMovement());
            setPos(end.x, end.y, end.z);
        }
    }

    @Override
    public void remove() {
        ACTIVE_DISCS.remove(this);
        super.remove();
    }

    public static void recoverForPlayer(PlayerEntity player) {
        UUID playerId = player.getUUID();
        for (ThrowingDiscEntity disc : new HashSet<>(ACTIVE_DISCS)) {
            if (!disc.removed && playerId.equals(disc.ownerUUID)) {
                if (!player.abilities.instabuild) {
                    ItemStack returnStack = disc.getItem();
                    if (!player.inventory.add(returnStack)) {
                        player.drop(returnStack, false);
                    }
                }
                disc.remove();
            }
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("FlightTicks", this.flightTicks);
        compound.putInt("Bounces", this.bounces);
        compound.putBoolean("Returning", isReturning());
        compound.putString("Tier", this.tier.name());
        compound.putInt("ThrownHand", this.thrownHand.ordinal());
        if (this.ownerUUID != null) {
            compound.putUUID("OwnerUUID", this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.flightTicks = compound.getInt("FlightTicks");
        this.bounces = compound.getInt("Bounces");
        if (compound.contains("Returning")) {
            setReturning(compound.getBoolean("Returning"));
        }
        if (compound.contains("Tier")) {
            try {
                this.tier = ThrowingDiscTier.valueOf(compound.getString("Tier"));
            } catch (Exception ignored) {
                this.tier = ThrowingDiscTier.IRON;
            }
        }
        if (compound.contains("ThrownHand")) {
            int handIdx = compound.getInt("ThrownHand");
            this.thrownHand = handIdx >= 0 && handIdx < Hand.values().length ? Hand.values()[handIdx] : Hand.MAIN_HAND;
        }
        if (compound.hasUUID("OwnerUUID")) {
            this.ownerUUID = compound.getUUID("OwnerUUID");
        }
    }
}
