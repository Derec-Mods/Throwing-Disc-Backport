package io.github.derexxd.throwing_disc.entity;

import io.github.derexxd.throwing_disc.item.ModItems;
import io.github.derexxd.throwing_disc.item.ThrowingDiscTier;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ThrowingDiscEntity extends EntityThrowable implements IEntityAdditionalSpawnData {
    private static final Set<ThrowingDiscEntity> ACTIVE_DISCS = new HashSet<>();
    private ItemStack discStack = ItemStack.EMPTY;
    private ThrowingDiscTier tier = ThrowingDiscTier.IRON;
    private EnumHand thrownHand = EnumHand.MAIN_HAND;
    private UUID ownerUUID;
    private int flightTicks = 0;
    private int bounces = 0;
    private int lastBounceTick = -1;
    private int bouncesThisTick = 0;
    private boolean returning = false;
    private final Set<Integer> hitEntities = new HashSet<>();

    public ThrowingDiscEntity(World world) {
        super(world);
        this.setSize(0.5F, 0.15F);
    }

    public ThrowingDiscEntity(World world, EntityLivingBase thrower, ItemStack stack, ThrowingDiscTier tier, EnumHand hand) {
        super(world, thrower);
        this.discStack = stack.copy();
        this.discStack.setCount(1);
        this.tier = tier;
        this.thrownHand = hand;
        this.ownerUUID = thrower.getUniqueID();
        this.setSize(0.5F, 0.15F);
        if (!world.isRemote) {
            ACTIVE_DISCS.add(this);
        }
    }

    public ItemStack getDiscStack() {
        return this.discStack.isEmpty() ? new ItemStack(ModItems.IRON_THROWING_DISC) : this.discStack;
    }

    public ItemStack getItem() {
        return this.getDiscStack();
    }

    public ThrowingDiscTier getTier() {
        return this.tier;
    }

    public EnumHand getThrownHand() {
        return this.thrownHand;
    }

    public boolean isReturning() {
        return this.returning;
    }

    public EntityLivingBase getOwner() {
        EntityLivingBase thrower = this.getThrower();
        if (thrower == null && this.ownerUUID != null && this.world instanceof WorldServer) {
            Entity entity = ((WorldServer) this.world).getEntityFromUuid(this.ownerUUID);
            if (entity instanceof EntityLivingBase) {
                return (EntityLivingBase) entity;
            }
        }
        return thrower;
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    public void onUpdate() {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        this.ticksExisted++;
        this.flightTicks++;

        if (this.world.isRemote) {
            if (this.isBurning()) {
                this.world.spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * 0.2D, this.posY + (this.rand.nextDouble() - 0.5D) * 0.2D, this.posZ + (this.rand.nextDouble() - 0.5D) * 0.2D, 0.0D, 0.0D, 0.0D);
            }
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.setPosition(this.posX, this.posY, this.posZ);
            return;
        }

        BlockPos currentBlockPos = new BlockPos(this.posX, this.posY, this.posZ);
        IBlockState currentState = this.world.getBlockState(currentBlockPos);
        if (currentState.getMaterial() == Material.FIRE || currentState.getMaterial() == Material.LAVA) {
            this.setFire(5);
        }

        if (this.ownerUUID != null) {
            ACTIVE_DISCS.add(this);
        }
        EntityLivingBase owner = this.getOwner();

        if (this.flightTicks > 240) {
            if (owner instanceof EntityPlayer && owner.isEntityAlive()) {
                EntityPlayer player = (EntityPlayer) owner;
                if (!player.capabilities.isCreativeMode) {
                    ItemStack returnStack = this.getDiscStack();
                    returnStack.setCount(1);
                    if (!player.inventory.addItemStackToInventory(returnStack)) {
                        player.dropItem(returnStack, false);
                    }
                }
            } else {
                this.entityDropItem(this.getDiscStack(), 0.0F);
            }
            this.setDead();
            return;
        }

        if (!this.returning) {
            if (this.flightTicks >= 25) {
                this.returning = true;
                this.hitEntities.clear();
                this.noClip = true;
            }
        } else if (owner != null) {
            this.noClip = true;
            Vec3d target = new Vec3d(owner.posX, owner.posY + (double) owner.getEyeHeight() - 0.2, owner.posZ);
            Vec3d currentPos = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d dir = target.subtract(currentPos);
            double dist = dir.lengthVector();

            if (dist < 1.5) {
                if (owner instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) owner;
                    if (!player.capabilities.isCreativeMode) {
                        ItemStack returnStack = this.getDiscStack();
                        if (player.getHeldItem(this.thrownHand).isEmpty()) {
                            player.setHeldItem(this.thrownHand, returnStack);
                        } else if (!player.inventory.addItemStackToInventory(returnStack)) {
                            player.dropItem(returnStack, false);
                        }
                    }
                    this.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 0.6F, 1.2F);
                }
                this.setDead();
                return;
            }

            double speed = Math.min(1.2, 0.5 + ((double) this.flightTicks * 0.02));
            Vec3d desiredVelocity = dir.normalize().scale(speed);
            Vec3d currentVelocity = new Vec3d(this.motionX, this.motionY, this.motionZ);
            Vec3d lerped = currentVelocity.add(desiredVelocity.subtract(currentVelocity).scale(0.28));
            this.motionX = lerped.x;
            this.motionY = lerped.y;
            this.motionZ = lerped.z;
        }

        Vec3d start = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d end = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult blockHit = this.returning ? null : this.world.rayTraceBlocks(start, end, false, true, false);

        Vec3d entityRayEnd = blockHit != null ? blockHit.hitVec : end;
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
        double closestDist = 0.0D;
        Entity hitEntity = null;

        for (Entity entity : list) {
            if (entity.canBeCollidedWith()) {
                if (entity == owner && (this.flightTicks < 5 || this.returning)) {
                    continue;
                }
                AxisAlignedBB aabb = entity.getEntityBoundingBox().grow(0.3D);
                RayTraceResult intercept = aabb.calculateIntercept(start, entityRayEnd);
                if (intercept != null) {
                    double dist = start.squareDistanceTo(intercept.hitVec);
                    if (dist < closestDist || closestDist == 0.0D) {
                        hitEntity = entity;
                        closestDist = dist;
                    }
                }
            }
        }

        if (hitEntity != null && !this.hitEntities.contains(hitEntity.getEntityId())) {
            this.hitEntities.add(hitEntity.getEntityId());
            DamageSource damageSource = owner != null ? DamageSource.causeThrownDamage(this, owner) : DamageSource.GENERIC;
            hitEntity.attackEntityFrom(damageSource, this.tier.getDamage());
            if (this.isBurning()) {
                hitEntity.setFire(5);
            }
            if (this.world instanceof WorldServer) {
                ((WorldServer) this.world).spawnParticle(EnumParticleTypes.CRIT, hitEntity.posX, hitEntity.posY + hitEntity.height * 0.5, hitEntity.posZ, 12, 0.25, 0.25, 0.25, 0.15);
            }
            this.playSound(SoundEvents.BLOCK_ANVIL_HIT, 0.6F, 1.5F);
        }

        if (blockHit != null && !this.returning) {
            EnumFacing face = blockHit.sideHit;
            if (face != null) {
                Vec3d normal = new Vec3d(face.getDirectionVec());
                Vec3d motion = new Vec3d(this.motionX, this.motionY, this.motionZ);
                double dot = motion.dotProduct(normal);

                if (dot < 0.0) {
                    if (this.ticksExisted == this.lastBounceTick) {
                        this.bouncesThisTick++;
                        if (this.bouncesThisTick > 2) {
                            this.returning = true;
                            this.hitEntities.clear();
                            this.noClip = true;
                            return;
                        }
                    } else {
                        this.lastBounceTick = this.ticksExisted;
                        this.bouncesThisTick = 1;
                    }

                    Vec3d reflected = motion.subtract(normal.scale(2.0 * dot));
                    if (reflected.lengthVector() > 1.25) {
                        reflected = reflected.normalize().scale(1.25);
                    }

                    this.posX = blockHit.hitVec.x + normal.x * 0.15D;
                    this.posY = blockHit.hitVec.y + normal.y * 0.15D;
                    this.posZ = blockHit.hitVec.z + normal.z * 0.15D;
                    this.setPosition(this.posX, this.posY, this.posZ);

                    this.motionX = reflected.x;
                    this.motionY = reflected.y;
                    this.motionZ = reflected.z;
                    this.playSound(SoundEvents.BLOCK_ANVIL_LAND, 0.5F, 1.8F);

                    this.bounces++;
                    if (this.bounces >= this.tier.getMaxBounces()) {
                        this.returning = true;
                        this.hitEntities.clear();
                        this.noClip = true;
                    }
                    return;
                }
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
    }

    @Override
    public void setDead() {
        ACTIVE_DISCS.remove(this);
        super.setDead();
    }

    public static void recoverForPlayer(EntityPlayerMP player) {
        UUID playerId = player.getUniqueID();
        for (ThrowingDiscEntity disc : new HashSet<>(ACTIVE_DISCS)) {
            if (disc.isEntityAlive() && playerId.equals(disc.ownerUUID)) {
                if (!player.capabilities.isCreativeMode) {
                    ItemStack returnStack = disc.getDiscStack();
                    if (!player.inventory.addItemStackToInventory(returnStack)) {
                        player.dropItem(returnStack, false);
                    }
                }
                disc.setDead();
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("FlightTicks", this.flightTicks);
        compound.setInteger("Bounces", this.bounces);
        compound.setBoolean("Returning", this.returning);
        compound.setString("Tier", this.tier.name());
        compound.setInteger("ThrownHand", this.thrownHand.ordinal());
        if (!this.discStack.isEmpty()) {
            compound.setTag("DiscStack", this.discStack.writeToNBT(new NBTTagCompound()));
        }
        if (this.ownerUUID != null) {
            compound.setUniqueId("OwnerUUID", this.ownerUUID);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.flightTicks = compound.getInteger("FlightTicks");
        this.bounces = compound.getInteger("Bounces");
        this.returning = compound.getBoolean("Returning");
        if (compound.hasKey("Tier")) {
            try {
                this.tier = ThrowingDiscTier.valueOf(compound.getString("Tier"));
            } catch (Exception ignored) {
                this.tier = ThrowingDiscTier.IRON;
            }
        }
        if (compound.hasKey("ThrownHand")) {
            int handIdx = compound.getInteger("ThrownHand");
            this.thrownHand = handIdx >= 0 && handIdx < EnumHand.values().length ? EnumHand.values()[handIdx] : EnumHand.MAIN_HAND;
        }
        if (compound.hasKey("DiscStack")) {
            this.discStack = new ItemStack(compound.getCompoundTag("DiscStack"));
        }
        if (compound.hasUniqueId("OwnerUUID")) {
            this.ownerUUID = compound.getUniqueId("OwnerUUID");
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        ByteBufUtils.writeItemStack(buffer, this.getDiscStack());
        buffer.writeInt(this.tier.ordinal());
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.discStack = ByteBufUtils.readItemStack(buffer);
        int tierIdx = buffer.readInt();
        if (tierIdx >= 0 && tierIdx < ThrowingDiscTier.values().length) {
            this.tier = ThrowingDiscTier.values()[tierIdx];
        }
    }
}
