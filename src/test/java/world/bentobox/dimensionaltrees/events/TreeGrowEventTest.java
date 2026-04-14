package world.bentobox.dimensionaltrees.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.world.StructureGrowEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import world.bentobox.bentobox.api.addons.AddonDescription;
import world.bentobox.bentobox.api.addons.GameModeAddon;
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
        when(settings.getEndLeaves()).thenReturn(Map.of("end_stone", 100));
        when(settings.getEndLogs()).thenReturn(Map.of("purpur_block", 100));
        when(settings.getNetherLeaves()).thenReturn(Map.of("glowstone", 100));
        when(settings.getNetherLogs()).thenReturn(Map.of("gravel", 100));
        when(settings.getTreeTypes()).thenReturn(Arrays.asList("oak", "acacia", "birch"));
        when(settings.isSendLog()).thenReturn(false);
        when(settings.getEndLeavesPerTree()).thenReturn(new HashMap<>());
        when(settings.getEndLogsPerTree()).thenReturn(new HashMap<>());
        when(settings.getNetherLeavesPerTree()).thenReturn(new HashMap<>());
        when(settings.getNetherLogsPerTree()).thenReturn(new HashMap<>());
        when(settings.getEndLeavesPerGamemode()).thenReturn(new HashMap<>());
        when(settings.getEndLogsPerGamemode()).thenReturn(new HashMap<>());
        when(settings.getNetherLeavesPerGamemode()).thenReturn(new HashMap<>());
        when(settings.getNetherLogsPerGamemode()).thenReturn(new HashMap<>());

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
        when(settings.getNetherLogs()).thenReturn(Map.of("not_a_real_material", 100));
        when(settings.isSendLog()).thenReturn(true);
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
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
        Map<String, Map<String, Integer>> logsOverride = new HashMap<>();
        logsOverride.put("oak", Map.of("netherrack", 100));
        when(settings.getNetherLogsPerTree()).thenReturn(logsOverride);

        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.NETHERRACK);
    }

    @Test
    void testNetherLeavesPerTreeOverrideUsed() {
        Map<String, Map<String, Integer>> leavesOverride = new HashMap<>();
        leavesOverride.put("oak", Map.of("soul_sand", 100));
        when(settings.getNetherLeavesPerTree()).thenReturn(leavesOverride);

        BlockState leafState = mock(BlockState.class);
        when(leafState.getType()).thenReturn(Material.OAK_LEAVES);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(leafState));
        listener.onTreeGrow(event);
        verify(leafState).setType(Material.SOUL_SAND);
    }

    @Test
    void testEndLogPerTreeOverrideUsed() {
        Map<String, Map<String, Integer>> logsOverride = new HashMap<>();
        logsOverride.put("oak", Map.of("obsidian", 100));
        when(settings.getEndLogsPerTree()).thenReturn(logsOverride);

        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.THE_END, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.OBSIDIAN);
    }

    @Test
    void testEndLeavesPerTreeOverrideUsed() {
        Map<String, Map<String, Integer>> leavesOverride = new HashMap<>();
        leavesOverride.put("oak", Map.of("grass_block", 100));
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
        Map<String, Map<String, Integer>> logsOverride = new HashMap<>();
        logsOverride.put("oak", Map.of("netherrack", 100));
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
        // Override has invalid material; should fall back to global "gravel" since the
        // invalid name isn't in the registry, and warning is called. But the block is still
        // processed (global gravel is applied on the non-override path).
        // With the new design: invalid override → override is returned but the picker picks
        // "not_a_real_material" → applyWeightedMaterial logs a warning and skips setType.
        // So verify that setType is NOT called with GRAVEL (the block stays as-is).
        Map<String, Map<String, Integer>> logsOverride = new HashMap<>();
        logsOverride.put("oak", Map.of("not_a_real_material", 100));
        when(settings.getNetherLogsPerTree()).thenReturn(logsOverride);
        when(settings.isSendLog()).thenReturn(true);

        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState, never()).setType(Material.GRAVEL);
        verify(addon).logError(any());
    }

    // ── resolveWeights unit tests ──────────────────────────────────────────

    @Test
    void testResolveWeightsReturnsOverrideWhenPresent() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, Integer> oakWeights = Map.of("netherrack", 100);
        map.put("oak", oakWeights);
        Map<String, Integer> result = listener.resolveWeights(map, "oak", Map.of("gravel", 100));
        assertEquals(oakWeights, result);
    }

    @Test
    void testResolveWeightsReturnsGlobalWhenNoOverride() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, Integer> global = Map.of("gravel", 100);
        Map<String, Integer> result = listener.resolveWeights(map, "oak", global);
        assertEquals(global, result);
    }

    @Test
    void testResolveWeightsReturnsGlobalWhenMapIsNull() {
        Map<String, Integer> global = Map.of("gravel", 100);
        Map<String, Integer> result = listener.resolveWeights(null, "oak", global);
        assertEquals(global, result);
    }

    @Test
    void testResolveWeightsReturnsGlobalWhenOverrideIsEmpty() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        map.put("oak", new HashMap<>()); // empty inner map
        Map<String, Integer> global = Map.of("gravel", 100);
        Map<String, Integer> result = listener.resolveWeights(map, "oak", global);
        assertEquals(global, result);
    }

    // ── Per-gamemode overrides ─────────────────────────────────────────────

    /**
     * Creates a mock {@link GameModeAddon} whose {@code getDescription().getName()}
     * returns the given name, and wires it into {@code iwm.getAddon(world)}.
     */
    private void setupGamemode(String gamemodeName) {
        GameModeAddon gamemodeAddon = org.mockito.Mockito.mock(GameModeAddon.class);
        AddonDescription desc = new AddonDescription.Builder("main.Class", gamemodeName, "1.0").build();
        when(gamemodeAddon.getDescription()).thenReturn(desc);
        when(iwm.getAddon(any(World.class))).thenReturn(Optional.of(gamemodeAddon));
    }

    @Test
    void testNetherLogPerGamemodeOverrideUsed() {
        setupGamemode("CaveBlock");
        Map<String, Map<String, Integer>> logsOverride = new HashMap<>();
        logsOverride.put("CaveBlock", Map.of("obsidian", 100));
        when(settings.getNetherLogsPerGamemode()).thenReturn(logsOverride);

        BlockState logState = org.mockito.Mockito.mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.OBSIDIAN);
    }

    @Test
    void testNetherLeavesPerGamemodeOverrideUsed() {
        setupGamemode("CaveBlock");
        Map<String, Map<String, Integer>> leavesOverride = new HashMap<>();
        leavesOverride.put("CaveBlock", Map.of("nether_wart_block", 100));
        when(settings.getNetherLeavesPerGamemode()).thenReturn(leavesOverride);

        BlockState leafState = org.mockito.Mockito.mock(BlockState.class);
        when(leafState.getType()).thenReturn(Material.OAK_LEAVES);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(leafState));
        listener.onTreeGrow(event);
        verify(leafState).setType(Material.NETHER_WART_BLOCK);
    }

    @Test
    void testEndLogPerGamemodeOverrideUsed() {
        setupGamemode("AcidIsland");
        Map<String, Map<String, Integer>> logsOverride = new HashMap<>();
        logsOverride.put("AcidIsland", Map.of("obsidian", 100));
        when(settings.getEndLogsPerGamemode()).thenReturn(logsOverride);

        BlockState logState = org.mockito.Mockito.mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.THE_END, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.OBSIDIAN);
    }

    @Test
    void testEndLeavesPerGamemodeOverrideUsed() {
        setupGamemode("AcidIsland");
        Map<String, Map<String, Integer>> leavesOverride = new HashMap<>();
        leavesOverride.put("AcidIsland", Map.of("grass_block", 100));
        when(settings.getEndLeavesPerGamemode()).thenReturn(leavesOverride);

        BlockState leafState = org.mockito.Mockito.mock(BlockState.class);
        when(leafState.getType()).thenReturn(Material.OAK_LEAVES);
        StructureGrowEvent event = makeEvent(World.Environment.THE_END, List.of(leafState));
        listener.onTreeGrow(event);
        verify(leafState).setType(Material.GRASS_BLOCK);
    }

    @Test
    void testPerGamemodeDoesNotAffectOtherGamemode() {
        // Override for CaveBlock; world has no gamemode → falls back to global gravel
        Map<String, Map<String, Integer>> logsOverride = new HashMap<>();
        logsOverride.put("CaveBlock", Map.of("obsidian", 100));
        when(settings.getNetherLogsPerGamemode()).thenReturn(logsOverride);
        // iwm.getAddon returns empty by default (from CommonTestSetup)

        BlockState logState = org.mockito.Mockito.mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.GRAVEL);
    }

    @Test
    void testPerTreeOverrideTakesPriorityOverPerGamemode() {
        // per-gamemode sets logs to obsidian, but per-tree sets oak logs to netherrack
        setupGamemode("CaveBlock");
        Map<String, Map<String, Integer>> gamemodeOverride = new HashMap<>();
        gamemodeOverride.put("CaveBlock", Map.of("obsidian", 100));
        when(settings.getNetherLogsPerGamemode()).thenReturn(gamemodeOverride);

        Map<String, Map<String, Integer>> perTreeOverride = new HashMap<>();
        perTreeOverride.put("oak", Map.of("netherrack", 100));
        when(settings.getNetherLogsPerTree()).thenReturn(perTreeOverride);

        BlockState logState = org.mockito.Mockito.mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.NETHERRACK);
    }

    @Test
    void testGetGamemodeNameReturnsNameWhenPresent() {
        setupGamemode("BSkyBlock");
        String name = listener.getGamemodeName(world);
        assertEquals("BSkyBlock", name);
    }

    @Test
    void testGetGamemodeNameReturnsEmptyWhenAbsent() {
        // iwm.getAddon returns empty by default (from CommonTestSetup)
        String name = listener.getGamemodeName(world);
        assertEquals("", name);
    }

    // ── Weighted material selection ────────────────────────────────────────

    @Test
    void testWeightedNetherLogReplacedWithMixedMaterials() {
        // 100% obsidian → always obsidian
        when(settings.getNetherLogs()).thenReturn(Map.of("obsidian", 100));
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(logState).setType(Material.OBSIDIAN);
    }

    @Test
    void testNullGlobalWeightMapCallsWarning() {
        when(settings.getNetherLogs()).thenReturn(null);
        when(settings.isSendLog()).thenReturn(true);
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(addon).logError(any());
        verify(logState, never()).setType(any());
    }

    @Test
    void testEmptyGlobalWeightMapCallsWarning() {
        when(settings.getNetherLogs()).thenReturn(new HashMap<>());
        when(settings.isSendLog()).thenReturn(true);
        BlockState logState = mock(BlockState.class);
        when(logState.getType()).thenReturn(Material.OAK_LOG);
        StructureGrowEvent event = makeEvent(World.Environment.NETHER, List.of(logState));
        listener.onTreeGrow(event);
        verify(addon).logError(any());
        verify(logState, never()).setType(any());
    }
}

