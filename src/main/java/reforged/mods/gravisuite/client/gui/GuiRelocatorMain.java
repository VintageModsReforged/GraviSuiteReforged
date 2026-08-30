package reforged.mods.gravisuite.client.gui;

import mods.vintage.core.platform.lang.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.common.DimensionManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.items.tools.relocator.ItemRelocator;
import reforged.mods.gravisuite.items.tools.relocator.TeleportPoint;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.RelocatorContainer;

import java.util.ArrayList;
import java.util.List;

public class GuiRelocatorMain extends GuiContainer {

    private static String TEXTURE = "/mods/gravisuite/textures/gui/relocator_display.png";


    private int mouseX;
    private int mouseY;

    private final int itemInterval;
    private final int firstItemX;
    private final int firstItemY;

    private final int cancelBtnX1;
    private int cancelBtnSize;

    private final int firstSelX;
    private final int firstSelY;

    private final int selWidth;
    private final int selHeight;

    private final int firstItemBGX;
    private final int firstItemBGY;

    private final int itemBGinterval;

    private final int cancelBtnWidth;
    private final int cancelBtnHeight;

    private final int openType;

    private final int itemBGX;
    private final int itemBGY;

    private final int itemBGWidth;
    private final int itemBGHeight;

    private final int itemBGdefX;
    private final int itemBGdefY;

    private final int itemBGselX;
    private final int itemBGselY;

    private final int itemBGdelX;
    private final int itemBGdelY;

    public static final int GUI_POINT_DISPLAY_LIST = 0;

    public static final int GUI_POINT_DISPLAY_DEFSELECT = 1;

    public GuiRelocatorMain(int type) {
        super(new RelocatorContainer());
        this.xSize = 162;
        this.ySize = 129;
        this.firstItemX = 17;
        this.firstItemY = 16;
        this.firstItemBGX = 14;
        this.firstItemBGY = 14;
        this.firstSelX = 15;
        this.firstSelY = 15;
        this.selWidth = 132;
        this.selHeight = 9;
        this.itemBGinterval = 10;
        this.itemInterval = 10;
        this.cancelBtnX1 = 138;
        this.cancelBtnWidth = 9;
        this.cancelBtnHeight = 9;
        this.itemBGX = 0;
        this.itemBGY = 131;
        this.itemBGWidth = 134;
        this.itemBGHeight = 11;
        this.itemBGselX = 0;
        this.itemBGselY = 144;
        this.itemBGdelX = 0;
        this.itemBGdelY = 157;
        this.itemBGdefX = 0;
        this.itemBGdefY = 170;
        this.openType = type;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    public SelectedItem getSelected(List<TeleportPoint> paramList) {
        SelectedItem selectedItem = new SelectedItem();
        int i = (this.height - this.ySize) / 2;
        int j = this.mouseY - i;
        int k = paramList.size();
        if (isPointInRegion(this.firstSelX, this.firstSelY + 1, this.selWidth, this.itemInterval * k - 2, this.mouseX, this.mouseY)) {
            double d = (double) (j - this.firstSelY + 1) / this.itemBGinterval;
            selectedItem.selItem = (int) Math.ceil(d);
            if (isPointInRegion(this.cancelBtnX1, this.firstSelY + 1, this.cancelBtnWidth, this.itemInterval * k - 1, this.mouseX, this.mouseY))
                selectedItem.delFlag = true;
            return selectedItem;
        }
        return null;
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        List<TeleportPoint> points = getPoints();
        SelectedItem selectedItem = getSelected(points);
        if (selectedItem == null) return;
        String name = points.get(selectedItem.selItem - 1).NAME;
        if (selectedItem.delFlag) {
            GraviSuite.NETWORK.sendRelocatorPoints(name, (byte) 0);
        } else {
            byte action = (byte) ((this.openType == 0) ? 2 : 3);
            GraviSuite.NETWORK.sendRelocatorPoints(name, action);
            this.mc.thePlayer.closeScreen();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        List<TeleportPoint> points = getPoints();
        if (!points.isEmpty()) {
            for (byte b = 0; b < points.size(); b++) {
                TeleportPoint teleportPoint = points.get(b);
                this.fontRenderer.drawString(teleportPoint.NAME, this.firstItemX, this.firstItemY + b * this.itemInterval, 16777215);
            }
            if (GraviSuite.PROXY.isSneakKeyDown()) {
                SelectedItem selectedItem = getSelected(points);
                if (selectedItem != null) {
                    List<String> arrayList1 = new ArrayList<String>();
                    TeleportPoint teleportPoint = points.get(selectedItem.selItem - 1);
                    int k = selectedItem.selItem;
                    if (k == 10)
                        k = 0;
                    arrayList1.add(Translator.GOLD.literal("Hotkey: " + k));
                    arrayList1.add("Dimension: " + DimensionManager.getProvider(teleportPoint.DIMENSION_ID).getDimensionName());
                    arrayList1.add("Height: " + teleportPoint.POS.getY());
                    arrayList1.add("X: " + teleportPoint.POS.getX());
                    arrayList1.add("Y: " + teleportPoint.POS.getZ());
                    int m = this.mouseX - i;
                    int n = this.mouseY - j;
                    Helpers.renderTooltip(m - 2, n, arrayList1);
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float paramFloat, int paramInt1, int paramInt2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int texture = this.mc.renderEngine.getTexture(TEXTURE);
        this.mc.renderEngine.bindTexture(texture);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        GL11.glDisable(3042);
        List<TeleportPoint> points = getPoints();
        if (!points.isEmpty()) {
            for (byte b = 0; b < points.size(); b++) {
                drawTexturedModalRect(i + this.firstItemBGX, j + this.firstItemBGY + b * this.itemInterval, this.itemBGX, this.itemBGY, this.itemBGWidth, this.itemBGHeight);
                TeleportPoint teleportPoint = points.get(b);
                if (teleportPoint.DEFAULT)
                    drawTexturedModalRect(i + this.firstItemBGX, j + this.firstItemBGY + b * this.itemInterval, this.itemBGdefX, this.itemBGdefY, this.itemBGWidth, this.itemBGHeight);
            }
            SelectedItem selectedItem = getSelected(points);
            if (selectedItem != null)
                if (!selectedItem.delFlag) {
                    drawTexturedModalRect(i + this.firstItemBGX, j + this.firstItemBGY + (selectedItem.selItem - 1) * this.itemInterval, this.itemBGselX, this.itemBGselY, this.itemBGWidth, this.itemBGHeight);
                } else {
                    drawTexturedModalRect(i + this.firstItemBGX, j + this.firstItemBGY + (selectedItem.selItem - 1) * this.itemInterval, this.itemBGdelX, this.itemBGdelY, this.itemBGWidth, this.itemBGHeight);
                }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float paramFloat) {
        super.drawScreen(mouseX, mouseY, paramFloat);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    protected void keyTyped(char paramChar, int paramInt) {
        if (paramInt == 1)
            this.mc.thePlayer.closeScreen();
        if (paramInt > 1 && paramInt < 12) {
            List<TeleportPoint> points = getPoints();
            if (paramInt - 2 < points.size()) {
                TeleportPoint teleportPoint = points.get(paramInt - 2);
                GraviSuite.NETWORK.sendRelocatorPoints(teleportPoint.NAME, (byte) 2);
                this.mc.thePlayer.closeScreen();
            }
        }
        super.keyTyped(paramChar, paramInt);
    }

    public static List<TeleportPoint> getPoints() {
        return new ArrayList<TeleportPoint>(ItemRelocator.readPointsFromStack(Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem()));
    }

    public static class SelectedItem {
        public int selItem = -1;
        public boolean delFlag = false;
    }
}
