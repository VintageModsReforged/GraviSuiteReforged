package reforged.mods.gravisuite.items.armors;

import ic2.api.Items;
import ic2.core.item.ElectricItem;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.items.armors.base.ItemBaseJetpack;
import reforged.mods.gravisuite.utils.Helpers;

public class ItemJetpack {

    public static class ItemAdvancedElectricJetpack extends ItemBaseJetpack {

        public ItemAdvancedElectricJetpack() {
            super(GraviSuiteMainConfig.ADVANCED_JETPACK_ID, 2, "advanced_jetpack");
        }
    }

    public static class ItemAdvancedNano extends ItemBaseJetpack implements ISpecialArmor {

        public byte TICK_RATE = 20;
        public byte TICKER;
        public int ENERGY_PER_EXTINGUISH = 50000;
        public int ENERGY_PER_DAMAGE = 800;

        public ItemAdvancedNano() {
            super(GraviSuiteMainConfig.ADVANCED_NANO_ID, 3, "advanced_nano");
        }

        @Override
        public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack stack) {
            super.onArmorTickUpdate(world, player, stack);
            if (TICKER++ % TICK_RATE == 0) {
                if (player.isBurning()) {
                    if (ElectricItem.canUse(stack, ENERGY_PER_EXTINGUISH)) {
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
        }

        @Override
        public ArmorProperties getProperties(EntityLiving entityLiving, ItemStack armor, DamageSource damageSource, double damage, int slot) {
            int damageLimit = Math.min(Integer.MAX_VALUE, 25 * Helpers.getCharge(armor) / ENERGY_PER_DAMAGE);
            double absorptionRatio = 0.4D * 0.9D;
            return new ArmorProperties(8, absorptionRatio, damageLimit);
        }

        @Override
        public int getArmorDisplay(EntityPlayer entityPlayer, ItemStack itemStack, int i) {
            return 8;
        }

        @Override
        public void damageArmor(EntityLiving entityLiving, ItemStack stack, DamageSource damageSource, int damage, int slot) {
            ElectricItem.discharge(stack, damage * ENERGY_PER_DAMAGE, Integer.MAX_VALUE, true, false);
        }
    }
 }
