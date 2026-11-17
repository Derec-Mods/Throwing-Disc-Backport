package io.github.derexxd.throwing_disc;

import io.github.derexxd.throwing_disc.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = ThrowingDiscMod.MODID,
    name = ThrowingDiscMod.NAME,
    version = ThrowingDiscMod.VERSION,
    acceptedMinecraftVersions = "[1.12.2]"
)
public class ThrowingDiscMod {
    public static final String MODID = "throwing_disc_backport";
    public static final String NAME = "Throwing Disc Backport";
    public static final String VERSION = "1.0.0.3";

    @Mod.Instance(MODID)
    public static ThrowingDiscMod instance;

    @SidedProxy(
        clientSide = "io.github.derexxd.throwing_disc.proxy.ClientProxy",
        serverSide = "io.github.derexxd.throwing_disc.proxy.CommonProxy"
    )
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
