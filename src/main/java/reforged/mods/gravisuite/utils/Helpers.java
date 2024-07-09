package reforged.mods.gravisuite.utils;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.input.Keyboard;
import reforged.mods.gravisuite.utils.pos.BlockPos;

import java.lang.reflect.Array;
import java.util.*;

public class Helpers {

    public static boolean areStacksEqual(ItemStack aStack, ItemStack bStack) {
        return aStack != null && bStack != null &&
                aStack.itemID == bStack.itemID &&
                (aStack.getTagCompound() == null == (bStack.getTagCompound() == null)) &&
                (aStack.getTagCompound() == null || aStack.getTagCompound().equals(bStack.getTagCompound()))
                && (aStack.getItemDamage() == bStack.getItemDamage() || aStack.getItemDamage() == 32767 || bStack.getItemDamage() == 32767);
    }

    public static Set<BlockPos> veinPos(World world, BlockPos origin, int maxVeinSize) {
        int originID = world.getBlockId(origin.getX(), origin.getY(), origin.getZ());
        Set<BlockPos> found = new LinkedHashSet<BlockPos>();
        Set<BlockPos> openSet = new LinkedHashSet<BlockPos>();
        openSet.add(origin); // add origin
        while (!openSet.isEmpty()) { // just in case check if it's not empty
            BlockPos blockPos = openSet.iterator().next();
            found.add(blockPos); // add blockPos to found list for return
            openSet.remove(blockPos); // remove it and continue
            if (found.size() > maxVeinSize) { // nah, too much
                return found;
            }
            for (BlockPos pos : BlockPos.getAllInBoxMutable(blockPos.add(-1, -1, -1), blockPos.add(1, 1, 1))) {
                if (!found.contains(pos)) { // we check if it's not in the list already
                    int checkedBlockId = world.getBlockId(pos.getX(), pos.getY(), pos.getZ());
                    if (checkedBlockId != 0) {
                        if (originID == checkedBlockId) {
                            openSet.add(pos.toImmutable()); // add to openSet so we add it later when !openSet.isEmpty()
                        }
                        if (originID == Block.oreRedstone.blockID || originID == Block.oreRedstoneGlowing.blockID) {
                            if (checkedBlockId == Block.oreRedstone.blockID || checkedBlockId == Block.oreRedstoneGlowing.blockID) {
                                openSet.add(pos.toImmutable());
                            }
                        }
                    }
                }
            }
        }
        return found;
    }

    @SuppressWarnings("unchecked")
    public static void removeRecipeByOutput(ItemStack stack) {
        if (stack != null) {
            ArrayList<IRecipe> recipeList = (ArrayList<IRecipe>) CraftingManager.getInstance().getRecipeList();
            for (int i = 0; i < recipeList.size(); i++) {
                ItemStack output = recipeList.get(i).getRecipeOutput();
                if (output != null) {
                    if (stack.isItemEqual(recipeList.get(i).getRecipeOutput())) {
                        recipeList.remove(i);
                    }
                }
            }
        }
    }

    public static ItemStack withSize(ItemStack stack, int count) {
        ItemStack returnStack = stack.copy();
        returnStack.stackSize = count;
        return returnStack;
    }

    public static MovingObjectPosition raytraceFromEntity(World world, Entity player, boolean par3, double range) {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * f;
        if ((!world.isRemote) && ((player instanceof EntityPlayer))) {
            d1 += 1.62D;
        }
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = range;
        if ((player instanceof EntityPlayerMP)) {
            d3 = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
        return world.rayTraceBlocks_do_do(vec3, vec31, par3, !par3);
    }

    // Java 8+ method
    public static <T> T[] add(final T[] array, final T element) {
        final Class<?> type;
        if (array != null) {
            type = array.getClass().getComponentType();
        } else if (element != null) {
            type = element.getClass();
        } else {
            throw new IllegalArgumentException("Arguments cannot both be null");
        }
        @SuppressWarnings("unchecked") // type must be T
        final T[] newArray = (T[]) copyArrayGrow(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    private static Object copyArrayGrow(final Object array, final Class<?> newArrayComponentType) {
        if (array != null) {
            final int arrayLength = Array.getLength(array);
            final Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return Array.newInstance(newArrayComponentType, 1);
    }

    public static List<ItemStack> getStackFromOre(String startWith) {
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        for (String name : OreDictionary.getOreNames()) {
            if (name.startsWith(startWith)) {
                List<ItemStack> oreDictList = OreDictionary.getOres(name);
                stacks.addAll(oreDictList);
            }
        }
        return stacks;
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

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.keyCode);
    }

    public static String pressForInfo(String data) {
        return TextFormatter.GRAY.format("message.info.press", TextFormatter.GOLD.format(data));
    }

    public static String pressXForY(String message, String key1, String action) {
        return TextFormatter.GRAY.format(message, TextFormatter.GOLD.literal(key1), TextFormatter.YELLOW.format(action));
    }

    public static String pressXAndYForZ(String message, String key1, String key2, String action) {
        return TextFormatter.GRAY.format(message, TextFormatter.GOLD.literal(key1), TextFormatter.GOLD.literal(key2), TextFormatter.YELLOW.format(action));
    }

    public static String clickFor(String key, String message) {
        return TextFormatter.GRAY.format("message.info.click.block", TextFormatter.GOLD.literal(key), TextFormatter.YELLOW.format(message));
    }
}
