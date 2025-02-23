package reforged.mods.gravisuite.utils;

import mods.vintage.core.platform.lang.FormattedTranslator;
import net.minecraft.util.StatCollector;

public class Refs {

    public static final String id = "gravisuite";
    public static final String name = "GraviSuite - Addon";
    public static final String version = "1.5.2-version";
    public static final String mc = "[1.5.2]";
    public static final String deps = "required-after:VintageCore;required-after:IC2;after:GregTech_Addon;";


    public static final String client = "reforged.mods.gravisuite.proxy.ClientProxy";
    public static final String common = "reforged.mods.gravisuite.proxy.CommonProxy";

    public static final String keyboardClient = "reforged.mods.gravisuite.keyboard.GraviSuiteKeyboardClient";
    public static final String keyboardCommon = "reforged.mods.gravisuite.keyboard.GraviSuiteKeyboard";

    public static final String networkClient = "reforged.mods.gravisuite.network.NetworkHandlerClient";
    public static final String networkCommon = "reforged.mods.gravisuite.network.NetworkHandler";

    public static final String IDs = "ids", general = "general", hud = "hud", tree_capitator = "tree_capitator";

    public static final String SNEAK_KEY = StatCollector.translateToLocal("key.sneak");

    // Messages

    public static final String ENERGY_LEVEL = "message.text.energy_level";
    public static final String JETPACK_ENGINE = "message.text.jetpack.engine";
    public static final String JETPACK_HOVER = "message.text.jetpack.hover";
    public static final String STATUS_ON = "message.text.on";
    public static final String STATUS_OFF = "message.text.off";
    public static final String STATUS_LOW_ENERGY = "message.text.low_energy";
    public static final String STATUS_SHUTDOWN = "message.text.shutdown";
    public static final String GRAVITATION_ENGINE = "message.gravitation.engine";
    public static final String LEVITATION = "message.gravitation.levitation";
    public static final String EFF_MODE = "message.tool.eff.mode";
    public static final String EFF_MODE_NORMAL = "message.tool.eff.mode.normal";
    public static final String EFF_MODE_LOW_POWER = "message.tool.eff.mode.low_power";
    public static final String EFF_MODE_FINE = "message.tool.eff.mode.fine";
    public static final String MAGNET_MODE = "message.tool.mode.magnet";
    public static final String SHEAR_MODE = "message.tool.mode.shear";
    public static final String CAPITATOR_MODE = "message.tool.mode.capitator";
    public static final String MINING_MODE = "message.tool.mining.mode";
    public static final String MODE = "message.tool.mode";
    public static final String MODE_NORMAL = "message.tool.mode.normal";
    public static final String MODE_BIG_HOLES = "message.tool.mode.big_holes";
    public static final String MODE_VEIN = "message.tool.mode.vein";
    public static final String MODE_VEIN_EXTENDED = "message.tool.mode.vein_extended";
    public static final String MODE_SILK = "message.tool.mode.silk";
    public static final String MODE_FORTUNE = "message.tool.mode.fortune";
    public static final String TOOL_MODE_HOE = "message.tool.mode.hoe";
    public static final String TOOL_MODE_TREETAP = "message.tool.mode.treetap";
    public static final String TOOL_MODE_WRENCH = "message.tool.mode.wrench";
    public static final String TOOL_MODE_SCREWDRIVER = "message.tool.mode.screwdriver";
    public static final String KEY_TOGGLE_DESC = "key.toggle.engine.desc";
    public static final String KEY_MAGNET_TOGGLE_DESC = "key.toggle.magnet.desc";

    public static final String ENCH_MODE = "message.ench.mode";
    public static final String BOOST_MODE = "message.text.jetpack.boost.stat";

    public static final String to_change_1 = "message.info.press.to1";
    public static final String to_change_2 = "message.info.press.to2";
    public static final String to_enable_1 = "message.info.press.to.enable1";
    public static final String to_enable_2 = "message.info.press.to.enable2";

    public static final String vein_miner = "message.vein.active";
    public static final String quick_charge = "message.info.quick_change";



    // Formatted Messages // \247

    public static final String energy_level = FormattedTranslator.WHITE.format(ENERGY_LEVEL);
    public static final String energy_level_gold = FormattedTranslator.GOLD.format(ENERGY_LEVEL);
    public static final String jetpack_engine = FormattedTranslator.YELLOW.format(JETPACK_ENGINE);
    public static final String jetpack_engine_gold = FormattedTranslator.GOLD.format(JETPACK_ENGINE);
    public static final String jetpack_hover = FormattedTranslator.YELLOW.format(JETPACK_HOVER);
    public static final String jetpack_hover_gold = FormattedTranslator.GOLD.format(JETPACK_HOVER);
    public static final String status_on = FormattedTranslator.GREEN.format(STATUS_ON);
    public static final String status_off = FormattedTranslator.RED.format(STATUS_OFF);
    public static final String status_low = FormattedTranslator.RED.format(STATUS_LOW_ENERGY);
    public static final String status_shutdown = FormattedTranslator.RED.format(STATUS_SHUTDOWN);
    public static final String gravitation_engine = FormattedTranslator.AQUA.format(GRAVITATION_ENGINE);
    public static final String gravitation_levitation = FormattedTranslator.AQUA.format(LEVITATION);
    public static final String eff_tool_mode = FormattedTranslator.YELLOW.format(EFF_MODE);
    public static final String eff_tool_mode_gold = FormattedTranslator.GOLD.format(EFF_MODE);
    public static final String eff_tool_mode_normal = FormattedTranslator.LIGHT_PURPLE.format(EFF_MODE_NORMAL);
    public static final String eff_tool_mode_low = FormattedTranslator.GREEN.format(EFF_MODE_LOW_POWER);
    public static final String eff_tool_mode_fine = FormattedTranslator.AQUA.format(EFF_MODE_FINE);
    public static final String tool_mode_magnet = FormattedTranslator.YELLOW.format(MAGNET_MODE);
    public static final String tool_mode_magnet_gold = FormattedTranslator.GOLD.format(MAGNET_MODE);
    public static final String tool_mode_shear = FormattedTranslator.YELLOW.format(SHEAR_MODE);
    public static final String tool_mode_capitator = FormattedTranslator.YELLOW.format(CAPITATOR_MODE);
    public static final String tool_mode_shear_gold = FormattedTranslator.GOLD.format(SHEAR_MODE);
    public static final String tool_mode_capitator_gold = FormattedTranslator.GOLD.format(CAPITATOR_MODE);
    public static final String tool_mining_mode = FormattedTranslator.YELLOW.format(MINING_MODE);
    public static final String tool_mining_mode_gold = FormattedTranslator.GOLD.format(MINING_MODE);
    public static final String tool_mode = FormattedTranslator.YELLOW.format(MODE);
    public static final String tool_mode_gold = FormattedTranslator.GOLD.format(MODE);
    public static final String ench_mode_yellow = FormattedTranslator.YELLOW.format(ENCH_MODE);
    public static final String tool_mode_normal = FormattedTranslator.GREEN.format(MODE_NORMAL);
    public static final String tool_mode_big_holes = FormattedTranslator.LIGHT_PURPLE.format(MODE_BIG_HOLES);
    public static final String tool_mode_vein = FormattedTranslator.AQUA.format(MODE_VEIN);
    public static final String tool_mode_vein_extended = FormattedTranslator.LIGHT_PURPLE.format(MODE_VEIN_EXTENDED);
    public static final String tool_mode_silk = FormattedTranslator.GREEN.format(MODE_SILK);
    public static final String tool_mode_fortune = FormattedTranslator.AQUA.format(MODE_FORTUNE);
    public static final String tool_mode_hoe = FormattedTranslator.GREEN.format(TOOL_MODE_HOE);
    public static final String tool_mode_treetap = FormattedTranslator.GOLD.format(TOOL_MODE_TREETAP);
    public static final String tool_mode_wrench = FormattedTranslator.AQUA.format(TOOL_MODE_WRENCH);
    public static final String tool_mode_screwdriver = FormattedTranslator.LIGHT_PURPLE.format(TOOL_MODE_SCREWDRIVER);
}
