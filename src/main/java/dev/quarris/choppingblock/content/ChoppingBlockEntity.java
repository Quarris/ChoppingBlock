package dev.quarris.choppingblock.content;

import dev.quarris.choppingblock.ModRef;
import dev.quarris.choppingblock.ModRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ChoppingBlockEntity extends TileEntity {

    private ChoppingBlockInventory inv = new ChoppingBlockInventory(this, 1);
    private LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> this.inv);

    private int hits;

    public ChoppingBlockEntity(TileEntityType<?> type) {
        super(type);
    }

    public ChoppingBlockEntity() {
        this(ModRegistry.CHOPPING_BLOCK_ENTITY.get());
    }

    public boolean interact(PlayerEntity player, ItemStack item) {
        boolean extracted = false;
        ItemStack slot = this.inv.getStackInSlot(0);
        if (!slot.isEmpty()) {
            this.clearRecipe();
            if (ItemEntity.areMergable(item, slot)) {
                item.grow(1);
                return true;
            } else {
                ItemHandlerHelper.giveItemToPlayer(player, slot, player.inventory.selected);
            }
            extracted = true;
        }

        Optional<ChoppingRecipe> recipe = this.level.getRecipeManager().getRecipeFor(ModRegistry.CHOPPING_RECIPE, new Inventory(item), this.level);
        if (recipe.isPresent()) {
            ItemStack toInsert = item.copy();
            toInsert.setCount(1);
            item.shrink(1);
            this.inv.setStackInSlot(0, toInsert);
            return true;
        }

        return extracted;
    }

    public void doChop(PlayerEntity player, ItemStack axe) {
        this.findRecipeFor(this.inv.getStackInSlot(0)).ifPresent(recipe -> {
            axe.hurtAndBreak(1, player, p -> {
            });
            player.getCooldowns().addCooldown(axe.getItem(), 10);
            this.hits++;
            SoundEvent sound = recipe.getHitSound();
            if (this.hits >= recipe.getHits()) {
                sound = recipe.getBreakSound();
                ItemStack result = recipe.assemble(null);
                ItemEntity drop = new ItemEntity(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.85, this.worldPosition.getZ() + 0.5, result);
                drop.setDeltaMovement(0, 0, 0);
                this.level.addFreshEntity(drop);
                this.clearRecipe();
            }
            this.level.playSound(player, this.worldPosition, sound, SoundCategory.BLOCKS, 1, 1);
        });
    }

    public Optional<ChoppingRecipe> findRecipeFor(ItemStack input) {
        return this.level.getRecipeManager().getRecipeFor(ModRegistry.CHOPPING_RECIPE, new Inventory(input), this.level);
    }

    public ItemStack getItem() {
        return this.inv.getStackInSlot(0);
    }

    private void clearRecipe() {
        this.inv.setStackInSlot(0, ItemStack.EMPTY);
        this.hits = 0;
    }

    public boolean hasRecipe() {
        return this.findRecipeFor(this.inv.getStackInSlot(0)).isPresent();
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        if (!this.inv.getStackInSlot(0).isEmpty()) {
            nbt.put("Inv", this.inv.serializeNBT());
        }
        nbt.putInt("Hits", this.hits);
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        if (nbt.contains("Inv")) {
            this.inv.deserializeNBT(nbt.getCompound("Inv"));
        }
        this.hits = nbt.getInt("Hits");
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.load(this.getBlockState(), pkt.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, -1, this.getUpdateTag());
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        this.lazyInv.invalidate();
    }

    public void sendToClients() {
        if (!this.level.isClientSide()) {
            ServerWorld serverLevel = ((ServerWorld) this.level);
            serverLevel.getNearbyPlayers(EntityPredicate.DEFAULT, null, new AxisAlignedBB(this.worldPosition).inflate(16)).stream().map(p -> ((ServerPlayerEntity) p)).forEach(player -> {
                player.connection.send(this.getUpdatePacket());
            });
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == ModRef.Capabilities.ITEMS && side != Direction.DOWN) {
            return this.lazyInv.cast();
        }

        return super.getCapability(cap, side);
    }
}
