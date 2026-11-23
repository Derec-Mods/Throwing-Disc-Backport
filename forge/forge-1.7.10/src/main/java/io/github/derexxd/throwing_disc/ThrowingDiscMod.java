package io.github.derexxd.throwing_disc;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod(modid = ThrowingDiscMod.MODID, name = ThrowingDiscMod.NAME, version = ThrowingDiscMod.VERSION)
public class ThrowingDiscMod {
    public static final String MODID = "throwing_disc_backport";
    public static final String NAME = "Throwing Disc Backport";
    public static final String VERSION = "1.2.0.0";

    @Mod.Instance(MODID)
    public static ThrowingDiscMod instance;

    @SidedProxy(clientSide = "io.github.derexxd.throwing_disc.ClientProxy", serverSide = "io.github.derexxd.throwing_disc.CommonProxy")
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModItems.init();
        EntityRegistry.registerModEntity(ThrowingDiscEntity.class, "throwing_disc", 1, this, 64, 1, true);
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        FMLCommonHandler.instance().bus().register(new PlayerEventHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }
}
