package reforged.mods.gravisuite;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.proxy.ClientProxy;
import reforged.mods.gravisuite.proxy.CommonProxy;
import reforged.mods.gravisuite.utils.Refs;

import java.util.EnumSet;

public class ClientTickHandler implements ITickHandler {

    public static boolean firstLoad = false;

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        if (type.contains(TickType.PLAYER)) {
            EntityPlayer player = (EntityPlayer) tickData[0];
            ItemStack itemstack = player.getCurrentArmor(2);
            if (itemstack != null) {
                if(itemstack.getItem() instanceof ItemAdvancedQuant) {
                    if (firstLoad && CommonProxy.wasUndressed(player)) {
                        ClientProxy.sendPacket("firstLoad", 1);
                        if (ItemAdvancedQuant.readFlyStatus(itemstack)) {
                            ItemAdvancedQuant.saveFlyStatus(itemstack, false);
                            firstLoad = false;
                        }
                    }
                    if (!ItemAdvancedQuant.readFlyStatus(itemstack)) {
                        if (!player.capabilities.isCreativeMode) {
                            player.capabilities.allowFlying = false;
                            player.capabilities.isFlying = false;
                        }
                    }
                }
            } else {
                ItemAdvancedQuant.removeSound();
                CommonProxy.wasUndressed.put(player, true);
                if (!player.capabilities.isCreativeMode) {
                    if (CommonProxy.isFlyActive(player)) {
                        player.capabilities.allowFlying = false;
                        player.capabilities.isFlying = false;
                    }
                }
            }
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> enumSet, Object... objects) {}

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.PLAYER, TickType.WORLDLOAD);
    }

    @Override
    public String getLabel() {
        return Refs.id;
    }
}
