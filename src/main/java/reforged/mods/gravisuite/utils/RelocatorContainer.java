package reforged.mods.gravisuite.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RelocatorContainer extends Container {

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public Slot getSlot(int i) {
        return new Slot(new InventoryEmpty(), 0, 0, 0);
    }

    public static class InventoryEmpty implements IInventory {

        @Override
        public int getSizeInventory() {
            return 0;
        }

        @Override
        public ItemStack getStackInSlot(int i) {
            return null;
        }

        @Override
        public ItemStack decrStackSize(int i, int j) {
            return null;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int i) {
            return null;
        }

        @Override
        public void setInventorySlotContents(int i, ItemStack stack) {
        }

        @Override
        public String getInvName() {
            return "";
        }

        @Override
        public boolean isInvNameLocalized() {
            return false;
        }

        @Override
        public int getInventoryStackLimit() {
            return 0;
        }

        @Override
        public void onInventoryChanged() {
        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
            return false;
        }

        @Override
        public void openChest() {
        }

        @Override
        public void closeChest() {
        }

        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemStack) {
            return false;
        }
    }
}
