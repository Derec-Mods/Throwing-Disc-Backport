package io.github.derexxd.throwing_disc.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class ThrowingDiscRenderer extends EntityRenderer<ThrowingDiscEntity> {
    private final ItemRenderer itemRenderer;

    public ThrowingDiscRenderer(EntityRenderDispatcher dispatcher, ItemRenderer itemRenderer) {
        super(dispatcher);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(ThrowingDiscEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float age = (float) entity.tickCount + partialTicks;
        float spinAngle = age * 40.0F;

        poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(spinAngle));
        poseStack.scale(1.0F, 1.0F, 1.0F);

        this.itemRenderer.renderStatic(
                entity.getItem(),
                ItemTransforms.TransformType.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrowingDiscEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
