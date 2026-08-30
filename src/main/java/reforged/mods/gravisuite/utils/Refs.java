package reforged.mods.gravisuite.utils;

import mods.vintage.core.platform.lang.Translator;
import net.minecraft.util.StatCollector;

public class Refs {

    public static final String ID = "gravisuite";
    public static final String NAME = "GraviSuite - Addon";
    public static final String DEPS = "required-after:VintageCore;required-after:IC2;after:RedPowerBase;after:AppliedEnergistics;after:GregTech_Addon";

    public static final String COMMON_PROXY = "reforged.mods.gravisuite.proxy.CommonProxy";
    public static final String CLIENT_PROXY = "reforged.mods.gravisuite.proxy.ClientProxy";

    public static final String KEYBOARD_CLIENT = "reforged.mods.gravisuite.keyboard.GraviSuiteKeyboardClient";
    public static final String KEYBOARD_COMMON = "reforged.mods.gravisuite.keyboard.GraviSuiteKeyboard";

    public static final String NETWORK_CLIENT = "reforged.mods.gravisuite.network.NetworkHandlerClient";
    public static final String NETWORK_COMMON = "reforged.mods.gravisuite.network.NetworkHandler";

    public static final int ARMOR_PACK_ID = 16, TOOLS_ID = 32, GRAVITOOL_ID = 48, HAMMERS_ID = 64, EXCAVATOR_ID = 80;
    public static final String GENERAL = "general", HUD = "hud", TREE_CAPITATOR = "tree_capitator";

    public static final String SNEAK_KEY = StatCollector.translateToLocal("key.sneak");
    public static final String USE_KEY = StatCollector.translateToLocal("key.use");
}
