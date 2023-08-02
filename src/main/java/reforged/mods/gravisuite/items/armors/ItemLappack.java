package reforged.mods.gravisuite.items.armors;

import net.minecraft.item.EnumRarity;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.armors.base.ItemArmorElectric;

public class ItemLappack {

    public static class ItemAdvancedLappack extends ItemArmorElectric {

        public ItemAdvancedLappack() {
            super(GraviSuiteConfig.ADVANCED_LAPPACK_ID, "advanced_lappack", 3, 1000, 1000000, EnumRarity.uncommon);
        }
    }

    public static class ItemUltimateLappack extends ItemArmorElectric {

        public ItemUltimateLappack() {
            super(GraviSuiteConfig.ULTIMATE_LAPPACK_ID, "ultimate_lappack", 3, 50000, 10000000, EnumRarity.rare);
        }
    }
 }
