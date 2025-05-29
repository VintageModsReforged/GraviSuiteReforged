package reforged.mods.gravisuite.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import reforged.mods.gravisuite.utils.Refs;
import reforged.mods.gravisuite.utils.VoiderContainer;

public class GuiVoider extends GuiContainer {

    public GuiVoider(EntityPlayer player) {
        super(new VoiderContainer(player));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1) {
        ResourceLocation TEXTURE = new ResourceLocation(Refs.id, "textures/gui/voider.png");
        this.mc.renderEngine.bindTexture(TEXTURE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
