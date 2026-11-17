package io.github.derexxd.throwing_disc.entity;

import io.github.derexxd.throwing_disc.ThrowingDiscNeoForge;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThrowingDiscNeoForge.MODID);

    private static final ResourceKey<EntityType<?>> THROWING_DISC_KEY = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ThrowingDiscNeoForge.MODID, "throwing_disc"));

    public static final DeferredHolder<EntityType<?>, EntityType<ThrowingDiscEntity>> THROWING_DISC = ENTITY_TYPES.register("throwing_disc",
            () -> EntityType.Builder.<ThrowingDiscEntity>of(ThrowingDiscEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.2F)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build(THROWING_DISC_KEY)
    );

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
