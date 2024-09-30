package reforged.mods.gravisuite.utils.pos;

import com.google.common.collect.AbstractIterator;
import net.jcip.annotations.Immutable;

import java.util.Iterator;

@Immutable
public class BlockPos extends Vec3i {

    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockPos(double x, double y, double z) {
        super(x, y, z);
    }

    public BlockPos(Vec3i source) {
        this(source.getX(), source.getY(), source.getZ());
    }

    public BlockPos add(double x, double y, double z) {
        return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPos((double) this.getX() + x, (double) this.getY() + y, (double) this.getZ() + z);
    }

    public BlockPos add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPos add(Vec3i vec) {
        return this.add(vec.getX(), vec.getY(), vec.getZ());
    }

    public BlockPos toImmutable() {
        return this;
    }

    public static Iterable<MutableBlockPos> getAllInBoxMutable(BlockPos from, BlockPos to) {
        return getAllInBoxMutable(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()), Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
    }

    public static Iterable<MutableBlockPos> getAllInBoxMutable(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        return new Iterable<MutableBlockPos>() {
            public Iterator<MutableBlockPos> iterator() {
                return new AbstractIterator<MutableBlockPos>() {
                    private MutableBlockPos pos;

                    protected MutableBlockPos computeNext() {
                        if (this.pos == null) {
                            this.pos = new MutableBlockPos(x1, y1, z1);
                            return this.pos;
                        } else if (this.pos.x == x2 && this.pos.y == y2 && this.pos.z == z2) {
                            return this.endOfData();
                        } else {
                            if (this.pos.x < x2) {
                                ++this.pos.x;
                            } else if (this.pos.y < y2) {
                                this.pos.x = x1;
                                ++this.pos.y;
                            } else if (this.pos.z < z2) {
                                this.pos.x = x1;
                                this.pos.y = y1;
                                ++this.pos.z;
                            }

                            return this.pos;
                        }
                    }
                };
            }
        };
    }

    public static class MutableBlockPos extends BlockPos {
        protected int x;
        protected int y;
        protected int z;

        public MutableBlockPos(int x, int y, int z) {
            super(0, 0, 0);
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public BlockPos add(double x, double y, double z) {
            return super.add(x, y, z).toImmutable();
        }

        public BlockPos add(int x, int y, int z) {
            return super.add(x, y, z).toImmutable();
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getZ() {
            return this.z;
        }

        public void setY(int yIn) {
            this.y = yIn;
        }

        public BlockPos toImmutable() {
            return new BlockPos(this);
        }
    }
}
