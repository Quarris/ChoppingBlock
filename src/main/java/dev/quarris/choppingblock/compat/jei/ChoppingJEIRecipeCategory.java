package dev.quarris.choppingblock.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.choppingblock.ModRef;
import dev.quarris.choppingblock.ModRegistry;
import dev.quarris.choppingblock.content.ChoppingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ChoppingJEIRecipeCategory implements IRecipeCategory<ChoppingRecipe> {

    public static final ResourceLocation ID = ModRef.res("chopping");
    private final IDrawable background;
    private final IDrawable icon;

    public ChoppingJEIRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(ModRef.res("textures/gui/jei/background.png"), 0, 0, 64, 32)
            .build();
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModRegistry.CHOPPING_BLOCK.get()));
    }

    @Override
    public RecipeType<ChoppingRecipe> getRecipeType() {
        return ModJEIPlugin.CHOPPING_TYPE;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("choppingblock.chopping.title");
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
    public void draw(ChoppingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack ms, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        String value = String.valueOf(recipe.getHits());
        font.drawShadow(ms, value, 32 - font.width(value) / 2f, 13, 0xffffff);
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
    public void setRecipe(IRecipeLayoutBuilder builder, ChoppingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 7).addIngredients(recipe.getIngredient());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 42, 7).addItemStack(recipe.getResultItem());
    }
}
