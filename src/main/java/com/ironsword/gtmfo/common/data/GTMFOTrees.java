package com.ironsword.gtmfo.common.data;

import com.ironsword.gtmfo.common.block.GTFOBlockLeaves;
import com.ironsword.gtmfo.data.GTMFOProviderTypes;
import com.ironsword.gtmfo.common.block.GTFOBlockLog;
import com.ironsword.gtmfo.common.block.GTFOBlockPlanks;
import com.ironsword.gtmfo.common.block.GTFOBlockSapling;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.ironsword.gtmfo.common.registry.GTMFORegistries.REGISTRATE;

/**
 * Per-tree block registration (log/planks/sapling/leaves) + simplified tree growth.
 * World generation is handled separately (P1).
 */
public class GTMFOTrees {

    public record TreeData(String name, int index, Supplier<? extends Item> fruit, int leafColor,
                            int fruitDivisor, int fruitMin, int fruitMax) {}

    public static final int RAINBOWWOOD_INDEX = 7;
    public static final int RAINBOWWOOD_ITEM_COLOR = 0x8F00FF;
    /** original {@code RainbowwoodTree.RAINBOW_ARRAY} */
    public static final int[] RAINBOW_ARRAY = {
            0xff0000, 0xff4000, 0xff8000, 0xffc000, 0xffff00, 0xc0ff00, 0x80ff00, 0x40ff00,
            0x00ff00, 0x00ff40, 0x00ff80, 0x00ffc0, 0x00ffff, 0x00c0ff, 0x0080ff, 0x0040ff,
            0x0000ff, 0x4000ff, 0x8000ff, 0xc000ff, 0xff00ff, 0xff00c0, 0xff0080, 0xff0040 };

    /** per-tree EN/CN names for log/planks/sapling/leaves (legacy translations) */
    public static final java.util.Map<String, String[]> TREE_LANG = new java.util.LinkedHashMap<>();

    static {
        TREE_LANG.put("banana", new String[] { "Banana Pseudostem", "\u9999\u8549\u5047\u830e",
                "Banana Planks", "\u9999\u8549\u6728\u677f", "Banana Sapling", "\u9999\u8549\u6811\u82d7",
                "Banana Leaves", "\u9999\u8549\u6811\u53f6" });
        TREE_LANG.put("orange", new String[] { "Orange Wood", "\u6a59\u6728",
                "Orange Planks", "\u6a59\u6728\u6728\u677f", "Orange Sapling", "\u6a59\u6811\u6811\u82d7",
                "Orange Leaves", "\u6a59\u6811\u6811\u53f6" });
        TREE_LANG.put("mango", new String[] { "Mango Wood", "\u8292\u679c\u6728",
                "Mango Planks", "\u8292\u679c\u6728\u677f", "Mango Sapling", "\u8292\u679c\u6811\u82d7",
                "Mango Leaves", "\u8292\u679c\u6811\u53f6" });
        TREE_LANG.put("apricot", new String[] { "Apricot Wood", "\u674f\u6728",
                "Apricot Planks", "\u674f\u6728\u6728\u677f", "Apricot Sapling", "\u674f\u6811\u6811\u82d7",
                "Apricot Leaves", "\u674f\u6811\u6811\u53f6" });
        TREE_LANG.put("lemon", new String[] { "Lemon Wood", "\u67e0\u6aac\u6728",
                "Lemon Planks", "\u67e0\u6aac\u6728\u677f", "Lemon Sapling", "\u67e0\u6aac\u6811\u82d7",
                "Lemon Leaves", "\u67e0\u6aac\u6811\u53f6" });
        TREE_LANG.put("lime", new String[] { "Lime Wood", "\u9178\u6a59\u6728",
                "Lime Planks", "\u9178\u6a59\u6728\u677f", "Lime Sapling", "\u9178\u6a59\u6811\u82d7",
                "Lime Leaves", "\u9178\u6a59\u6811\u53f6" });
        TREE_LANG.put("olive", new String[] { "Olive Wood", "\u6a44\u6984\u6728",
                "Olive Planks", "\u6a44\u6984\u6728\u677f", "Olive Sapling", "\u6a44\u6984\u6811\u82d7",
                "Olive Leaves", "\u6a44\u6984\u6811\u53f6" });
        TREE_LANG.put("rainbowwood", new String[] { "Rainbowwood Wood", "\u5f69\u8679\u6728",
                "Rainbowwood Planks", "\u5f69\u8679\u6728\u6728\u677f", "Rainbowwood Sapling", "\u5f69\u8679\u6811\u82d7",
                "Rainbowwood Leaves", "\u5f69\u8679\u6811\u6811\u53f6" });
        TREE_LANG.put("nutmeg", new String[] { "Nutmeg Wood", "\u8089\u6842\u6728",
                "Nutmeg Planks", "\u8089\u6842\u6728\u677f", "Nutmeg Sapling", "\u8089\u6842\u6811\u82d7",
                "Nutmeg Leaves", "\u8089\u6842\u6811\u53f6" });
        TREE_LANG.put("coconut", new String[] { "Coconut Wood", "\u6930\u6728",
                "Coconut Planks", "\u6930\u6728\u6728\u677f", "Coconut Sapling", "\u6930\u6811\u6811\u82d7",
                "Coconut Leaves", "\u6930\u6811\u6811\u53f6" });
    }

    public static final List<TreeData> TREES = List.of(
            new TreeData("banana", 0, () -> GTMFOItems.BANANA.get(), 0x396A2E, 8, 3, 6),
            new TreeData("orange", 1, () -> GTMFOItems.ORANGE.get(), 0x76c92c, 10, 1, 2),
            new TreeData("mango", 2, () -> GTMFOItems.MANGO.get(), 0x7D921E, 10, 0, 2),
            new TreeData("apricot", 3, () -> GTMFOItems.APRICOT.get(), 0x87A92C, 15, 1, 1),
            new TreeData("lemon", 4, () -> GTMFOItems.LEMON.get(), 0x87A92C, 10, 1, 2),
            new TreeData("lime", 5, () -> GTMFOItems.LIME.get(), 0x426801, 10, 1, 2),
            new TreeData("olive", 6, () -> GTMFOItems.OLIVE.get(), 0x828E5A, 15, 1, 4),
            new TreeData("rainbowwood", 7, null, RAINBOWWOOD_ITEM_COLOR, 0, 0, 0),
            new TreeData("nutmeg", 8, () -> GTMFOItems.NUTMEG.get(), 0x6DB626, 10, 1, 2),
            new TreeData("coconut", 9, () -> GTMFOItems.COCONUT.get(), 0x657F1C, 7, 0, 1));

    private static final int COCONUT_INDEX = 9;

    public static final List<BlockEntry<GTFOBlockLog>> LOGS = new ArrayList<>();
    public static final List<BlockEntry<GTFOBlockPlanks>> PLANKS = new ArrayList<>();
    public static final List<BlockEntry<GTFOBlockSapling>> SAPLINGS = new ArrayList<>();
    public static final List<BlockEntry<GTFOBlockLeaves>> LEAVES = new ArrayList<>();

    /** tree index -> (sapling item, log item, planks item, leaves item, fruit item) */
    public static final Map<Integer, TreeData> BY_INDEX = new LinkedHashMap<>();

    static {
        for (int i = 0; i < TREES.size(); i++) {
            TreeData tree = TREES.get(i);
            String name = tree.name();
            int index = i;
            BY_INDEX.put(index, tree);

            BlockEntry<GTFOBlockLog> log = REGISTRATE
                    .block(name + "_log", GTFOBlockLog::new)
                    .initialProperties(() -> Blocks.OAK_LOG)
                    .lang(TREE_LANG.get(name)[0])
                    .setData(GTMFOProviderTypes.CNLANG, (ctx, prov) -> prov.add(ctx.get().getDescriptionId(), TREE_LANG.get(name)[1]))
                    .tag(BlockTags.LOGS_THAT_BURN, BlockTags.MINEABLE_WITH_AXE)
                    .blockstate(NonNullBiConsumer.noop())
                    .item().model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                            prov.modLoc("block/log_" + name))).tag(ItemTags.LOGS_THAT_BURN).build()
                    .register();
            LOGS.add(log);

            BlockEntry<GTFOBlockPlanks> planks = REGISTRATE
                    .block(name + "_planks", GTFOBlockPlanks::new)
                    .initialProperties(() -> Blocks.OAK_PLANKS)
                    .lang(TREE_LANG.get(name)[2])
                    .setData(GTMFOProviderTypes.CNLANG, (ctx, prov) -> prov.add(ctx.get().getDescriptionId(), TREE_LANG.get(name)[3]))
                    .tag(BlockTags.PLANKS, BlockTags.MINEABLE_WITH_AXE)
                    .blockstate(NonNullBiConsumer.noop())
                    .item().model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                            prov.modLoc("block/planks_" + name))).tag(ItemTags.PLANKS).build()
                    .register();
            PLANKS.add(planks);

            BlockEntry<GTFOBlockSapling> sapling = REGISTRATE
                    .block(name + "_sapling", p -> new GTFOBlockSapling(p, index))
                    .initialProperties(() -> Blocks.OAK_SAPLING)
                    .lang(TREE_LANG.get(name)[4])
                    .setData(GTMFOProviderTypes.CNLANG, (ctx, prov) -> prov.add(ctx.get().getDescriptionId(), TREE_LANG.get(name)[5]))
                    .tag(BlockTags.SAPLINGS)
                    .blockstate(NonNullBiConsumer.noop())
                    .item().model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                            prov.modLoc("block/sapling_" + name))).tag(ItemTags.SAPLINGS).build()
                    .register();
            SAPLINGS.add(sapling);
            Supplier<? extends Item> saplingItem = () -> SAPLINGS.get(index).get().asItem();

            Supplier<? extends Item> fruit = tree.fruit();
            BlockEntry<GTFOBlockLeaves> leaves = REGISTRATE
                    .block(name + "_leaves", p -> new GTFOBlockLeaves(p, saplingItem, fruit, tree.fruitDivisor(), tree.fruitMin(), tree.fruitMax()))
                    .initialProperties(() -> Blocks.OAK_LEAVES)
                    .lang(TREE_LANG.get(name)[6])
                    .setData(GTMFOProviderTypes.CNLANG, (ctx, prov) -> prov.add(ctx.get().getDescriptionId(), TREE_LANG.get(name)[7]))
                    .tag(BlockTags.LEAVES, BlockTags.MINEABLE_WITH_HOE)
                    .blockstate(NonNullBiConsumer.noop())
                    .item().model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                            prov.modLoc("block/leaves_" + name))).tag(ItemTags.LEAVES).build()
                    .register();
            LEAVES.add(leaves);
        }
    }

    private static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // === growth ===

    public static void growTree(ServerLevel level, BlockPos pos, int treeIndex, RandomSource random) {
        if (treeIndex < 0 || treeIndex >= TREES.size()) return;
        TreeData tree = TREES.get(treeIndex);

        boolean palm = treeIndex == COCONUT_INDEX;
        int trunkHeight = palm ? 6 + random.nextInt(3) : 5 + random.nextInt(3);

        // check space
        for (int y = 0; y <= trunkHeight + 2; y++) {
            int radius = palm ? 1 : (y <= 1 ? 1 : 2);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockState state = level.getBlockState(pos.offset(x, y, z));
                    if (y > 0 && !state.isAir() && !state.canBeReplaced()) {
                        return;
                    }
                }
            }
        }

        BlockState logState = LOGS.get(treeIndex).get().defaultBlockState();
        for (int y = 0; y < trunkHeight; y++) {
            level.setBlock(pos.above(y), logState, 2);
        }

        BlockState leafState = LEAVES.get(treeIndex).get().defaultBlockState()
                .setValue(GTFOBlockLeaves.PERSISTENT, false)
                .setValue(GTFOBlockLeaves.DISTANCE, 7);

        int topY = trunkHeight;
        if (palm) {
            for (int y = -1; y <= 1; y++) {
                int radius = y == 1 ? 1 : 2;
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (Math.abs(x) == radius && Math.abs(z) == radius && radius > 1) continue;
                        BlockPos leafPos = pos.offset(x, topY + y, z);
                        if (level.getBlockState(leafPos).isAir()) {
                            level.setBlock(leafPos, leafState, 2);
                        }
                    }
                }
            }
            level.setBlock(pos.above(topY), leafState, 2);
        } else {
            for (int y = -2; y <= 1; y++) {
                int radius = y >= 1 ? 1 : 2;
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (Math.abs(x) == radius && Math.abs(z) == radius && random.nextInt(2) == 0) continue;
                        BlockPos leafPos = pos.offset(x, topY + y, z);
                        if (level.getBlockState(leafPos).isAir()) {
                            level.setBlock(leafPos, leafState, 2);
                        }
                    }
                }
            }
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos leafPos = pos.offset(x, topY + 2, z);
                    if (level.getBlockState(leafPos).isAir()) {
                        level.setBlock(leafPos, leafState, 2);
                    }
                }
            }
        }
    }

    public static void init() {
    }
}
