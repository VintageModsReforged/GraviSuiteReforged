package reforged.mods.gravisuite;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import ic2.api.item.IElectricItem;
import ic2.core.IC2;
import ic2.core.item.armor.ItemArmorBatpack;
import ic2.core.item.armor.ItemArmorJetpackElectric;
import ic2.core.item.armor.ItemArmorLappack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.items.armors.ItemLappack;
import reforged.mods.gravisuite.items.armors.base.ItemBaseJetpack;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.EnumSet;

public class GraviSuiteOverlay implements ITickHandler {

    int offset = 3;
    int textOffset = 2;
    int xPosEnergy = offset, xPosJetpack = offset, xPosHover = offset, xPosGravi = offset, xPosLevitation = offset;
    int yPosEnergy = offset, yPosEnergyJoint = offset, yPosJetpack = offset, yPosHover = offset, yPosGravi = offset, yPosLevitation = offset;

    public static Minecraft mc = Minecraft.getMinecraft();

    public GraviSuiteOverlay() {}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... objects) {
        if (type.contains(TickType.RENDER) && GraviSuiteConfig.enable_hud && mc.theWorld != null && mc.inGameHasFocus) {
            renderOverlay(mc);
        }
    }

    public void renderOverlay(Minecraft mc) {
        if (IC2.platform.isRendering()) {
            ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            EntityPlayer player = mc.thePlayer;
            ItemStack armor = player.getCurrentArmor(2);

            if (armor != null && armor.getItem() instanceof IElectricItem) {
                int curCharge = Helpers.getCharge(armor);
                int maxCharge = ((IElectricItem) armor.getItem()).getMaxCharge(armor);
                int charge = curCharge * 100 / maxCharge;

                // ENERGY STATUS
                String energyToDisplay = Refs.energy_level + " " + getEnergyTextColor(charge) + charge + "\247f%";

                // HOVER MODE STATUS

                boolean isHoverOn = ItemBaseJetpack.readWorkMode(armor);
                String hoverS = isHoverOn ? Refs.status_on : Refs.status_off;
                String hoverStatusToDisplay = Refs.jetpack_hover + " " + hoverS;

                // ENGINE STATUS

                boolean isJetpackOn = ItemBaseJetpack.readFlyStatus(armor);
                String jetpackS = isJetpackOn ? Refs.status_on : Refs.status_off;
                String jetpackStatusToDisplay = Refs.jetpack_engine + " " + jetpackS;

                // GRAVITATION ENGINE STATUS

                boolean isGraviEngineOn = ItemAdvancedQuant.readFlyStatus(armor);
                String graviEngineS = isGraviEngineOn ? Refs.status_on : Refs.status_off;
                String graviEngineToDisplay = Refs.gravitation_engine + " " + graviEngineS;

                // LEVITATION STATUS
                boolean isLevitationOn = ItemAdvancedQuant.readWorkMode(armor);
                String levitationS = isLevitationOn ? Refs.status_on : Refs.status_off;
                String levitationToDisplay = Refs.gravitation_levitation + " " + levitationS;

                if (GraviSuiteConfig.use_fixed_values) {
                    switch (GraviSuiteConfig.hud_position) {
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
                    xPosEnergy = GraviSuiteConfig.hud_pos_gravi_x;
                    yPosEnergy = yPosEnergyJoint = GraviSuiteConfig.hud_pos_energy_y;

                    xPosJetpack = GraviSuiteConfig.hud_pos_jetpack_x;
                    yPosJetpack = GraviSuiteConfig.hud_pos_jetpack_y;

                    xPosHover = xPosJetpack;
                    yPosHover = yPosJetpack + textOffset + mc.fontRenderer.FONT_HEIGHT;

                    xPosGravi = GraviSuiteConfig.hud_pos_gravi_x;
                    yPosGravi = GraviSuiteConfig.hud_pos_gravi_y;
                    xPosLevitation = xPosGravi;
                    yPosLevitation = yPosGravi + textOffset + mc.fontRenderer.FONT_HEIGHT;
                }


                boolean ic2armor = armor.getItem() instanceof ItemArmorJetpackElectric || armor.getItem() instanceof ItemArmorBatpack || armor.getItem() instanceof ItemArmorLappack;

                if (GraviSuiteConfig.enable_hud) {
                    if (armor.getItem() instanceof ItemLappack.ItemAdvancedLappack || armor.getItem() instanceof ItemLappack.ItemUltimateLappack || ic2armor) {
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
            }
        }
    }

    private int getXOffset(String value) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int xPos = 0;
        switch (GraviSuiteConfig.hud_position) {
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
        String colorCode = "f"; // white
        if (energyLevel >= 90) {
            colorCode = "a"; // green
        }
        if ((energyLevel <= 90) && (energyLevel > 75)) {
            colorCode = "e"; // yellow
        }
        if ((energyLevel <= 75) && (energyLevel > 50)) {
            colorCode = "6"; // gold
        }
        if ((energyLevel <= 50) && (energyLevel > 35)) {
            colorCode = "c"; // red
        }
        if (energyLevel <= 35) {
            colorCode = "4"; // dark_red
        }
        return "\247" + colorCode;
    }

    @Override
    public EnumSet<TickType> ticks() { return EnumSet.of(TickType.RENDER); }

    @Override
    public String getLabel() { return Refs.id; }

    @Override
    public void tickStart(EnumSet<TickType> enumSet, Object... objects) {}
}
