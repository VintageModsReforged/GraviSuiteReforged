package reforged.mods.gravisuite.proxy;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import mods.vintage.core.platform.config.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.GraviSuiteData;
import reforged.mods.gravisuite.GraviSuiteRecipes;
import reforged.mods.gravisuite.events.server.ServerArmorHandler;
import reforged.mods.gravisuite.utils.Refs;

import java.util.HashMap;
import java.util.Map;

public class CommonProxy {

    public static Map<EntityPlayer, Boolean> isFlyActive = new HashMap<EntityPlayer, Boolean>();
    public static Map<EntityPlayer, Boolean> wasUndressed = new HashMap<EntityPlayer, Boolean>();

    GraviSuiteConfig CONFIG = new GraviSuiteConfig();
    ConfigHandler CONFIG_HANDLER = new ConfigHandler(Refs.id);

    public void preInit(FMLPreInitializationEvent e) {
        CONFIG_HANDLER.initIDs(CONFIG);
        registerTickHandlers(ServerArmorHandler.THIS);
    }

    public void init(FMLInitializationEvent e) {
        CONFIG_HANDLER.confirmIDs(CONFIG);
        GraviSuiteData.init();
    }

    public void postInit(FMLPostInitializationEvent e) {
        CONFIG_HANDLER.confirmOwnership(CONFIG);
        GraviSuiteRecipes.initRecipes();
    }

    public int addArmor(String armorName) { return 0; }

    public static boolean isFlyActive(EntityPlayer player) {
        return isFlyActive.containsKey(player) && isFlyActive.get(player);
    }

    public static boolean wasUndressed(EntityPlayer player) {
        return wasUndressed.containsKey(player) && wasUndressed.get(player);
    }

    public boolean isFlying(EntityPlayer player) {
        return false;
    }

    public boolean isSneakKeyDown() {
        return false;
    }

    public void registerTickHandlers(ITickHandler handler) {
        TickRegistry.registerTickHandler(handler, Side.SERVER);
    }

    public void sendChatMessage(EntityPlayer player, String message, Object... args) {
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            ChatMessageComponent messageComponent;
            if (args.length > 0) {
                messageComponent = ChatMessageComponent.createFromTranslationWithSubstitutions(message, args);
            } else {
                messageComponent = ChatMessageComponent.createFromTranslationKey(message);
            }
            playerMP.sendChatToPlayer(messageComponent);
        }
    }
}
