package reforged.mods.gravisuite;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import mods.vintage.core.platform.lang.FormattedTranslator;
import mods.vintage.core.platform.lang.ILangProvider;
import mods.vintage.core.platform.lang.LangManager;
import reforged.mods.gravisuite.keyboard.GraviSuiteKeyboard;
import reforged.mods.gravisuite.network.NetworkHandler;
import reforged.mods.gravisuite.network.NetworkHandlerClient;
import reforged.mods.gravisuite.proxy.CommonProxy;

import java.util.Arrays;
import java.util.List;

@Mod(modid = Refs.ID, name = Refs.NAME, dependencies = Refs.DEPS, useMetadata = true)
@NetworkMod(clientSideRequired = true,
        clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = { Refs.ID }, packetHandler = NetworkHandlerClient.class),
        serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = { Refs.ID }, packetHandler = NetworkHandler.class))
public class GraviSuite implements ILangProvider {

    @SidedProxy(clientSide = Refs.PROXY_CLIENT, serverSide = Refs.PROXY_COMMON)
    public static CommonProxy PROXY;

    @SidedProxy(clientSide = Refs.KEYBOARD_CLIENT, serverSide = Refs.KEYBOARD_COMMON)
    public static GraviSuiteKeyboard KEYBOARD;

    @SidedProxy(clientSide = Refs.NET_CLIENT, serverSide = Refs.NET_COMMON)
    public static NetworkHandler NETWORK;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        PROXY.preInit(e);
        LangManager.THIS.registerLangProvider(this);
        LangManager.THIS.loadCreativeTabName(Refs.ID, FormattedTranslator.BLUE.literal(Refs.NAME));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        PROXY.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        PROXY.postInit(e);
//        NEIHandler.init();
    }

    @Override
    public String getModid() {
        return Refs.ID;
    }

    @Override
    public List<String> getLocalizationList() {
        return Arrays.asList(GraviSuiteConfig.langs);
    }
}
