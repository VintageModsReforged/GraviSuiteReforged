package reforged.mods.gravisuite;

import cpw.mods.fml.relauncher.FMLInjectionData;
import mods.vintage.core.helpers.ConfigHelper;
import net.minecraftforge.common.Configuration;
import reforged.mods.gravisuite.utils.Refs;

import java.io.File;

public class GraviSuiteConfig {

    public static Configuration id_config;
    public static Configuration main_config;
    public static String[] langs;
    public static String[] logs;
    public static String[] leaves;

    public static int magnet_range = 8;
    public static int magnet_max_capacity = 200;

    public static boolean log_wrench = false;
    public static boolean enable_hud = true;
    public static boolean use_fixed_values = true;
    public static boolean chainsaw_tree_capitator = false;
    public static boolean inspect_mode = false;

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

        COMPONENT_ID = ConfigHelper.getId(id_config, Refs.IDs, "component_id", COMPONENT_ID);

        ADVANCED_DIAMOND_DRILL = ConfigHelper.getId(id_config, Refs.IDs, "advanced_diamond_drill", ADVANCED_DIAMOND_DRILL);
        ADVANCED_IRIDIUM_DRILL = ConfigHelper.getId(id_config, Refs.IDs, "advanced_iridium_drill", ADVANCED_IRIDIUM_DRILL);
        ADVANCED_CHAINSAW_ID = ConfigHelper.getId(id_config, Refs.IDs, "advanced_chainsaw", ADVANCED_CHAINSAW_ID);
        GRAVI_TOOL_ID = ConfigHelper.getId(id_config, Refs.IDs, "gravitool_id", GRAVI_TOOL_ID);
        VAJRA_ID = ConfigHelper.getId(id_config, Refs.IDs, "vajra", VAJRA_ID);
        MAGNET_ID = ConfigHelper.getId(id_config, Refs.IDs, "magnet", MAGNET_ID);

        ADVANCED_LAPPACK_ID = ConfigHelper.getId(id_config, Refs.IDs, "advanced_lappack", ADVANCED_LAPPACK_ID);
        ULTIMATE_LAPPACK_ID = ConfigHelper.getId(id_config, Refs.IDs, "utlimate_lappack", ULTIMATE_LAPPACK_ID);
        ADVANCED_JETPACK_ID = ConfigHelper.getId(id_config, Refs.IDs, "advanced_jetpack_id", ADVANCED_JETPACK_ID);
        ADVANCED_NANO_ID = ConfigHelper.getId(id_config, Refs.IDs, "advanced_nano_id", ADVANCED_NANO_ID);
        ADVANCED_QUANT_ID = ConfigHelper.getId(id_config, Refs.IDs, "advanced_quant_id", ADVANCED_QUANT_ID);
        VOIDER_ID = ConfigHelper.getId(id_config, Refs.IDs, "voider_id", VOIDER_ID);

        if (id_config.hasChanged()) id_config.save();

        main_config = new Configuration(new File((File)FMLInjectionData.data()[6], "config/gravisuite/common.cfg"));

        enable_hud = ConfigHelper.getBoolean(main_config, Refs.hud, "enable_hud", enable_hud, "Should GraviSuite display the HUD with info about electric armor?");
        use_fixed_values = ConfigHelper.getBoolean(main_config, Refs.hud, "enable_hud_fixed", use_fixed_values, "Should GraviSuite HUD use fixed values from `hud_position`?");
        hud_position = ConfigHelper.getInt(main_config, Refs.hud, "hud_pos", 1, 4, hud_position, "Fixed HUD Position. [1] - TOP_LEFT, [2] - TOP_RIGHT, [3] - BOTTOM_LEFT, [4] - BOTTOM_RIGHT");

        hud_pos_energy_x = ConfigHelper.getInt(main_config, Refs.hud, "hud_pos_energy_x", 0, Integer.MAX_VALUE, hud_pos_energy_x, "X Pos for energy status info.");
        hud_pos_energy_y = ConfigHelper.getInt(main_config, Refs.hud, "hud_pos_energy_y", 0, Integer.MAX_VALUE, hud_pos_energy_y, "Y Pos for energy status info.");

        hud_pos_jetpack_x = ConfigHelper.getInt(main_config, Refs.hud, "hud_pos_jetpack_x", 0, Integer.MAX_VALUE, hud_pos_jetpack_x, "X Pos for jetpack status info.");
        hud_pos_jetpack_y = ConfigHelper.getInt(main_config, Refs.hud, "hud_pos_jetpack_y", 0, Integer.MAX_VALUE, hud_pos_jetpack_y, "Y Pos for jetpack status info.");

        hud_pos_gravi_x = ConfigHelper.getInt(main_config, Refs.hud, "hud_pos_gravi_x", 0, Integer.MAX_VALUE, hud_pos_gravi_x, "X Pos for Gravitational Chestplate status info.");
        hud_pos_gravi_y = ConfigHelper.getInt(main_config, Refs.hud, "hud_pos_gravi_y", 0, Integer.MAX_VALUE, hud_pos_gravi_y, "Y Pos for Gravitational Chestplate status info.");

        log_wrench = ConfigHelper.getBoolean(main_config, Refs.general, "enable_wrench_logging", log_wrench, "Should GraviTool Wrench be logged? [Debug purposes only!]");
        inspect_mode = ConfigHelper.getBoolean(main_config, Refs.general, "enable_inspect_mode", inspect_mode, "Enable inspect mode. Helps identify block name, class and metadata.");
        langs = ConfigHelper.getStrings(main_config, Refs.general, "localizations", new String[] { "en_US", "ru_RU" }, "Supported localizations. Place your <name>.lang file in config/gravisuite/lang folder or inside mods/gravisuite/lang inside modJar");
        magnet_range = ConfigHelper.getInt(main_config, Refs.general, "magnet_range", 1, 16, magnet_range, "Magnet Range.");
        magnet_max_capacity = ConfigHelper.getInt(main_config, Refs.general, "magnet_max_capacity", 1, Integer.MAX_VALUE, magnet_max_capacity, "Magnet Attraction Capacity.");

        chainsaw_tree_capitator = ConfigHelper.getBoolean(main_config, Refs.tree_capitator, "chainsaw_tree_capitator", chainsaw_tree_capitator, "Enable TreeCapitator Mode for Advanced Chainsaw");
        logs = ConfigHelper.getStrings(main_config, Refs.tree_capitator, "logs", new String[]{"thaumcraft.common.world.BlockMagicalLog"}, "Support for custom logs block that aren't instances of `BlockLog`. Enable inspect_mode and right click with a stick to get more info in the log.");
        leaves = ConfigHelper.getStrings(main_config, Refs.tree_capitator, "leaves", new String[]{}, "Support for custom leaves block. This shouldn't be here, but just in case, for blocks that have their `isLeaves=false` for some reasons, but still are leaves... Enable inspect_mode and right click with a stick to get more info in the log.");

        if (main_config.hasChanged()) main_config.save();
    }
}
