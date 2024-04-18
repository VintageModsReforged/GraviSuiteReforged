package reforged.mods.gravisuite;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.creativetab.CreativeTabs;
import reforged.mods.gravisuite.proxy.CommonProxy;
import reforged.mods.gravisuite.utils.Refs;

import java.util.logging.Logger;

@Mod(modid = Refs.id, name = Refs.name, version = Refs.version, acceptedMinecraftVersions = Refs.mc, dependencies = Refs.deps)
public class GraviSuite {

    @SidedProxy(clientSide = Refs.client, serverSide = Refs.common)
    public static CommonProxy proxy;

    public static final CreativeTabs graviTab = new GraviSuiteTab();

    public static final Logger logger = Logger.getLogger(Refs.id);

    public GraviSuite() {
        logger.setParent(FMLLog.getLogger());
    }

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit(e);

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
