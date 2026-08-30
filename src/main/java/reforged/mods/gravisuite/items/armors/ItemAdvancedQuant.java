package reforged.mods.gravisuite.items.armors;

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
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.audio.IAudioProvider;
import reforged.mods.gravisuite.items.armors.base.ItemArmorElectric;
import reforged.mods.gravisuite.proxy.CommonProxy;
import reforged.mods.gravisuite.utils.*;

import java.util.List;

public class ItemAdvancedQuant extends ItemArmorElectric implements ISpecialArmor, IAudioProvider {

    public int ENERGY_PER_DAMAGE = 800;
    public static int MIN_CHARGE = 80000;
    public int BOOST_MULTIPLIER;
    public int USAGE_IN_AIR;
    public int USAGE_ON_GROUND;
    public float BOOST_SPEED;
    public static byte TOGGLE_TIMER;

    public ItemAdvancedQuant() {
        super(GraviSuiteConfig.ADVANCED_QUANT_ID.get(), EnumArmorMaterial.DIAMOND, "advanced_quant", 4, EnergyValues.ADV_QUANT.tier, EnergyValues.ADV_QUANT.transfer, EnergyValues.ADV_QUANT.maxCapacity);
        this.USAGE_IN_AIR = 278;
        this.USAGE_ON_GROUND = 1;
        this.BOOST_SPEED = 0.5F;
        this.BOOST_MULTIPLIER = 3;
        TOGGLE_TIMER = 5;
        MinecraftForge.EVENT_BUS.register(this);
        this.ENERGY_PER_DAMAGE = 900;
        this.DAMAGE_PRIORITY = 8;
        this.BASE_ABSORPTION = 1.1D;
        this.DAMAGE_ABSORPTION = 0.4D;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, final List tooltip, boolean par4) {
        super.addInformation(stack, player, tooltip, par4);
        boolean isGraviEngineOn = readFlyStatus(stack);
        boolean isLevitationOn = readWorkMode(stack);
        String gravitationEngine = Helpers.getStatusMessage(isGraviEngineOn);
        String levitationStatus = Helpers.getStatusMessage(isLevitationOn);
        tooltip.add(Messages.Translations.GRAVITATION_ENGINE.format(gravitationEngine));
        tooltip.add(Messages.Translations.GRAVITATION_LEVITATION.format(levitationStatus));
        if (GraviSuite.PROXY.isSneakKeyDown()) {
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.TOGGLE_KEY, KeyDescriptionHelper.KeyMode.ENABLE, Messages.Translations.GRAVITATION_ENGINE_STAT.format()));
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.MODE_KEY, KeyDescriptionHelper.Keys.JUMP_KEY, KeyDescriptionHelper.KeyMode.ENABLE, Messages.Translations.LEVITATION_STAT.format()));
            tooltip.add(KeyDescriptionHelper.buildKeyDescription(KeyDescriptionHelper.Keys.BOOST_KEY, KeyDescriptionHelper.KeyMode.ENABLE, Messages.Translations.BOOST_MODE.format()));
        } else {
            tooltip.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
        }
    }

    @Override
    public void onArmorTickUpdate(World worldObj, EntityPlayer player, ItemStack itemStack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(itemStack);
        byte toggleTimer = tag.getByte("toggleTimer");

        if (GraviSuite.KEYBOARD.isEngineToggleKeyDown(player) && toggleTimer == 0) {
            switchFlyState(player, itemStack);
        }

        if (IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
            switchWorkMode(player, itemStack);
        }

        if (IC2.platform.isSimulating() && toggleTimer > 0) {
            toggleTimer--;
            tag.setByte("toggleTimer", toggleTimer);
        }

        if (readFlyStatus(itemStack)) {
            use(player, itemStack);
            player.capabilities.allowFlying = true;
            if (readWorkMode(itemStack)) {
                player.capabilities.isFlying = true;
            }
        } else {
            if (!player.capabilities.isCreativeMode) {
                player.capabilities.allowFlying = false;
                player.capabilities.isFlying = false;
                CommonProxy.isFlyActive.put(player, true);
                IC2.platform.resetPlayerInAirTime(player);
            }
        }

        CommonProxy.wasUndressed.put(player, false);
        if (player.isBurning()) {
            player.extinguish();
        }
    }

    public void use(EntityPlayer player, ItemStack itemStack) {
        double currCharge = ElectricHelper.getCharge(itemStack);
        if (!player.capabilities.isCreativeMode) {
            if (currCharge < USAGE_IN_AIR) {
                IC2.platform.messagePlayer(player, Messages.Translations.STATUS_SHUTDOWN.format());
                switchFlyState(player, itemStack);
            } else if (!player.onGround) {
                ElectricItem.discharge(itemStack, USAGE_IN_AIR, 3, false, false);
            } else {
                ElectricItem.discharge(itemStack, USAGE_ON_GROUND, 3, false, false);
            }
        }
        player.fallDistance = 0.0F;
        if ((!player.onGround) && (player.capabilities.isFlying) && (IC2.keyboard.isBoostKeyDown(player))) {
            boostMode(player, itemStack);
            if ((currCharge > USAGE_IN_AIR * BOOST_MULTIPLIER) || (player.capabilities.isCreativeMode)) {
                if (IC2.keyboard.isJumpKeyDown(player)) {
                    player.motionY += BOOST_SPEED + 0.1F;
                }
                if (IC2.keyboard.isSneakKeyDown(player)) {
                    player.motionY -= BOOST_SPEED + 0.1F;
                }
                if (!player.capabilities.isCreativeMode) {
                    ElectricItem.discharge(itemStack, USAGE_IN_AIR * BOOST_MULTIPLIER, 3, true, false);
                }
            } else {
                IC2.platform.messagePlayer(player, Messages.Translations.STATUS_LOW.format());
            }
        }
    }

    public void boostMode(EntityPlayer player, ItemStack itemstack) {
        if ((readFlyStatus(itemstack)) && (!player.onGround) && (player.capabilities.isFlying)
                && (!player.isInWater())) {
            double currCharge = ElectricHelper.getCharge(itemstack);
            if ((currCharge > USAGE_IN_AIR * BOOST_MULTIPLIER) || (player.capabilities.isCreativeMode)) {
                player.moveFlying(0.0F, 0.4F, BOOST_SPEED + 0.1F);

                if (!player.capabilities.isCreativeMode) {
                    ElectricItem.discharge(itemstack, USAGE_IN_AIR * BOOST_MULTIPLIER, 3, true, false);
                }
            }
        }
    }

    public static boolean readWorkMode(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return tag.getBoolean("isLevitationActive");
    }

    public static void saveWorkMode(ItemStack stack, boolean workMode) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        tag.setBoolean("isLevitationActive", workMode);
        tag.setByte("toggleTimer", TOGGLE_TIMER);
    }

    public static void switchWorkMode(EntityPlayer player, ItemStack itemstack) {
        String message;
        if (readWorkMode(itemstack)) {
            saveWorkMode(itemstack, false);
            message = Messages.Translations.GRAVITATION_LEVITATION.format(Messages.Translations.STATUS_OFF.format());
        } else {
            saveWorkMode(itemstack, true);
            message = Messages.Translations.GRAVITATION_LEVITATION.format(Messages.Translations.STATUS_ON.format());
        }
        if (IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, message);
        }
    }

    public static boolean readFlyStatus(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return tag.getBoolean("isFlyActive");
    }

    public static void saveFlyStatus(ItemStack stack, boolean flyMode) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        tag.setBoolean("isFlyActive", flyMode);
        tag.setByte("toggleTimer", TOGGLE_TIMER);
    }

    public static void switchFlyState(EntityPlayer player, ItemStack itemstack) {
        String message;
        if (readFlyStatus(itemstack)) {
            saveFlyStatus(itemstack, false);
            message = Messages.Translations.GRAVITATION_ENGINE.format(Messages.Translations.STATUS_OFF.format());
        } else {
            double currCharge = ElectricHelper.getCharge(itemstack);
            if ((currCharge >= MIN_CHARGE) || (player.capabilities.isCreativeMode)) {
                message = Messages.Translations.GRAVITATION_ENGINE.format(Messages.Translations.STATUS_ON.format());
                saveFlyStatus(itemstack, true);
            } else {
                message = Messages.Translations.STATUS_LOW.format();
            }
        }
        if (IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, message);
        }
    }

    @Override
    public AudioSource getAudio(EntityPlayer player) {
        return IC2.audioManager.createSource(player, PositionSpec.Backpack, "graviengine.ogg", true, false, IC2.audioManager.defaultVolume);
    }
}
