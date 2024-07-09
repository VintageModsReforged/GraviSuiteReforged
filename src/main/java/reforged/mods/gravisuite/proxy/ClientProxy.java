package reforged.mods.gravisuite.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import reforged.mods.gravisuite.ClientTickHandler;
import reforged.mods.gravisuite.GraviSuiteOverlay;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
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
}
