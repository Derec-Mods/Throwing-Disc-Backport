package io.github.derexxd.throwing_disc;

import io.github.derexxd.throwing_disc.entity.ModEntities;
import io.github.derexxd.throwing_disc.item.ModItems;
import net.fabricmc.api.ModInitializer;

public class ThrowingDiscBackport implements ModInitializer {
    public static final String MODID = "throwing_disc_backport";

    @Override
    public void onInitialize() {
        ModItems.register();
        ModEntities.register();
    }
}
