package reforged.mods.gravisuite.utils;

import net.minecraft.block.*;
import net.minecraft.world.World;

public class BlockHelper {

    public static final int[][] SIDE_COORD_MOD = new int[][]{{0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0}};
    public static final int[] SIDE_LEFT = new int[]{4, 5, 5, 4, 2, 3};
    public static final int[] SIDE_RIGHT = new int[]{5, 4, 4, 5, 3, 2};
    public static final int[] SIDE_OPPOSITE = new int[]{1, 0, 3, 2, 5, 4};

    public static int getRotateType(Block block) {
        if (block instanceof BlockChest) {
            return 9;
        }
        if (block instanceof BlockWood) {
            return 7;
        }
        if (block instanceof BlockDispenser || block instanceof BlockPistonBase) {
            return 2;
        }
        if (block instanceof  BlockRail) {
            return 3;
        }
        if (block instanceof BlockHalfSlab) {
            return 8;
        }
        if (block instanceof BlockStairs) {
            return 5;
        }
        if (block instanceof BlockFurnace) {
            return 1;
        }
        if (block instanceof BlockSign) {
            return 11;
        }
        if (block instanceof BlockLever) {
            return 10;
        }
        if (block instanceof BlockPumpkin) {
            return 4;
        }
        if (block instanceof BlockRedstoneRepeater) {
            return 6;
        }
        return 0;
    }

    public static int[] getAdjacentCoordinatesForSide(int x, int y, int z, int side) {
        return new int[]{x + SIDE_COORD_MOD[side][0], y + SIDE_COORD_MOD[side][1], z + SIDE_COORD_MOD[side][2]};
    }

    public static int rotate(World world, int block, int meta, int x, int y, int z) {
        int shift;
        switch (getRotateType(Block.blocksList[block])) {
            case 1:
                return SIDE_LEFT[meta];
            case 2:
                if (meta < 6) {
                    ++meta;
                    return meta % 6;
                }

                return meta;
            case 3:
                if (meta < 2) {
                    ++meta;
                    return meta % 2;
                }

                return meta;
            case 4:
                ++meta;
                return meta % 4;
            case 5:
                ++meta;
                return meta % 8;
            case 6:
                int upper = meta & 12;
                int lower = meta & 3;
                ++lower;
                return upper + lower % 4;
            case 7:
                return (meta + 4) % 12;
            case 8:
                return (meta + 8) % 16;
            case 9:
                for(shift = 2; shift < 6; ++shift) {
                    int[] coords;
                    coords = getAdjacentCoordinatesForSide(x, y, z, shift);
                    if (world.getBlockId(coords[0], coords[1], coords[2]) == block) {
                        world.setBlockMetadataWithNotify(coords[0], coords[1], coords[2], SIDE_OPPOSITE[meta]);
                        return SIDE_OPPOSITE[meta];
                    }
                }

                return SIDE_LEFT[meta];
            case 10:
                shift = 0;
                if (meta > 7) {
                    meta -= 8;
                    shift = 8;
                }

                if (meta == 5) {
                    return 6 + shift;
                } else if (meta == 6) {
                    return 5 + shift;
                } else if (meta == 7) {
                    return 0 + shift;
                } else {
                    if (meta == 0) {
                        return 7 + shift;
                    }

                    return meta + shift;
                }
            case 11:
                ++meta;
                return meta % 16;
            default:
                return meta;
        }
    }

    public static int rotateAlt(World world, int block, int meta, int x, int y, int z) {
        int shift;
        switch (getRotateType(Block.blocksList[block])) {
            case 1:
                return SIDE_RIGHT[meta];
            case 2:
                if (meta < 6) {
                    return (meta + 5) % 6;
                }

                return meta;
            case 3:
                if (meta < 2) {
                    ++meta;
                    return meta % 2;
                }

                return meta;
            case 4:
                return (meta + 3) % 4;
            case 5:
                return (meta + 7) % 8;
            case 6:
                int upper = meta & 12;
                int lower = meta & 3;
                return upper + (lower + 3) % 4;
            case 7:
                return (meta + 8) % 12;
            case 8:
                return (meta + 8) % 16;
            case 9:
                for(shift = 2; shift < 6; ++shift) {
                    int[] coords;
                    coords = getAdjacentCoordinatesForSide(x, y, z, shift);
                    if (world.getBlockId(coords[0], coords[1], coords[2]) == block) {
                        world.setBlockMetadataWithNotify(coords[0], coords[1], coords[2], SIDE_OPPOSITE[meta]);
                        return SIDE_OPPOSITE[meta];
                    }
                }

                return SIDE_RIGHT[meta];
            case 10:
                shift = 0;
                if (meta > 7) {
                    meta -= 8;
                    shift = 8;
                }

                if (meta == 5) {
                    return 6 + shift;
                } else if (meta == 6) {
                    return 5 + shift;
                } else if (meta == 7) {
                    return shift;
                } else if (meta == 0) {
                    return 7 + shift;
                }
            case 11:
                ++meta;
                return meta % 16;
            default:
                return meta;
        }
    }
}
