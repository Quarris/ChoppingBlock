package dev.quarris.choppingblock;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;


@Mod.EventBusSubscriber(modid = ModRef.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        if (event.includeClient()) {
            gen.addProvider(new ModBlockStateProvider(gen, ModRef.ID, existingFileHelper));
            gen.addProvider(new ModModelProvider(gen, ModRef.ID, existingFileHelper));
            gen.addProvider(new ModEnUsLangProvider(gen, ModRef.ID));
        }

        if (event.includeServer()) {
            gen.addProvider(new ModBlockTagProvider(gen, ForgeRegistries.BLOCKS, ModRef.ID, existingFileHelper));
        }
    }

    public static class ModBlockStateProvider extends BlockStateProvider {

        public ModBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
            super(gen, modid, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            this.horizontalBlock(ModRegistry.CHOPPING_BLOCK.get(), this.models().withExistingParent("chopping_block", this.modLoc("chopping_block_raw")).texture("side", this.modLoc("block/chopping_block_side")).texture("end", this.modLoc("block/chopping_block_top")));
        }
    }

    public static class ModModelProvider extends ItemModelProvider {

        public ModModelProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
            super(gen, modid, exFileHelper);
        }

        @Override
        protected void registerModels() {
            this.withExistingParent("chopping_block", this.modLoc("block/chopping_block"));
        }
    }

    public static class ModEnUsLangProvider extends LanguageProvider {

        public ModEnUsLangProvider(DataGenerator gen, String modid) {
            super(gen, modid, "en_us");
        }

        @Override
        protected void addTranslations() {
            this.add(ModRegistry.CHOPPING_BLOCK.get(), "Chopping Block");
            this.add("choppingblock.chopping.title", "Chopping Block");
        }
    }

    public static class ModBlockTagProvider extends ForgeRegistryTagsProvider<Block> {

        public ModBlockTagProvider(DataGenerator generator, IForgeRegistry<Block> forgeRegistry, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(generator, forgeRegistry, modId, existingFileHelper);
        }

        @Override
        protected void addTags() {
            this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModRegistry.CHOPPING_BLOCK.get());
        }

        @Override
        public String getName() {
            return "ChoppingBlock Block Tags";
        }
    }
}
