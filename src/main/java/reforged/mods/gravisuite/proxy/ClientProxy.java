package reforged.mods.gravisuite.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import reforged.mods.gravisuite.*;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.I18n;
import reforged.mods.gravisuite.utils.ItemGraviToolRenderer;
import reforged.mods.gravisuite.utils.Refs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.input.Keyboard;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
        MinecraftForge.EVENT_BUS.register(this);
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

    @ForgeSubscribe
    public void onWorldLoad(WorldEvent.Load event) {
        ClientTickHandler.firstLoad = true;
    }

    public static void sendPacket(String typePacket, int first_int) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);
        try {
            data.writeUTF(typePacket);
            data.writeInt(first_int);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = Refs.ID;
        packet.data = bytes.toByteArray();
        packet.length = packet.data.length;
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue((Packet) packet);
    }
}
