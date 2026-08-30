package reforged.mods.gravisuite;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import mods.vintage.core.platform.lang.LangManager;
import mods.vintage.core.platform.lang.Translator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import reforged.mods.gravisuite.compat.NEIHandler;
import reforged.mods.gravisuite.events.client.HighlightHandler;
import reforged.mods.gravisuite.keyboard.GraviSuiteKeyboard;
import reforged.mods.gravisuite.network.NetworkHandler;
import reforged.mods.gravisuite.network.NetworkHandlerClient;
import reforged.mods.gravisuite.proxy.CommonProxy;
import reforged.mods.gravisuite.utils.GraviSuiteGuiHandler;
import reforged.mods.gravisuite.utils.Refs;

import java.util.logging.Logger;

@Mod(modid = Refs.ID, useMetadata = true)
@NetworkMod(clientSideRequired = true,
        clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = { Refs.ID }, packetHandler = NetworkHandlerClient.class),
        serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = { Refs.ID }, packetHandler = NetworkHandler.class))
public class GraviSuite {

    @SidedProxy(clientSide = Refs.CLIENT_PROXY, serverSide = Refs.COMMON_PROXY)
    public static CommonProxy PROXY;

    @SidedProxy(clientSide = Refs.NETWORK_CLIENT, serverSide = Refs.NETWORK_COMMON)
    public static NetworkHandler NETWORK;

    @SidedProxy(clientSide = Refs.KEYBOARD_CLIENT, serverSide = Refs.KEYBOARD_COMMON)
    public static GraviSuiteKeyboard KEYBOARD;

    @Mod.Instance(Refs.ID)
    public static GraviSuite instance;

    public static int blockRelocatorPortalRenderID;

    public static final CreativeTabs TAB = new CreativeTabs(Refs.ID) {
        @Override
        public Item getTabIconItem() {
            return GraviSuiteData.ADVANCED_QUANT;
        }
    };

    public static final Logger LOGGER = Logger.getLogger(Refs.NAME);
    public static final String TEXTURE = "/mods/gravisuite/textures/item_components.png";

    public GraviSuite() {
        LOGGER.setParent(FMLLog.getLogger());
        MinecraftForge.EVENT_BUS.register(new HighlightHandler());
    }

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent e) {
        PROXY.preInit(e);
        NetworkRegistry.instance().registerGuiHandler(this, new GraviSuiteGuiHandler());
        LangManager.INSTANCE.loadCreativeTabName(Refs.ID, Translator.BLUE.literal(Refs.NAME));
    }

    @Mod.Init
    public void init(FMLInitializationEvent e) {
        PROXY.init(e);
        PROXY.registerRenderers();
    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent e) {
        PROXY.postInit(e);
        NEIHandler.init();
    }
}
