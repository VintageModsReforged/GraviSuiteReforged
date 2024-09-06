package reforged.mods.gravisuite;

import cpw.mods.fml.relauncher.FMLInjectionData;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import reforged.mods.gravisuite.utils.Refs;

import java.io.File;
import java.io.FileWriter;

public class GraviSuiteConfig {

    public static File info;
    public static Configuration id_config;
    public static Configuration main_config;
    public static String additional_languages;
    public static String default_language;
    public static int magnet_range = 8;
    public static int magnet_max_capacity = 200;
    public static boolean log_wrench = false;
    public static boolean enable_hud = true;
    public static boolean use_fixed_values = true;
    public static boolean chainsaw_tree_capitator = false;
    public static int hud_position = 1;

    public static int hud_pos_energy_x = 3;
    public static int hud_pos_energy_y = 3;

    public static int hud_pos_jetpack_x = 3;
    public static int hud_pos_jetpack_y = 15;

    public static int hud_pos_gravi_x = 3;
    public static int hud_pos_gravi_y = 15;

    public static int COMPONENT_ID = 30219;
    public static int ADVANCED_DIAMOND_DRILL = 30226;
    public static int ADVANCED_IRIDIUM_DRILL = 30227;
    public static int ADVANCED_CHAINSAW_ID = 30228;
    public static int VAJRA_ID = 30229;
    public static int MAGNET_ID = 30230;
    public static int GRAVI_TOOL_ID = 30231;
    public static int ADVANCED_LAPPACK_ID = 30232;
    public static int ULTIMATE_LAPPACK_ID = 30233;
    public static int ADVANCED_JETPACK_ID = 30234;
    public static int ADVANCED_NANO_ID = 30235;
    public static int ADVANCED_QUANT_ID = 30236;
    public static int VOIDER_ID = 30237;

    public static void initConfig() {

        id_config = new Configuration(new File((File) FMLInjectionData.data()[6], "config/gravisuite/ids.cfg"));
        id_config.load();

        COMPONENT_ID = getId("component_id", COMPONENT_ID, "component");

        ADVANCED_DIAMOND_DRILL = getId("advanced_diamond_drill", ADVANCED_DIAMOND_DRILL, "Advanced Diamond Drill ID");
        ADVANCED_IRIDIUM_DRILL = getId("advanced_iridium_drill", ADVANCED_IRIDIUM_DRILL, "Advanced Iridium Drill ID");
        ADVANCED_CHAINSAW_ID = getId("advanced_chainsaw", ADVANCED_CHAINSAW_ID, "advanced_chainsaw_id");
        GRAVI_TOOL_ID = getId("gravitool_id", GRAVI_TOOL_ID, "gravitool_id");
        VAJRA_ID = getId("vajra", VAJRA_ID, "vajra_id");
        MAGNET_ID = getId("magnet", MAGNET_ID, "magnet_id");

        ADVANCED_LAPPACK_ID = getId("advanced_lappack", ADVANCED_LAPPACK_ID, "advanced_lappack_id");
        ULTIMATE_LAPPACK_ID = getId("utlimate_lappack", ULTIMATE_LAPPACK_ID, "utlimate_lappack_id");
        ADVANCED_JETPACK_ID = getId("advanced_jetpack_id", ADVANCED_JETPACK_ID, "advanced_jetpack_id");
        ADVANCED_NANO_ID = getId("advanced_nano_id", ADVANCED_NANO_ID, "advanced_nano_id");
        ADVANCED_QUANT_ID = getId("advanced_quant_id", ADVANCED_QUANT_ID, "advanced_quant_id");
        VOIDER_ID = getId("voider_id", VOIDER_ID, "voider_id");

        if (id_config.hasChanged()) id_config.save();

        main_config = new Configuration(new File((File)FMLInjectionData.data()[6], "config/gravisuite/common.cfg"));

        enable_hud = getBoolean(Refs.hud, "enable_hud", enable_hud, "Should GraviSuite display the HUD with info about electric armor?");
        use_fixed_values = getBoolean(Refs.hud, "enable_hud_fixed", use_fixed_values, "Should GraviSuite HUD use fixed values from below?");
        hud_position = getInt(Refs.hud, "hud_pos", 1, 4, hud_position, "Fixed HUD Position. [1] - TOP_LEFT, [2] - TOP_RIGHT, [3] - BOTTOM_LEFT, [4] - BOTTOM_RIGHT");

        hud_pos_energy_x = getInt(Refs.hud, "hud_pos_energy_x", 0, Integer.MAX_VALUE, hud_pos_energy_x, "X Pos for energy status info.");
        hud_pos_energy_y = getInt(Refs.hud, "hud_pos_energy_y", 0, Integer.MAX_VALUE, hud_pos_energy_y, "Y Pos for energy status info.");

        hud_pos_jetpack_x = getInt(Refs.hud, "hud_pos_jetpack_x", 0, Integer.MAX_VALUE, hud_pos_jetpack_x, "X Pos for jetpack status info.");
        hud_pos_jetpack_y = getInt(Refs.hud, "hud_pos_jetpack_y", 0, Integer.MAX_VALUE, hud_pos_jetpack_y, "Y Pos for jetpack status info.");

        hud_pos_gravi_x = getInt(Refs.hud, "hud_pos_gravi_x", 0, Integer.MAX_VALUE, hud_pos_gravi_x, "X Pos for Gravitational Chestplate status info.");
        hud_pos_gravi_y = getInt(Refs.hud, "hud_pos_gravi_y", 0, Integer.MAX_VALUE, hud_pos_gravi_y, "Y Pos for Gravitational Chestplate status info.");

        chainsaw_tree_capitator = getBoolean(Refs.general, "chainsaw_tree_capitator", chainsaw_tree_capitator, "Enable TreeCapitator Mode for Advanced Chainsaw. GraviSuite provides builtin compat for TreeCapitator mod. Does this config value make sense then?");
        log_wrench = getBoolean(Refs.general, "enable_wrench_logging", log_wrench, "Should GraviTool Wrench be logged? [Debug purposes only!]");
        default_language = getString(Refs.general, "default_language", "en_US", "Default Language. DO NOT CHANGE THIS! Use additional_languages field instead!");
        additional_languages = getString(Refs.general, "additional_languages", "", "Additional supported localizations. Place your <name>.lang file in config/gravisuite/lang folder and list <name> here. Format: no spaces, comma separated. Ex: <name>,<name>");
        magnet_range = getInt(Refs.general, "magnet_range", 1, 16, magnet_range, "Magnet Range.");
        magnet_max_capacity = getInt(Refs.general, "magnet_max_capacity", 1, Integer.MAX_VALUE, magnet_max_capacity, "Magnet Attraction Capacity.");

        if (main_config.hasChanged()) main_config.save();

        info = new File((File)FMLInjectionData.data()[6], "config/gravisuite/langs.md");

        try {
            FileWriter writer = new FileWriter(info);
            writer.write("# Additional Langs \nYou can have multiple different lang files for GraviSuite. \n\nFirst you need to create a new folder called \"lang\" inside #config/gravisuite folder and copy your .lang file there and don't forget to specify the file name in the common config#additional_languages field as well.");
            writer.close();
        } catch (Throwable e) {
            GraviSuite.logger.info("Failed to create new file!");
        }
    }

    private static int getInt(String cat, String tag, int min, int max, int defaultValue, String comment) {
        comment = comment.replace("{t}", tag) + "\n";
        Property prop = main_config.get(cat, tag, defaultValue);
        prop.comment = comment + "Min: " + min + ", Max: " + max + ", Default: " + defaultValue;
        int value = prop.getInt(defaultValue);
        value = Math.max(value, min);
        value = Math.min(value, max);
        prop.set(Integer.toString(value));
        return value;
    }

    public static String getString(String cat, String tag, String defaultValue, String comment) {
        comment = comment.replace("{t}", tag) + "\n";
        Property prop = main_config.get(cat, tag, defaultValue);
        prop.comment = comment + "Default: " + defaultValue;
        return prop.getString();
    }

    public static int getId(String tag, int defaultValue, String comment) {
        comment = comment.replace("{t}", tag) + "\n";
        Property prop = id_config.get(Refs.IDs, tag, defaultValue);
        prop.comment = comment + "Default: " + defaultValue;
        int value = prop.getInt(defaultValue);
        prop.set(Integer.toString(value));
        return value;
    }

    private static boolean getBoolean(String cat, String tag, boolean defaultValue, String comment) {
        comment = comment.replace("{t}", tag) + "\n";
        Property prop = main_config.get(cat, tag, defaultValue);
        prop.comment = comment + "Default: " + defaultValue;
        return prop.getBoolean(defaultValue);
    }
}
