package reforged.mods.gravisuite.items.armors;

import ic2.api.item.ElectricItem;
import ic2.api.item.Items;
import ic2.core.IC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.armors.base.ItemBaseJetpack;

public class ItemAdvancedJetpack extends ItemBaseJetpack {

    public ItemAdvancedJetpack() {
        super(GraviSuiteConfig.ADVANCED_JETPACK_ID, "advanced_jetpack");
    }

    public static class ItemAdvancedNano extends ItemBaseJetpack {

        public final byte TICK_RATE = 20;
        public byte TICKER;
        public final int ENERGY_PER_EXTINGUISH = 50000;

        public ItemAdvancedNano() {
            super(GraviSuiteConfig.ADVANCED_NANO_ID, "advanced_nano");
            this.energy_per_damage = 800;
            this.damage_priority = 8;
            this.base_absorption = 0.4D;
            this.damage_absorption = 0.9D;
        }

        @Override
        public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack stack) {
            super.onArmorTickUpdate(world, player, stack);
            if (IC2.platform.isSimulating()) {
                if (TICKER++ % TICK_RATE == 0) {
                    if (player.isBurning()) {
                        if (ElectricItem.manager.canUse(stack, this.ENERGY_PER_EXTINGUISH)) {
                            for (ItemStack waterCell : player.inventory.mainInventory) {
                                if (waterCell != null) {
                                    if (waterCell.getItem() == Items.getItem("waterCell").getItem()) {
                                        if (waterCell.stackSize > 0) {
                                            waterCell.stackSize--;
                                        }
                                        ElectricItem.manager.discharge(stack, ENERGY_PER_EXTINGUISH, this.tier, true, false);
                                        player.extinguish();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public int getArmorDisplay(EntityPlayer entityPlayer, ItemStack itemStack, int i) {
            return 8;
        }
    }
}
