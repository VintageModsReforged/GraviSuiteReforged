package reforged.mods.gravisuite;

import mods.vintage.core.helpers.ConfigHelper;
import mods.vintage.core.platform.config.ItemBlockID;
import mods.vintage.core.platform.lang.LocalizationProvider;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.Configuration;
import reforged.mods.gravisuite.utils.Refs;

@LocalizationProvider
public class GraviSuiteConfig extends Configuration {

    @LocalizationProvider.List(modId = Refs.ID)
    public static String[] LANGS;

    public static ItemBlockID RELOCATOR_PORTAL_BLOCK_ID = ItemBlockID.ofBlock("relocator_portal_block", 3219);

    public static ItemBlockID COMPONENT_ID = ItemBlockID.ofItem("component", 30219);
    public static ItemBlockID ADVANCED_LAPPACK_ID = ItemBlockID.ofItem("advanced_lappack", 30226);
    public static ItemBlockID ULTIMATE_LAPPACK_ID = ItemBlockID.ofItem("ultimate_lappack", 30227);

    public static ItemBlockID ADVANCED_DRILL_ID = ItemBlockID.ofItem("advanced_diamond_drill", 30228);
    public static ItemBlockID ADVANCED_CHAINSAW_ID = ItemBlockID.ofItem("advanced_chainsaw", 30229);
    public static ItemBlockID VAJRA_ID = ItemBlockID.ofItem("vajra", 30230);
    public static ItemBlockID MAGNET_ID = ItemBlockID.ofItem("magnet", 30231);
    public static ItemBlockID GRAVI_TOOL_ID = ItemBlockID.ofItem("gravitool", 30232);
    public static ItemBlockID ADVANCED_JETPACK_ID = ItemBlockID.ofItem("advanced_jetpack", 30233);
    public static ItemBlockID ADVANCED_NANO_ID = ItemBlockID.ofItem("advanced_nano", 30234);
    public static ItemBlockID ADVANCED_QUANT_ID = ItemBlockID.ofItem("advanced_quant", 30235);
    public static ItemBlockID WOOD_HAMMER_ID = ItemBlockID.ofItem("wood_hammer", 30236);
    public static ItemBlockID STONE_HAMMER_ID = ItemBlockID.ofItem("stone_hammer", 30237);
    public static ItemBlockID IRON_HAMMER_ID = ItemBlockID.ofItem("iron_hammer", 30238);
    public static ItemBlockID DIAMOND_HAMMER_ID = ItemBlockID.ofItem("diamond_hammer", 30239);
    public static ItemBlockID QUARTZ_HAMMER_ID = ItemBlockID.ofItem("quartz_hammer", 30240);
    public static ItemBlockID RUBY_HAMMER_ID = ItemBlockID.ofItem("ruby_hammer", 30241);
    public static ItemBlockID SAPPHIRE_HAMMER_ID = ItemBlockID.ofItem("sapphire_hammer", 30242);
    public static ItemBlockID GREEN_SAPPHIRE_HAMMER_ID = ItemBlockID.ofItem("green_sapphire_hammer", 30243);
    public static ItemBlockID BRONZE_HAMMER_ID = ItemBlockID.ofItem("bronze_hammer", 30244);

    public static ItemBlockID WOOD_EXCAVATOR_ID = ItemBlockID.ofItem("wood_excavator", 30245);
    public static ItemBlockID STONE_EXCAVATOR_ID = ItemBlockID.ofItem("stone_excavator", 30246);
    public static ItemBlockID IRON_EXCAVATOR_ID = ItemBlockID.ofItem("iron_excavator", 30247);
    public static ItemBlockID DIAMOND_EXCAVATOR_ID = ItemBlockID.ofItem("diamond_excavator", 30248);
    public static ItemBlockID QUARTZ_EXCAVATOR_ID = ItemBlockID.ofItem("quartz_excavator", 30249);
    public static ItemBlockID RUBY_EXCAVATOR_ID = ItemBlockID.ofItem("ruby_excavator", 30250);
    public static ItemBlockID SAPPHIRE_EXCAVATOR_ID = ItemBlockID.ofItem("sapphire_excavator", 30251);
    public static ItemBlockID GREEN_SAPPHIRE_EXCAVATOR_ID = ItemBlockID.ofItem("green_sapphire_excavator", 30252);
    public static ItemBlockID BRONZE_EXCAVATOR_ID = ItemBlockID.ofItem("bronze_excavator", 30253);

    public static ItemBlockID RELOCATOR_ID = ItemBlockID.ofItem("relocator", 30254);
    public static ItemBlockID VOIDER_ID = ItemBlockID.ofItem("voider", 30255);
    public static ItemBlockID ADVANCED_IRIDIUM_DRILL = ItemBlockID.ofItem("advanced_iridium_drill", 30256);

    public static int MAGNET_RANGE = 8;
    public static int MAGNET_MAX_CAPACITY = 200;
    public static double DURABILITY_FACTOR = 1.0;

    public static int ENERGY_STANDARD_TP = 1000000;
    public static int ENERGY_CROSS_TP = 1500000;
    public static int ENERGY_PORTAL = 2500000;
    public static int ENERGY_SHOOT = 2000000;

    public static boolean LOG_WRENCH = false;
    public static boolean ENABLE_HAMMERS = true;
    public static boolean ENABLE_EXCAVATORS = true;
    public static boolean ENABLE_HUD = true;
    public static boolean USE_FIXED_VALUES = true;
    public static boolean CHAINSAW_TREE_CAPITATOR = false;

    public static boolean enableTranslocator = true;
    public static boolean enablePortal = true;

    public static int HUD_POSITION = 1;

    public static int HUD_POS_ENERGY_X = 3;
    public static int HUD_POS_ENERGY_Y = 3;

    public static int HUD_POS_JETPACK_X = 3;
    public static int HUD_POS_JETPACK_Y = 15;

    public static int HUD_POS_GRAVI_X = 3;
    public static int HUD_POS_GRAVI_Y = 15;

    public GraviSuiteConfig() {
        super(ConfigHelper.getConfigFileFor("gravisuite_main"));
        load();

        ENABLE_HUD = ConfigHelper.getBoolean(this, Refs.HUD, "enable_hud", ENABLE_HUD, "Enable GraviSuite Status HUD.");
        USE_FIXED_VALUES = ConfigHelper.getBoolean(this, Refs.HUD, "enable_hud_fixed", USE_FIXED_VALUES, "Should GraviSuite HUD use fixed values from `hud_position`?");
        HUD_POSITION = ConfigHelper.getInt(this, Refs.HUD, "hud_position", 1, 4, HUD_POSITION, "GraviSuite Status HUD Position. 1 - Top Left, 2 - Top Right, 3 - Bottom Left, 4 - Bottom Right.");

        HUD_POS_ENERGY_X = ConfigHelper.getInt(this, Refs.HUD, "hud_pos_energy_x", 0, Integer.MAX_VALUE, HUD_POS_ENERGY_X, "X Pos for energy status info.");
        HUD_POS_ENERGY_Y = ConfigHelper.getInt(this, Refs.HUD, "hud_pos_energy_y", 0, Integer.MAX_VALUE, HUD_POS_ENERGY_Y, "Y Pos for energy status info.");

        HUD_POS_JETPACK_X = ConfigHelper.getInt(this, Refs.HUD, "hud_pos_jetpack_x", 0, Integer.MAX_VALUE, HUD_POS_JETPACK_X, "X Pos for jetpack status info.");
        HUD_POS_JETPACK_Y = ConfigHelper.getInt(this, Refs.HUD, "hud_pos_jetpack_y", 0, Integer.MAX_VALUE, HUD_POS_JETPACK_Y, "Y Pos for jetpack status info.");

        HUD_POS_GRAVI_X = ConfigHelper.getInt(this, Refs.HUD, "hud_pos_gravi_x", 0, Integer.MAX_VALUE, HUD_POS_GRAVI_X, "X Pos for Gravitational Chestplate status info.");
        HUD_POS_GRAVI_Y = ConfigHelper.getInt(this, Refs.HUD, "hud_pos_gravi_y", 0, Integer.MAX_VALUE, HUD_POS_GRAVI_Y, "Y Pos for Gravitational Chestplate status info.");

        LOG_WRENCH = ConfigHelper.getBoolean(this, Refs.GENERAL, "enable_wrench_logging", LOG_WRENCH, "Should GraviTool Wrench be logged? [Debug purposes only!]");
        LANGS = ConfigHelper.getLocalizations(this, new String[]{"en_US", "ru_RU"}, Refs.ID);
        MAGNET_RANGE = ConfigHelper.getInt(this, Refs.GENERAL, "magnet_range", 1, 16, MAGNET_RANGE, "Magnet Range.");
        MAGNET_MAX_CAPACITY = ConfigHelper.getInt(this, Refs.GENERAL, "magnet_max_capacity", 1, Integer.MAX_VALUE, MAGNET_MAX_CAPACITY, "Magnet Attraction Capacity.");
        enableTranslocator = ConfigHelper.getBoolean(this, Refs.GENERAL, "relocator_translocator", enableTranslocator, "Enable Relocator's Translocator Mode");
        enablePortal = ConfigHelper.getBoolean(this, Refs.GENERAL, "relocator_portal", enablePortal, "Enable Relocator's Portal Mode");
        DURABILITY_FACTOR = ConfigHelper.getDouble(this, Refs.GENERAL, "durability_factor", 0.1, 1.0, 1.0, "Durability factor for Hammers.");
        ENABLE_HAMMERS = ConfigHelper.getBoolean(this, Refs.GENERAL, "enable_hammers", ENABLE_HAMMERS, "Enable Hammers.");
        ENABLE_EXCAVATORS = ConfigHelper.getBoolean(this, Refs.GENERAL, "enable_excavators", ENABLE_EXCAVATORS, "Enable Excavators.");
        CHAINSAW_TREE_CAPITATOR = ConfigHelper.getBoolean(this, Refs.TREE_CAPITATOR, "chainsaw_tree_capitator", CHAINSAW_TREE_CAPITATOR, "Enable TreeCapitator Mode for Advanced Chainsaw.");
    }
}
