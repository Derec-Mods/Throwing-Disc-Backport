package io.github.derexxd.throwing_disc.event;

import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = "throwing_disc_backport")
public class PlayerEventHandler {
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            ThrowingDiscEntity.recoverForPlayer((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            ThrowingDiscEntity.recoverForPlayer((EntityPlayerMP) event.player);
        }
    }
}
