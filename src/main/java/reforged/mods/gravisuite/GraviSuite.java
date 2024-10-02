package reforged.mods.gravisuite;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import reforged.mods.gravisuite.events.BlockEvents;
import reforged.mods.gravisuite.keyboard.GraviSuiteKeyboard;
import reforged.mods.gravisuite.network.NetworkHandler;
import reforged.mods.gravisuite.network.NetworkHandlerClient;
import reforged.mods.gravisuite.proxy.CommonProxy;
import reforged.mods.gravisuite.utils.Refs;

import java.util.logging.Logger;

@Mod(modid = Refs.id, name = Refs.name, version = Refs.version, acceptedMinecraftVersions = Refs.mc, dependencies = Refs.deps)
@NetworkMod(clientSideRequired = true,
        clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = { Refs.id }, packetHandler = NetworkHandlerClient.class),
        serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = { Refs.id }, packetHandler = NetworkHandler.class))
public class GraviSuite {

    @SidedProxy(clientSide = Refs.client, serverSide = Refs.common)
    public static CommonProxy proxy;

    @SidedProxy(clientSide = Refs.keyboardClient, serverSide = Refs.keyboardCommon)
    public static GraviSuiteKeyboard keyboard;

    @SidedProxy(clientSide = Refs.networkClient, serverSide = Refs.networkCommon)
    public static NetworkHandler network;

    public static final CreativeTabs graviTab = new CreativeTabs(Refs.id) {{
            LanguageRegistry.instance().addStringLocalization("itemGroup." + Refs.id, Refs.name);
        }

        @Override
        public Item getTabIconItem() {
            return GraviSuiteData.advanced_quant;
        }
    };

    public static final Logger logger = Logger.getLogger(Refs.id);

    public GraviSuite() {
        logger.setParent(FMLLog.getLogger());
    }

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit(e);
        GraviSuiteData.init();
        MinecraftForge.EVENT_BUS.register(new BlockEvents());
    }

    @Mod.Init
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }
}
