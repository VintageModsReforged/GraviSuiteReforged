package reforged.mods.gravisuite.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import reforged.mods.gravisuite.audio.IAudioTicker;
import reforged.mods.gravisuite.audio.tickers.ArmorAudioTicker;
import reforged.mods.gravisuite.audio.tickers.ChainsawAudioTicker;
import reforged.mods.gravisuite.events.client.AudioHandler;
import reforged.mods.gravisuite.events.client.ClientArmorHandler;
import reforged.mods.gravisuite.events.client.KeyboardHandler;
import reforged.mods.gravisuite.events.client.OverlayHandler;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.items.armors.base.ItemBaseJetpack;
import reforged.mods.gravisuite.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends CommonProxy {

    public static List<IAudioTicker> TICKERS = new ArrayList<IAudioTicker>();

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
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

    @ForgeSubscribe
    public void onWorldLoad(WorldEvent.Load event) {
        ClientArmorHandler.firstLoad = true;
    }

    @SideOnly(Side.CLIENT)
    @ForgeSubscribe
    public void onSpawn(EntityJoinWorldEvent e) {
        Entity entity = e.entity;
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            addAudioTicker(new ChainsawAudioTicker(player));
            addAudioTicker(new ArmorAudioTicker(player));
        }
    }

    @Override
    public boolean isFlying(EntityPlayer player) {
        ItemStack armorStack = player.getCurrentArmor(2);
        if (armorStack != null) {
            NBTTagCompound tag = Helpers.getOrCreateTag(armorStack);
            int energyStorage = ElectricItem.manager.getCharge(armorStack);
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

    public void addAudioTicker(IAudioTicker ticker) {
        TICKERS.add(ticker);
    }
}
