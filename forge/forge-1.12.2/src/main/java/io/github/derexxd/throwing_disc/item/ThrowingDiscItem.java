package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ThrowingDiscItem extends Item {
    private final ThrowingDiscTier tier;

    public ThrowingDiscItem(ThrowingDiscTier tier) {
        this.tier = tier;
        this.setMaxStackSize(1);
        this.setMaxDamage(tier.getDurability());
        this.setCreativeTab(CreativeTabs.COMBAT);
    }

    public ThrowingDiscTier getTier() {
        return this.tier;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.8F, 1.2F);
        player.getCooldownTracker().setCooldown(this, 10);

        if (!world.isRemote) {
            ThrowingDiscEntity disc = new ThrowingDiscEntity(world, player, itemStack, this.tier, hand);
            disc.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.1F, 0.1F);
            world.spawnEntity(disc);
        }

        player.addStat(StatList.getObjectUseStats(this));
        if (!player.capabilities.isCreativeMode) {
            itemStack.shrink(1);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack target = this.tier.getRepairItemStack();
        return (!target.isEmpty() && net.minecraftforge.oredict.OreDictionary.itemMatches(target, repair, false)) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability() {
        return this.tier == ThrowingDiscTier.GOLDEN ? 22 : (this.tier == ThrowingDiscTier.NETHERITE ? 15 : 10);
    }
}
