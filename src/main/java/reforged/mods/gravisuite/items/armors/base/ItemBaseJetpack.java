package reforged.mods.gravisuite.items.armors.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.item.ElectricItem;
import ic2.core.util.StackUtil;
import mods.vintage.core.helpers.ElectricHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.audio.IAudioProvider;
import reforged.mods.gravisuite.utils.*;

import java.util.List;

public class ItemBaseJetpack extends ItemArmorElectric implements IAudioProvider {

    public static byte TOGGLE_TIMER;
    public double HOVER_FALL_SPEED;

    public static final String NBT_ACTIVE = "fly_active";
    public static final String NBT_HOVER_ACTIVE = "hover_active";
    public static final String NBT_TOGGLE_TIMER = "toggle_timer";

    public ItemBaseJetpack(int id, EnumArmorMaterial armorMaterial, int meta, String name) {
        super(id, armorMaterial, name, meta, EnergyValues.ADV_JETPACK.tier, EnergyValues.ADV_JETPACK.transfer, EnergyValues.ADV_JETPACK.maxCapacity);
        this.HOVER_FALL_SPEED = 0.03D;
        TOGGLE_TIMER = 5;
    }

    @SuppressWarnings({"unchecked"})
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, final List tooltip, boolean par4) {
        super.addInformation(stack, player, tooltip, par4);
        boolean isHoverMode = readWorkMode(stack);
        boolean isEngineOn = readFlyStatus(stack);

        String hoverStatus = Helpers.getStatusMessage(isHoverMode);
        String jetpackStatus = Helpers.getStatusMessage(isEngineOn);

        tooltip.add(Messages.Translations.JETPACK_ENGINE.toTooltip().format(jetpackStatus));
        tooltip.add(Messages.Translations.JETPACK_HOVER.toTooltip().format(hoverStatus));
        if (GraviSuite.PROXY.isSneakKeyDown()) {
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.TOGGLE_KEY, KeyDescriptionHelper.KeyMode.ENABLE, Messages.Translations.JETPACK_ENGINE_STAT.format()));
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.MODE_KEY, KeyDescriptionHelper.Keys.JUMP_KEY, KeyDescriptionHelper.KeyMode.ENABLE, Messages.Translations.JETPACK_HOVER_STAT.format()));
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.BOOST_KEY, KeyDescriptionHelper.KeyMode.ENABLE, Messages.Translations.BOOST_MODE.format()));
        } else {
            tooltip.add(Helpers.pressForInfo(Messages.Translations.KEY_SNEAK.format()));
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

    public void useJetpack(EntityPlayer player, ItemStack stack, boolean hover) {
        int usage = 12;
        double charge = ElectricHelper.getCharge(stack);
        if (charge < usage && !player.capabilities.isCreativeMode)
            return;
        float power = 1.0F;
        double dropPercentage = 0.001D;
        double dropLimit = this.getMaxCharge() * 0.05D;
        if (charge / this.getMaxCharge() <= dropPercentage)
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
                    ElectricItem.discharge(stack, 60, Integer.MAX_VALUE, true, false);
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
                    ElectricItem.discharge(stack, 60, Integer.MAX_VALUE, true, false);
                }
            if (player.motionY > maxHoverY) {
                player.motionY = maxHoverY;
                if (motionY > player.motionY)
                    player.motionY = motionY;
            }
        }
        if (!player.capabilities.isCreativeMode && !player.onGround)
            ElectricItem.discharge(stack, usage, Integer.MAX_VALUE, true, false);
        player.fallDistance = 0.0F;
        player.distanceWalkedModified = 0.0F;
        IC2.platform.resetPlayerInAirTime(player);
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
            message = Messages.Translations.JETPACK_HOVER.format(Messages.Translations.STATUS_OFF.format());
        } else {
            saveWorkMode(itemstack, true);
            message = Messages.Translations.JETPACK_HOVER.format(Messages.Translations.STATUS_ON.format());
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
            message = Messages.Translations.JETPACK_ENGINE.format(Messages.Translations.STATUS_OFF.format());
        } else {
            saveFlyStatus(itemstack, true);
            message = Messages.Translations.JETPACK_ENGINE.format(Messages.Translations.STATUS_ON.format());
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
