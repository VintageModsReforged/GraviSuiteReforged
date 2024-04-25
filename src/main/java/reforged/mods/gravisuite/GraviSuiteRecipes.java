package reforged.mods.gravisuite;

import cpw.mods.fml.common.Loader;
import ic2.api.Ic2Recipes;
import ic2.api.Items;
import ic2.core.util.StackUtil;
import reforged.mods.gravisuite.utils.Helpers;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class GraviSuiteRecipes {

    public static void initRecipes() {
        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.ULTIMATE_LAPPACK),
                "RBR", "RDR", "RAR",
                'R', Items.getItem("lapotronCrystal"),
                'B', Items.getItem("iridiumPlate"),
                'D', Items.getItem("lapPack"),
                'A', GraviSuiteData.SUPERCONDUCTOR);

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.ULTIMATE_LAPPACK),
                "RBR", "RDR", "RAR",
                'R', Items.getItem("lapotronCrystal"),
                'B', Items.getItem("iridiumPlate"),
                'D', GraviSuiteData.ADVANCED_LAPPACK,
                'A', GraviSuiteData.SUPERCONDUCTOR);

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.ADVANCED_QUANT),
                "RAR", "DBD", "RCR",
                'R', GraviSuiteData.SUPERCONDUCTOR,
                'A', Items.getItem("quantumBodyarmor"),
                'D', GraviSuiteData.GRAVI_ENGINE,
                'B', Items.getItem("hvTransformer"),
                'C', GraviSuiteData.ULTIMATE_LAPPACK);

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.ADVANCED_LAPPACK),
                "P", "C", "L",
                'P', Items.getItem("lapPack"),
                'C', Items.getItem("advancedCircuit"),
                'L', Items.getItem("lapotronCrystal"));

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.ADVANCED_JETPACK),
                "ABA", "CDC", "EFE",
                'A', Items.getItem("carbonPlate"),
                'B', Items.getItem("electricJetpack"),
                'C', GraviSuiteData.ENGINE_BOOSTER,
                'D', GraviSuiteData.ADVANCED_LAPPACK,
                'E', Items.getItem("glassFiberCableItem"),
                'F', Items.getItem("advancedCircuit"));

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.ADVANCED_NANO),
                "ABA", "ACA", "DFD",
                'A', Items.getItem("carbonPlate"),
                'B', GraviSuiteData.ADVANCED_JETPACK,
                'C', Items.getItem("nanoBodyarmor"),
                'D', Items.getItem("glassFiberCableItem"),
                'F', Items.getItem("advancedCircuit"));

        Helpers.removeRecipeByOutput(Items.getItem("quantumBodyarmor"));
        Ic2Recipes.addCraftingRecipe(Items.getItem("quantumBodyarmor"),
                "ANA", "ILI", "IAI",
                'A', Items.getItem("advancedAlloy"),
                'N', GraviSuiteData.ADVANCED_NANO,
                'I', Items.getItem("iridiumPlate"),
                'L', Items.getItem("lapotronCrystal"));

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.ADVANCED_DRILL),
                "ABA", "CAC",
                'A', Items.getItem("overclockerUpgrade"),
                'B', Items.getItem("diamondDrill"),
                'C', Items.getItem("advancedCircuit"));

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.ADVANCED_CHAINSAW),
                " F ", "ABA", "CAC",
                'F', Item.diamond,
                'A', Items.getItem("overclockerUpgrade"),
                'B', Items.getItem("chainsaw"),
                'C', Items.getItem("advancedCircuit"));

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.VAJRA),
                "ABA", "CDC", "FGF",
                'A', Items.getItem("refinedIronIngot"),
                'B', Items.getItem("energyCrystal"),
                'C', Items.getItem("carbonPlate"),
                'D', GraviSuiteData.VAJRA_CORE,
                'F', Items.getItem("advancedAlloy"),
                'G', Items.getItem("lapotronCrystal"));

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.GRAVI_TOOL),
                "ABA", "CDC", "EFG",
                'A', Items.getItem("carbonPlate"),
                'B', Items.getItem("electricHoe"),
                'C', Items.getItem("advancedAlloy"),
                'D', Items.getItem("energyCrystal"),
                'E', Items.getItem("electricWrench"),
                'F', Items.getItem("advancedCircuit"),
                'G', Items.getItem("electricTreetap"));


        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.MAGNET),
                "ICR", "T  ", "ICL",
                'I', Item.ingotIron,
                'T', Items.getItem("teslaCoil"),
                'R', Item.redstone,
                'L', new ItemStack(Item.dyePowder, 1, 4),
                'C', Items.getItem("refinedIronIngot"));

        Ic2Recipes.addCraftingRecipe(count(GraviSuiteData.SUPERCONDUCTOR_COVER, 3),
                "RBR", "CCC", "RBR",
                'R', Items.getItem("advancedAlloy"),
                'B', Items.getItem("iridiumPlate"),
                'C', Items.getItem("carbonPlate"));

        Ic2Recipes.addCraftingRecipe(count(GraviSuiteData.SUPERCONDUCTOR, 3),
                "RRR", "CBC", "RRR",
                'R', GraviSuiteData.SUPERCONDUCTOR_COVER,
                'B', Item.ingotGold,
                'C', Items.getItem("glassFiberCableItem"));

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.COOLING_CORE),
                "RBR", "CDC", "RBR",
                'R', Items.getItem("reactorCoolantSix"),
                'B', Items.getItem("reactorHeatSwitchDiamond"),
                'C', Items.getItem("reactorPlatingHeat"),
                'D', Items.getItem("iridiumPlate"));

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.GRAVI_ENGINE),
                "RBR", "CDC", "RBR",
                'R', Items.getItem("teslaCoil"),
                'B', GraviSuiteData.SUPERCONDUCTOR,
                'C', GraviSuiteData.COOLING_CORE,
                'D', Items.getItem("hvTransformer"));

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.MAGNETRON),
                "ABA", "BCB", "ABA",
                'A', Items.getItem("refinedIronIngot"),
                'B', Items.getItem("copperIngot"),
                'C', GraviSuiteData.SUPERCONDUCTOR);

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.VAJRA_CORE),
                " A ", "BCB", "FDF",
                'A', GraviSuiteData.MAGNETRON,
                'B', Items.getItem("iridiumPlate"),
                'C', Items.getItem("teslaCoil"),
                'F', GraviSuiteData.SUPERCONDUCTOR,
                'D', Items.getItem("hvTransformer"));

        Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.ENGINE_BOOSTER),
                "ABA", "CDC", "BFB",
                'A', Item.lightStoneDust,
                'B', Items.getItem("advancedAlloy"),
                'C', Items.getItem("advancedCircuit"),
                'D', Items.getItem("overclockerUpgrade"),
                'F', Items.getItem("reactorVentDiamond"));

        if (GraviSuiteMainConfig.ENABLE_HAMMERS) {
            Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.WOOD_HAMMER),
                    "PPP", "PSP", " S ",
                    'P', Block.planks,
                    'S', Item.stick);

            Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.STONE_HAMMER),
                    "PPP", "PSP", " S ",
                    'P', Block.cobblestone,
                    'S', Item.stick);

            Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.IRON_HAMMER),
                    "PPP", "PSP", " S ",
                    'P', Item.ingotIron,
                    'S', Item.stick);

            Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.DIAMOND_HAMMER),
                    "PPP", "PSP", " S ",
                    'P', Item.diamond,
                    'S', Item.stick);

            Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.BRONZE_HAMMER),
                    "PPP", "PSP", " S ",
                    'P', Items.getItem("bronzeIngot"),
                    'S', Item.stick);
            if (Loader.isModLoaded("AppliedEnergistics")) {
                OreDictionary.registerOre("crystalCertusQuartz", appeng.api.Materials.matQuartz);

                Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.QUARTZ_HAMMER),
                        "PPP", "PSP", " S ",
                        'P', "crystalCertusQuartz",
                        'S', Item.stick);
            }
            if (Loader.isModLoaded("RedPowerBase")) {
                Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.RUBY_HAMMER),
                        "PPP", "PSP", " S ",
                        'P', "gemRuby",
                        'S', Item.stick);
                Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.SAPPHIRE_HAMMER),
                        "PPP", "PSP", " S ",
                        'P', "gemSapphire",
                        'S', Item.stick);
                Ic2Recipes.addCraftingRecipe(new ItemStack(GraviSuiteData.GREEN_SAPPHIRE_HAMMER),
                        "PPP", "PSP", " S ",
                        'P', "gemGreenSapphire",
                        'S', Item.stick);
            }
        }
    }

    public static ItemStack count(Item item, int count) {
        ItemStack stack = new ItemStack(item);
        return StackUtil.copyWithSize(stack, count);
    }
}
