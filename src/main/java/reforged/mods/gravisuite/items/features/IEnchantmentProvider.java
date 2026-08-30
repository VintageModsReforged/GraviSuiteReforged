package reforged.mods.gravisuite.items.features;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import reforged.mods.gravisuite.utils.Messages;

import java.util.IdentityHashMap;
import java.util.Map;

public interface IEnchantmentProvider {

    String TAG_ENCH_MODE = "enchMode";

    enum EnchantmentMode {
        SILK(Messages.Translations.TOOL_MODE_SILK.format(), Enchantment.silkTouch.effectId, 1),
        FORTUNE(Messages.Translations.TOOL_MODE_FORTUNE.format(), Enchantment.fortune.effectId, 3);

        public static final EnchantmentMode[] VALUES = values();
        public final String MESSAGE;
        public final int enchId;
        public final int enchLevel;

        EnchantmentMode(String message, int enchId, int enchLevel) {
            this.MESSAGE = message;
            this.enchId = enchId;
            this.enchLevel = enchLevel;
        }

        public static EnchantmentMode getFromId(int id) {
            return VALUES[id % VALUES.length];
        }
    }

    class EnchantHelper {

        private EnchantHelper() {}

        public static EnchantmentMode getEnch(ItemStack stack) {
            NBTTagCompound nbt = stack.getTagCompound();
            return nbt != null && nbt.hasKey(TAG_ENCH_MODE)
                    ? EnchantmentMode.getFromId(nbt.getInteger(TAG_ENCH_MODE))
                    : EnchantmentMode.VALUES[0];
        }

        public static EnchantmentMode cycleAndSave(ItemStack stack) {
            EnchantmentMode current = getEnch(stack);
            EnchantmentMode next = EnchantmentMode.getFromId(current.ordinal() + 1);

            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            nbt.setInteger(IEnchantmentProvider.TAG_ENCH_MODE, next.ordinal());

            Map<Integer, Integer> enchMap = new IdentityHashMap<Integer, Integer>();
            enchMap.put(next.enchId, next.enchLevel);

            NBTTagList enchTagList = stack.getEnchantmentTagList();
            if (enchTagList != null) {
                for (int i = 0; i < enchTagList.tagCount(); i++) {
                    NBTTagCompound ench = (NBTTagCompound) enchTagList.tagAt(i);
                    int id = ench.getShort("id");
                    int lvl = ench.getShort("lvl");
                    if (id != Enchantment.silkTouch.effectId && id != Enchantment.fortune.effectId) {
                        enchMap.put(id, lvl);
                    }
                }
            }

            EnchantmentHelper.setEnchantments(enchMap, stack);
            return next;
        }
    }
}
