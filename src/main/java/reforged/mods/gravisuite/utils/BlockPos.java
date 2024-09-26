package reforged.mods.gravisuite.utils;

import com.google.common.base.Objects;
import com.google.common.collect.AbstractIterator;
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@Immutable
public class BlockPos implements Comparable<BlockPos> {

    /**
     * X coordinate
     */
    private final int x;
    /**
     * Y coordinate
     */
    private final int y;
    /**
     * Z coordinate
     */
    private final int z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos(BlockPos source) {
        this(source.getX(), source.getY(), source.getZ());
    }

    /**
     * Gets the X coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the Y coordinate.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Gets the Z coordinate.
     */
    public int getZ() {
        return this.z;
    }

    public BlockPos add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPos toImmutable() {
        return this;
    }

    @Override
    public int compareTo(@NotNull BlockPos anotherPos) {
        if (this.getY() == anotherPos.getY()) {
            return this.getZ() == anotherPos.getZ() ? this.getX() - anotherPos.getX() : this.getZ() - anotherPos.getZ();
        } else {
            return this.getY() - anotherPos.getY();
        }
    }

    public String toString() {
        return Objects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
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
