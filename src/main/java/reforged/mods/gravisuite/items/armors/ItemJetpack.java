package reforged.mods.gravisuite.items.armors;

import ic2.api.Items;
import ic2.core.IC2;
import ic2.core.item.ElectricItem;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import reforged.mods.gravisuite.GraviSuiteData;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.items.armors.base.ItemBaseJetpack;

public class ItemJetpack {

    public static class ItemAdvancedElectricJetpack extends ItemBaseJetpack {

        public ItemAdvancedElectricJetpack() {
            super(GraviSuiteMainConfig.ADVANCED_JETPACK_ID, GraviSuiteData.GRAVI_MATERIAL, 2, "advanced_jetpack");
        }
    }

    public static class ItemAdvancedNano extends ItemBaseJetpack implements ISpecialArmor {

        public byte TICK_RATE = 20;
        public byte TICKER;
        public int ENERGY_PER_EXTINGUISH = 50000;
        public int ENERGY_PER_DAMAGE = 800;

        public ItemAdvancedNano() {
            super(GraviSuiteMainConfig.ADVANCED_NANO_ID, EnumArmorMaterial.DIAMOND, 3, "advanced_nano");
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

        @Override
        public ArmorProperties getProperties(EntityLiving entityLiving, ItemStack armor, DamageSource damageSource, double damage, int slot) {
            if (damageSource.isUnblockable()) {
                return new ArmorProperties(0, 0, 0);
            } else {
                double absorptionRatio = 0.4D * 0.9D;
                int energyPerDamage = ENERGY_PER_DAMAGE;
                int damageLimit = energyPerDamage > 0 ? 25 * ElectricItem.discharge(armor, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / energyPerDamage : 0;
                return new ISpecialArmor.ArmorProperties(0, absorptionRatio, damageLimit);
            }
        }

        @Override
        public int getArmorDisplay(EntityPlayer entityPlayer, ItemStack stack, int slot) {
            return ElectricItem.discharge(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) >= this.ENERGY_PER_DAMAGE ? (int) Math.round((double) 20.0F * 1.1D * 0.4D) : 0;
        }
        @Override
        public void damageArmor(EntityLiving entityLiving, ItemStack stack, DamageSource damageSource, int damage, int slot) {
            ElectricItem.discharge(stack, damage * ENERGY_PER_DAMAGE, Integer.MAX_VALUE, true, false);
        }
    }
 }
