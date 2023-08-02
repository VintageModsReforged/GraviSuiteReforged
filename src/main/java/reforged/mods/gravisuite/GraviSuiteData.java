package reforged.mods.gravisuite;

import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.common.EnumHelper;
import reforged.mods.gravisuite.items.ItemComponent;
import reforged.mods.gravisuite.items.armors.ItemAdvancedJetpack;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.items.armors.ItemLappack;
import reforged.mods.gravisuite.items.tools.*;

public class GraviSuiteData {

    public static final EnumArmorMaterial GRAVI_MATERIAL = EnumHelper.addArmorMaterial("g_mat", 0, new int[] { 0, 0, 0, 0 }, 0);

    public static Item superconductor_cover, superconductor, cooling_core, gravi_engine, magnetron, vajra_core, engine_booster;
    public static Item advanced_diamond_drill, advanced_iridium_drill, advanced_chainsaw;
    public static Item vajra, magnet, gravitool, debug;
    public static Item advanced_lappack, ultimate_lappack;
    public static Item advanced_jetpack, advanced_nano, advanced_quant;

    public static void init() {

        superconductor_cover = new ItemComponent(GraviSuiteConfig.SUPERCONDUCTOR_COVER_ID, "superconductor_cover");
        superconductor = new ItemComponent(GraviSuiteConfig.SUPERCONDUCTOR_ID, "superconductor");
        cooling_core = new ItemComponent(GraviSuiteConfig.COOLING_CORE_ID, "cooling_core");
        gravi_engine = new ItemComponent(GraviSuiteConfig.GRAVI_ENGINE_ID, "gravi_engine");
        magnetron = new ItemComponent(GraviSuiteConfig.MAGNETRON_ID, "magnetron");
        vajra_core = new ItemComponent(GraviSuiteConfig.VAJRA_CORE_ID, "vajra_core");
        engine_booster = new ItemComponent(GraviSuiteConfig.ENGINE_BOOSTER_ID, "engine_booster");

        advanced_diamond_drill = new ItemAdvancedDrill.ItemAdvancedDiamondDrill();
        advanced_iridium_drill = new ItemAdvancedDrill.ItemAdvancedIridiumDrill();
        advanced_chainsaw = new ItemAdvancedChainsaw();
        vajra = new ItemVajra();
        magnet = new ItemMagnet();
        gravitool = new ItemGraviTool();

        advanced_lappack = new ItemLappack.ItemAdvancedLappack();
        ultimate_lappack = new ItemLappack.ItemUltimateLappack();

        advanced_jetpack = new ItemAdvancedJetpack();
        advanced_nano = new ItemAdvancedJetpack.ItemAdvancedNano();
        advanced_quant = new ItemAdvancedQuant();
    }
}
