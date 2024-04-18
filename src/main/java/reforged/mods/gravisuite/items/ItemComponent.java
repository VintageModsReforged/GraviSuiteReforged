package reforged.mods.gravisuite.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.utils.Refs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemComponent extends Item {

    public int META;

    public ItemComponent(int id, String name, int meta) {
        super(id);
        this.setCreativeTab(GraviSuite.TAB);
        this.setItemName(name);
        this.META = meta;
        this.setIconIndex(Refs.COMPONENTS_ICON_ID + META);
    }

    @Override
    public String getTextureFile() {
        return GraviSuite.TEXTURE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubItems(int id, CreativeTabs tab, List items) {
        items.add(new ItemStack(this));
    }
}
