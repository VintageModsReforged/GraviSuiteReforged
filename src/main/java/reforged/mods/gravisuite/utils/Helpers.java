package reforged.mods.gravisuite.utils;

import ic2.api.IElectricItem;
import ic2.core.item.ElectricItem;
import ic2.core.util.StackUtil;
import mods.vintage.core.platform.lang.FormattedTranslator;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class Helpers {

    public static ItemStack getCharged(Item item, int charge) {
        if (!(item instanceof IElectricItem)) {
            throw new IllegalArgumentException(item + " must be an instanceof IElectricItem");
        } else {
            ItemStack ret = new ItemStack(item);
            ElectricItem.charge(ret, charge, Integer.MAX_VALUE, true, false);
            return ret;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addChargeVariants(Item item, List list) {
        list.add(getCharged(item, 0));
        list.add(getCharged(item, Integer.MAX_VALUE));
    }

    public static int getCharge(ItemStack stack) {
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
        return tag.getInteger("charge");
    }

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.keyCode);
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
