package io.github.derexxd.throwing_disc;

import io.github.derexxd.throwing_disc.client.renderer.ThrowingDiscRenderer;
import io.github.derexxd.throwing_disc.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;

public class ThrowingDiscClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(ModEntities.THROWING_DISC, (dispatcher, context) ->
                new ThrowingDiscRenderer(dispatcher, Minecraft.getInstance().getItemRenderer()));
    }
}
