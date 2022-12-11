package dev.quarris.choppingblock.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.quarris.choppingblock.ModRef;
import dev.quarris.choppingblock.ModRegistry;
import dev.quarris.choppingblock.content.ChoppingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class ChoppingJEIRecipeCategory implements IRecipeCategory<ChoppingRecipe> {

    public static final ResourceLocation ID = ModRef.res("chopping");
    private final IDrawable background;
    private final IDrawable icon;

    public ChoppingJEIRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(ModRef.res("textures/gui/jei/background.png"), 0, 0, 64, 32)
            .build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModRegistry.CHOPPING_BLOCK.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends ChoppingRecipe> getRecipeClass() {
        return ChoppingRecipe.class;
    }

    @Override
    public String getTitle() {
        return ModRegistry.CHOPPING_BLOCK.get().getName().getString();
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(ChoppingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(Arrays.asList(recipe.getIngredient()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void draw(ChoppingRecipe recipe, MatrixStack ms, double mouseX, double mouseY) {
        FontRenderer font = Minecraft.getInstance().font;
        String value = String.valueOf(recipe.getHits());
        font.drawShadow(ms, value, 32 - font.width(value) / 2f, 13, 0xffffff);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ChoppingRecipe recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 4, 7);
        recipeLayout.getItemStacks().init(1, false, 42, 7);
        recipeLayout.getItemStacks().set(ingredients);
    }
}
