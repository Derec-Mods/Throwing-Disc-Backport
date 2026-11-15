package io.github.derexxd.throwing_disc;

import io.github.derexxd.throwing_disc.entity.ModEntities;
import io.github.derexxd.throwing_disc.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThrowingDiscBackport implements ModInitializer {
    public static final String MODID = "throwing_disc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Throwing Disc Backport");
        ModItems.register();
        ModEntities.register();
    }
}
