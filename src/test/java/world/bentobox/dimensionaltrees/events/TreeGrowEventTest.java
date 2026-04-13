package world.bentobox.dimensionaltrees.events;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        when(settings.getEndLeavesPerTree()).thenReturn(new HashMap<>());
        when(settings.getEndLogsPerTree()).thenReturn(new HashMap<>());
        when(settings.getNetherLeavesPerTree()).thenReturn(new HashMap<>());
        when(settings.getNetherLogsPerTree()).thenReturn(new HashMap<>());

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

    // ── Per-tree overrides ─────────────────────────────────────────────────

    @Test
    void testNetherLogPerTreeOverrideUsed() {
        Map<String, String> logsOverride = new HashMap<>();
        logsOverride.put("oak", "netherrack");
        when(settings.getNetherLogsPerTree()).thenReturn(logsOverride);

        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.NETHERRACK);
    }

    @Test
    void testNetherLeavesPerTreeOverrideUsed() {
        Map<String, String> leavesOverride = new HashMap<>();
        leavesOverride.put("oak", "soul_sand");
        when(settings.getNetherLeavesPerTree()).thenReturn(leavesOverride);

        BlockState leafState = mock(BlockState.class);
        when(leafState.getType()).thenReturn(Material.OAK_LEAVES);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(leafState));
        listener.onTreeGrow(event);
        verify(leafState).setType(Material.SOUL_SAND);
    }

    @Test
    void testEndLogPerTreeOverrideUsed() {
        Map<String, String> logsOverride = new HashMap<>();
        logsOverride.put("oak", "obsidian");
        when(settings.getEndLogsPerTree()).thenReturn(logsOverride);

        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.THE_END, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.OBSIDIAN);
    }

    @Test
    void testEndLeavesPerTreeOverrideUsed() {
        Map<String, String> leavesOverride = new HashMap<>();
        leavesOverride.put("oak", "grass_block");
        when(settings.getEndLeavesPerTree()).thenReturn(leavesOverride);

        BlockState leafState = mock(BlockState.class);
        when(leafState.getType()).thenReturn(Material.OAK_LEAVES);
        StructureGrowEvent event = makeEvent(World.Environment.THE_END, List.of(leafState));
        listener.onTreeGrow(event);
        verify(leafState).setType(Material.GRASS_BLOCK);
    }

    @Test
    void testPerTreeOverrideDoesNotAffectOtherTreeTypes() {
        // Override only affects oak; acacia should still use global gravel
        Map<String, String> logsOverride = new HashMap<>();
        logsOverride.put("oak", "netherrack");
        when(settings.getNetherLogsPerTree()).thenReturn(logsOverride);
        when(settings.getTreeTypes()).thenReturn(Arrays.asList("oak", "acacia", "birch"));
        when(saplingBlock.getType()).thenReturn(Material.ACACIA_SAPLING);

        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.ACACIA_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.GRAVEL);
    }

    @Test
    void testInvalidPerTreeOverrideFallsBackToGlobal() {
        // Override has invalid material; should fall back to global "gravel"
        Map<String, String> logsOverride = new HashMap<>();
        logsOverride.put("oak", "not_a_real_material");
        when(settings.getNetherLogsPerTree()).thenReturn(logsOverride);

        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.GRAVEL);
    }

    // ── resolveMaterial unit tests ─────────────────────────────────────────

    @Test
    void testResolveMaterialReturnsOverrideWhenValid() {
        Map<String, String> map = new HashMap<>();
        map.put("oak", "netherrack");
        String result = listener.resolveMaterial(map, "oak", "gravel");
        org.junit.jupiter.api.Assertions.assertEquals("netherrack", result);
    }

    @Test
    void testResolveMaterialReturnsGlobalWhenNoOverride() {
        Map<String, String> map = new HashMap<>();
        String result = listener.resolveMaterial(map, "oak", "gravel");
        org.junit.jupiter.api.Assertions.assertEquals("gravel", result);
    }

    @Test
    void testResolveMaterialReturnsGlobalWhenMapIsNull() {
        String result = listener.resolveMaterial(null, "oak", "gravel");
        org.junit.jupiter.api.Assertions.assertEquals("gravel", result);
    }

    @Test
    void testResolveMaterialReturnsGlobalWhenOverrideInvalid() {
        Map<String, String> map = new HashMap<>();
        map.put("oak", "not_real");
        String result = listener.resolveMaterial(map, "oak", "gravel");
        org.junit.jupiter.api.Assertions.assertEquals("gravel", result);
    }
}
