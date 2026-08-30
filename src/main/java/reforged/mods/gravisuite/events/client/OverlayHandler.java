package reforged.mods.gravisuite.events.client;

import cpw.mods.fml.common.TickType;
import ic2.api.IElectricItem;
import ic2.core.IC2;
import ic2.core.util.StackUtil;
import mods.vintage.core.VintageConfig;
import mods.vintage.core.helpers.ElectricHelper;
import mods.vintage.core.platform.events.tick.TickEvents;
import mods.vintage.core.platform.lang.Translator;
import mods.vintage.core.utils.Utils;
import mods.vintage.core.utils.VeinSearchResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.items.armors.base.ItemBaseJetpack;
import reforged.mods.gravisuite.items.features.IHighlightProvider;
import reforged.mods.gravisuite.items.tools.ItemAdvancedChainsaw;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Messages;
import reforged.mods.gravisuite.utils.Refs;
import reforged.mods.gravisuite.utils.VeinDataHandler;

import java.util.EnumSet;

public class OverlayHandler extends TickEvents.RenderTickEvent {

    public static final OverlayHandler THIS = new OverlayHandler();

    int offset = 3;
    int textOffset = 2;
    int xPosEnergy = offset, xPosJetpack = offset, xPosHover = offset, xPosGravi = offset, xPosLevitation = offset;
    int yPosEnergy = offset, yPosEnergyJoint = offset, yPosJetpack = offset, yPosHover = offset, yPosGravi = offset, yPosLevitation = offset;

    public static Minecraft mc = Minecraft.getMinecraft();

    public OverlayHandler() {
        super(Refs.ID);
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... objects) {
        if (shouldTick(type)) {
            if (IC2.platform.isRendering()) {
                if (mc.theWorld != null && mc.inGameHasFocus) {
                    EntityPlayer player = mc.thePlayer;
                    ItemStack heldStack = player.getHeldItem();
                    if (heldStack != null && heldStack.getItem() instanceof IHighlightProvider) {
                        renderHighlightOverlay(mc, player, heldStack);
                    }
                    if (GraviSuiteConfig.ENABLE_HUD) {
                        renderOverlay(mc);
                    }
                }
            }
        }
    }

    public void renderHighlightOverlay(Minecraft mc, EntityPlayer player, ItemStack heldStack) {
        IHighlightProvider provider = (IHighlightProvider) heldStack.getItem();
        if (heldStack.getItem() instanceof ItemAdvancedChainsaw) {
            if (provider.isProvidingHighlight(heldStack)) {
                boolean show = VeinDataHandler.getShow(player);
                if (show) {
                    VeinSearchResult veinSearchResult = VeinDataHandler.getSearchResult(player);
                    int count = veinSearchResult.getPositions().size();
                    int maxCount = VintageConfig.veinMaxCount;
                    String text = "";
                    if (veinSearchResult.getType() == VeinSearchResult.Type.ABORT_OVER_LIMIT) {
                        text = Translator.RED.format("message.text.info.lowTreeCapitatorCount");
                    } else if (veinSearchResult.getType() == VeinSearchResult.Type.SUCCESS) {
                        text = Translator.GREEN.format("%s / %s", count, maxCount);
                    }

                    if (!text.isEmpty()) {
                        ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
                        int screenWidth = sr.getScaledWidth();
                        int screenHeight = sr.getScaledHeight();

                        FontRenderer font = mc.fontRenderer;
                        int x = screenWidth / 2;
                        int y = screenHeight / 2 + 10;

                        int textWidth = font.getStringWidth(text);

                        GL11.glPushMatrix();
                        // Move to pivot point
                        GL11.glTranslatef(x, y, 0.0F);
                        // Apply rotation
                        GL11.glRotatef(-40.0F, 1.0F, 0.0F, 0.0F);
                        // Draw relative to pivot
                        font.drawStringWithShadow(text, -textWidth / 2, 0, 0xFFFFFF);
                        GL11.glPopMatrix();
                    }
                }
            }
        }
    }

    public void renderOverlay(Minecraft mc) {
        if (IC2.platform.isRendering()) {
            ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            EntityPlayer player = mc.thePlayer;
            ItemStack armor = player.getCurrentArmor(2);

            if (armor != null && armor.getItem() instanceof IElectricItem && !Utils.instanceOf(armor.getItem(), "net.machinemuse.powersuits.item.ItemPowerArmor")) {
                NBTTagCompound tag = StackUtil.getOrCreateNbtData(armor);
                IElectricItem electricItem = (IElectricItem) armor.getItem();
                int curCharge = ElectricHelper.getCharge(armor);
                int maxCharge = electricItem.getMaxCharge();
                int charge = 0;
                if (maxCharge > 0) {
                    charge = curCharge * 100 / maxCharge;
                }

                // ENERGY STATUS
                String energyToDisplay = Messages.Translations.ENERGY_LEVEL.format(getEnergyTextColor(charge));

                // HOVER MODE STATUS

                boolean isHoverOn = ItemBaseJetpack.readWorkMode(armor);
                String hoverS = Helpers.getStatusMessage(isHoverOn);
                String hoverStatusToDisplay = Messages.Translations.JETPACK_HOVER.format(hoverS);

                // ENGINE STATUS

                boolean isJetpackOn = ItemBaseJetpack.readFlyStatus(armor);
                String jetpackS = Helpers.getStatusMessage(isJetpackOn);
                String jetpackStatusToDisplay = Messages.Translations.JETPACK_ENGINE.format(jetpackS);

                // GRAVITATION ENGINE STATUS

                boolean isGraviEngineOn = ItemAdvancedQuant.readFlyStatus(armor);
                String graviEngineS = Helpers.getStatusMessage(isGraviEngineOn);
                String graviEngineToDisplay = Messages.Translations.GRAVITATION_ENGINE.format(graviEngineS);

                // LEVITATION STATUS
                boolean isLevitationOn = ItemAdvancedQuant.readWorkMode(armor);
                String levitationS = Helpers.getStatusMessage(isLevitationOn);
                String levitationToDisplay = Messages.Translations.GRAVITATION_LEVITATION.format(levitationS);

                if (GraviSuiteConfig.USE_FIXED_VALUES) {
                    switch (GraviSuiteConfig.HUD_POSITION) {
                        case 1:
                        case 2:
                            yPosEnergy = offset;
                            yPosJetpack = yPosEnergy + offset + mc.fontRenderer.FONT_HEIGHT;
                            yPosGravi = yPosJetpack;
                            yPosHover = yPosJetpack + offset + mc.fontRenderer.FONT_HEIGHT;
                            yPosLevitation = yPosHover;
                            break;
                        case 3:
                        case 4:
                            yPosEnergy = scaledRes.getScaledHeight() - offset - mc.fontRenderer.FONT_HEIGHT;
                            yPosEnergyJoint = scaledRes.getScaledHeight() - offset - (mc.fontRenderer.FONT_HEIGHT * 3) - textOffset * 2;
                            yPosJetpack = yPosGravi = yPosEnergyJoint + textOffset + mc.fontRenderer.FONT_HEIGHT;
                            yPosHover = yPosLevitation = yPosJetpack + textOffset + mc.fontRenderer.FONT_HEIGHT;
                            break;
                        default:
                            break;
                    }
                    xPosEnergy = getXOffset(energyToDisplay);
                    xPosJetpack = getXOffset(jetpackStatusToDisplay);
                    xPosHover = getXOffset(hoverStatusToDisplay);
                    xPosGravi = getXOffset(graviEngineToDisplay);
                    xPosLevitation = getXOffset(levitationToDisplay);
                } else {
                    xPosEnergy = GraviSuiteConfig.HUD_POS_GRAVI_X;
                    yPosEnergy = yPosEnergyJoint = GraviSuiteConfig.HUD_POS_ENERGY_Y;

                    xPosJetpack = GraviSuiteConfig.HUD_POS_JETPACK_X;
                    yPosJetpack = GraviSuiteConfig.HUD_POS_JETPACK_Y;

                    xPosHover = xPosJetpack;
                    yPosHover = yPosJetpack + textOffset + mc.fontRenderer.FONT_HEIGHT;

                    xPosGravi = GraviSuiteConfig.HUD_POS_GRAVI_X;
                    yPosGravi = GraviSuiteConfig.HUD_POS_GRAVI_Y;
                    xPosLevitation = xPosGravi;
                    yPosLevitation = yPosGravi + textOffset + mc.fontRenderer.FONT_HEIGHT;
                }

                if (GraviSuiteConfig.ENABLE_HUD) {
                    if (!(armor.getItem() instanceof ItemBaseJetpack) && !(armor.getItem() instanceof ItemAdvancedQuant)) {
                        mc.ingameGUI.drawString(mc.fontRenderer, energyToDisplay, xPosEnergy, yPosEnergy, 0);
                    }
                    if (armor.getItem() instanceof ItemBaseJetpack) {
                        mc.ingameGUI.drawString(mc.fontRenderer, energyToDisplay, xPosEnergy, yPosEnergyJoint, 0);
                        mc.ingameGUI.drawString(mc.fontRenderer, jetpackStatusToDisplay, xPosJetpack, yPosJetpack, 0);
                        mc.ingameGUI.drawString(mc.fontRenderer, hoverStatusToDisplay, xPosHover, yPosHover, 0);
                    }
                    if (armor.getItem() instanceof ItemAdvancedQuant) {
                        mc.ingameGUI.drawString(mc.fontRenderer, energyToDisplay, xPosEnergy, yPosEnergyJoint, 0);
                        mc.ingameGUI.drawString(mc.fontRenderer, graviEngineToDisplay, xPosGravi, yPosGravi, 0);
                        mc.ingameGUI.drawString(mc.fontRenderer, levitationToDisplay, xPosLevitation, yPosLevitation, 0);
                    }
                }

                if (armor.getItem() instanceof ItemBaseJetpack) {
                    int xPos = scaledRes.getScaledWidth() / 2;
                    int yPos = scaledRes.getScaledHeight() - 85;
                    String quick_change = Messages.Translations.QUICK_CHARGE.format();
                    int width = mc.fontRenderer.getStringWidth(quick_change);
                    if (tag.getBoolean(ItemBaseJetpack.NBT_ACTIVE) && IC2.keyboard.isAltKeyDown(player)) {
                        mc.ingameGUI.drawString(mc.fontRenderer, quick_change, xPos - width / 2, yPos, 0);
                    }
                }
            }
        }
    }

    private int getXOffset(String value) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int xPos = 0;
        switch (GraviSuiteConfig.HUD_POSITION) {
            case 1:
            case 3:
                xPos = offset;
                break;
            case 2:
            case 4:
                xPos = scaledRes.getScaledWidth() - mc.fontRenderer.getStringWidth(value) - offset;
                break;
            default:
                break;
        }
        return xPos;
    }

    public static String getEnergyTextColor(int energyLevel) {
        Translator colorCode = Translator.WHITE; // white
        if (energyLevel >= 90) {
            colorCode = Translator.GREEN; // green
        }
        if ((energyLevel <= 90) && (energyLevel > 75)) {
            colorCode = Translator.YELLOW; // yellow
        }
        if ((energyLevel <= 75) && (energyLevel > 50)) {
            colorCode = Translator.GOLD; // gold
        }
        if ((energyLevel <= 50) && (energyLevel > 35)) {
            colorCode = Translator.RED; // red
        }
        if (energyLevel <= 35) {
            colorCode = Translator.DARK_RED; // dark_red
        }
        return colorCode.literal(String.valueOf(energyLevel));
    }
}
