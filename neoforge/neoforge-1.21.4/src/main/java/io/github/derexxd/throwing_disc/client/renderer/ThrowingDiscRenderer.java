package io.github.derexxd.throwing_disc.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;

public class ThrowingDiscRenderer extends EntityRenderer<ThrowingDiscEntity, ThrowingDiscRenderState> {
    private final ItemModelResolver itemModelResolver;

    public ThrowingDiscRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public ThrowingDiscRenderState createRenderState() {
        return new ThrowingDiscRenderState();
    }

    @Override
    public void extractRenderState(ThrowingDiscEntity entity, ThrowingDiscRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        this.itemModelResolver.updateForNonLiving(state.item, entity.getItem(), ItemDisplayContext.FIXED, entity);
        state.ageInTicks = (float) entity.tickCount + partialTicks;
    }

    @Override
    public void render(ThrowingDiscRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float spinAngle = state.ageInTicks * 40.0F;

        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(spinAngle));
        poseStack.scale(1.0F, 1.0F, 1.0F);

        state.item.render(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        super.render(state, poseStack, buffer, packedLight);
    }
}
