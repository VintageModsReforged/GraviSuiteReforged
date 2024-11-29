package reforged.mods.gravisuite.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteMainConfig;

import java.util.List;

public class ItemComponent extends Item {

    public String[] names = new String[] {
            "superconductor_cover",
            "superconductor",
            "cooling_core",
            "gravi_engine",
            "magnetron",
            "vajra_core",
            "engine_booster"
    };

    public ItemComponent() {
        super(GraviSuiteMainConfig.COMPONENT_ID);
        this.setCreativeTab(GraviSuite.TAB);
        this.setItemName("component");
        this.setHasSubtypes(true);
    }

    @Override
    public String getItemNameIS(ItemStack stack) {
        return super.getItemNameIS(stack) + "." + names[stack.getItemDamage()];
    }

    @Override
    public int getIconFromDamage(int index) {
        return index;
    }

    @Override
    public String getTextureFile() {
        return GraviSuite.TEXTURE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubItems(int id, CreativeTabs tab, List items) {
        for (int i = 0; i < names.length; i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }
}
