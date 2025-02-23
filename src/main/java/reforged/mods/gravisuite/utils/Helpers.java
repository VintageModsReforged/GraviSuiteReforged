package reforged.mods.gravisuite.utils;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import mods.vintage.core.platform.lang.FormattedTranslator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class Helpers {

    public static ItemStack withSize(ItemStack stack, int count) {
        ItemStack returnStack = stack.copy();
        returnStack.stackSize = count;
        return returnStack;
    }

    public static ItemStack getCharged(Item item, int charge) {
        if (!(item instanceof IElectricItem)) {
            throw new IllegalArgumentException(item + " must be an instanceof IElectricItem");
        } else {
            ItemStack ret = new ItemStack(item);
            ElectricItem.manager.charge(ret, charge, Integer.MAX_VALUE, true, false);
            return ret;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addChargeVariants(Item item, List list) {
        list.add(getCharged(item, 0));
        list.add(getCharged(item, Integer.MAX_VALUE));
    }

    public static int getCharge(ItemStack stack) {
        NBTTagCompound tag = getOrCreateTag(stack);
        return tag.getInteger("charge");
    }

    public static NBTTagCompound getOrCreateTag(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound("gravi_data");
            stack.setTagCompound(tag);
        }
        return tag;
    }

    public static String pressForInfo(String data) {
        return FormattedTranslator.GRAY.format("message.info.press", FormattedTranslator.GOLD.format(data));
    }

    public static String pressXForY(String message, String key1, String action) {
        return FormattedTranslator.GRAY.format(message, FormattedTranslator.GOLD.literal(key1), FormattedTranslator.YELLOW.format(action));
    }

    public static String pressXAndYForZ(String message, String key1, String key2, String action) {
        return FormattedTranslator.GRAY.format(message, FormattedTranslator.GOLD.literal(key1), FormattedTranslator.GOLD.literal(key2), FormattedTranslator.YELLOW.format(action));
    }

    public static String clickFor(String key, String message) {
        return FormattedTranslator.GRAY.format("message.info.click.block", FormattedTranslator.GOLD.literal(key), FormattedTranslator.YELLOW.format(message));
    }

    public static String getStatusMessage(boolean status) {
        return status ? Refs.status_on : Refs.status_off;
    }
}
