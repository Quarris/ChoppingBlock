package dev.quarris.choppingblock.content;

import com.google.gson.JsonObject;
import dev.quarris.choppingblock.ModRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class ChoppingRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final int hits;
    private final SoundEvent hitSound;
    private final SoundEvent breakSound;

    private final NonNullList ingredients = NonNullList.create();

    @SuppressWarnings("unchecked")
    public ChoppingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, int hits, SoundEvent hitSound, SoundEvent breakSound) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.hits = hits;
        this.hitSound = hitSound;
        this.breakSound = breakSound;

        this.ingredients.add(this.ingredient);
    }

    @Override
    public boolean matches(Container inv, Level level) {
        return this.ingredient.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(Container inv) {
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

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1, this.ingredient);
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
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.CHOPPING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRegistry.CHOPPING_RECIPE.get();
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ChoppingRecipe> {

        @Override
        public ChoppingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredient = CraftingHelper.getIngredient(json.get("ingredient"));
            ItemStack result = CraftingHelper.getItemStack(json.getAsJsonObject("result"), true);
            int hits = json.has("hits") ? json.get("hits").getAsInt() : 3;
            String hitSoundName = json.has("hitSound") ? json.get("hitSound").getAsString() : "block.wood.hit";
            SoundEvent hitSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(hitSoundName));
            String breakSoundName = json.has("breakSound") ? json.get("breakSound").getAsString() : "block.wood.break";
            SoundEvent breakSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(breakSoundName));
            return new ChoppingRecipe(id, ingredient, result, hits, hitSound, breakSound);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ChoppingRecipe recipe) {
            recipe.ingredient.toNetwork(buf);
            buf.writeItem(recipe.result);
            buf.writeVarInt(recipe.hits);
            buf.writeResourceLocation(recipe.hitSound.getRegistryName());
            buf.writeResourceLocation(recipe.breakSound.getRegistryName());
        }

        @Nullable
        @Override
        public ChoppingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient ingredient = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            int hits = buf.readVarInt();
            SoundEvent hitSound = ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation());
            SoundEvent breakSound = ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation());
            return new ChoppingRecipe(id, ingredient, result, hits, hitSound, breakSound);
        }
    }
}
