package reforged.mods.gravisuite.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import reforged.mods.gravisuite.*;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.I18n;
import reforged.mods.gravisuite.utils.ItemGraviToolRenderer;
import reforged.mods.gravisuite.utils.Refs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public static KeyBinding ENGINE_TOGGLE, MAGNET_TOGGLE;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        GraviSuiteMainConfig.initMainConfig();
        MinecraftForgeClient.preloadTexture(GraviSuite.TEXTURE);
        I18n.init();
        ENGINE_TOGGLE = new KeyBinding(Refs.KEY_TOGGLE_DESC, Keyboard.KEY_F);
        MAGNET_TOGGLE = new KeyBinding(Refs.KEY_MAGNET_TOGGLE_DESC, Keyboard.KEY_G);
        // Register Keybinds using 1.7.10 method
        Minecraft.getMinecraft().gameSettings.keyBindings = Helpers.add(Minecraft.getMinecraft().gameSettings.keyBindings, ENGINE_TOGGLE);
        Minecraft.getMinecraft().gameSettings.keyBindings = Helpers.add(Minecraft.getMinecraft().gameSettings.keyBindings, MAGNET_TOGGLE);
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

    @Override
    public void registerRenderers() {
        MinecraftForgeClient.registerItemRenderer(GraviSuiteData.GRAVI_TOOL.itemID, new ItemGraviToolRenderer());
    }
}
