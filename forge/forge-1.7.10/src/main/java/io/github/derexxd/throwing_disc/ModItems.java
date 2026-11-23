package io.github.derexxd.throwing_disc;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModItems {
    public static Item copper_throwing_disc;
    public static Item iron_throwing_disc;
    public static Item golden_throwing_disc;
    public static Item diamond_throwing_disc;
    public static Item netherite_throwing_disc;
    public static Item captain_america_shield;

    public static void init() {
        copper_throwing_disc = new ThrowingDiscItem(ThrowingDiscTier.COPPER, "copper_throwing_disc");
        iron_throwing_disc = new ThrowingDiscItem(ThrowingDiscTier.IRON, "iron_throwing_disc");
        golden_throwing_disc = new ThrowingDiscItem(ThrowingDiscTier.GOLDEN, "golden_throwing_disc");
        diamond_throwing_disc = new ThrowingDiscItem(ThrowingDiscTier.DIAMOND, "diamond_throwing_disc");
        netherite_throwing_disc = new ThrowingDiscItem(ThrowingDiscTier.NETHERITE, "netherite_throwing_disc");
        captain_america_shield = new ThrowingDiscItem(ThrowingDiscTier.CAPTAIN_AMERICA, "captain_america_shield");

        GameRegistry.registerItem(copper_throwing_disc, "copper_throwing_disc");
        GameRegistry.registerItem(iron_throwing_disc, "iron_throwing_disc");
        GameRegistry.registerItem(golden_throwing_disc, "golden_throwing_disc");
        GameRegistry.registerItem(diamond_throwing_disc, "diamond_throwing_disc");
        GameRegistry.registerItem(netherite_throwing_disc, "netherite_throwing_disc");
        GameRegistry.registerItem(captain_america_shield, "captain_america_shield");

        registerRecipes();
    }

    private static void registerRecipes() {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(iron_throwing_disc),
                " I ",
                "I I",
                " I ",
                'I', "ingotIron"
        ));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(golden_throwing_disc),
                " G ",
                "G G",
                " G ",
                'G', "ingotGold"
        ));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diamond_throwing_disc),
                " D ",
                "D D",
                " D ",
                'D', "gemDiamond"
        ));

        if (!OreDictionary.getOres("ingotCopper").isEmpty()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(copper_throwing_disc),
                    " C ",
                    "C C",
                    " C ",
                    'C', "ingotCopper"
            ));
        }

        if (!OreDictionary.getOres("ingotNetherite").isEmpty()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(netherite_throwing_disc),
                    " N ",
                    "NDN",
                    " N ",
                    'N', "ingotNetherite",
                    'D', diamond_throwing_disc
            ));
        } else {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(netherite_throwing_disc),
                    " O ",
                    "ODO",
                    " O ",
                    'O', Items.nether_star,
                    'D', diamond_throwing_disc
            ));
        }
    }
}
