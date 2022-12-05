package dev.quarris.choppingblock;

import dev.quarris.choppingblock.content.ChoppingBlock;
import dev.quarris.choppingblock.content.ChoppingBlockEntity;
import dev.quarris.choppingblock.content.ChoppingRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModRef.ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModRef.ID);
    public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ModRef.ID);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ModRef.ID);

    public static final RegistryObject<Block> CHOPPING_BLOCK = BLOCKS.register("chopping_block", ChoppingBlock::new);

    public static final RegistryObject<Item> CHOPPING_BLOCK_ITEM = ITEMS.register("chopping_block", () -> new BlockItem(CHOPPING_BLOCK.get(), new Item.Properties().addToolType(ToolType.AXE, 0).tab(ItemGroup.TAB_DECORATIONS)));

    public static final RegistryObject<TileEntityType<ChoppingBlockEntity>> CHOPPING_BLOCK_ENTITY = BLOCK_ENTITIES.register("chopping_block", () -> TileEntityType.Builder.of(ChoppingBlockEntity::new, CHOPPING_BLOCK.get()).build(null));

    public static final RegistryObject<IRecipeSerializer<ChoppingRecipe>> CHOPPING_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("chopping", ChoppingRecipe.Serializer::new);
    public static final IRecipeType<ChoppingRecipe> CHOPPING_RECIPE = IRecipeType.register(ModRef.res("chopping").toString());


    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }

}
