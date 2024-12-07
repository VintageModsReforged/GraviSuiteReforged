package reforged.mods.gravisuite;

import cpw.mods.fml.relauncher.FMLInjectionData;
import mods.vintage.core.helpers.ConfigHelper;
import net.minecraftforge.common.Configuration;
import reforged.mods.gravisuite.utils.Refs;

import java.io.File;

public class GraviSuiteMainConfig {

    public static Configuration MAIN_CONFIG;

    public static int COMPONENT_ID = 30219;
    public static int ADVANCED_LAPPACK_ID = 30226;
    public static int ULTIMATE_LAPPACK_ID = 30227;

    public static int ADVANCED_DRILL_ID = 30228;
    public static int ADVANCED_CHAINSAW_ID = 30229;
    public static int VAJRA_ID = 30230;
    public static int MAGNET_ID = 30231;
    public static int GRAVI_TOOL_ID = 30232;
    public static int ADVANCED_JETPACK_ID = 30233;
    public static int ADVANCED_NANO_ID = 30234;
    public static int ADVANCED_QUANT_ID = 30235;
    public static int WOOD_HAMMER_ID = 30236;
    public static int STONE_HAMMER_ID = 30237;
    public static int IRON_HAMMER_ID = 30238;
    public static int DIAMOND_HAMMER_ID = 30239;
    public static int QUARTZ_HAMMER_ID = 30240;
    public static int RUBY_HAMMER_ID = 30241;
    public static int SAPPHIRE_HAMMER_ID = 30242;
    public static int GREEN_SAPPHIRE_HAMMER_ID = 30243;
    public static int BRONZE_HAMMER_ID = 30244;

    public static int WOOD_EXCAVATOR_ID = 30245;
    public static int STONE_EXCAVATOR_ID = 30246;
    public static int IRON_EXCAVATOR_ID = 30247;
    public static int DIAMOND_EXCAVATOR_ID = 30248;
    public static int QUARTZ_EXCAVATOR_ID = 30249;
    public static int RUBY_EXCAVATOR_ID = 30250;
    public static int SAPPHIRE_EXCAVATOR_ID = 30251;
    public static int GREEN_SAPPHIRE_EXCAVATOR_ID = 30252;
    public static int BRONZE_EXCAVATOR_ID = 30253;

    public static String[] LANGUAGES;
    public static String[] LOGS;
    public static String[] LEAVES;

    public static int MAGNET_RANGE = 8;
    public static int MAGNET_MAX_CAPACITY = 200;
    public static double DURABILITY_FACTOR = 1.0;
    public static boolean LOG_WRENCH = false;
    public static boolean ENABLE_HAMMERS = true;
    public static boolean ENABLE_EXCAVATORS = true;
    public static boolean ENABLE_HUD = true;
    public static boolean USE_FIXED_VALUES = true;
    public static boolean CHAINSAW_TREE_CAPITATOR = false;
    public static boolean INSPECT_MODE = false;

    public static int HUD_POSITION = 1;

    public static int HUD_POS_ENERGY_X = 3;
    public static int HUD_POS_ENERGY_Y = 3;

    public static int HUD_POS_JETPACK_X = 3;
    public static int HUD_POS_JETPACK_Y = 15;

    public static int HUD_POS_GRAVI_X = 3;
    public static int HUD_POS_GRAVI_Y = 15;

    public static void initMainConfig() {
        MAIN_CONFIG = new Configuration(new File((File) FMLInjectionData.data()[6], "config/gravisuite_main.cfg"));
        MAIN_CONFIG.load();

        MAGNET_RANGE = ConfigHelper.getInt(MAIN_CONFIG, Refs.GENERAL, "magnet_range", 1, 16, MAGNET_RANGE, "Magnet Range.");
        MAGNET_MAX_CAPACITY = ConfigHelper.getInt(MAIN_CONFIG, Refs.GENERAL, "magnet_max_capacity", 1, Integer.MAX_VALUE, MAGNET_MAX_CAPACITY, "Magnet Attraction Capacity.");

        HUD_POSITION = ConfigHelper.getInt(MAIN_CONFIG, Refs.HUD, "hud_position", 1, 4, HUD_POSITION, "GraviSuite Status HUD Position. 1 - Top Left, 2 - Top Right, 3 - Bottom Left, 4 - Bottom Right.");
        ENABLE_HUD = ConfigHelper.getBoolean(MAIN_CONFIG, Refs.HUD, "enable_hud", ENABLE_HUD, "Enable GraviSuite Status HUD.");
        USE_FIXED_VALUES = ConfigHelper.getBoolean(MAIN_CONFIG, Refs.HUD, "enable_hud_fixed", USE_FIXED_VALUES, "Should GraviSuite HUD use fixed values from `hud_position`?");

        HUD_POS_ENERGY_X = ConfigHelper.getInt(MAIN_CONFIG, Refs.HUD, "hud_pos_energy_x", 0, Integer.MAX_VALUE, HUD_POS_ENERGY_X, "X Pos for energy status info.");
        HUD_POS_ENERGY_Y = ConfigHelper.getInt(MAIN_CONFIG, Refs.HUD, "hud_pos_energy_y", 0, Integer.MAX_VALUE, HUD_POS_ENERGY_Y, "Y Pos for energy status info.");

        HUD_POS_JETPACK_X = ConfigHelper.getInt(MAIN_CONFIG, Refs.HUD, "hud_pos_jetpack_x", 0, Integer.MAX_VALUE, HUD_POS_JETPACK_X, "X Pos for jetpack status info.");
        HUD_POS_JETPACK_Y = ConfigHelper.getInt(MAIN_CONFIG, Refs.HUD, "hud_pos_jetpack_y", 0, Integer.MAX_VALUE, HUD_POS_JETPACK_Y, "Y Pos for jetpack status info.");

        HUD_POS_GRAVI_X = ConfigHelper.getInt(MAIN_CONFIG, Refs.HUD, "hud_pos_gravi_x", 0, Integer.MAX_VALUE, HUD_POS_GRAVI_X, "X Pos for Gravitational Chestplate status info.");
        HUD_POS_GRAVI_Y = ConfigHelper.getInt(MAIN_CONFIG, Refs.HUD, "hud_pos_gravi_y", 0, Integer.MAX_VALUE, HUD_POS_GRAVI_Y, "Y Pos for Gravitational Chestplate status info.");

        DURABILITY_FACTOR = ConfigHelper.getDouble(MAIN_CONFIG, Refs.GENERAL, "durability_factor", 0.1, 1.0, 1.0, "Durability factor for Hammers.");
        ENABLE_HAMMERS = ConfigHelper.getBoolean(MAIN_CONFIG, Refs.GENERAL, "enable_hammers", ENABLE_HAMMERS, "Enable Hammers.");
        ENABLE_EXCAVATORS = ConfigHelper.getBoolean(MAIN_CONFIG, Refs.GENERAL, "enable_excavators", ENABLE_EXCAVATORS, "Enable Excavators.");
        LOG_WRENCH = ConfigHelper.getBoolean(MAIN_CONFIG, Refs.GENERAL, "enable_wrench_logging", LOG_WRENCH, "Should GraviTool Wrench be logged? [Debug purposes only!]");
        INSPECT_MODE = ConfigHelper.getBoolean(MAIN_CONFIG, Refs.GENERAL, "enable_inspect_mode", INSPECT_MODE, "Enable inspect mode. Helps identify block name, class and metadata.");
        LANGUAGES = ConfigHelper.getStrings(MAIN_CONFIG, Refs.GENERAL, "localizations", new String[] { "en_US", "ru_RU" }, "Supported localizations. Place your <name>.lang file in config/gravisuite/lang folder or inside mods/gravisuite/lang inside modJar");

        CHAINSAW_TREE_CAPITATOR = ConfigHelper.getBoolean(MAIN_CONFIG, Refs.TREE_CAPITATOR, "chainsaw_tree_capitator", CHAINSAW_TREE_CAPITATOR, "Enable TreeCapitator Mode for Advanced Chainsaw.");
        LOGS = ConfigHelper.getStrings(MAIN_CONFIG, Refs.TREE_CAPITATOR, "logs", new String[]{"thaumcraft.common.world.BlockMagicalLog"}, "Support for custom logs block that aren't instances of `BlockLog`. Enable inspect_mode and right click with a stick to get more info in the log.");
        LEAVES = ConfigHelper.getStrings(MAIN_CONFIG, Refs.TREE_CAPITATOR, "leaves", new String[]{}, "Support for custom leaves block. This shouldn't be here, but just in case, for blocks that have their `isLeaves=false` for some reasons, but still are leaves... Enable inspect_mode and right click with a stick to get more info in the log.");

        COMPONENT_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "component_id", COMPONENT_ID);

        ADVANCED_LAPPACK_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "advanced_lappack", ADVANCED_LAPPACK_ID);
        ULTIMATE_LAPPACK_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "utlimate_lappack", ULTIMATE_LAPPACK_ID);
        ADVANCED_JETPACK_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "advanced_jetpack_id", ADVANCED_JETPACK_ID);
        ADVANCED_NANO_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "advanced_nano_id", ADVANCED_NANO_ID);
        ADVANCED_QUANT_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "advanced_quant_id", ADVANCED_QUANT_ID);

        ADVANCED_DRILL_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "advanced_diamond_drill", ADVANCED_DRILL_ID);
        ADVANCED_CHAINSAW_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "advanced_chainsaw", ADVANCED_CHAINSAW_ID);
        GRAVI_TOOL_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "gravitool_id", GRAVI_TOOL_ID);
        VAJRA_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "vajra", VAJRA_ID);
        MAGNET_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "magnet", MAGNET_ID);

        WOOD_HAMMER_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "wooden_hammer", WOOD_HAMMER_ID);
        STONE_HAMMER_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "stone_hammer", STONE_HAMMER_ID);
        IRON_HAMMER_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "iron_hammer", IRON_HAMMER_ID);
        DIAMOND_HAMMER_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "diamond_hammer", DIAMOND_HAMMER_ID);
        BRONZE_HAMMER_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "bronze_hammer_id", BRONZE_HAMMER_ID);
        QUARTZ_HAMMER_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "quartz_hammer_id", QUARTZ_HAMMER_ID);
        RUBY_HAMMER_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "ruby_hammer_id", RUBY_HAMMER_ID);
        SAPPHIRE_HAMMER_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "sapphire_hammer_id", SAPPHIRE_HAMMER_ID);
        GREEN_SAPPHIRE_HAMMER_ID = ConfigHelper.getId(MAIN_CONFIG, Refs.IDS, "green_sapphire_hammer_id", GREEN_SAPPHIRE_HAMMER_ID);

        if (MAIN_CONFIG != null) {
            MAIN_CONFIG.save();
        }
    }
}
