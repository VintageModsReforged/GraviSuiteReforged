package reforged.mods.gravisuite.utils;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.items.tools.ItemGraviTool;

import static reforged.mods.gravisuite.items.tools.ItemGraviTool.readToolMode;

public class ItemGraviToolRenderer implements IItemRenderer {

    private static final RenderItem renderItem = new RenderItem();

    public ItemGraviToolRenderer() {
        GraviSuite.LOGGER.info("Renderer Call!");
    }

    @Override
    public boolean handleRenderType(ItemStack itemStack, ItemRenderType itemRenderType) {
        return itemRenderType == ItemRenderType.INVENTORY || itemRenderType == ItemRenderType.EQUIPPED;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType itemRenderType, ItemStack itemStack, ItemRendererHelper itemRendererHelper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType itemRenderType, ItemStack itemStack, Object... objects) {
        ItemGraviTool.ToolMode mode = readToolMode(itemStack);
        if (itemRenderType == ItemRenderType.INVENTORY) {
            renderItem.renderTexturedQuad(0, 0, mode.index % 16 * 16, mode.index / 16 * 16, 16, 16);
        }

        if (itemRenderType == ItemRenderType.EQUIPPED) {
            CustomRender.renderItem(GraviSuite.TEXTURE, mode.index, itemStack, 1);
        }
    }
}
