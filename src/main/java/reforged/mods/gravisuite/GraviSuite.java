package reforged.mods.gravisuite;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import reforged.mods.gravisuite.keyboard.GraviSuiteKeyboard;
import reforged.mods.gravisuite.network.NetworkHandler;
import reforged.mods.gravisuite.network.NetworkHandlerClient;
import reforged.mods.gravisuite.proxy.CommonProxy;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.logging.Logger;

@Mod(modid = Refs.ID, name = Refs.NAME, dependencies = Refs.DEPS, version = Refs.VERSION, acceptedMinecraftVersions = Refs.MC_VERSION)
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

    public static final CreativeTabs TAB = new CreativeTabs(Refs.ID) {{
            LanguageRegistry.instance().addStringLocalization("itemGroup." + Refs.ID, Refs.NAME);
    }
        @Override
        public Item getTabIconItem() {
            return GraviSuiteData.ADVANCED_QUANT;
        }
    };

    public static final Logger LOGGER = Logger.getLogger(Refs.NAME);
    public static final String TEXTURE = "/mods/gravisuite/textures/item_components.png";

    public GraviSuite() {
        LOGGER.setParent(FMLLog.getLogger());
        MinecraftForge.EVENT_BUS.register(this);
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

    @ForgeSubscribe
    public void onRightClick(PlayerInteractEvent e) {
        if (GraviSuiteMainConfig.INSPECT_MODE && e.entityPlayer.getHeldItem() != null) {
            if (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && e.entityPlayer.getHeldItem().getItem() == Item.stick) {
                Block block = Helpers.getBlock(e.entity.worldObj, e.x, e.y, e.z);
                int metadata = e.entityPlayer.worldObj.getBlockMetadata(e.x, e.y, e.z);
                if (block != null) {
                    LOGGER.info("Block: " + block.translateBlockName() + " | Class Name: " + block.getClass().getName());
                    LOGGER.info("Block Metadata: " + metadata);
                }
            }
        }
    }
}
