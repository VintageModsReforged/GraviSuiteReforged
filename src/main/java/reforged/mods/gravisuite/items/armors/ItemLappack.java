package reforged.mods.gravisuite.items.armors;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.items.armors.base.ItemBaseEnergyPack;

public class ItemLappack {

    public static class ItemAdvancedLappack extends ItemBaseEnergyPack {

        public ItemAdvancedLappack() {
            super(GraviSuiteMainConfig.ADVANCED_LAPPACK_ID,0, "advanced_lappack", 2, 1000, 1000000);
        }
    }

    public static class ItemUltimateLappack extends ItemBaseEnergyPack {

        public ItemUltimateLappack() {
            super(GraviSuiteMainConfig.ULTIMATE_LAPPACK_ID, 1, "ultimate_lappack", 3, 20000, 10000000);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public EnumRarity getRarity(ItemStack stack) {
            return EnumRarity.epic;
        }
    }
}
