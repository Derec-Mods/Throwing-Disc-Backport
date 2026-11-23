package io.github.derexxd.throwing_disc;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ThrowingDiscRenderer extends Render {
    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (!(entity instanceof ThrowingDiscEntity disc)) {
            return;
        }

        Item item = disc.getItem();
        if (item == null) {
            return;
        }

        IIcon icon = item.getIconFromDamage(0);
        if (icon == null) {
            return;
        }

        bindEntityTexture(disc);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        float age = (float) disc.ticksExisted + partialTicks;
        float spinAngle = age * 40.0F;

        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(spinAngle, 0.0F, 0.0F, 1.0F);

        float scale = 0.5F;
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(-0.5F, -0.5F, 0.03125F);

        Tessellator tessellator = Tessellator.instance;
        ItemRenderer.renderItemIn2D(
                tessellator,
                icon.getMaxU(),
                icon.getMinV(),
                icon.getMinU(),
                icon.getMaxV(),
                icon.getIconWidth(),
                icon.getIconHeight(),
                0.0625F
        );

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.locationItemsTexture;
    }
}
