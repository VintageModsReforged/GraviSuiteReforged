package reforged.mods.gravisuite.items.armors.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.item.ElectricItem;
import ic2.core.util.StackUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.audio.IAudioProvider;
import reforged.mods.gravisuite.keyboard.GraviSuiteKeyboardClient;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.List;

public class ItemBaseJetpack extends ItemBaseEnergyPack implements IAudioProvider {

    public static byte TOGGLE_TIMER;
    public double HOVER_FALL_SPEED;

    public static final String NBT_ACTIVE = "fly_active";
    public static final String NBT_HOVER_ACTIVE = "hover_active";
    public static final String NBT_TOGGLE_TIMER = "toggle_timer";

    public ItemBaseJetpack(int id, int meta, String name) {
        super(id, meta, name, 2, 1000, 1000000);
        this.HOVER_FALL_SPEED = 0.03D;
        TOGGLE_TIMER = 5;
    }

    @SuppressWarnings({"unchecked"})
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
        super.addInformation(stack, player, tooltip, par4);
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
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        boolean hoverMode = readWorkMode(stack);

        byte toggleTimer = tag.getByte(NBT_TOGGLE_TIMER);

        if (GraviSuite.KEYBOARD.isEngineToggleKeyDown(player) && toggleTimer <= 0) {
            switchFlyState(player, stack);
        }

        if (IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer <= 0) {
            switchWorkMode(player, stack);
        }

        if (IC2.keyboard.isAltKeyDown(player)) {
            hoverMode = !hoverMode;
        }

        if ((IC2.keyboard.isJumpKeyDown(player)
                || (hoverMode && player.motionY < -HOVER_FALL_SPEED && !player.onGround)) && readFlyStatus(stack))
            useJetpack(player, stack, hoverMode);
        if (IC2.platform.isSimulating() && toggleTimer > 0) {
            toggleTimer--;
            tag.setByte(NBT_TOGGLE_TIMER, toggleTimer);
        }
    }

    public static void useJetpack(EntityPlayer paramEntityPlayer, ItemStack stack, boolean paramBoolean) {
        double d1 = Helpers.getCharge(stack);
        if (d1 < 12.0D && !paramEntityPlayer.capabilities.isCreativeMode)
            return;
        float f = 1.0F;
        double d2 = 0.0010000000474974513D;
        double d3 = 150000.0D;
        if (d1 / 3000000.0D <= d2)
            f = (float) (f * d1 / d3);
        if (paramEntityPlayer.capabilities.isCreativeMode)
            f = 1.0F;
        if (IC2.keyboard.isForwardKeyDown(paramEntityPlayer)) {
            float f1 = 0.3F;
            if (paramBoolean)
                f1 = 0.65F;
            float f2 = f * f1 * 2.0F;
            float f3 = 0.0F;
            if (IC2.keyboard.isBoostKeyDown(paramEntityPlayer)
                    && (d1 > 60.0D || paramEntityPlayer.capabilities.isCreativeMode)) {
                f3 = 0.09F;
                if (paramBoolean)
                    f3 = 0.07F;
            }
            if (f2 > 0.0F) {
                paramEntityPlayer.moveFlying(0.0F, 0.4F * f2 + f3, 0.02F + f3);
                if (f3 > 0.0F && !paramEntityPlayer.capabilities.isCreativeMode && IC2.platform.isSimulating())
                    ElectricItem.discharge(stack, 60, 2147483647, true, false);
            }
        }
        int i = paramEntityPlayer.worldObj.getHeight();
        double d4 = paramEntityPlayer.posY;
        if (d4 > (i - 25)) {
            if (d4 > i)
                d4 = i;
            f = (float) (f * (i - d4) / 25.0D);
        }
        double d5 = paramEntityPlayer.motionY;
        paramEntityPlayer.motionY = Math.min(paramEntityPlayer.motionY + (f * 0.2F),
                0.6000000238418579D);
        if (paramBoolean) {
            double d = -0.03D;
            if (IC2.keyboard.isJumpKeyDown(paramEntityPlayer))
                d = 0.2D;
            if (IC2.keyboard.isSneakKeyDown(paramEntityPlayer))
                d = -0.2D;
            if ((d1 > 60.0D || paramEntityPlayer.capabilities.isCreativeMode)
                    && IC2.keyboard.isBoostKeyDown(paramEntityPlayer))
                if (IC2.keyboard.isSneakKeyDown(paramEntityPlayer) || IC2.keyboard.isJumpKeyDown(paramEntityPlayer)) {
                    d *= 2.0D;
                    ElectricItem.discharge(stack, 60, 2147483647, true, false);
                }
            if (paramEntityPlayer.motionY > d) {
                paramEntityPlayer.motionY = d;
                if (d5 > paramEntityPlayer.motionY)
                    paramEntityPlayer.motionY = d5;
            }
        }
        if (!paramEntityPlayer.capabilities.isCreativeMode && !paramEntityPlayer.onGround)
            ElectricItem.discharge(stack, 12, 2147483647, true, false);
        paramEntityPlayer.fallDistance = 0.0F;
        paramEntityPlayer.distanceWalkedModified = 0.0F;
        IC2.platform.resetPlayerInAirTime(paramEntityPlayer);
    }

    public static boolean readWorkMode(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return tag.getBoolean(NBT_HOVER_ACTIVE);
    }

    public static void saveWorkMode(ItemStack stack, boolean workMode) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        tag.setBoolean(NBT_HOVER_ACTIVE, workMode);
        tag.setByte(NBT_TOGGLE_TIMER, TOGGLE_TIMER);
    }

    public static void switchWorkMode(EntityPlayer player, ItemStack itemstack) {
        String message;
        if (readWorkMode(itemstack)) {
            saveWorkMode(itemstack, false);
            message = Refs.jetpack_hover + " " + Refs.status_off;
        } else {
            saveWorkMode(itemstack, true);
            message = Refs.jetpack_hover + " " + Refs.status_on;
        }
        if (IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, message);
        }
    }

    public static boolean readFlyStatus(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return tag.getBoolean(NBT_ACTIVE);
    }

    public static void saveFlyStatus(ItemStack stack, boolean flyMode) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        tag.setBoolean(NBT_ACTIVE, flyMode);
        tag.setByte(NBT_TOGGLE_TIMER, TOGGLE_TIMER);
    }

    public static void switchFlyState(EntityPlayer player, ItemStack itemstack) {
        String message;
        if (readFlyStatus(itemstack)) {
            saveFlyStatus(itemstack, false);
            message = Refs.jetpack_engine + " " + Refs.status_off;
        } else {
            saveFlyStatus(itemstack, true);
            message = Refs.jetpack_engine + " " + Refs.status_on;
        }
        if (IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, message);
        }
    }

    @Override
    public AudioSource getAudio(EntityPlayer player) {
        return IC2.audioManager.createSource(player, PositionSpec.Backpack, "Tools/Jetpack/JetpackLoop.ogg", true, false, IC2.audioManager.defaultVolume);
    }
}
