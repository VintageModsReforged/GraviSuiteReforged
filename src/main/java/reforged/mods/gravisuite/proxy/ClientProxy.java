package reforged.mods.gravisuite.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.ElectricItem;
import ic2.core.IC2;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteData;
import reforged.mods.gravisuite.events.client.AudioHandler;
import reforged.mods.gravisuite.events.client.ClientArmorHandler;
import reforged.mods.gravisuite.events.client.KeyboardHandler;
import reforged.mods.gravisuite.events.client.OverlayHandler;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.items.armors.base.ItemBaseJetpack;
import reforged.mods.gravisuite.utils.ItemGraviToolRenderer;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForgeClient.preloadTexture(GraviSuite.TEXTURE);
        registerTickHandlers(OverlayHandler.THIS);
        registerTickHandlers(AudioHandler.THIS);
        registerTickHandlers(KeyboardHandler.THIS);
        registerTickHandlers(ClientArmorHandler.THIS);
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

    @Override
    public void registerRenderers() {
        MinecraftForgeClient.registerItemRenderer(GraviSuiteData.GRAVI_TOOL.itemID, new ItemGraviToolRenderer());
    }

    @ForgeSubscribe
    public void onWorldLoad(WorldEvent.Load event) {
        ClientArmorHandler.firstLoad = true;
    }

    @ForgeSubscribe
    public void onWorldUnload(WorldEvent.Unload event) {
        AudioHandler.THIS.getPlayerTickers().clear();
    }

    @Override
    public boolean isFlying(EntityPlayer player) {
        ItemStack armorStack = player.getCurrentArmor(2);
        if (armorStack != null) {
            NBTTagCompound tag = StackUtil.getOrCreateNbtData(armorStack);
            int energyStorage = ElectricItem.discharge(armorStack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
            if (energyStorage > 0) {
                if (armorStack.getItem() instanceof ItemBaseJetpack) {
                    if (!tag.getBoolean(ItemBaseJetpack.NBT_ACTIVE)) {
                        return false;
                    }
                    if (!tag.getBoolean(ItemBaseJetpack.NBT_HOVER_ACTIVE)) {
                        if (IC2.keyboard.isJumpKeyDown(player)) {
                            return tag.getBoolean(ItemBaseJetpack.NBT_ACTIVE);
                        } else {
                            return IC2.keyboard.isAltKeyDown(player) && !player.onGround;
                        }
                    } else {
                        return !player.onGround && (!IC2.keyboard.isAltKeyDown(player) || IC2.keyboard.isJumpKeyDown(player));
                    }
                } else if (armorStack.getItem() instanceof ItemAdvancedQuant) {
                    return tag.getBoolean("isFlyActive");
                }
            }
        }
        return false;
    }

    @Override
    public void registerTickHandlers(ITickHandler handler) {
        TickRegistry.registerTickHandler(handler, Side.CLIENT);
    }
}
