package io.github.derexxd.throwing_disc.entity;

import io.github.derexxd.throwing_disc.ThrowingDiscBackport;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final EntityType<ThrowingDiscEntity> THROWING_DISC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(ThrowingDiscBackport.MODID, "throwing_disc"),
            FabricEntityTypeBuilder.<ThrowingDiscEntity>create(MobCategory.MISC, ThrowingDiscEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5F, 0.15F))
                    .trackRangeBlocks(64)
                    .trackedUpdateRate(10)
                    .build()
    );

    public static void register() {
    }
}
