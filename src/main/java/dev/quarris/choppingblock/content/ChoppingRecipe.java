package dev.quarris.choppingblock.content;

import com.google.gson.JsonObject;
import dev.quarris.choppingblock.ModRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class ChoppingRecipe implements IRecipe<IInventory> {

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final int hits;
    private final SoundEvent hitSound;
    private final SoundEvent breakSound;

    public ChoppingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, int hits, SoundEvent hitSound, SoundEvent breakSound) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.hits = hits;
        this.hitSound = hitSound;
        this.breakSound = breakSound;
    }

    @Override
    public boolean matches(IInventory inv, World level) {
        return this.ingredient.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(IInventory inv) {
        return this.result.copy();
    }

    public int getHits() {
        return this.hits;
    }

    public SoundEvent getHitSound() {
        return this.hitSound;
    }

    public SoundEvent getBreakSound() {
        return this.breakSound;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRegistry.CHOPPING_RECIPE_SERIALIZER.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRegistry.CHOPPING_RECIPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ChoppingRecipe> {

        @Override
        public ChoppingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredient = CraftingHelper.getIngredient(json.get("ingredient"));
            ItemStack result = CraftingHelper.getItemStack(json.getAsJsonObject("result"), true);
            int hits = JSONUtils.getAsInt(json, "hits", 3);
            SoundEvent hitSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(JSONUtils.getAsString(json, "hitSound", "block.wood.hit")));
            SoundEvent breakSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(JSONUtils.getAsString(json, "breakSound", "block.wood.break")));
            return new ChoppingRecipe(id, ingredient, result, hits, hitSound, breakSound);
        }

        @Override
        public void toNetwork(PacketBuffer buf, ChoppingRecipe recipe) {
            recipe.ingredient.toNetwork(buf);
            buf.writeItem(recipe.result);
            buf.writeVarInt(recipe.hits);
            buf.writeResourceLocation(recipe.hitSound.getRegistryName());
            buf.writeResourceLocation(recipe.breakSound.getRegistryName());
        }

        @Nullable
        @Override
        public ChoppingRecipe fromNetwork(ResourceLocation id, PacketBuffer buf) {
            Ingredient ingredient = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            int hits = buf.readVarInt();
            SoundEvent hitSound = ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation());
            SoundEvent breakSound = ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation());
            return new ChoppingRecipe(id, ingredient, result, hits, hitSound, breakSound);
        }
    }
}
