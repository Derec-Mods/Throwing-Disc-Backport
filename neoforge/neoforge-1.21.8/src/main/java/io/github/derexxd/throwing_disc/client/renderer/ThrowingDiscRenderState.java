package io.github.derexxd.throwing_disc.client.renderer;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ThrowingDiscRenderState extends EntityRenderState {
    public final ItemStackRenderState item = new ItemStackRenderState();
    public float ageInTicks;
}
