package io.github.derexxd.throwing_disc;

import io.github.derexxd.throwing_disc.entity.ModEntities;
import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import io.github.derexxd.throwing_disc.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ThrowingDiscForge.MODID)
public class ThrowingDiscForge {
    public static final String MODID = "throwing_disc_backport";
    public static final Logger LOGGER = LogManager.getLogger();

    public ThrowingDiscForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);

        MinecraftForge.EVENT_BUS.addListener(this::onPlayerRespawn);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLogout);
    }

    private void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        ThrowingDiscEntity.recoverForPlayer(event.getPlayer());
    }

    private void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ThrowingDiscEntity.recoverForPlayer(event.getPlayer());
    }
}
