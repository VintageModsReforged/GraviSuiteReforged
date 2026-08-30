package reforged.mods.gravisuite.items.armors.base;

import mods.vintage.core.platform.config.IItemBlockIDProvider;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import reforged.mods.gravisuite.GraviSuite;

public class ItemArmorBase extends ItemArmor implements IItemBlockIDProvider {

    public ItemArmorBase(int id, String name, EnumArmorMaterial armorMaterial) {
        super(id, armorMaterial, GraviSuite.PROXY.addArmor(name), 1);
        this.setItemName(name);
        this.setCreativeTab(GraviSuite.TAB);
    }
}
