package io.github.derexxd.throwing_disc.client;

import io.github.derexxd.throwing_disc.ThrowingDiscNeoForge;
import io.github.derexxd.throwing_disc.client.renderer.ThrowingDiscRenderer;
import io.github.derexxd.throwing_disc.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ThrowingDiscNeoForge.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.THROWING_DISC.get(), ThrowingDiscRenderer::new);
    }
}
