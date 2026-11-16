package io.github.derexxd.throwing_disc;

import io.github.derexxd.throwing_disc.entity.ModEntities;
import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import io.github.derexxd.throwing_disc.item.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod(ThrowingDiscNeoForge.MODID)
public class ThrowingDiscNeoForge {
    public static final String MODID = "throwing_disc_backport";

    public ThrowingDiscNeoForge(IEventBus modEventBus) {
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ThrowingDiscEntity.recoverForPlayer(player);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ThrowingDiscEntity.recoverForPlayer(player);
        }
    }
}
