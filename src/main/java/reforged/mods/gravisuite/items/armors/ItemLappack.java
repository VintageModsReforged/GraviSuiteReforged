package reforged.mods.gravisuite.items.armors;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.armors.base.ItemArmorElectric;

public class ItemLappack {

    public static class ItemAdvancedLappack extends ItemArmorElectric {

        public ItemAdvancedLappack() {
            super(GraviSuiteConfig.ADVANCED_LAPPACK_ID, "advanced_lappack", 3, 1000, 1000000);
        }
    }

    public static class ItemUltimateLappack extends ItemArmorElectric {

        public ItemUltimateLappack() {
            super(GraviSuiteConfig.ULTIMATE_LAPPACK_ID, "ultimate_lappack", 3, 20000, 10000000);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public EnumRarity getRarity(ItemStack stack) {
            return EnumRarity.epic;
        }
    }
 }
