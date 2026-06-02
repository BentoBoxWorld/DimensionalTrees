package world.bentobox.dimensionaltrees.events;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.eclipse.jdt.annotation.NonNull;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.Util;
import world.bentobox.dimensionaltrees.DimensionalTrees;
import world.bentobox.dimensionaltrees.MaterialWeightedPicker;

public class TreeGrowEvent implements Listener {

    private final DimensionalTrees addon;

    public TreeGrowEvent(@NonNull DimensionalTrees addon) {
        this.addon = addon;
    }

    /**
     * Converts trees to other materials.
     *
     * @param e event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onTreeGrow(StructureGrowEvent e) {
        if (!isEnabled()) {
            return;
        }
        // Verify global settings are non-null and non-empty
        if (endLeaves() == null || endLeaves().isEmpty()
                || endLogs() == null || endLogs().isEmpty()
                || netherLeaves() == null || netherLeaves().isEmpty()
                || netherLogs() == null || netherLogs().isEmpty()) {
            warning(e);
            return;
        }
        // Get tree type from the sapling block (e.g. OAK_SAPLING -> "oak")
        String treeType = e.getLocation().getBlock().getType().name().replace("_SAPLING", "").toLowerCase(Locale.ENGLISH);
        // Verify the sapling is in the settings list
        if (!treeTypes().contains(treeType)) {
            return;
        }
        // Determine the gamemode name for the world so per-gamemode overrides can be applied
        String gamemodeName = getGamemodeName(e.getWorld());
        try {
            if (e.getWorld().getEnvironment().equals(World.Environment.NETHER) && isNetherEnabled()) {
                Map<String, Integer> logsWeights = resolveWeights(netherLogsPerTree(), treeType,
                        resolveWeights(netherLogsPerGamemode(), gamemodeName, netherLogs()));
                Map<String, Integer> leavesWeights = resolveWeights(netherLeavesPerTree(), treeType,
                        resolveWeights(netherLeavesPerGamemode(), gamemodeName, netherLeaves()));
                // Modify everything!
                for (BlockState b : e.getBlocks()) {
                    if (Tag.LOGS.isTagged(b.getType())) {
                        applyWeightedMaterial(b, logsWeights, e);
                    } else if (Tag.LEAVES.isTagged(b.getType())) {
                        applyWeightedMaterial(b, leavesWeights, e);
                    }
                }
            } else if (e.getWorld().getEnvironment().equals(World.Environment.THE_END) && isEndEnabled()) {
                Map<String, Integer> logsWeights = resolveWeights(endLogsPerTree(), treeType,
                        resolveWeights(endLogsPerGamemode(), gamemodeName, endLogs()));
                Map<String, Integer> leavesWeights = resolveWeights(endLeavesPerTree(), treeType,
                        resolveWeights(endLeavesPerGamemode(), gamemodeName, endLeaves()));
                // Modify everything!
                for (BlockState b : e.getBlocks()) {
                    if (Tag.LOGS.isTagged(b.getType())) {
                        applyWeightedMaterial(b, logsWeights, e);
                    } else if (Tag.LEAVES.isTagged(b.getType())) {
                        applyWeightedMaterial(b, leavesWeights, e);
                    }
                }
            }
        } catch (Exception exception) {
            warning(e);
        }
    }

    /**
     * Picks a random material from {@code weights} and sets the block to that material.
     * If the picked name is not a valid registry entry the block is left unchanged.
     *
     * @param b       block state to modify
     * @param weights weighted material map
     * @param e       the grow event (used for warning messages)
     */
    private void applyWeightedMaterial(BlockState b, Map<String, Integer> weights, StructureGrowEvent e) {
        String materialName = MaterialWeightedPicker.pickRandom(weights);
        Material mat = Registry.MATERIAL.get(NamespacedKey.minecraft(materialName.toLowerCase(Locale.ENGLISH)));
        if (mat != null) {
            b.setType(mat);
        } else {
            warning(e);
        }
    }

    /**
     * Resolves the weight map to use for a given key. If the per-override map contains
     * a non-empty weight map for {@code key}, that map is returned; otherwise the
     * {@code globalDefault} is returned.
     *
     * @param perOverrideMap map of key → weight-map override (may be null)
     * @param key            the lookup key (tree type or gamemode name)
     * @param globalDefault  the global fallback weight map
     * @return the resolved weight map
     */
    Map<String, Integer> resolveWeights(Map<String, Map<String, Integer>> perOverrideMap,
            String key, Map<String, Integer> globalDefault) {
        if (perOverrideMap != null) {
            Map<String, Integer> override = perOverrideMap.get(key);
            if (override != null && !override.isEmpty()) {
                return override;
            }
        }
        return globalDefault;
    }


    public void warning(StructureGrowEvent e) {
        if (e.isFromBonemeal() && e.getPlayer() != null) {
            User.getInstance(e.getPlayer()).sendMessage("dimensionaltrees.invalid-material");
        }
        if (sendLog()) {
            addon.logError("Can't grow the tree. A invalid material has been detected.\n" +
                    "Tree location: " + e.getWorld().getName() + " " + Util.xyz(e.getLocation().toVector()) +
                    "\nDimension= " + Util.prettifyText(e.getWorld().getEnvironment().name()));
        }
    }

    private Map<String, Integer> endLeaves() {
        return addon.getSettings().getEndLeaves();
    }

    private Map<String, Integer> endLogs() {
        return addon.getSettings().getEndLogs();
    }

    private Map<String, Integer> netherLeaves() {
        return addon.getSettings().getNetherLeaves();
    }

    private Map<String, Integer> netherLogs() {
        return addon.getSettings().getNetherLogs();
    }

    private boolean sendLog() {
        return addon.getSettings().isSendLog();
    }

    private List<String> treeTypes() {
        return addon.getSettings().getTreeTypes();
    }

    private boolean isEnabled() {
        return addon.getSettings().isEnabled();
    }

    private boolean isEndEnabled() {
        return addon.getSettings().isEndEnabled();
    }

    private boolean isNetherEnabled() {
        return addon.getSettings().isNetherEnabled();
    }

    private Map<String, Map<String, Integer>> endLeavesPerTree() {
        return addon.getSettings().getEndLeavesPerTree();
    }

    private Map<String, Map<String, Integer>> endLogsPerTree() {
        return addon.getSettings().getEndLogsPerTree();
    }

    private Map<String, Map<String, Integer>> netherLeavesPerTree() {
        return addon.getSettings().getNetherLeavesPerTree();
    }

    private Map<String, Map<String, Integer>> netherLogsPerTree() {
        return addon.getSettings().getNetherLogsPerTree();
    }

    /**
     * Returns the gamemode addon name for the given world, or an empty string if the
     * world is not associated with any gamemode addon.
     *
     * @param world the world to look up
     * @return the gamemode addon name, or {@code ""} if not found
     */
    String getGamemodeName(World world) {
        return addon.getPlugin().getIWM().getAddon(world)
                .map(a -> a.getDescription().getName())
                .orElse("");
    }

    private Map<String, Map<String, Integer>> endLeavesPerGamemode() {
        return addon.getSettings().getEndLeavesPerGamemode();
    }

    private Map<String, Map<String, Integer>> endLogsPerGamemode() {
        return addon.getSettings().getEndLogsPerGamemode();
    }

    private Map<String, Map<String, Integer>> netherLeavesPerGamemode() {
        return addon.getSettings().getNetherLeavesPerGamemode();
    }

    private Map<String, Map<String, Integer>> netherLogsPerGamemode() {
        return addon.getSettings().getNetherLogsPerGamemode();
    }

}

