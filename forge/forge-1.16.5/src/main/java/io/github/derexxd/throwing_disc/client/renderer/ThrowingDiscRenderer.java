package io.github.derexxd.throwing_disc.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ThrowingDiscRenderer extends EntityRenderer<ThrowingDiscEntity> {
    private final ItemRenderer itemRenderer;

    public ThrowingDiscRenderer(EntityRendererManager dispatcher, ItemRenderer itemRenderer) {
        super(dispatcher);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(ThrowingDiscEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        matrixStack.pushPose();

        float age = (float) entity.tickCount + partialTicks;
        float spinAngle = age * 40.0F;

        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(spinAngle));
        matrixStack.scale(1.0F, 1.0F, 1.0F);

        this.itemRenderer.renderStatic(
                entity.getItem(),
                ItemCameraTransforms.TransformType.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                matrixStack,
                buffer
        );

        matrixStack.popPose();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrowingDiscEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS;
    }
}
