package io.github.derexxd.throwing_disc.client.renderer;

import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import io.github.derexxd.throwing_disc.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ThrowingDiscRenderer extends Render<ThrowingDiscEntity> {
    private final RenderItem itemRenderer;

    public ThrowingDiscRenderer(RenderManager renderManager) {
        super(renderManager);
        this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
        this.shadowSize = 0.15F;
    }

    @Override
    public void doRender(ThrowingDiscEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();

        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);

        float age = (float) entity.ticksExisted + partialTicks;
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(age * 40.0F, 0.0F, 0.0F, 1.0F);

        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) {
            stack = new ItemStack(ModItems.IRON_THROWING_DISC);
        }

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.GROUND);

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(ThrowingDiscEntity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
