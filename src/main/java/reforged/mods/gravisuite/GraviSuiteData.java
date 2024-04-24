package reforged.mods.gravisuite;

import cpw.mods.fml.common.Loader;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;
import reforged.mods.gravisuite.items.ItemComponent;
import reforged.mods.gravisuite.items.armors.ItemAdvancedJetpack;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.items.armors.ItemLappack;
import reforged.mods.gravisuite.items.tools.*;

public class GraviSuiteData {

    public static final EnumArmorMaterial GRAVI_MATERIAL = EnumHelper.addArmorMaterial("g_mat", 0, new int[] { 0, 0, 0, 0 }, 0);

    public static Item component;
    public static ItemStack superconductor_cover, superconductor, cooling_core, gravi_engine, magnetron, vajra_core, engine_booster;
    public static Item advanced_diamond_drill, advanced_iridium_drill, advanced_chainsaw;
    public static Item vajra, magnet, gravitool, debug;
    public static Item advanced_lappack, ultimate_lappack;
    public static Item advanced_jetpack, advanced_nano, advanced_quant;

    public static void init() {

        component = new ItemComponent();

        superconductor_cover = new ItemStack(component, 1, 0);
        superconductor = new ItemStack(component, 1, 1);
        cooling_core = new ItemStack(component, 1, 2);
        gravi_engine = new ItemStack(component, 1, 3);
        magnetron = new ItemStack(component, 1, 4);
        vajra_core = new ItemStack(component, 1, 5);
        engine_booster = new ItemStack(component, 1, 6);

        advanced_diamond_drill = new ItemAdvancedDrill.ItemAdvancedDiamondDrill();
        advanced_iridium_drill = new ItemAdvancedDrill.ItemAdvancedIridiumDrill();
        advanced_chainsaw = new ItemAdvancedChainsaw();
        vajra = new ItemVajra();
        magnet = new ItemMagnet();
        gravitool = new ItemGraviTool();

        if (Loader.isModLoaded("GregTech_Addon")) {
            gregtechmod.api.GregTech_API.registerWrench(new ItemStack(gravitool.itemID, 1, 32767));
        }
        advanced_lappack = new ItemLappack.ItemAdvancedLappack();
        ultimate_lappack = new ItemLappack.ItemUltimateLappack();

        advanced_jetpack = new ItemAdvancedJetpack();
        advanced_nano = new ItemAdvancedJetpack.ItemAdvancedNano();
        advanced_quant = new ItemAdvancedQuant();
    }
}
