package reforged.mods.gravisuite.items.armors;

import ic2.api.Items;
import ic2.core.IC2;
import ic2.core.item.ElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import reforged.mods.gravisuite.GraviSuiteData;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.armors.base.ItemBaseJetpack;

public class ItemAdvancedJetpack {

    public static class ItemAdvancedElectricJetpack extends ItemBaseJetpack {

        public ItemAdvancedElectricJetpack() {
            super(GraviSuiteConfig.ADVANCED_JETPACK_ID.get(), GraviSuiteData.GRAVI_MATERIAL, 2, "advanced_jetpack");
        }
    }

    public static class ItemAdvancedNano extends ItemBaseJetpack implements ISpecialArmor {

        public byte TICK_RATE = 20;
        public byte TICKER;
        public int ENERGY_PER_EXTINGUISH = 50000;
        public int ENERGY_PER_DAMAGE = 800;

        public ItemAdvancedNano() {
            super(GraviSuiteConfig.ADVANCED_NANO_ID.get(), EnumArmorMaterial.DIAMOND, 3, "advanced_nano");
            this.ENERGY_PER_DAMAGE = 800;
            this.DAMAGE_PRIORITY = 8;
            this.BASE_ABSORPTION = 0.4D;
            this.DAMAGE_ABSORPTION = 0.9D;
        }

        @Override
        public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack stack) {
            super.onArmorTickUpdate(world, player, stack);
            if (IC2.platform.isRendering() || !player.isBurning() || TICKER++ % TICK_RATE != 0) return;
            if (ElectricItem.canUse(stack, ENERGY_PER_EXTINGUISH)) return;
            for (ItemStack item : player.inventory.mainInventory) {
                if (item != null && item.getItem() == Items.getItem("waterCell").getItem()) {
                    if (item.stackSize > 0) {
                        item.stackSize--;
                    }
                    ElectricItem.discharge(stack, ENERGY_PER_EXTINGUISH, Integer.MAX_VALUE, true, false);
                    player.extinguish();
                }
            }
        }
    }
 }
