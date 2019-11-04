package world.bentobox.dimensionaltrees.events;

import java.util.List;

import org.bukkit.Material;
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

    private DimensionalTrees addon;

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
        // Verify settings
        if (endLeaves() == null || endLogs() == null || netherLeaves() == null || netherLogs() == null
                || Material.matchMaterial(endLeaves()) == null || Material.matchMaterial(endLogs()) == null
                || Material.matchMaterial(netherLeaves()) == null || Material.matchMaterial(netherLogs()) == null) {
            warning(e);
            return;
        }
        // Verify the sapling is in the settings list
        if (!treeTypes().contains(e.getLocation().getBlock().getType().name().replace("_SAPLING", "").toLowerCase())) {
            return;
        }
        try {
            if (e.getWorld().getEnvironment().equals(World.Environment.NETHER) && isNetherEnabled()) {
                // Modify everything!
                for (BlockState b : e.getBlocks()) {
                    if (Tag.LOGS.isTagged(b.getType())) {
                        b.setType(Material.matchMaterial(netherLogs()));
                    } else if (Tag.LEAVES.isTagged(b.getType())) {
                        b.setType(Material.matchMaterial(netherLeaves()));
                    }
                }
            } else if (e.getWorld().getEnvironment().equals(World.Environment.THE_END) && isEndEnabled()) {
                // Modify everything!
                for (BlockState b : e.getBlocks()) {
                    if (Tag.LOGS.isTagged(b.getType())) {
                        b.setType(Material.matchMaterial(endLogs()));
                    } else if (Tag.LEAVES.isTagged(b.getType())) {
                        b.setType(Material.matchMaterial(endLeaves()));
                    }
                }
            }
        } catch (Exception exception) {
            warning(e);
        }
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

}
