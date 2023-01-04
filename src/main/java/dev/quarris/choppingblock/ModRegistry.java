package dev.quarris.choppingblock;

import dev.quarris.choppingblock.content.ChoppingBlock;
import dev.quarris.choppingblock.content.ChoppingBlockEntity;
import dev.quarris.choppingblock.content.ChoppingRecipe;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModRef.ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModRef.ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ModRef.ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ModRef.ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, ModRef.ID);

    public static final RegistryObject<Block> CHOPPING_BLOCK = BLOCKS.register("chopping_block", ChoppingBlock::new);

    public static final RegistryObject<Item> CHOPPING_BLOCK_ITEM = ITEMS.register("chopping_block", () -> new BlockItem(CHOPPING_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistryObject<BlockEntityType<ChoppingBlockEntity>> CHOPPING_BLOCK_ENTITY = BLOCK_ENTITIES.register("chopping_block", () -> BlockEntityType.Builder.of(ChoppingBlockEntity::new, CHOPPING_BLOCK.get()).build(null));

    public static final RegistryObject<RecipeSerializer<ChoppingRecipe>> CHOPPING_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("chopping", ChoppingRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<ChoppingRecipe>> CHOPPING_RECIPE = RECIPE_TYPES.register("chopping", () -> new RecipeType<>() {
    });


    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
    }

}
