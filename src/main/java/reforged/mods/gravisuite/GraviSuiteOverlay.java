package reforged.mods.gravisuite;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import ic2.api.IElectricItem;
import ic2.core.IC2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.items.armors.IHasOverlay;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.items.armors.ItemLappack;
import reforged.mods.gravisuite.items.armors.base.ItemBaseJetpack;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.EnumSet;

public class GraviSuiteOverlay implements ITickHandler {

    int offset = 3;
    int textOffset = 2;
    int xPos = offset;
    int yPos = offset;
    int yPos1 = 0;
    int yPos2 = 0;
    int yPos3 = 0;

    public static Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... objects) {
        if (type.contains(TickType.RENDER) && GraviSuiteMainConfig.ENABLE_HUD && mc.theWorld != null && mc.inGameHasFocus) {
            Minecraft minecraft = mc;
            renderOverlay(minecraft);
        }
    }

    public void renderOverlay(Minecraft mc) {
        if (IC2.platform.isRendering()) {
            ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            EntityPlayer player = mc.thePlayer;
            ItemStack armor = null;
            Item itemArmor = null;
            for (ItemStack stack : player.inventory.armorInventory) {
                if (stack != null && stack.getItem() instanceof IHasOverlay) {
                    armor = stack;
                    itemArmor = stack.getItem();
                    break;
                }
            }

            if (itemArmor instanceof IElectricItem) {
                int curCharge = Helpers.getCharge(armor);
                int maxCharge = ((IElectricItem) armor.getItem()).getMaxCharge();
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

                switch (GraviSuiteMainConfig.HUD_POSITION) {
                    case 1:
                    case 2:
                        yPos1 = offset;
                        yPos2 = yPos1 + offset + mc.fontRenderer.FONT_HEIGHT;
                        yPos3 = yPos2 + offset + mc.fontRenderer.FONT_HEIGHT;
                        break;
                    case 3:
                    case 4:
                        yPos = scaledRes.getScaledHeight() - offset - mc.fontRenderer.FONT_HEIGHT;
                        yPos1 = scaledRes.getScaledHeight() - offset - (mc.fontRenderer.FONT_HEIGHT * 3) - textOffset * 2;
                        yPos2 = yPos1 + textOffset + mc.fontRenderer.FONT_HEIGHT;
                        yPos3 = yPos2 + textOffset + mc.fontRenderer.FONT_HEIGHT;
                        break;
                    default:
                        break;
                }
                if (GraviSuiteMainConfig.ENABLE_HUD) {
                    if (armor.getItem() instanceof ItemLappack.ItemAdvancedLappack || armor.getItem() instanceof ItemLappack.ItemUltimateLappack) {
                        mc.ingameGUI.drawString(mc.fontRenderer, energyToDisplay, getXOffset(energyToDisplay), yPos, 0);
                    }
                    if (armor.getItem() instanceof ItemBaseJetpack) {
                        mc.ingameGUI.drawString(mc.fontRenderer, energyToDisplay, getXOffset(energyToDisplay), yPos1, 0);
                        mc.ingameGUI.drawString(mc.fontRenderer, jetpackStatusToDisplay, getXOffset(jetpackStatusToDisplay), yPos2, 0);
                        mc.ingameGUI.drawString(mc.fontRenderer, hoverStatusToDisplay, getXOffset(hoverStatusToDisplay), yPos3, 0);
                    }
                    if (armor.getItem() instanceof ItemAdvancedQuant) {
                        mc.ingameGUI.drawString(mc.fontRenderer, energyToDisplay, getXOffset(energyToDisplay), yPos1, 0);
                        mc.ingameGUI.drawString(mc.fontRenderer, graviEngineToDisplay, getXOffset(graviEngineToDisplay), yPos2, 0);
                        mc.ingameGUI.drawString(mc.fontRenderer, levitationToDisplay, getXOffset(levitationToDisplay), yPos3, 0);
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
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.RENDER);
    }

    @Override
    public String getLabel() {
        return Refs.ID;
    }

    @Override
    public void tickStart(EnumSet<TickType> enumSet, Object... objects) { /** nothing*/ }
}
