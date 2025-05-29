package reforged.mods.gravisuite.items.armors;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.items.armors.base.ItemBaseEnergyPack;
import reforged.mods.gravisuite.utils.EnergyValues;

public class ItemLappack {

    public static class ItemAdvancedLappack extends ItemBaseEnergyPack {

        public ItemAdvancedLappack() {
            super(GraviSuiteMainConfig.ADVANCED_LAPPACK_ID,0, "advanced_lappack", EnergyValues.ADV_LAPPACK.tier, EnergyValues.ADV_LAPPACK.transfer, EnergyValues.ADV_LAPPACK.maxCapacity);
        }
    }

    public static class ItemUltimateLappack extends ItemBaseEnergyPack {

        public ItemUltimateLappack() {
            super(GraviSuiteMainConfig.ULTIMATE_LAPPACK_ID, 1, "ultimate_lappack", EnergyValues.ULT_LAPPACK.tier, EnergyValues.ULT_LAPPACK.transfer, EnergyValues.ULT_LAPPACK.maxCapacity);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public EnumRarity getRarity(ItemStack stack) {
            return EnumRarity.epic;
        }
    }
}
