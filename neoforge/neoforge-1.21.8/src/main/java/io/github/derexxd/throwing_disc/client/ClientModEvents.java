package io.github.derexxd.throwing_disc.client;

import io.github.derexxd.throwing_disc.client.renderer.ThrowingDiscRenderer;
import io.github.derexxd.throwing_disc.entity.ModEntities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientModEvents {
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.THROWING_DISC.get(), ThrowingDiscRenderer::new);
    }
}
