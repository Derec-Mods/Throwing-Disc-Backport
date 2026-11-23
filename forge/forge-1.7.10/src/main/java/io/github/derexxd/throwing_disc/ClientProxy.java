package io.github.derexxd.throwing_disc;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        super.init();
        RenderingRegistry.registerEntityRenderingHandler(ThrowingDiscEntity.class, new ThrowingDiscRenderer());
    }
}
