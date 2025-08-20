package reforged.mods.gravisuite.items.features;

import mods.vintage.core.helpers.StackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import reforged.mods.gravisuite.utils.Messages;

public interface IPropsProvider {

    String TAG_PROPS = "toolProps";

    enum Props {
        NORMAL(new Pair<Float, Float>(35f, 20000f), new Pair<Integer, Integer>(160, 3200), Messages.Translations.EFF_TOOL_MODE_NORMAL.format(), new int[] { 84, 84, 252 }),
        LOW_POWER(new Pair<Float, Float>(16f, 1000f), new Pair<Integer, Integer>(80, 1600), Messages.Translations.EFF_TOOL_MODE_LOW.format(), new int[] { 84, 251, 84 }),
        FINE(new Pair<Float, Float>(10f, 100f), new Pair<Integer, Integer>(50, 800), Messages.Translations.EFF_TOOL_MODE_FINE.format(), new int[] { 84, 251, 251 });

        public static final Props[] VALUES = values();
        public final Pair<Float, Float> EFF;
        public final Pair<Integer, Integer> COST;
        public final String MESSAGE;
        public final int[] COLOR;

        Props(Pair<Float, Float> eff, Pair<Integer, Integer> cost, String name, int[] color) {
            this.EFF = eff;
            this.COST = cost;
            this.MESSAGE = name;
            this.COLOR = color;
        }

        public static Props getFromId(int id) {
            return VALUES[id % VALUES.length];
        }
    }

    class Pair<DRILL, VAJRA> {
        DRILL drill;
        VAJRA vajra;

        public Pair(DRILL drill, VAJRA vajra) {
            this.drill = drill;
            this.vajra = vajra;
        }

        public DRILL getDrill() {
            return drill;
        }

        public VAJRA getVajra() {
            return vajra;
        }
    }

    final class PropsHelper {

        private PropsHelper() {} // prevent instantiation

        public static Props getProps(ItemStack stack) {
            NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
            int id = tag.getInteger(TAG_PROPS);
            return Props.getFromId(id);
        }

        public static Props cycleAndSave(ItemStack stack) {
            Props current = getProps(stack);
            Props next = Props.getFromId(current.ordinal() + 1);
            NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
            tag.setInteger(TAG_PROPS, next.ordinal());
            return next;
        }
    }
}
