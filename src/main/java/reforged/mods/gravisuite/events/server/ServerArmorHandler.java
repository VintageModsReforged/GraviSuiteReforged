package reforged.mods.gravisuite.events.server;

import cpw.mods.fml.common.TickType;
import mods.vintage.core.platform.events.tick.TickEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.proxy.CommonProxy;
import reforged.mods.gravisuite.utils.Refs;

import java.util.EnumSet;

public class ServerArmorHandler extends TickEvents.PlayerTickEvent {

    public static final ServerArmorHandler THIS = new ServerArmorHandler();

    public static boolean firstLoad = false;

    public ServerArmorHandler() {
        super(Refs.ID);
    }

    @Override
    public void tickStart(EnumSet<TickType> enumSet, Object... objects) {
        if (shouldTick(enumSet)) {
            EntityPlayer player = (EntityPlayer) objects[0];
            ItemStack itemstack = player.getCurrentArmor(2);
            if (itemstack != null) {
                if(itemstack.getItem() instanceof ItemAdvancedQuant) {
                    if (firstLoad && CommonProxy.wasUndressed(player)) {
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
