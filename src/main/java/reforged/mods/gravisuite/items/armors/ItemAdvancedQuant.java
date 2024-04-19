package reforged.mods.gravisuite.items.armors;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import reforged.mods.gravisuite.GraviSuiteConfig;
import reforged.mods.gravisuite.items.armors.base.ItemArmorElectric;
import reforged.mods.gravisuite.proxy.ClientProxy;
import reforged.mods.gravisuite.proxy.CommonProxy;
import reforged.mods.gravisuite.utils.Helpers;
import reforged.mods.gravisuite.utils.Refs;

import java.util.List;

public class ItemAdvancedQuant extends ItemArmorElectric {

    public static int MIN_CHARGE = 10000;
    public int BOOST_MULTIPLIER;
    public int USAGE_IN_AIR;
    public int USAGE_ON_GROUND;
    public float BOOST_SPEED;
    public static AudioSource AUDIO_SOURCE;
    public static byte TOGGLE_TIMER;
    static boolean LAST_USED = false;

    public ItemAdvancedQuant() {
        super(GraviSuiteConfig.ADVANCED_QUANT_ID, "advanced_quant", 3, 50000, 10000000, EnumRarity.epic);
        this.USAGE_IN_AIR = 278;
        this.USAGE_ON_GROUND = 1;
        this.BOOST_SPEED = 0.2F;
        this.BOOST_MULTIPLIER = 3;
        TOGGLE_TIMER = 5;
        MinecraftForge.EVENT_BUS.register(this);
        this.energy_per_damage = 2000;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isDebugMode) {
        super.addInformation(stack, player, tooltip, isDebugMode);
        boolean isGraviEngineOn = readFlyStatus(stack);
        boolean isLevitationOn = readWorkMode(stack);
        String gravitationEngine = isGraviEngineOn ? Refs.status_on : Refs.status_off;
        String levitationStatus = isLevitationOn ? Refs.status_on : Refs.status_off;
        tooltip.add(Refs.gravitation_engine + " " + gravitationEngine);
        tooltip.add(Refs.gravitation_levitation + " " + levitationStatus);
        if (Helpers.isShiftKeyDown()) {
            tooltip.add(Helpers.pressXForY(Refs.to_enable_1, StatCollector.translateToLocal(ClientProxy.engine_toggle.keyDescription), Refs.GRAVITATION_ENGINE + ".stat"));
            tooltip.add(Helpers.pressXAndYForZ(Refs.to_enable_2, "Mode Switch Key", StatCollector.translateToLocal(Minecraft.getMinecraft().gameSettings.keyBindJump.keyDescription), Refs.LEVITATION + ".stat"));
            tooltip.add(Helpers.pressXForY(Refs.to_enable_1, "Boost Key", Refs.BOOST_MODE));
        } else {
            tooltip.add(Helpers.pressForInfo(Refs.SNEAK_KEY));
        }
    }



    @Override
    public void onArmorTickUpdate(World worldObj, EntityPlayer player, ItemStack itemStack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(itemStack);
        byte toggleTimer = tag.getByte("toggleTimer");
        boolean used = false;

        if (ClientProxy.engine_toggle.pressed && toggleTimer == 0) {
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
            used = use(player, itemStack);
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
        if (IC2.platform.isRendering()) {
            createSound(player, used);
        }
    }

    public static void createSound(EntityPlayer player, boolean used) {
        if (LAST_USED != used) {
            if (used) {
                if (AUDIO_SOURCE == null) {
                    AUDIO_SOURCE = IC2.audioManager.createSource(player, PositionSpec.Backpack, "graviengine.ogg", true, false, IC2.audioManager.defaultVolume);
                }
                if (AUDIO_SOURCE != null) {
                    AUDIO_SOURCE.play();
                }
            } else if (AUDIO_SOURCE != null) {
                AUDIO_SOURCE.remove();
                AUDIO_SOURCE = null;
            }
            LAST_USED = used ;
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

    public boolean use(EntityPlayer player, ItemStack itemStack) {
        double currCharge = Helpers.getCharge(itemStack);
        if (!player.capabilities.isCreativeMode) {
            if (currCharge < USAGE_IN_AIR) {
                IC2.platform.messagePlayer(player, Refs.status_shutdown);
                switchFlyState(player, itemStack);
            } else if (!player.onGround) {
                ElectricItem.manager.discharge(itemStack, USAGE_IN_AIR, 3, false, false);
            } else {
                ElectricItem.manager.discharge(itemStack, USAGE_ON_GROUND, 3, false, false);
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
                    ElectricItem.manager.discharge(itemStack, USAGE_IN_AIR * BOOST_MULTIPLIER, 3, true, false);
                }
            } else {
                IC2.platform.messagePlayer(player, Refs.status_low);

            }
        }
        return true;
    }

    public void boostMode(EntityPlayer player, ItemStack itemstack) {
        if ((readFlyStatus(itemstack)) && (!player.onGround) && (player.capabilities.isFlying)
                && (!player.isInWater())) {
            double currCharge = Helpers.getCharge(itemstack);
            if ((currCharge > USAGE_IN_AIR * BOOST_MULTIPLIER) || (player.capabilities.isCreativeMode)) {
                player.moveFlying(0.0F, 0.4F, BOOST_SPEED);

                if (!player.capabilities.isCreativeMode) {
                    ElectricItem.manager.discharge(itemstack, USAGE_IN_AIR * BOOST_MULTIPLIER, 3, true, false);
                }
            }
        }
    }

    public static boolean readWorkMode(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return tag.getBoolean("isLevitationActive");
    }

    public static void saveWorkMode(ItemStack stack, boolean workMode) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        tag.setBoolean("isLevitationActive", workMode);
        tag.setByte("toggleTimer", TOGGLE_TIMER);
    }

    public static void switchWorkMode(EntityPlayer player, ItemStack itemstack) {
        String message;
        if (readWorkMode(itemstack)) {
            saveWorkMode(itemstack, false);
            message = Refs.gravitation_levitation + " " + Refs.status_off;
        } else {
            saveWorkMode(itemstack, true);
            message = Refs.gravitation_levitation + " " + Refs.status_on;
        }
        if (IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, message);
        }
    }

    public static boolean readFlyStatus(ItemStack stack) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        return tag.getBoolean("isFlyActive");
    }

    public static void saveFlyStatus(ItemStack stack, boolean flyMode) {
        NBTTagCompound tag = Helpers.getOrCreateTag(stack);
        tag.setBoolean("isFlyActive", flyMode);
        tag.setByte("toggleTimer", TOGGLE_TIMER);
    }

    public static void switchFlyState(EntityPlayer player, ItemStack itemstack) {
        String message;
        if (readFlyStatus(itemstack)) {
            saveFlyStatus(itemstack, false);
            message = Refs.gravitation_engine + " " + Refs.status_off;
        } else {
            double currCharge = Helpers.getCharge(itemstack);
            if ((currCharge >= MIN_CHARGE) || (player.capabilities.isCreativeMode)) {
                message = Refs.gravitation_engine + " " + Refs.status_on;
                saveFlyStatus(itemstack, true);
            } else {
                message = Refs.status_low;
            }
        }
        if (IC2.platform.isSimulating()) {
            IC2.platform.messagePlayer(player, message);
        }
    }

    @Override
    public ArmorProperties getProperties(EntityLiving entityLiving, ItemStack armor, DamageSource damageSource, double damage, int slot) {
        int energyPerDamage = energy_per_damage;
        int damageLimit = Integer.MAX_VALUE;
        if (energyPerDamage > 0) {
            damageLimit = Math.min(damageLimit, 25 * Helpers.getCharge(armor) / energyPerDamage);
        }
        if (damageSource == DamageSource.fall) {
            if (this.armorType == 1) {
                return new ArmorProperties(10, 1.0, damageLimit);
            }
        }
        double absorptionRatio = 1.1D;
        return new ArmorProperties(8, absorptionRatio, damageLimit);
    }

    @Override
    public int getArmorDisplay(EntityPlayer entityPlayer, ItemStack itemStack, int i) {
        return 9;
    }

    @ForgeSubscribe
    public void onEntityLivingFallEvent(LivingFallEvent event) {
        if (IC2.platform.isSimulating() && event.entity instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving)event.entity;
            ItemStack armor = entity.getCurrentArmor(2);
            if (armor != null && armor.getItem() == this) {
                int fallDamage = Math.max((int)event.distance - 10, 0);
                int energyCost = energy_per_damage * fallDamage;
                if (energyCost <= Helpers.getCharge(armor)) {
                    ElectricItem.manager.discharge(armor, energyCost, Integer.MAX_VALUE, true, false);
                    event.setCanceled(true);
                }
            }
        }
    }
}