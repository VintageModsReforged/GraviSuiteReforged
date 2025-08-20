package reforged.mods.gravisuite;

import mods.vintage.core.helpers.ConfigHelper;
import mods.vintage.core.platform.config.ItemBlockID;
import mods.vintage.core.platform.lang.LocalizationProvider;
import net.minecraftforge.common.Configuration;
import reforged.mods.gravisuite.utils.Refs;

@LocalizationProvider
public class GraviSuiteConfig extends Configuration {

    @LocalizationProvider.List(modId = Refs.id)
    public static String[] langs;

    public static int magnet_range = 8;
    public static int magnet_max_capacity = 200;

    public static int ENERGY_STANDARD_TP = 1000000;
    public static int ENERGY_CROSS_TP = 1500000;
    public static int ENERGY_PORTAL = 2500000;
    public static int ENERGY_SHOOT = 2000000;

    public static boolean log_wrench = false;
    public static boolean enable_hud = true;
    public static boolean use_fixed_values = true;
    public static boolean chainsaw_tree_capitator = false;
    public static boolean enableTranslocator = true;
    public static boolean enablePortal = true;

    public static int hud_position = 1;

    public static int hud_pos_energy_x = 3;
    public static int hud_pos_energy_y = 3;

    public static int hud_pos_jetpack_x = 3;
    public static int hud_pos_jetpack_y = 15;

    public static int hud_pos_gravi_x = 3;
    public static int hud_pos_gravi_y = 15;

    public static ItemBlockID RELOCATOR_PORTAL_BLOCK_ID = ItemBlockID.ofBlock("relocator_portal_block", 3219);

    public static ItemBlockID COMPONENT_ID = ItemBlockID.ofItem("component", 30219);
    public static ItemBlockID ADVANCED_DIAMOND_DRILL = ItemBlockID.ofItem("advanced_diamond_drill", 30226);
    public static ItemBlockID ADVANCED_IRIDIUM_DRILL = ItemBlockID.ofItem("advanced_iridium_drill", 30227);
    public static ItemBlockID ADVANCED_CHAINSAW_ID = ItemBlockID.ofItem("advanced_chainsaw", 30228);
    public static ItemBlockID VAJRA_ID = ItemBlockID.ofItem("vajra", 30229);
    public static ItemBlockID MAGNET_ID = ItemBlockID.ofItem("magnet", 30230);
    public static ItemBlockID GRAVI_TOOL_ID = ItemBlockID.ofItem("gravitool", 30231);
    public static ItemBlockID ADVANCED_LAPPACK_ID = ItemBlockID.ofItem("advanced_lappack", 30232);
    public static ItemBlockID ULTIMATE_LAPPACK_ID = ItemBlockID.ofItem("utlimate_lappack", 30233);
    public static ItemBlockID ADVANCED_JETPACK_ID = ItemBlockID.ofItem("advanced_jetpack", 30234);
    public static ItemBlockID ADVANCED_NANO_ID = ItemBlockID.ofItem("advanced_nano", 30235);
    public static ItemBlockID ADVANCED_QUANT_ID = ItemBlockID.ofItem("advanced_quant", 30236);
    public static ItemBlockID VOIDER_ID = ItemBlockID.ofItem("voider", 30237);
    public static ItemBlockID RELOCATOR_ID = ItemBlockID.ofItem("relocator", 30238);

    public GraviSuiteConfig() {
        super(ConfigHelper.getConfigFileFor("gravisuite"));
        load();

        enable_hud = ConfigHelper.getBoolean(this, Refs.hud, "enable_hud", enable_hud, "Should GraviSuite display the HUD with info about electric armor?");
        use_fixed_values = ConfigHelper.getBoolean(this, Refs.hud, "enable_hud_fixed", use_fixed_values, "Should GraviSuite HUD use fixed values from `hud_position`?");
        hud_position = ConfigHelper.getInt(this, Refs.hud, "hud_pos", 1, 4, hud_position, "Fixed HUD Position. [1] - TOP_LEFT, [2] - TOP_RIGHT, [3] - BOTTOM_LEFT, [4] - BOTTOM_RIGHT");

        hud_pos_energy_x = ConfigHelper.getInt(this, Refs.hud, "hud_pos_energy_x", 0, Integer.MAX_VALUE, hud_pos_energy_x, "X Pos for energy status info.");
        hud_pos_energy_y = ConfigHelper.getInt(this, Refs.hud, "hud_pos_energy_y", 0, Integer.MAX_VALUE, hud_pos_energy_y, "Y Pos for energy status info.");

        hud_pos_jetpack_x = ConfigHelper.getInt(this, Refs.hud, "hud_pos_jetpack_x", 0, Integer.MAX_VALUE, hud_pos_jetpack_x, "X Pos for jetpack status info.");
        hud_pos_jetpack_y = ConfigHelper.getInt(this, Refs.hud, "hud_pos_jetpack_y", 0, Integer.MAX_VALUE, hud_pos_jetpack_y, "Y Pos for jetpack status info.");

        hud_pos_gravi_x = ConfigHelper.getInt(this, Refs.hud, "hud_pos_gravi_x", 0, Integer.MAX_VALUE, hud_pos_gravi_x, "X Pos for Gravitational Chestplate status info.");
        hud_pos_gravi_y = ConfigHelper.getInt(this, Refs.hud, "hud_pos_gravi_y", 0, Integer.MAX_VALUE, hud_pos_gravi_y, "Y Pos for Gravitational Chestplate status info.");

        log_wrench = ConfigHelper.getBoolean(this, Refs.general, "enable_wrench_logging", log_wrench, "Should GraviTool Wrench be logged? [Debug purposes only!]");
        langs = ConfigHelper.getLocalizations(this, new String[]{"en_US", "ru_RU"}, Refs.id);
        magnet_range = ConfigHelper.getInt(this, Refs.general, "magnet_range", 1, 16, magnet_range, "Magnet Range.");
        magnet_max_capacity = ConfigHelper.getInt(this, Refs.general, "magnet_max_capacity", 1, Integer.MAX_VALUE, magnet_max_capacity, "Magnet Attraction Capacity.");
        enableTranslocator = ConfigHelper.getBoolean(this, Refs.general, "relocator_translocator", enableTranslocator, "Enable Relocator's Translocator Mode");
        enablePortal = ConfigHelper.getBoolean(this, Refs.general, "relocator_portal", enablePortal, "Enable Relocator's Portal Mode");

        chainsaw_tree_capitator = ConfigHelper.getBoolean(this, Refs.tree_capitator, "chainsaw_tree_capitator", chainsaw_tree_capitator, "Enable TreeCapitator Mode for Advanced Chainsaw");
    }
}
