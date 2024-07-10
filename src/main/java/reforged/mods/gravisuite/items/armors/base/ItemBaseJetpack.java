package reforged.mods.gravisuite.items.armors.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.keyboard.GraviSuiteKeyboardClient;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.List;

public class ItemBaseJetpack extends ItemArmorElectric {

    public static byte TOGGLE_TIMER;
    public boolean LAST_JETPACK_USED = false;
    public static AudioSource AUDIO_SOURCE;
    public double HOVER_FALL_SPEED;

    public static final String NBT_ACTIVE = "fly_active";
    public static final String NBT_HOVER_ACTIVE = "hover_active";
    public static final String NBT_TOGGLE_TIMER = "toggle_timer";

    public ItemBaseJetpack(int id, String name) {
        super(id, name, 2, 1000, 1000000);
        this.HOVER_FALL_SPEED = 0.03D;
        TOGGLE_TIMER = 5;
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isDebugMode) {
        super.addInformation(stack, player, tooltip, isDebugMode);
        boolean isHoverMode = readWorkMode(stack);
        boolean isEngineOn = readFlyStatus(stack);

        String hoverStatus = isHoverMode ? Refs.status_on : Refs.status_off;
        String jetpackStatus = isEngineOn ? Refs.status_on : Refs.status_off;

        tooltip.add(Refs.jetpack_engine_gold + " " + jetpackStatus);
        tooltip.add(Refs.jetpack_hover_gold + " " + hoverStatus);
        if (Helpers.isShiftKeyDown()) {
            tooltip.add(Helpers.pressXForY(Refs.to_enable_1, StatCollector.translateToLocal(GraviSuiteKeyboardClient.engine_toggle.keyDescription), Refs.JETPACK_ENGINE + ".stat"));
            tooltip.add(Helpers.pressXAndYForZ(Refs.to_enable_2, "Mode Switch Key", StatCollector.translateToLocal(Minecraft.getMinecraft().gameSettings.keyBindJump.keyDescription), Refs.JETPACK_HOVER + ".stat"));
            tooltip.add(Helpers.pressXForY(Refs.to_enable_1, "Boost Key", Refs.BOOST_MODE));
        } else {
            tooltip.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
        }
    }

    @Override
    public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        boolean hoverMode = readWorkMode(stack);

        byte toggleTimer = tag.getByte(NBT_TOGGLE_TIMER);
        boolean jetpackUsed = false;

        if (GraviSuite.keyboard.isEngineToggleKeyDown(player) && toggleTimer <= 0) {
            switchFlyState(player, stack);
        }

        if (IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer <= 0) {
            switchWorkMode(player, stack);
        }

        if ((IC2.keyboard.isJumpKeyDown(player)
                || (hoverMode && player.motionY < -HOVER_FALL_SPEED && !player.onGround)) && readFlyStatus(stack))
            jetpackUsed  = useJetpack(player, stack, hoverMode);
        if (IC2.platform.isSimulating() && toggleTimer > 0) {
            toggleTimer--;
            tag.setByte(NBT_TOGGLE_TIMER, toggleTimer);
        }
        if (IC2.platform.isRendering()) {
            createSound(player, jetpackUsed);
        }
    }

    public void createSound(EntityPlayer player, boolean used) {
        if (LAST_JETPACK_USED != used) {
            if (used) {
                if (AUDIO_SOURCE == null) {
                    AUDIO_SOURCE = IC2.audioManager.createSource(player, PositionSpec.Backpack,
                            "Tools/Jetpack/JetpackLoop.ogg", true, false, IC2.audioManager.defaultVolume);
                }
                if (AUDIO_SOURCE != null) {
                    AUDIO_SOURCE.play();
                }
            } else if (AUDIO_SOURCE != null) {
                AUDIO_SOURCE.remove();
                AUDIO_SOURCE = null;
            }
            LAST_JETPACK_USED = used ;
        }
        if (AUDIO_SOURCE != null) {
            AUDIO_SOURCE.updatePosition();
        }
    }

    public static void removeSound() {
        if (AUDIO_SOURCE != null) {
            AUDIO_SOURCE.remove();
            AUDIO_SOURCE = null;
        }
    }

    public boolean useJetpack(EntityPlayer player, ItemStack stack, boolean hover) {
        int usage = 12;
        double charge = Helpers.getCharge(stack);
        if (charge < usage && !player.capabilities.isCreativeMode)
            return false;
        float power = 1.0F;
        double dropPercentage = 0.001D;
        double dropLimit = this.getMaxCharge(stack) * 0.05D;
        if (charge / this.getMaxCharge(stack) <= dropPercentage)
            power = (float) (power * charge / dropLimit);
        if (player.capabilities.isCreativeMode)
            power = 1.0F;
        if (IC2.keyboard.isForwardKeyDown(player)) {
            float retruster = 0.3F;
            if (hover)
                retruster = 0.65F;
            float forwardPower = power * retruster * 2.0F;
            float boost = 0.0F;
            if (IC2.keyboard.isBoostKeyDown(player)
                    && (charge > 60.0D || player.capabilities.isCreativeMode)) {
                boost = 0.09F;
                if (hover)
                    boost = 0.07F;
            }
            if (forwardPower > 0.0F) {
                player.moveFlying(0.0F, 0.4F * forwardPower + boost, 0.02F + boost);
                if (boost > 0.0F && !player.capabilities.isCreativeMode && IC2.platform.isSimulating())
                    ElectricItem.manager.discharge(stack, 60, Integer.MAX_VALUE, true, false);
            }
        }
        int worldHeight = player.worldObj.getHeight();
        double posY = player.posY;
        if (posY > (worldHeight - 25)) {
            if (posY > worldHeight)
                posY = worldHeight;
            power = (float) (power * (worldHeight - posY) / 25.0D);
        }
        double motionY = player.motionY;
        player.motionY = Math.min(player.motionY + (power * 0.2F), 0.6D);
        if (hover) {
            double maxHoverY = -HOVER_FALL_SPEED;
            if (IC2.keyboard.isJumpKeyDown(player))
                maxHoverY = 0.2D;
            if (IC2.keyboard.isSneakKeyDown(player))
                maxHoverY = -0.2D;
            if ((charge > 60.0D || player.capabilities.isCreativeMode)
                    && IC2.keyboard.isBoostKeyDown(player))
                if (IC2.keyboard.isSneakKeyDown(player) || IC2.keyboard.isJumpKeyDown(player)) {
                    maxHoverY *= 2.0D;
                    ElectricItem.manager.discharge(stack, 60, Integer.MAX_VALUE, true, false);
                }
            if (player.motionY > maxHoverY) {
                player.motionY = maxHoverY;
                if (motionY > player.motionY)
                    player.motionY = motionY;
            }
        }
        if (!player.capabilities.isCreativeMode && !player.onGround)
            ElectricItem.manager.discharge(stack, usage, Integer.MAX_VALUE, true, false);
        player.fallDistance = 0.0F;
        player.distanceWalkedModified = 0.0F;
        IC2.platform.resetPlayerInAirTime(player);
        return true;
    }

    public static boolean readWorkMode(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return tag.getBoolean(NBT_HOVER_ACTIVE);
    }

    public static void saveWorkMode(ItemStack stack, boolean workMode) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        tag.setBoolean(NBT_HOVER_ACTIVE, workMode);
        tag.setByte(NBT_TOGGLE_TIMER, TOGGLE_TIMER);
    }

    public static void switchWorkMode(EntityPlayer player, ItemStack stack) {
        String message;
        if (readWorkMode(stack)) {
            saveWorkMode(stack, false);
            message = Refs.jetpack_hover + " " + Refs.status_off;
        } else {
            saveWorkMode(stack, true);
            message = Refs.jetpack_hover + " " + Refs.status_on;
        }
        if (IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, message);
        }
    }

    public static boolean readFlyStatus(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return tag.getBoolean(NBT_ACTIVE);
    }

    public static void saveFlyStatus(ItemStack stack, boolean flyMode) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        tag.setBoolean(NBT_ACTIVE, flyMode);
        tag.setByte(NBT_TOGGLE_TIMER, TOGGLE_TIMER);
    }

    public static void switchFlyState(EntityPlayer player, ItemStack stack) {
        String message;
        if (readFlyStatus(stack)) {
            saveFlyStatus(stack, false);
            message = Refs.jetpack_engine + " " + Refs.status_off;
        } else {
            saveFlyStatus(stack, true);
            message = Refs.jetpack_engine + " " + Refs.status_on;
        }
        if (IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, message);
        }
    }
}
