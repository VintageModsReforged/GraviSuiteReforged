package reforged.mods.gravisuite.items.tools.base;

import mods.vintage.core.platform.config.IItemBlockIDProvider;
import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemTool;
import org.jetbrains.annotations.Nullable;
import reforged.mods.gravisuite.GraviSuite;

public class ItemToolBase extends ItemTool implements IItemBlockIDProvider {

    public Integer meta;

    protected ItemToolBase(int id, String name, EnumToolMaterial toolMaterial, @Nullable Integer meta) {
        super(id, 0, toolMaterial, new Block[0]);
        this.setItemName(name);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setCreativeTab(GraviSuite.TAB);
        this.meta = meta;
    }

    @Override
    public String getTextureFile() {
        return GraviSuite.TEXTURE;
    }
}
