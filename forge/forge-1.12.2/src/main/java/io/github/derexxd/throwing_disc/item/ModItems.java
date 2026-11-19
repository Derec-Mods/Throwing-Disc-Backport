package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.ThrowingDiscMod;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ThrowingDiscMod.MODID)
public class ModItems {
    public static final List<Item> ITEMS = new ArrayList<>();

    public static final Item COPPER_THROWING_DISC = register("copper_throwing_disc", new ThrowingDiscItem(ThrowingDiscTier.COPPER));
    public static final Item IRON_THROWING_DISC = register("iron_throwing_disc", new ThrowingDiscItem(ThrowingDiscTier.IRON));
    public static final Item GOLDEN_THROWING_DISC = register("golden_throwing_disc", new ThrowingDiscItem(ThrowingDiscTier.GOLDEN));
    public static final Item DIAMOND_THROWING_DISC = register("diamond_throwing_disc", new ThrowingDiscItem(ThrowingDiscTier.DIAMOND));
    public static final Item NETHERITE_THROWING_DISC = register("netherite_throwing_disc", new ThrowingDiscItem(ThrowingDiscTier.NETHERITE));
    public static final Item CAPTAIN_AMERICA_SHIELD = register("captain_america_shield", new ThrowingDiscItem(ThrowingDiscTier.CAPTAIN_AMERICA));

    private static Item register(String name, Item item) {
        item.setRegistryName(new ResourceLocation(ThrowingDiscMod.MODID, name));
        item.setUnlocalizedName(ThrowingDiscMod.MODID + "." + name);
        ITEMS.add(item);
        return item;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Item item : ITEMS) {
            event.getRegistry().register(item);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Item item : ITEMS) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
