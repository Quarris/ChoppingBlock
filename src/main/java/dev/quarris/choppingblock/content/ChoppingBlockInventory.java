package dev.quarris.choppingblock.content;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ChoppingBlockInventory extends ItemStackHandler {

    private final ChoppingBlockEntity choppingBlock;
    public ChoppingBlockInventory(ChoppingBlockEntity choppingBlock, int size) {
        super(size);
        this.choppingBlock = choppingBlock;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.choppingBlock.sendToClients();
    }

}
