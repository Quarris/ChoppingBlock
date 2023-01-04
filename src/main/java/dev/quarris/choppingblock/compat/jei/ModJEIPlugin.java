package dev.quarris.choppingblock.compat.jei;

import dev.quarris.choppingblock.ModRef;
import dev.quarris.choppingblock.ModRegistry;
import dev.quarris.choppingblock.content.ChoppingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class ModJEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = ModRef.res("jei");
    public static final RecipeType<ChoppingRecipe> CHOPPING_TYPE = new RecipeType<>(ChoppingJEIRecipeCategory.ID, ChoppingRecipe.class);

    public ModJEIPlugin() { }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CHOPPING_TYPE, Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(ModRegistry.CHOPPING_RECIPE.get()));
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ChoppingJEIRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModRegistry.CHOPPING_BLOCK.get()), CHOPPING_TYPE);
    }
}
