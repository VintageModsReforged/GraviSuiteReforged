package reforged.mods.gravisuite.items.tools.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemTool;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.utils.Refs;

public class ItemToolBase extends ItemTool {

    public String name;

    protected ItemToolBase(int id, String name, EnumToolMaterial material) {
        super(id, 0, material, new Block[0]);
        this.setUnlocalizedName(name);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setCreativeTab(GraviSuite.graviTab);
        this.name = name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister icons) {
        this.itemIcon = icons.registerIcon(Refs.id + ":" + this.name);
    }
}
