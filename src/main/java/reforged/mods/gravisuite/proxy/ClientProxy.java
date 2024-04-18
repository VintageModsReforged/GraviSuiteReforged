package reforged.mods.gravisuite.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import reforged.mods.gravisuite.ClientTickHandler;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.GraviSuiteOverlay;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.LangHelper;
import reforged.mods.gravisuite.utils.Refs;

public class ClientProxy extends CommonProxy {

    public static KeyBinding engine_toggle, magnet_toggle;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        GraviSuiteConfig.initConfig();
        LangHelper.init();
        engine_toggle = new KeyBinding(Refs.KEY_TOGGLE_DESC, Keyboard.KEY_F);
        magnet_toggle = new KeyBinding(Refs.KEY_MAGNET_TOGGLE_DESC, Keyboard.KEY_G);
        // Register Keybinds using 1.7.10 method
        Minecraft.getMinecraft().gameSettings.keyBindings = Helpers.add(Minecraft.getMinecraft().gameSettings.keyBindings, engine_toggle);
        Minecraft.getMinecraft().gameSettings.keyBindings = Helpers.add(Minecraft.getMinecraft().gameSettings.keyBindings, magnet_toggle);
        TickRegistry.registerTickHandler(new GraviSuiteOverlay(), Side.CLIENT);
        TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    @Override
    public int addArmor(String armorName) {
        return RenderingRegistry.addNewArmourRendererPrefix(armorName);
    }
}
