package reforged.mods.gravisuite;

import cpw.mods.fml.relauncher.FMLInjectionData;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
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

    public static String LANGUAGES;
    public static int MAGNET_RANGE = 8;
    public static int MAGNET_MAX_CAPACITY = 200;
    public static double DURABILITY_FACTOR = 1.0;
    public static boolean LOG_WRENCH = false;
    public static boolean ENABLE_HAMMERS = true;
    public static boolean ENABLE_HUD = true;
    public static boolean USE_FIXED_VALUES = true;
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

        MAGNET_RANGE = getInt(Refs.GENERAL, "magnet_range", 1, 16, MAGNET_RANGE, "Magnet Range.");
        MAGNET_MAX_CAPACITY = getInt(Refs.GENERAL, "magnet_max_capacity", 1, Integer.MAX_VALUE, MAGNET_MAX_CAPACITY, "Magnet Attraction Capacity.");

        HUD_POSITION = getInt(Refs.HUD, "hud_position", 1, 4, HUD_POSITION, "GraviSuite Status HUD Position. 1 - Top Left, 2 - Top Right, 3 - Bottom Left, 4 - Bottom Right.");
        ENABLE_HUD = getBoolean(Refs.HUD, "enable_hud", ENABLE_HUD, "Enable GraviSuite Status HUD.");
        USE_FIXED_VALUES = getBoolean(Refs.HUD, "enable_hud_fixed", USE_FIXED_VALUES, "Should GraviSuite HUD use fixed values from `hud_position`?");

        HUD_POS_ENERGY_X = getInt(Refs.HUD, "hud_pos_energy_x", 0, Integer.MAX_VALUE, HUD_POS_ENERGY_X, "X Pos for energy status info.");
        HUD_POS_ENERGY_Y = getInt(Refs.HUD, "hud_pos_energy_y", 0, Integer.MAX_VALUE, HUD_POS_ENERGY_Y, "Y Pos for energy status info.");

        HUD_POS_JETPACK_X = getInt(Refs.HUD, "hud_pos_jetpack_x", 0, Integer.MAX_VALUE, HUD_POS_JETPACK_X, "X Pos for jetpack status info.");
        HUD_POS_JETPACK_Y = getInt(Refs.HUD, "hud_pos_jetpack_y", 0, Integer.MAX_VALUE, HUD_POS_JETPACK_Y, "Y Pos for jetpack status info.");

        HUD_POS_GRAVI_X = getInt(Refs.HUD, "hud_pos_gravi_x", 0, Integer.MAX_VALUE, HUD_POS_GRAVI_X, "X Pos for Gravitational Chestplate status info.");
        HUD_POS_GRAVI_Y = getInt(Refs.HUD, "hud_pos_gravi_y", 0, Integer.MAX_VALUE, HUD_POS_GRAVI_Y, "Y Pos for Gravitational Chestplate status info.");

        DURABILITY_FACTOR = getDouble(Refs.GENERAL, "durability_factor", 0.1, 1.0, 1.0, "Durability factor for Hammers.");
        ENABLE_HAMMERS = getBoolean(Refs.GENERAL, "enable_hammers", ENABLE_HAMMERS, "Enable Hammers.");
        LOG_WRENCH = getBoolean(Refs.GENERAL, "enable_wrench_logging", LOG_WRENCH, "Should GraviTool Wrench be logged? [Debug purposes only!]");
        LANGUAGES = getString(Refs.GENERAL, "localization_list", "en_US", "Supported localizations. Place your <name>.lang file in gravisuite/lang folder and list <name> here. Format: no spaces, comma separated. Ex: <name>,<name>");

        COMPONENT_ID = getId(Refs.IDS, "component_id", COMPONENT_ID, "component");

        ADVANCED_LAPPACK_ID = getId(Refs.IDS, "advanced_lappack", ADVANCED_LAPPACK_ID, "advanced_lappack_id");
        ULTIMATE_LAPPACK_ID = getId(Refs.IDS, "utlimate_lappack", ULTIMATE_LAPPACK_ID, "utlimate_lappack_id");
        ADVANCED_JETPACK_ID = getId(Refs.IDS, "advanced_jetpack_id", ADVANCED_JETPACK_ID, "advanced_jetpack_id");
        ADVANCED_NANO_ID = getId(Refs.IDS, "advanced_nano_id", ADVANCED_NANO_ID, "advanced_nano_id");
        ADVANCED_QUANT_ID = getId(Refs.IDS, "advanced_quant_id", ADVANCED_QUANT_ID, "advanced_quant_id");

        ADVANCED_DRILL_ID = getId(Refs.IDS, "advanced_diamond_drill", ADVANCED_DRILL_ID, "advanced_diamond_drill_id");
        ADVANCED_CHAINSAW_ID = getId(Refs.IDS, "advanced_chainsaw", ADVANCED_CHAINSAW_ID, "advanced_chainsaw_id");
        GRAVI_TOOL_ID = getId(Refs.IDS, "gravitool_id", GRAVI_TOOL_ID, "gravitool_id");
        VAJRA_ID = getId(Refs.IDS, "vajra", VAJRA_ID, "vajra_id");
        MAGNET_ID = getId(Refs.IDS, "magnet", MAGNET_ID, "magnet_id");

        WOOD_HAMMER_ID = getId(Refs.IDS, "wooden_hammer", WOOD_HAMMER_ID,"wooden_hammer_id");
        STONE_HAMMER_ID = getId(Refs.IDS, "stone_hammer", STONE_HAMMER_ID,"stone_hammer_id");
        IRON_HAMMER_ID = getId(Refs.IDS, "iron_hammer", IRON_HAMMER_ID,"iron_hammer_id");
        DIAMOND_HAMMER_ID = getId(Refs.IDS, "diamond_hammer", DIAMOND_HAMMER_ID,"diamond_hammer_id");
        BRONZE_HAMMER_ID = getId(Refs.IDS, "bronze_hammer_id", BRONZE_HAMMER_ID, "bronze_hammer_id");
        QUARTZ_HAMMER_ID = getId(Refs.IDS, "quartz_hammer_id", QUARTZ_HAMMER_ID, "quartz_hammer_id");
        RUBY_HAMMER_ID = getId(Refs.IDS, "ruby_hammer_id", RUBY_HAMMER_ID, "ruby_hammer_id");
        SAPPHIRE_HAMMER_ID = getId(Refs.IDS, "sapphire_hammer_id", SAPPHIRE_HAMMER_ID, "sapphire_hammer_id");
        GREEN_SAPPHIRE_HAMMER_ID = getId(Refs.IDS, "green_sapphire_hammer_id", GREEN_SAPPHIRE_HAMMER_ID, "green_sapphire_hammer_id");

        if (MAIN_CONFIG != null) {
            MAIN_CONFIG.save();
        }
    }

    public static String getString(String cat, String tag, String defaultValue, String comment) {
        comment = comment.replace("{t}", tag) + "\n";
        Property prop = MAIN_CONFIG.get(cat, tag, defaultValue);
        prop.comment = comment + "Default: " + defaultValue;
        return prop.value;
    }

    public static int getId(String cat, String tag, int defaultValue, String comment) {
        comment = comment.replace("{t}", tag) + "\n";
        Property prop = MAIN_CONFIG.get(cat, tag, defaultValue);
        prop.comment = comment + "Default: " + defaultValue;
        int value = prop.getInt(defaultValue);
        prop.value = Integer.toString(value);
        return value;
    }

    private static int getInt(String cat, String tag, int min, int max, int defaultValue, String comment) {
        comment = comment.replace("{t}", tag) + "\n";
        Property prop = MAIN_CONFIG.get(cat, tag, defaultValue);
        prop.comment = comment + "Min: " + min + ", Max: " + max + ", Default: " + defaultValue;
        int value = prop.getInt(defaultValue);
        value = Math.max(value, min);
        value = Math.min(value, max);
        prop.value = Integer.toString(value);
        return value;
    }

    private static double getDouble(String cat, String tag, double min, double max, double defaultValue, String comment) {
        comment = comment.replace("{t}", tag) + "\n";
        Property prop = MAIN_CONFIG.get(cat, tag, defaultValue);
        prop.comment = comment + "Min: " + min + ", Max: " + max + ", Default: " + defaultValue;
        double value = prop.getDouble(defaultValue);
        value = Math.max(value, min);
        value = Math.min(value, max);
        prop.value = Double.toString(value);
        return value;
    }

    private static boolean getBoolean(String cat, String tag, boolean defaultValue, String comment) {
        comment = comment.replace("{t}", tag) + "\n";
        Property prop = MAIN_CONFIG.get(cat, tag, defaultValue);
        prop.comment = comment + "Default: " + defaultValue;
        return prop.getBoolean(defaultValue);
    }
}
