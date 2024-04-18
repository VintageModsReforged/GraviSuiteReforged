package reforged.mods.gravisuite;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import reforged.mods.gravisuite.network.ClientPacketHandler;
import reforged.mods.gravisuite.network.ServerPacketHandler;
import reforged.mods.gravisuite.proxy.CommonProxy;
import reforged.mods.gravisuite.utils.Refs;
import net.minecraft.creativetab.CreativeTabs;

import java.util.logging.Logger;

@Mod(modid = Refs.ID, name = Refs.NAME, dependencies = Refs.DEPS, version = Refs.VERSION, acceptedMinecraftVersions = Refs.MC_VERSION)
@NetworkMod(clientSideRequired = true,
        clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = { Refs.ID }, packetHandler = ClientPacketHandler.class),
        serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = { Refs.ID }, packetHandler = ServerPacketHandler.class))
public class GraviSuite {

    @SidedProxy(clientSide = Refs.CLIENT_PROXY, serverSide = Refs.COMMON_PROXY)
    public static CommonProxy PROXY;

    @Mod.Instance(Refs.ID)
    public static GraviSuite INSTANCE;

    public static CreativeTabs TAB = new GraviSuiteTab();
    public static final Logger LOGGER = Logger.getLogger(Refs.NAME);
    public static final String TEXTURE = "/mods/gravisuite/textures/item_components.png";

    public GraviSuite() {
        LOGGER.setParent(FMLLog.getLogger());
    }

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent e) {
        PROXY.preInit(e);
        GraviSuiteData.init();
    }

    @Mod.Init
    public void init(FMLInitializationEvent e) {
        PROXY.registerRenderers();
    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent e) {
        PROXY.postInit(e);
    }
}
