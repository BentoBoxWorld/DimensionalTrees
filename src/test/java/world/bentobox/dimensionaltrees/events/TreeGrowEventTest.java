package world.bentobox.dimensionaltrees.events;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.world.StructureGrowEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import world.bentobox.dimensionaltrees.CommonTestSetup;
import world.bentobox.dimensionaltrees.DimensionalTrees;
import world.bentobox.dimensionaltrees.Settings;

/**
 * Tests for {@link TreeGrowEvent}.
 */
class TreeGrowEventTest extends CommonTestSetup {

    @Mock
    private DimensionalTrees addon;
    @Mock
    private Settings settings;
    @Mock
    private Block saplingBlock;

    private TreeGrowEvent listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        when(addon.getPlugin()).thenReturn(plugin);
        when(addon.getSettings()).thenReturn(settings);
        when(settings.isEnabled()).thenReturn(true);
        when(settings.isEndEnabled()).thenReturn(true);
        when(settings.isNetherEnabled()).thenReturn(true);
        when(settings.getEndLeaves()).thenReturn("end_stone");
        when(settings.getEndLogs()).thenReturn("purpur_block");
        when(settings.getNetherLeaves()).thenReturn("glowstone");
        when(settings.getNetherLogs()).thenReturn("gravel");
        when(settings.getTreeTypes()).thenReturn(Arrays.asList("oak", "acacia", "birch"));
        when(settings.isSendLog()).thenReturn(false);

        // Sapling block at the event location
        when(location.getBlock()).thenReturn(saplingBlock);
        when(saplingBlock.getType()).thenReturn(Material.OAK_SAPLING);

        listener = new TreeGrowEvent(addon);
    }

    private StructureGrowEvent makeEvent(World.Environment env, List<BlockState> blocks) {
        when(world.getEnvironment()).thenReturn(env);
        return new StructureGrowEvent(location, TreeType.TREE, false, null, blocks);
    }

    // ── disabled / gating ──────────────────────────────────────────────────

    @Test
    void testAddonDisabledSkipsProcessing() {
        when(settings.isEnabled()).thenReturn(false);
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState, never()).setType(any());
    }

    @Test
    void testNotInWorldSkipsProcessing() {
        when(iwm.inWorld(any(World.class))).thenReturn(false);
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState, never()).setType(any());
    }

    @Test
    void testSaplingNotInTypeListSkipsProcessing() {
        when(saplingBlock.getType()).thenReturn(Material.DARK_OAK_SAPLING);
        // "dark_oak" is not in the tree_types list
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.DARK_OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState, never()).setType(any());
    }

    // ── invalid material ───────────────────────────────────────────────────

    @Test
    void testInvalidNetherLogMaterialCallsWarning() {
        when(settings.getNetherLogs()).thenReturn("not_a_real_material");
        when(settings.isSendLog()).thenReturn(true);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, Collections.emptyList());
        listener.onTreeGrow(event);
        verify(addon).logError(any());
    }

    // ── Nether ─────────────────────────────────────────────────────────────

    @Test
    void testNetherLogReplaced() {
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.GRAVEL);
    }

    @Test
    void testNetherLeavesReplaced() {
        BlockState leafState = mock(BlockState.class);
        when(leafState.getType()).thenReturn(Material.OAK_LEAVES);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(leafState));
        listener.onTreeGrow(event);
        verify(leafState).setType(Material.GLOWSTONE);
    }

    @Test
    void testNetherMixedBlocksReplaced() {
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        BlockState leafState = mock(BlockState.class);
        when(leafState.getType()).thenReturn(Material.OAK_LEAVES);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState, leafState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.GRAVEL);
        verify(leafState).setType(Material.GLOWSTONE);
    }

    @Test
    void testNetherDisabledSkipsProcessing() {
        when(settings.isNetherEnabled()).thenReturn(false);
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState, never()).setType(any());
    }

    // ── End ────────────────────────────────────────────────────────────────

    @Test
    void testEndLogReplaced() {
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.THE_END, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.PURPUR_BLOCK);
    }

    @Test
    void testEndLeavesReplaced() {
        BlockState leafState = mock(BlockState.class);
        when(leafState.getType()).thenReturn(Material.OAK_LEAVES);
        StructureGrowEvent event = makeEvent(World.Environment.THE_END, List.of(leafState));
        listener.onTreeGrow(event);
        verify(leafState).setType(Material.END_STONE);
    }

    @Test
    void testEndDisabledSkipsProcessing() {
        when(settings.isEndEnabled()).thenReturn(false);
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.THE_END, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState, never()).setType(any());
    }

    // ── Normal world ───────────────────────────────────────────────────────

    @Test
    void testNormalWorldSkipsProcessing() {
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NORMAL, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState, never()).setType(any());
    }

    // ── Empty block list ───────────────────────────────────────────────────

    @Test
    void testEmptyBlockListDoesNotThrow() {
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, Collections.emptyList());
        listener.onTreeGrow(event);
    }
}
