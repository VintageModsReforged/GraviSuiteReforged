package reforged.mods.gravisuite.events.client;

import cpw.mods.fml.common.TickType;
import ic2.api.IElectricItem;
import ic2.core.IC2;
import ic2.core.util.StackUtil;
import mods.vintage.core.platform.events.tick.TickEvents;
import mods.vintage.core.platform.lang.FormattedTranslator;
import mods.vintage.core.platform.lang.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import reforged.mods.gravisuite.GraviSuiteMainConfig;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.items.armors.base.ItemBaseJetpack;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

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
            if (GraviSuiteMainConfig.ENABLE_HUD && mc.theWorld != null && mc.inGameHasFocus) {
                renderOverlay(mc);
            }
        }
    }

    public void renderOverlay(Minecraft mc) {
        if (IC2.platform.isRendering()) {
            ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            EntityPlayer player = mc.thePlayer;
            ItemStack armor = player.getCurrentArmor(2);

            if (armor != null && armor.getItem() instanceof IElectricItem) {
                NBTTagCompound tag = StackUtil.getOrCreateNbtData(armor);
                IElectricItem electricItem = (IElectricItem) armor.getItem();
                int curCharge = Helpers.getCharge(armor);
                int maxCharge = electricItem.getMaxCharge();
                int charge = 0;
                if (maxCharge > 0) {
                    charge = curCharge * 100 / maxCharge;
                }

                // ENERGY STATUS
                String energyToDisplay = Refs.energy_level + " " + getEnergyTextColor(charge) + FormattedTranslator.WHITE.literal("%");

                // HOVER MODE STATUS

                boolean isHoverOn = ItemBaseJetpack.readWorkMode(armor);
                String hoverS = Helpers.getStatusMessage(isHoverOn);
                String hoverStatusToDisplay = Refs.jetpack_hover + " " + hoverS;

                // ENGINE STATUS

                boolean isJetpackOn = ItemBaseJetpack.readFlyStatus(armor);
                String jetpackS = Helpers.getStatusMessage(isJetpackOn);
                String jetpackStatusToDisplay = Refs.jetpack_engine + " " + jetpackS;

                // GRAVITATION ENGINE STATUS

                boolean isGraviEngineOn = ItemAdvancedQuant.readFlyStatus(armor);
                String graviEngineS = Helpers.getStatusMessage(isGraviEngineOn);
                String graviEngineToDisplay = Refs.gravitation_engine + " " + graviEngineS;

                // LEVITATION STATUS
                boolean isLevitationOn = ItemAdvancedQuant.readWorkMode(armor);
                String levitationS = Helpers.getStatusMessage(isLevitationOn);
                String levitationToDisplay = Refs.gravitation_levitation + " " + levitationS;

                if (GraviSuiteMainConfig.USE_FIXED_VALUES) {
                    switch (GraviSuiteMainConfig.HUD_POSITION) {
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
                    xPosEnergy = GraviSuiteMainConfig.HUD_POS_GRAVI_X;
                    yPosEnergy = yPosEnergyJoint = GraviSuiteMainConfig.HUD_POS_ENERGY_Y;

                    xPosJetpack = GraviSuiteMainConfig.HUD_POS_JETPACK_X;
                    yPosJetpack = GraviSuiteMainConfig.HUD_POS_JETPACK_Y;

                    xPosHover = xPosJetpack;
                    yPosHover = yPosJetpack + textOffset + mc.fontRenderer.FONT_HEIGHT;

                    xPosGravi = GraviSuiteMainConfig.HUD_POS_GRAVI_X;
                    yPosGravi = GraviSuiteMainConfig.HUD_POS_GRAVI_Y;
                    xPosLevitation = xPosGravi;
                    yPosLevitation = yPosGravi + textOffset + mc.fontRenderer.FONT_HEIGHT;
                }

                if (GraviSuiteMainConfig.ENABLE_HUD) {
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
                    String quick_change = Translator.format(Refs.quick_charge);
                    int width = mc.fontRenderer.getStringWidth(quick_change);
                    if (tag.getBoolean(ItemBaseJetpack.NBT_ACTIVE) && IC2.keyboard.isAltKeyDown(player)) {
                        mc.ingameGUI.drawString(mc.fontRenderer, FormattedTranslator.GREEN.literal(quick_change), xPos - width / 2, yPos, 0);
                    }
                }
            }
        }
    }

    private int getXOffset(String value) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int xPos = 0;
        switch (GraviSuiteMainConfig.HUD_POSITION) {
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
        FormattedTranslator colorCode = FormattedTranslator.WHITE; // white
        if (energyLevel >= 90) {
            colorCode = FormattedTranslator.GREEN; // green
        }
        if ((energyLevel <= 90) && (energyLevel > 75)) {
            colorCode = FormattedTranslator.YELLOW; // yellow
        }
        if ((energyLevel <= 75) && (energyLevel > 50)) {
            colorCode = FormattedTranslator.GOLD; // gold
        }
        if ((energyLevel <= 50) && (energyLevel > 35)) {
            colorCode = FormattedTranslator.RED; // red
        }
        if (energyLevel <= 35) {
            colorCode = FormattedTranslator.DARK_RED; // dark_red
        }
        return colorCode.literal(String.valueOf(energyLevel));
    }
}
