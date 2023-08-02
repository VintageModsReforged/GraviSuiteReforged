package reforged.mods.gravisuite.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {}
    public void init(FMLInitializationEvent e) {}
    public void postInit(FMLPostInitializationEvent e) {}

    public int addArmor(String armorName) { return 0; }
}
