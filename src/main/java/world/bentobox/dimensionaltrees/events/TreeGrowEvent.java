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
        // Don't do anything if we're not in the right place.
        if (!addon.getPlugin().getIWM().inWorld(e.getWorld())) {
            return;
        }
        // Verify global settings
        if (endLeaves() == null || endLogs() == null || netherLeaves() == null || netherLogs() == null
                || Registry.MATERIAL.get(NamespacedKey.minecraft(endLeaves().toLowerCase(Locale.ENGLISH))) == null
                || Registry.MATERIAL.get(NamespacedKey.minecraft(endLogs().toLowerCase(Locale.ENGLISH))) == null
                || Registry.MATERIAL.get(NamespacedKey.minecraft(netherLeaves().toLowerCase(Locale.ENGLISH))) == null
                || Registry.MATERIAL.get(NamespacedKey.minecraft(netherLogs().toLowerCase(Locale.ENGLISH))) == null) {
            warning(e);
            return;
        }
        // Get tree type from the sapling block (e.g. OAK_SAPLING -> "oak")
        String treeType = e.getLocation().getBlock().getType().name().replace("_SAPLING", "").toLowerCase(Locale.ENGLISH);
        // Verify the sapling is in the settings list
        if (!treeTypes().contains(treeType)) {
            return;
        }
        try {
            if (e.getWorld().getEnvironment().equals(World.Environment.NETHER) && isNetherEnabled()) {
                String resolvedLogs = resolveMaterial(netherLogsPerTree(), treeType, netherLogs());
                String resolvedLeaves = resolveMaterial(netherLeavesPerTree(), treeType, netherLeaves());
                // Modify everything!
                for (BlockState b : e.getBlocks()) {
                    if (Tag.LOGS.isTagged(b.getType())) {
                        b.setType(Registry.MATERIAL.get(NamespacedKey.minecraft(resolvedLogs)));
                    } else if (Tag.LEAVES.isTagged(b.getType())) {
                        b.setType(Registry.MATERIAL.get(NamespacedKey.minecraft(resolvedLeaves)));
                    }
                }
            } else if (e.getWorld().getEnvironment().equals(World.Environment.THE_END) && isEndEnabled()) {
                String resolvedLogs = resolveMaterial(endLogsPerTree(), treeType, endLogs());
                String resolvedLeaves = resolveMaterial(endLeavesPerTree(), treeType, endLeaves());
                // Modify everything!
                for (BlockState b : e.getBlocks()) {
                    if (Tag.LOGS.isTagged(b.getType())) {
                        b.setType(Registry.MATERIAL.get(NamespacedKey.minecraft(resolvedLogs)));
                    } else if (Tag.LEAVES.isTagged(b.getType())) {
                        b.setType(Registry.MATERIAL.get(NamespacedKey.minecraft(resolvedLeaves)));
                    }
                }
            }
        } catch (Exception exception) {
            warning(e);
        }
    }

    /**
     * Resolves the material to use for a given tree type. If the per-tree map contains
     * a valid material for {@code treeType}, that material is returned; otherwise the
     * {@code globalDefault} is returned. Both values are lowercased before use.
     *
     * @param perTreeMap    map of tree-type → material override
     * @param treeType      the tree type key (e.g. "oak", "acacia")
     * @param globalDefault the global fallback material name
     * @return the resolved material name in lowercase
     */
    String resolveMaterial(Map<String, String> perTreeMap, String treeType, String globalDefault) {
        if (perTreeMap != null) {
            String override = perTreeMap.get(treeType);
            if (override != null) {
                String lower = override.toLowerCase(Locale.ENGLISH);
                if (Registry.MATERIAL.get(NamespacedKey.minecraft(lower)) != null) {
                    return lower;
                }
            }
        }
        return globalDefault.toLowerCase(Locale.ENGLISH);
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

    private String endLeaves() {
        return addon.getSettings().getEndLeaves();
    }

    private String endLogs() {
        return addon.getSettings().getEndLogs();
    }

    private String netherLeaves() {
        return addon.getSettings().getNetherLeaves();
    }

    private String netherLogs() {
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

    private Map<String, String> endLeavesPerTree() {
        return addon.getSettings().getEndLeavesPerTree();
    }

    private Map<String, String> endLogsPerTree() {
        return addon.getSettings().getEndLogsPerTree();
    }

    private Map<String, String> netherLeavesPerTree() {
        return addon.getSettings().getNetherLeavesPerTree();
    }

    private Map<String, String> netherLogsPerTree() {
        return addon.getSettings().getNetherLogsPerTree();
    }

}
