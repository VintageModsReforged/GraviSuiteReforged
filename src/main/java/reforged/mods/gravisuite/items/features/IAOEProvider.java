package reforged.mods.gravisuite.items.features;

import mods.vintage.core.helpers.StackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import reforged.mods.gravisuite.utils.Messages;

public interface IAOEProvider {

    String TAG_AOE = "toolMode";

    enum Mode {
        NORMAL(Messages.Translations.TOOL_MODE_AOE_NORMAL.format()),
        BIG_HOLES(Messages.Translations.TOOL_MODE_AOE_BIG_HOLES.format());

        public static final Mode[] VALUES = values();
        public final String MESSAGE;

        Mode(String name) {
            this.MESSAGE = name;
        }

        public static Mode getFromId(int id) {
            return VALUES[id % values().length];
        }
    }

    class AOEHelper {

        private AOEHelper() {}

        public static Mode getMode(ItemStack stack) {
            NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
            int id = tag.getInteger(TAG_AOE);
            return Mode.getFromId(id);
        }

        public static Mode cycleAndSave(ItemStack stack) {
            Mode current = getMode(stack);
            Mode next = Mode.getFromId(current.ordinal() + 1);
            NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
            tag.setInteger(TAG_AOE, next.ordinal());
            return next;
        }
    }
}
