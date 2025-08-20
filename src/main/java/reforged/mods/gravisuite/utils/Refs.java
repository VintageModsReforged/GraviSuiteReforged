package reforged.mods.gravisuite.utils;

import net.minecraft.util.StatCollector;

public class Refs {

    public static final String id = "gravisuite";
    public static final String name = "GraviSuite - Addon";
    public static final String deps = "required-after:VintageCore;required-after:IC2;after:GregTech_Addon;";


    public static final String client = "reforged.mods.gravisuite.proxy.ClientProxy";
    public static final String common = "reforged.mods.gravisuite.proxy.CommonProxy";

    public static final String keyboardClient = "reforged.mods.gravisuite.keyboard.GraviSuiteKeyboardClient";
    public static final String keyboardCommon = "reforged.mods.gravisuite.keyboard.GraviSuiteKeyboard";

    public static final String networkClient = "reforged.mods.gravisuite.network.NetworkHandlerClient";
    public static final String networkCommon = "reforged.mods.gravisuite.network.NetworkHandler";

    public static final String general = "general", hud = "hud", tree_capitator = "tree_capitator";

    public static final String SNEAK_KEY = StatCollector.translateToLocal("key.sneak");
}
