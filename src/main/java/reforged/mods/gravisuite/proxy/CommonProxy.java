package reforged.mods.gravisuite.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import reforged.mods.gravisuite.GraviSuiteData;
import reforged.mods.gravisuite.GraviSuiteRecipes;
import reforged.mods.gravisuite.CommonTickHandler;

import java.util.HashMap;
import java.util.Map;

public class CommonProxy {

    public static Map<EntityPlayer, Boolean> isFlyActive = new HashMap<EntityPlayer, Boolean>();
    public static Map<EntityPlayer, Boolean> wasUndressed = new HashMap<EntityPlayer, Boolean>();

    public void preInit(FMLPreInitializationEvent e) {
        TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);
        GraviSuiteData.init();
    }

    public void init(FMLInitializationEvent e) {}

    public void postInit(FMLPostInitializationEvent e) {
        GraviSuiteRecipes.initRecipes();
    }

    public int addArmor(String armorName) { return 0; }

    public static boolean isFlyActive(EntityPlayer player) {
        return isFlyActive.containsKey(player) ? isFlyActive.get(player) : false;
    }

    public static boolean wasUndressed(EntityPlayer player) {
        return wasUndressed.containsKey(player) ? wasUndressed.get(player) : false;
    }
}
