package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrowingDiscItem extends Item {
    private final ThrowingDiscTier tier;

    public ThrowingDiscItem(ThrowingDiscTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public ThrowingDiscTier getTier() {
        return this.tier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 0.8F, 0.8F / (level.random.nextFloat() * 0.4F + 0.8F));
        player.getCooldowns().addCooldown(this, 10);

        if (!level.isClientSide) {
            ThrowingDiscEntity disc = new ThrowingDiscEntity(level, player, itemStack, this.tier, hand);
            disc.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 1.1F, 0.1F);
            level.addFreshEntity(disc);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.abilities.instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return this.tier.getRepairIngredient().test(repair) || super.isValidRepairItem(toRepair, repair);
    }

    @Override
    public int getEnchantmentValue() {
        return this.tier == ThrowingDiscTier.GOLDEN ? 22 : (this.tier == ThrowingDiscTier.NETHERITE ? 15 : 10);
    }
}
