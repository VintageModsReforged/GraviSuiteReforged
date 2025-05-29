package reforged.mods.gravisuite.utils;

import ic2.api.item.ElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class VoiderContainer extends Container {

    ItemStack voider;

    public VoiderContainer(EntityPlayer player) {
        this.voider = player.getCurrentEquippedItem();
        this.addSlotToContainer(new VoidSlot(80, 35));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int id) {
        Slot slotObject = (Slot) this.inventorySlots.get(id);
        slotObject.putStack(null);
        ElectricItem.manager.discharge(voider, 500, Integer.MAX_VALUE, true, false);
        return null;
    }

    public static class InventoryDummy implements IInventory {
        @Override
        public int getSizeInventory() {
            return 1;
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
            return "Void";
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
        public boolean isStackValidForSlot(int i, ItemStack stack) {
            return false;
        }
    }

    public static class VoidSlot extends Slot {

        public VoidSlot(int x, int y) {
            super(new InventoryDummy(), 0, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return true;
        }

        @Override
        public void putStack(ItemStack stack) {
        }

        @Override
        public boolean canTakeStack(EntityPlayer player) {
            return false;
        }

        @Override
        public ItemStack decrStackSize(int amount) {
            return null;
        }
    }
}

