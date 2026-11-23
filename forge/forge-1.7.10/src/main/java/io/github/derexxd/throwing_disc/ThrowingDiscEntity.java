package io.github.derexxd.throwing_disc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ThrowingDiscEntity extends EntityThrowable {
    private static Set<ThrowingDiscEntity> ACTIVE_DISCS = new HashSet<>();
    private ThrowingDiscTier tier = ThrowingDiscTier.IRON;
    private ItemStack itemStack;
    private UUID ownerUUID;
    private int flightTicks = 0;
    private int bounces = 0;
    private Set<UUID> hitEntities = new HashSet<>();

    public ThrowingDiscEntity(World world) {
        super(world);
        setSize(0.5F, 0.2F);
    }

    public ThrowingDiscEntity(World world, EntityLivingBase thrower, ItemStack stack, ThrowingDiscTier tier) {
        super(world, thrower);
        this.itemStack = stack.copy();
        this.tier = tier;
        this.ownerUUID = thrower.getUniqueID();
        setSize(0.5F, 0.2F);

        float speed = 1.2F;
        this.motionX = -Math.sin(thrower.rotationYaw / 180.0F * (float) Math.PI) * Math.cos(thrower.rotationPitch / 180.0F * (float) Math.PI) * speed;
        this.motionZ = Math.cos(thrower.rotationYaw / 180.0F * (float) Math.PI) * Math.cos(thrower.rotationPitch / 180.0F * (float) Math.PI) * speed;
        this.motionY = -Math.sin(thrower.rotationPitch / 180.0F * (float) Math.PI) * speed;

        if (!world.isRemote) {
            ACTIVE_DISCS.add(this);
            this.dataWatcher.updateObject(21, (byte) tier.ordinal());
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, (byte) 0);
        this.dataWatcher.addObject(21, (byte) 0);
    }

    public boolean isReturning() {
        return this.dataWatcher.getWatchableObjectByte(20) != 0;
    }

    public void setReturning(boolean returning) {
        this.dataWatcher.updateObject(20, (byte) (returning ? 1 : 0));
        if (returning) {
            this.hitEntities.clear();
        }
    }

    public ThrowingDiscTier getTier() {
        int ordinal = this.dataWatcher.getWatchableObjectByte(21);
        if (ordinal >= 0 && ordinal < ThrowingDiscTier.values().length) {
            return ThrowingDiscTier.values()[ordinal];
        }
        return this.tier != null ? this.tier : ThrowingDiscTier.IRON;
    }

    public Item getItem() {
        ThrowingDiscTier currentTier = getTier();
        switch (currentTier) {
            case COPPER: return ModItems.copper_throwing_disc;
            case GOLDEN: return ModItems.golden_throwing_disc;
            case DIAMOND: return ModItems.diamond_throwing_disc;
            case NETHERITE: return ModItems.netherite_throwing_disc;
            case CAPTAIN_AMERICA: return ModItems.captain_america_shield;
            default: return ModItems.iron_throwing_disc;
        }
    }

    public ItemStack getItemStack() {
        if (this.itemStack == null) {
            this.itemStack = new ItemStack(getItem());
        }
        return this.itemStack;
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    public void onUpdate() {
        this.flightTicks++;

        if (isReturning()) {
            if (!this.worldObj.isRemote) {
                if (this.ownerUUID != null) {
                    ACTIVE_DISCS.add(this);
                }
                EntityLivingBase thrower = getThrower();

                if (this.flightTicks > 240) {
                    recoverOrDrop(thrower);
                    setDead();
                    return;
                }

                if (thrower != null && thrower.isEntityAlive()) {
                    double targetY = thrower.posY + thrower.getEyeHeight() - 0.2;
                    double dx = thrower.posX - this.posX;
                    double dy = targetY - this.posY;
                    double dz = thrower.posZ - this.posZ;
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

                    if (dist < 1.3) {
                        recoverOrDrop(thrower);
                        this.worldObj.playSoundAtEntity(thrower, "random.pop", 0.6F, 1.0F);
                        setDead();
                        return;
                    }

                    double speed = Math.min(1.2, 0.5 + (this.flightTicks * 0.02));
                    this.motionX = this.motionX * 0.72 + (dx / dist * speed) * 0.28;
                    this.motionY = this.motionY * 0.72 + (dy / dist * speed) * 0.28;
                    this.motionZ = this.motionZ * 0.72 + (dz / dist * speed) * 0.28;
                } else {
                    entityDropItem(getItemStack(), 0.0F);
                    setDead();
                    return;
                }
            }

            AxisAlignedBB checkAabb = this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0);
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, checkAabb);
            if (list != null) {
                for (Object obj : list) {
                    if (obj instanceof EntityLivingBase target) {
                        if (target.isEntityAlive() && target != getThrower() && !this.hitEntities.contains(target.getUniqueID())) {
                            this.hitEntities.add(target.getUniqueID());
                            target.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), getTier().getDamage());
                            this.worldObj.playSoundAtEntity(this, "damage.hit", 1.0F, 1.0F);
                            this.worldObj.spawnParticle("crit", target.posX, target.posY + target.height * 0.5, target.posZ, 0.0, 0.0, 0.0);
                        }
                    }
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            setPosition(this.posX, this.posY, this.posZ);
            return;
        }

        if (!this.worldObj.isRemote) {
            if (this.ownerUUID != null) {
                ACTIVE_DISCS.add(this);
            }
            if (this.flightTicks > 240) {
                recoverOrDrop(getThrower());
                setDead();
                return;
            }
            if (this.flightTicks >= 25) {
                setReturning(true);
                return;
            }
        }

        super.onUpdate();
    }

    @Override
    protected void onImpact(MovingObjectPosition mop) {
        if (isReturning()) {
            return;
        }

        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            Entity entity = mop.entityHit;
            if (entity instanceof EntityLivingBase target) {
                if (target != getThrower() && !this.hitEntities.contains(target.getUniqueID())) {
                    this.hitEntities.add(target.getUniqueID());
                    target.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), getTier().getDamage());
                    this.worldObj.playSoundAtEntity(this, "damage.hit", 1.0F, 1.0F);
                    this.worldObj.spawnParticle("crit", target.posX, target.posY + target.height * 0.5, target.posZ, 0.0, 0.0, 0.0);
                }
            }
        } else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            double nx = 0.0;
            double ny = 0.0;
            double nz = 0.0;

            switch (mop.sideHit) {
                case 0: ny = -1.0; break;
                case 1: ny = 1.0; break;
                case 2: nz = -1.0; break;
                case 3: nz = 1.0; break;
                case 4: nx = -1.0; break;
                case 5: nx = 1.0; break;
            }

            double dot = this.motionX * nx + this.motionY * ny + this.motionZ * nz;
            if (dot < 0.0) {
                this.motionX -= 2.0 * dot * nx;
                this.motionY -= 2.0 * dot * ny;
                this.motionZ -= 2.0 * dot * nz;

                this.posX += nx * 0.05;
                this.posY += ny * 0.05;
                this.posZ += nz * 0.05;
                setPosition(this.posX, this.posY, this.posZ);

                this.worldObj.playSoundAtEntity(this, "random.bowhit", 0.9F, 1.2F);
                this.bounces++;
                if (this.bounces >= getTier().getMaxBounces()) {
                    setReturning(true);
                }
            }
        }
    }

    private void recoverOrDrop(Entity thrower) {
        ItemStack returnStack = getItemStack();
        if (thrower instanceof EntityPlayer player && player.isEntityAlive()) {
            if (!player.capabilities.isCreativeMode) {
                if (!player.inventory.addItemStackToInventory(returnStack)) {
                    player.dropPlayerItemWithRandomChoice(returnStack, false);
                }
            }
        } else {
            entityDropItem(returnStack, 0.0F);
        }
    }

    public static void recoverForPlayer(EntityPlayer player) {
        UUID playerId = player.getUniqueID();
        for (ThrowingDiscEntity disc : new HashSet<>(ACTIVE_DISCS)) {
            if (disc.isEntityAlive() && playerId.equals(disc.ownerUUID)) {
                if (!player.capabilities.isCreativeMode) {
                    ItemStack returnStack = disc.getItemStack();
                    if (!player.inventory.addItemStackToInventory(returnStack)) {
                        player.dropPlayerItemWithRandomChoice(returnStack, false);
                    }
                }
                disc.setDead();
            }
        }
    }

    @Override
    public void setDead() {
        ACTIVE_DISCS.remove(this);
        super.setDead();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("FlightTicks", this.flightTicks);
        nbt.setInteger("Bounces", this.bounces);
        nbt.setBoolean("Returning", isReturning());
        nbt.setString("Tier", getTier().name());
        if (this.itemStack != null) {
            NBTTagCompound itemTag = new NBTTagCompound();
            this.itemStack.writeToNBT(itemTag);
            nbt.setTag("Item", itemTag);
        }
        if (this.ownerUUID != null) {
            nbt.setString("OwnerUUID", this.ownerUUID.toString());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.flightTicks = nbt.getInteger("FlightTicks");
        this.bounces = nbt.getInteger("Bounces");
        if (nbt.hasKey("Returning")) {
            setReturning(nbt.getBoolean("Returning"));
        }
        if (nbt.hasKey("Tier")) {
            try {
                this.tier = ThrowingDiscTier.valueOf(nbt.getString("Tier"));
                this.dataWatcher.updateObject(21, (byte) this.tier.ordinal());
            } catch (Exception ignored) {
                this.tier = ThrowingDiscTier.IRON;
            }
        }
        if (nbt.hasKey("Item")) {
            this.itemStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Item"));
        }
        if (nbt.hasKey("OwnerUUID")) {
            try {
                this.ownerUUID = UUID.fromString(nbt.getString("OwnerUUID"));
            } catch (Exception ignored) {
            }
        }
    }
}
