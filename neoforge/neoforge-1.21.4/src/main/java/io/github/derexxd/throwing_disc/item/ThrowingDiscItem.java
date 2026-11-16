package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 0.8F, 1.2F);
        player.getCooldowns().addCooldown(stack, 10);

        if (!level.isClientSide()) {
            ThrowingDiscEntity disc = new ThrowingDiscEntity(level, player, stack, this.tier, hand);
            disc.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.1F, 1.0F);
            level.addFreshEntity(disc);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            player.setItemInHand(hand, ItemStack.EMPTY);
        }

        return InteractionResult.SUCCESS;
    }
}
