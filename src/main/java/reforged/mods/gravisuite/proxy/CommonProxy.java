package reforged.mods.gravisuite.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import reforged.mods.gravisuite.GraviSuiteData;
import reforged.mods.gravisuite.GraviSuiteRecipes;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {
        GraviSuiteData.init();
    }
    public void init(FMLInitializationEvent e) {}
    public void postInit(FMLPostInitializationEvent e) {
        GraviSuiteRecipes.initRecipes();
    }

    public int addArmor(String armorName) { return 0; }
}
