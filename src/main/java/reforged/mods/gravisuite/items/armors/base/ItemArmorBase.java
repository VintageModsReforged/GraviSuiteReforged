package reforged.mods.gravisuite.items.armors.base;

import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import reforged.mods.gravisuite.GraviSuite;

public class ItemArmorBase extends ItemArmor {

    public ItemArmorBase(int id, String name, EnumArmorMaterial armorMaterial) {
        super(id, armorMaterial, GraviSuite.proxy.addArmor(name), 1);
        this.setUnlocalizedName(name);
        this.setCreativeTab(GraviSuite.graviTab);
    }
}
