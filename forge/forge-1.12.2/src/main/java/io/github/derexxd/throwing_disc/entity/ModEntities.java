package io.github.derexxd.throwing_disc.entity;

import io.github.derexxd.throwing_disc.ThrowingDiscMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {
    public static void registerEntities() {
        EntityRegistry.registerModEntity(
            new ResourceLocation(ThrowingDiscMod.MODID, "throwing_disc"),
            ThrowingDiscEntity.class,
            "throwing_disc",
            1,
            ThrowingDiscMod.instance,
            64,
            1,
            true
        );
    }
}
