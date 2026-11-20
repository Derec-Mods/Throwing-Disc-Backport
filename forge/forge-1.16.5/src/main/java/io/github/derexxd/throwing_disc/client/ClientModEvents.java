package io.github.derexxd.throwing_disc.client;

import io.github.derexxd.throwing_disc.ThrowingDiscForge;
import io.github.derexxd.throwing_disc.client.renderer.ThrowingDiscRenderer;
import io.github.derexxd.throwing_disc.entity.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ThrowingDiscForge.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                RenderingRegistry.registerEntityRenderingHandler(ModEntities.THROWING_DISC.get(), dispatcher ->
                        new ThrowingDiscRenderer(dispatcher, Minecraft.getInstance().getItemRenderer()))
        );
    }
}
