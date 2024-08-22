package reforged.mods.gravisuite.proxy;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import reforged.mods.gravisuite.*;
import reforged.mods.gravisuite.events.tick.server.ServerArmorHandler;
import reforged.mods.gravisuite.utils.LangHelper;
import reforged.mods.gravisuite.utils.Refs;

import java.util.HashMap;
import java.util.Map;

public class CommonProxy {

    public static Map<EntityPlayer, Boolean> isFlyActive = new HashMap<EntityPlayer, Boolean>();
    public static Map<EntityPlayer, Boolean> wasUndressed = new HashMap<EntityPlayer, Boolean>();

    public void preInit(FMLPreInitializationEvent e) {
        registerTickHandlers(ServerArmorHandler.THIS);
        GraviSuiteConfig.initConfig();
        LangHelper.init();
    }

    public void init(FMLInitializationEvent e) {
        if (Loader.isModLoaded("TreeCapitator")) {
            NBTTagCompound capitatorModConfig = new NBTTagCompound();
            capitatorModConfig.setString("modID", Refs.id);
            capitatorModConfig.setString("axeIDList", GraviSuiteData.advanced_chainsaw.itemID + "; " + GraviSuiteData.vajra.itemID);
            FMLInterModComms.sendMessage("TreeCapitator", "ThirdPartyModConfig", capitatorModConfig);
            GraviSuite.logger.info("TreeCapitator Compat Loaded!");
        }
    }

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

    public boolean isFlying(EntityPlayer player) {
        return false;
    }

    public void registerTickHandlers(ITickHandler handler) {
        TickRegistry.registerTickHandler(handler, Side.SERVER);
    }
}
