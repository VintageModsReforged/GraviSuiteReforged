package reforged.mods.gravisuite.client.gui;

import mods.vintage.core.platform.lang.FormattedTranslator;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.utils.RelocatorContainer;

public class GuiRelocatorAdd extends GuiContainer {

    private static String TEXTURE = "/mods/gravisuite/textures/gui/relocator_add.png";

    private GuiTextField TEXT_FIELD;

    private int MOUSE_X;

    private int MOUSE_Y;

    public GuiRelocatorAdd() {
        super(new RelocatorContainer());

        this.xSize = 135;
        this.ySize = 59;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.TEXT_FIELD = new GuiTextField(fontRenderer, i + 7, j + 19, 121, 12);
        this.TEXT_FIELD.setTextColor(16777215);
        this.TEXT_FIELD.setDisabledTextColour(-1);
        this.TEXT_FIELD.setEnableBackgroundDrawing(false);
        this.TEXT_FIELD.setMaxStringLength(20);
        this.TEXT_FIELD.setFocused(true);
        this.TEXT_FIELD.setCanLoseFocus(false);
        this.TEXT_FIELD.setText("");
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        if (isPointInRegion(37, 37, 26, 13, this.MOUSE_X, this.MOUSE_Y))
            sendPoint();
        if (isPointInRegion(70, 37, 26, 13, this.MOUSE_X, this.MOUSE_Y))
            this.mc.thePlayer.closeScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);
        this.MOUSE_X = mouseX;
        this.MOUSE_Y = mouseY;
        this.TEXT_FIELD.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        String addText = FormattedTranslator.WHITE.format("message.text.relocator.add");
        this.fontRenderer.drawString(addText, 13, 5, 16777215);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        if (isPointInRegion(37, 37, 26, 13, this.MOUSE_X, this.MOUSE_Y)) {
            drawTexturedModalRect(i + 37, j + 37, 140, 19, 26, 13);
        } else {
            drawTexturedModalRect(i + 37, j + 37, 140, 3, 26, 13);
        }
        if (isPointInRegion(70, 37, 26, 13, this.MOUSE_X, this.MOUSE_Y)) {
            drawTexturedModalRect(i + 70, j + 37, 173, 19, 26, 13);
        } else {
            drawTexturedModalRect(i + 70, j + 37, 173, 3, 26, 13);
        }
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    protected void keyTyped(char character, int i) {
        this.TEXT_FIELD.textboxKeyTyped(character, i);
        if (i == 1) {
            this.mc.thePlayer.closeScreen();
        } else if (i == 28) {
            sendPoint();
        }
    }

    @Override
    public void updateScreen() {
        this.TEXT_FIELD.updateCursorCounter();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    public void sendPoint() {
        if (!this.TEXT_FIELD.getText().trim().isEmpty()) {
            GraviSuite.network.sendRelocatorPoints(this.TEXT_FIELD.getText(), (byte) 1);
            this.mc.thePlayer.closeScreen();
        }
    }
}
