package io.github.derexxd.throwing_disc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ThrowingDiscItem extends Item {
    private ThrowingDiscTier tier;

    public ThrowingDiscItem(ThrowingDiscTier tier, String name) {
        this.tier = tier;
        setMaxStackSize(1);
        setMaxDamage(tier.getDurability());
        setCreativeTab(CreativeTabs.tabCombat);
        setUnlocalizedName("throwing_disc_backport." + name);
        setTextureName("throwing_disc_backport:" + name);
    }

    public ThrowingDiscTier getTier() {
        return this.tier;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        world.playSoundAtEntity(player, "random.bow", 0.8F, 0.8F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote) {
            ThrowingDiscEntity disc = new ThrowingDiscEntity(world, player, itemStack, this.tier);
            world.spawnEntityInWorld(disc);
        }

        if (!player.capabilities.isCreativeMode) {
            itemStack.stackSize--;
        }

        return itemStack;
    }
}
