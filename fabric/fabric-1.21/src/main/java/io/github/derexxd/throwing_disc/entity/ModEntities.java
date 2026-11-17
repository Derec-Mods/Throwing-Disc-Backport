package io.github.derexxd.throwing_disc.entity;

import io.github.derexxd.throwing_disc.ThrowingDiscBackport;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final EntityType<ThrowingDiscEntity> THROWING_DISC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(ThrowingDiscBackport.MODID, "throwing_disc"),
            EntityType.Builder.<ThrowingDiscEntity>of(ThrowingDiscEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.2F)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build("throwing_disc")
    );

    public static void register() {
    }
}
