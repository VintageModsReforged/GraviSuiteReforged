package reforged.mods.gravisuite.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import org.lwjgl.input.Keyboard;
import reforged.mods.gravisuite.ClientTickHandler;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.GraviSuiteOverlay;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.LangHelper;
import reforged.mods.gravisuite.utils.Refs;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
        packet.channel = Refs.id;
        packet.data = bytes.toByteArray();
        packet.length = packet.data.length;
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue((Packet) packet);
    }
}
