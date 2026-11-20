package io.github.derexxd.throwing_disc.entity;

import io.github.derexxd.throwing_disc.ThrowingDiscForge;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, ThrowingDiscForge.MODID);

    public static final RegistryObject<EntityType<ThrowingDiscEntity>> THROWING_DISC = ENTITIES.register("throwing_disc",
            () -> EntityType.Builder.<ThrowingDiscEntity>of(ThrowingDiscEntity::new, EntityClassification.MISC)
                    .sized(0.5F, 0.15F)
                    .clientTrackingRange(64)
                    .updateInterval(10)
                    .build(new ResourceLocation(ThrowingDiscForge.MODID, "throwing_disc").toString())
    );

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
