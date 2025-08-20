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

@Mod(modid = Refs.id, name = Refs.name, useMetadata = true, dependencies = Refs.deps)
@NetworkMod(clientSideRequired = true,
        clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {Refs.id}, packetHandler = NetworkHandlerClient.class),
        serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {Refs.id}, packetHandler = NetworkHandler.class))
public class GraviSuite {

    @SidedProxy(clientSide = Refs.client, serverSide = Refs.common)
    public static CommonProxy proxy;

    @SidedProxy(clientSide = Refs.keyboardClient, serverSide = Refs.keyboardCommon)
    public static GraviSuiteKeyboard keyboard;

    @SidedProxy(clientSide = Refs.networkClient, serverSide = Refs.networkCommon)
    public static NetworkHandler network;

    @Mod.Instance(Refs.id)
    public static GraviSuite instance;

    public static int blockRelocatorPortalRenderID;

    public static final CreativeTabs graviTab = new CreativeTabs(Refs.id) {
        @Override
        public Item getTabIconItem() {
            return GraviSuiteData.advanced_quant;
        }
    };

    public static final Logger logger = Logger.getLogger(Refs.id);

    public GraviSuite() {
        logger.setParent(FMLLog.getLogger());
        MinecraftForge.EVENT_BUS.register(new HighlightHandler());
    }

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit(e);
        NetworkRegistry.instance().registerGuiHandler(this, new GraviSuiteGuiHandler());
        LangManager.INSTANCE.loadCreativeTabName(Refs.id, Translator.RESET.literal(Refs.name));
    }

    @Mod.Init
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
        NEIHandler.init();
    }
}
