package reforged.mods.gravisuite.items.features;

import mods.vintage.core.helpers.BlockHelper;
import mods.vintage.core.helpers.StackHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import reforged.mods.gravisuite.utils.Messages;

public interface IVeinMiner {

    String TAG_VEIN = "toolMode";

    enum Vein {
        OFF(Messages.Translations.TOOL_MODE_VEIN_OFF.format()) {
            @Override
            public boolean matches(World world, int x, int y, int z) {
                return false;
            }
        },
        ORES(Messages.Translations.TOOL_MODE_VEIN_ORES.format()) {
            @Override
            public boolean matches(World world, int x, int y, int z) {
                Block block = BlockHelper.getBlock(world, x, y, z);
                if (block == Block.oreRedstoneGlowing) {
                    block = Block.oreRedstone;
                }
                ItemStack stack = new ItemStack(block, 1, world.getBlockMetadata(x, y, z));
                for (ItemStack ore : StackHelper.getStackFromOre("ore")) {
                    if (StackHelper.areStacksEqual(ore, stack))
                        return true;
                }
                return false;
            }
        },
        EXTENDED(Messages.Translations.TOOL_MODE_VEIN_EXTENDED.format()) {
            @Override
            public boolean matches(World world, int x, int y, int z) {
                return true;
            }
        };

        public static final Vein[] VALUES = values();
        public final String MESSAGE;

        Vein(String name) {
            this.MESSAGE = name;
        }

        public static Vein getFromId(int id) {
            return VALUES[id % VALUES.length];
        }

        public abstract boolean matches(World world, int x, int y, int z);
    }

    class VeinMinerHelper {

        private VeinMinerHelper() {};

        public static Vein getVein(ItemStack stack) {
            NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
            int id = tag.getInteger(TAG_VEIN);
            return Vein.getFromId(id);
        }

        public static Vein cycleAndSave(ItemStack stack) {
            Vein current = getVein(stack);
            Vein next = Vein.getFromId(current.ordinal() + 1);
            NBTTagCompound tag = StackHelper.getOrCreateTag(stack);
            tag.setInteger(TAG_VEIN, next.ordinal());
            return next;
        }
    }
}
