package reforged.mods.gravisuite;

import ic2.api.item.Items;
import ic2.api.recipe.Recipes;
import mods.vintage.core.helpers.RecipeHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.utils.Helpers;

public class GraviSuiteRecipes {

    public static void initRecipes() {
        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.ultimate_lappack),
                "RBR", "RDR", "RAR",
                'R', Items.getItem("lapotronCrystal"),
                'B', Items.getItem("iridiumPlate"),
                'D', Items.getItem("lapPack"),
                'A', GraviSuiteData.superconductor);

        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.ultimate_lappack),
                "RBR", "RDR", "RAR",
                'R', Items.getItem("lapotronCrystal"),
                'B', Items.getItem("iridiumPlate"),
                'D', GraviSuiteData.advanced_lappack,
                'A', GraviSuiteData.superconductor);

        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.advanced_quant),
                "RAR", "DBD", "RCR",
                'R', GraviSuiteData.superconductor,
                'A', Items.getItem("quantumBodyarmor"),
                'D', GraviSuiteData.gravi_engine,
                'B', Items.getItem("hvTransformer"),
                'C', GraviSuiteData.ultimate_lappack);

        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.advanced_lappack),
                "P", "C", "L",
                'P', Items.getItem("lapPack"),
                'C', Items.getItem("advancedCircuit"),
                'L', Items.getItem("lapotronCrystal"));

        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.advanced_jetpack),
                "ABA", "CDC", "EFE",
                'A', Items.getItem("carbonPlate"),
                'B', Items.getItem("electricJetpack"),
                'C', GraviSuiteData.engine_booster,
                'D', GraviSuiteData.advanced_lappack,
                'E', Items.getItem("glassFiberCableItem"),
                'F', Items.getItem("advancedCircuit"));

        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.advanced_nano),
                "ABA", "ACA", "DFD",
                'A', Items.getItem("carbonPlate"),
                'B', GraviSuiteData.advanced_jetpack,
                'C', Items.getItem("nanoBodyarmor"),
                'D', Items.getItem("glassFiberCableItem"),
                'F', Items.getItem("advancedCircuit"));

        RecipeHelper.removeRecipeByOutput(Items.getItem("quantumBodyarmor"));
        Recipes.advRecipes.addRecipe(Items.getItem("quantumBodyarmor"),
                "ANA", "ILI", "IAI",
                'A', Items.getItem("advancedAlloy"),
                'N', GraviSuiteData.advanced_nano,
                'I', Items.getItem("iridiumPlate"),
                'L', Items.getItem("lapotronCrystal"));

        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.advanced_diamond_drill),
                "ABA", "CAC",
                'A', Items.getItem("overclockerUpgrade"),
                'B', Items.getItem("diamondDrill"),
                'C', Items.getItem("advancedCircuit"));

        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.advanced_iridium_drill),
                " I ", "IDI", " E ",
                'I', Items.getItem("iridiumPlate"),
                'D', Items.getItem("diamondDrill"),
                'E', Items.getItem("energyCrystal"));

        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.advanced_chainsaw),
                " F ", "ABA", "CAC",
                'F', Item.diamond,
                'A', Items.getItem("overclockerUpgrade"),
                'B', Items.getItem("chainsaw"),
                'C', Items.getItem("advancedCircuit"));

        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.vajra),
                "ABA", "CDC", "FGF",
                'A', Items.getItem("refinedIronIngot"),
                'B', Items.getItem("energyCrystal"),
                'C', Items.getItem("carbonPlate"),
                'D', GraviSuiteData.vajra_core,
                'F', Items.getItem("advancedAlloy"),
                'G', Items.getItem("lapotronCrystal"));

        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.gravitool),
                "ABA", "CDC", "EFG",
                'A', Items.getItem("carbonPlate"),
                'B', Items.getItem("electricHoe"),
                'C', Items.getItem("advancedAlloy"),
                'D', Items.getItem("energyCrystal"),
                'E', Items.getItem("electricWrench"),
                'F', Items.getItem("advancedCircuit"),
                'G', Items.getItem("electricTreetap"));


        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.magnet),
                "ICR", "T  ", "ICL",
                'I', Item.ingotIron,
                'T', Items.getItem("teslaCoil"),
                'R', Item.redstone,
                'L', new ItemStack(Item.dyePowder, 1, 4),
                'C', Items.getItem("refinedIronIngot"));

        Recipes.advRecipes.addRecipe(Helpers.withSize(GraviSuiteData.superconductor_cover, 3),
                "RBR", "CCC", "RBR",
                'R', Items.getItem("advancedAlloy"),
                'B', Items.getItem("iridiumPlate"),
                'C', Items.getItem("carbonPlate"));

        Recipes.advRecipes.addRecipe(Helpers.withSize(GraviSuiteData.superconductor, 3),
                "RRR", "CBC", "RRR",
                'R', GraviSuiteData.superconductor_cover,
                'B', Item.ingotGold,
                'C', Items.getItem("glassFiberCableItem"));

        Recipes.advRecipes.addRecipe(GraviSuiteData.cooling_core,
                "RBR", "CDC", "RBR",
                'R', Items.getItem("reactorCoolantSix"),
                'B', Items.getItem("reactorHeatSwitchDiamond"),
                'C', Items.getItem("reactorPlatingHeat"),
                'D', Items.getItem("iridiumPlate"));

        Recipes.advRecipes.addRecipe(GraviSuiteData.gravi_engine,
                "RBR", "CDC", "RBR",
                'R', Items.getItem("teslaCoil"),
                'B', GraviSuiteData.superconductor,
                'C', GraviSuiteData.cooling_core,
                'D', Items.getItem("hvTransformer"));

        Recipes.advRecipes.addRecipe(GraviSuiteData.magnetron,
                "ABA", "BCB", "ABA",
                'A', Items.getItem("refinedIronIngot"),
                'B', Items.getItem("copperIngot"),
                'C', GraviSuiteData.superconductor);

        Recipes.advRecipes.addRecipe(GraviSuiteData.vajra_core,
                " A ", "BCB", "FDF",
                'A', GraviSuiteData.magnetron,
                'B', Items.getItem("iridiumPlate"),
                'C', Items.getItem("teslaCoil"),
                'F', GraviSuiteData.superconductor,
                'D', Items.getItem("hvTransformer"));

        Recipes.advRecipes.addRecipe(GraviSuiteData.engine_booster,
                "ABA", "CDC", "BFB",
                'A', Item.lightStoneDust,
                'B', Items.getItem("advancedAlloy"),
                'C', Items.getItem("advancedCircuit"),
                'D', Items.getItem("overclockerUpgrade"),
                'F', Items.getItem("reactorVentDiamond"));

        Recipes.advRecipes.addRecipe(new ItemStack(GraviSuiteData.voider),
                "CLC", "LSL", "CLC",
                'C', Items.getItem("advancedCircuit"),
                'L', Items.getItem("lavaCell"),
                'S', Items.getItem("odScanner"));
    }
}
