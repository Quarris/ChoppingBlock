package dev.quarris.choppingblock.content;

import dev.quarris.choppingblock.ModRef;
import dev.quarris.choppingblock.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ChoppingBlockEntity extends BlockEntity {

    private ChoppingBlockInventory inv = new ChoppingBlockInventory(this, 1);
    private LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> this.inv);
    private ItemStack axe = ItemStack.EMPTY;

    private int hits;

    public float axeAX;
    public float axeAZ;

    public ChoppingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ChoppingBlockEntity(BlockPos pos, BlockState state) {
        this(ModRegistry.CHOPPING_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean interact(Player player, ItemStack item) {
        boolean extracted = false;
        ItemStack slot = this.inv.getStackInSlot(0);
        if (!slot.isEmpty()) {
            this.clearRecipe();
            if (ItemEntity.areMergable(item, slot)) {
                item.grow(1);
                return true;
            } else {
                ItemHandlerHelper.giveItemToPlayer(player, slot, player.getInventory().selected);
            }
            extracted = true;
        }

        Optional<ChoppingRecipe> recipe = this.level.getRecipeManager().getRecipeFor(ModRegistry.CHOPPING_RECIPE.get(), new SimpleContainer(item), this.level);
        if (recipe.isPresent()) {
            ItemStack toInsert = item.copy();
            toInsert.setCount(1);
            item.shrink(1);
            this.inv.setStackInSlot(0, toInsert);
            return true;
        }

        return extracted;
    }

    public void doChop(Player player, ItemStack axe) {
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
            this.level.playSound(player, this.worldPosition, sound, SoundSource.BLOCKS, 1, 1);
        });
    }

    public boolean insertAxe(ItemStack axe) {
        if (this.axe.isEmpty()) {
            this.axe = axe;
            this.axeAX = (float) (Math.random() * 10 - 5);
            this.axeAZ = (float) (Math.random() * 10 - 5);
            return true;
        }
        return false;
    }

    public ItemStack extractAxe() {
        ItemStack axe = this.axe;
        this.axe = ItemStack.EMPTY;
        return axe;
    }

    public boolean hasAxe() {
        return !this.axe.isEmpty();
    }

    public Optional<ChoppingRecipe> findRecipeFor(ItemStack input) {
        return this.level.getRecipeManager().getRecipeFor(ModRegistry.CHOPPING_RECIPE.get(), new SimpleContainer(input), this.level);
    }

    public ItemStack getItem() {
        return this.inv.getStackInSlot(0).copy();
    }

    public ItemStack getAxe() {
        return this.axe.copy();
    }

    private void clearRecipe() {
        this.inv.setStackInSlot(0, ItemStack.EMPTY);
        this.hits = 0;
    }

    public boolean hasRecipe() {
        return this.findRecipeFor(this.inv.getStackInSlot(0)).isPresent();
    }


    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (!this.inv.getStackInSlot(0).isEmpty()) {
            nbt.put("Inv", this.inv.serializeNBT());
        }
        nbt.putInt("Hits", this.hits);
        if (!this.axe.isEmpty()) {
            nbt.put("Axe", this.axe.save(new CompoundTag()));
            if (nbt.contains("AxeAX")) {
                nbt.putFloat("AxeAX", this.axeAX);
                nbt.putFloat("AxeAZ", this.axeAZ);
            }
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("Inv")) {
            this.inv.deserializeNBT(nbt.getCompound("Inv"));
        }
        this.hits = nbt.getInt("Hits");
        if (nbt.contains("Axe")) {
            this.axe = ItemStack.of(nbt.getCompound("Axe"));
            this.axeAX = nbt.getFloat("AxeAX");
            this.axeAZ = nbt.getFloat("AxeAZ");
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        this.saveAdditional(nbt);
        return nbt;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.lazyInv.invalidate();
    }

    public void sendToClients() {
        if (!this.level.isClientSide()) {
            ServerLevel serverLevel = ((ServerLevel) this.level);
            serverLevel.getNearbyPlayers(TargetingConditions.DEFAULT, null, new AABB(this.worldPosition).inflate(16)).stream().map(p -> ((ServerPlayer) p)).forEach(player -> {
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
