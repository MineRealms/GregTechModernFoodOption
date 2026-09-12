package com.ironsword.gtmfo.common.data;

import com.ironsword.gtmfo.common.block.GTFOBlockLeaves;
import com.ironsword.gtmfo.common.block.GTFOBlockLog;
import com.ironsword.gtmfo.common.block.GTFOBlockPlanks;
import com.ironsword.gtmfo.common.block.GTFOBlockSapling;
import com.tterrag.registrate.util.entry.BlockEntry;
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

    public record TreeData(String name, int index, Supplier<? extends Item> fruit, int leafColor) {}

    public static final int RAINBOWWOOD_INDEX = 7;
    public static final int RAINBOWWOOD_ITEM_COLOR = 0x8F00FF;
    /** original {@code RainbowwoodTree.RAINBOW_ARRAY} */
    public static final int[] RAINBOW_ARRAY = {
            0xff0000, 0xff4000, 0xff8000, 0xffc000, 0xffff00, 0xc0ff00, 0x80ff00, 0x40ff00,
            0x00ff00, 0x00ff40, 0x00ff80, 0x00ffc0, 0x00ffff, 0x00c0ff, 0x0080ff, 0x0040ff,
            0x0000ff, 0x4000ff, 0x8000ff, 0xc000ff, 0xff00ff, 0xff00c0, 0xff0080, 0xff0040 };

    public static final List<TreeData> TREES = List.of(
            new TreeData("banana", 0, () -> GTMFOItems.BANANA.get(), 0x396A2E),
            new TreeData("orange", 1, () -> GTMFOItems.ORANGE.get(), 0x76c92c),
            new TreeData("mango", 2, () -> GTMFOItems.MANGO.get(), 0x7D921E),
            new TreeData("apricot", 3, () -> GTMFOItems.APRICOT.get(), 0x87A92C),
            new TreeData("lemon", 4, () -> GTMFOItems.LEMON.get(), 0x87A92C),
            new TreeData("lime", 5, () -> GTMFOItems.LIME.get(), 0x426801),
            new TreeData("olive", 6, () -> GTMFOItems.OLIVE.get(), 0x828E5A),
            new TreeData("rainbowwood", 7, null, RAINBOWWOOD_ITEM_COLOR),
            new TreeData("nutmeg", 8, () -> GTMFOItems.NUTMEG.get(), 0x6DB626),
            new TreeData("coconut", 9, () -> GTMFOItems.COCONUT.get(), 0x657F1C));

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
                    .lang(capitalize(name) + " Log")
                    .tag(BlockTags.LOGS_THAT_BURN, BlockTags.MINEABLE_WITH_AXE)
                    .item().model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                            prov.modLoc("block/log_" + name))).tag(ItemTags.LOGS_THAT_BURN).build()
                    .register();
            LOGS.add(log);

            BlockEntry<GTFOBlockPlanks> planks = REGISTRATE
                    .block(name + "_planks", GTFOBlockPlanks::new)
                    .initialProperties(() -> Blocks.OAK_PLANKS)
                    .lang(capitalize(name) + " Planks")
                    .tag(BlockTags.PLANKS, BlockTags.MINEABLE_WITH_AXE)
                    .item().model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                            prov.modLoc("block/planks_" + name))).tag(ItemTags.PLANKS).build()
                    .register();
            PLANKS.add(planks);

            BlockEntry<GTFOBlockSapling> sapling = REGISTRATE
                    .block(name + "_sapling", p -> new GTFOBlockSapling(p, index))
                    .initialProperties(() -> Blocks.OAK_SAPLING)
                    .lang(capitalize(name) + " Sapling")
                    .tag(BlockTags.SAPLINGS)
                    .item().model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                            prov.modLoc("block/sapling_" + name))).tag(ItemTags.SAPLINGS).build()
                    .register();
            SAPLINGS.add(sapling);
            Supplier<? extends Item> saplingItem = () -> SAPLINGS.get(index).get().asItem();

            Supplier<? extends Item> fruit = tree.fruit();
            BlockEntry<GTFOBlockLeaves> leaves = REGISTRATE
                    .block(name + "_leaves", p -> new GTFOBlockLeaves(p, saplingItem, fruit))
                    .initialProperties(() -> Blocks.OAK_LEAVES)
                    .lang(capitalize(name) + " Leaves")
                    .tag(BlockTags.LEAVES, BlockTags.MINEABLE_WITH_HOE)
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
