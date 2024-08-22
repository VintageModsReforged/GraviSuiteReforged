package reforged.mods.gravisuite.events.tick.client;

import cpw.mods.fml.common.TickType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.events.tick.base.TickEvents;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.proxy.CommonProxy;

import java.util.EnumSet;

public class ClientArmorHandler extends TickEvents.PlayerTickEvent {

    public static final ClientArmorHandler THIS = new ClientArmorHandler();

    public static boolean firstLoad = false;

    @Override
    public void tickStart(EnumSet<TickType> enumSet, Object... objects) {
        if (shouldTick(enumSet)) {
            EntityPlayer player = (EntityPlayer) objects[0];
            ItemStack itemstack = player.getCurrentArmor(2);
            if (itemstack != null) {
                if(itemstack.getItem() instanceof ItemAdvancedQuant) {
                    if (firstLoad && CommonProxy.wasUndressed(player)) {
                        GraviSuite.network.sendWorldLoadState();
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
                } else {
                    CommonProxy.wasUndressed.put(player, true);
                    if (!player.capabilities.isCreativeMode) {
                        if (CommonProxy.isFlyActive(player)) {
                            player.capabilities.allowFlying = false;
                            player.capabilities.isFlying = false;
                        }
                    }
                }
            } else {
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
}
