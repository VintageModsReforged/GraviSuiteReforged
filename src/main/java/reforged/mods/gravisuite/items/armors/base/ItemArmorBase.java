package reforged.mods.gravisuite.items.armors.base;

import net.minecraft.item.ItemArmor;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteData;

public class ItemArmorBase extends ItemArmor {

    public ItemArmorBase(int id, String name) {
        super(id, GraviSuiteData.GRAVI_MATERIAL, GraviSuite.proxy.addArmor(name), 1);
        this.setUnlocalizedName(name);
        this.setCreativeTab(GraviSuite.graviTab);
    }
}
