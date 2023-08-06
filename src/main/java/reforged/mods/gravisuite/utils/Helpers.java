package reforged.mods.gravisuite.utils;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.util.StackUtil;
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
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.input.Keyboard;
import reforged.mods.gravisuite.utils.pos.BlockPos;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Helpers {

    public static List<BlockPos> veinPos(BlockPos origin, World world, EntityPlayer player) {
        List<BlockPos> found = new ArrayList<BlockPos>();
        Set<BlockPos> checked = new HashSet<BlockPos>();
        found.add(origin);
        Block block = Block.blocksList[world.getBlockId(origin.getX(), origin.getY(), origin.getZ())];

        if (player.isSneaking()) return found;

        for (int i = 0; i < found.size(); i++) {
            BlockPos pos = found.get(i);
            checked.add(pos);
            for (BlockPos foundPos : BlockPos.getAllInBoxMutable(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
                if (!checked.contains(foundPos)) {
                    int checkedBlockId = world.getBlockId(foundPos.getX(), foundPos.getY(), foundPos.getZ());
                    Block checkedBlock = Block.blocksList[checkedBlockId];
                    if (!(checkedBlockId == 0)) {
                        if (block == checkedBlock) {
                            found.add(foundPos.toImmutable());
                        }
                        if (block == Block.oreRedstone || block == Block.oreRedstoneGlowing) {
                            if (checkedBlock == Block.oreRedstone || checkedBlock == Block.oreRedstoneGlowing) {
                                found.add(foundPos.toImmutable());
                            }
                        }
                        if (found.size() > 127) {
                            return found;
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
                if (StackUtil.isStackEqual(stack, recipeList.get(i).getRecipeOutput())) {
                    recipeList.remove(i);
                }
            }
        }
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
        NBTTagCompound tag = StackUtil.getOrCreateNbtData(stack);
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
        return "\2477" + StatCollector.translateToLocalFormatted("message.info.press", "\2476" + data + "\2477");
    }

    public static String pressXForY(String message, String key1, String action) {
        return "\2477" + StatCollector.translateToLocalFormatted(message, "\2476" + key1 + "\2477", "\2476" + StatCollector.translateToLocal(action));
    }

    public static String pressXAndYForZ(String message, String key1, String key2, String action) {
        return "\2477" + StatCollector.translateToLocalFormatted(message, "\2476" + key1 + "\2477", "\2476" + key2 + "\2477", "\2476" + StatCollector.translateToLocal(action));
    }
}
