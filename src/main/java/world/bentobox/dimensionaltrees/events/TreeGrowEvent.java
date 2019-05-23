package world.bentobox.dimensionaltrees.events;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.eclipse.jdt.annotation.NonNull;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.dimensionaltrees.DimensionalTrees;

import java.util.List;

public class TreeGrowEvent implements Listener {

    private DimensionalTrees addon;
    private BentoBox bentoBox;

    public TreeGrowEvent(@NonNull DimensionalTrees addon, BentoBox bentoBox) {
        this.addon = addon;
        this.bentoBox = bentoBox;
    }

    /**
     * Converts trees to other materials.
     *
     * @param e event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onTreeGrow(StructureGrowEvent e) {

        addon.log("" + e.getWorld().getBlockAt(e.getLocation()).getType());
        if (!isEnabled()) {
            return;
        }

        // Don't do anything if we're not in the right place.
        if (!bentoBox.getIWM().inWorld(e.getWorld())) {
            addon.log("world");
            return;
        }

        if (!saplingVerification(e.getWorld().getBlockAt(e.getLocation()).getType())) {
            addon.log("sapling");
            return;
        }

        if (endLeaves() == null || endLogs() == null || netherLeaves() == null || netherLogs() == null) {
            e.setCancelled(true);
            double x = e.getLocation().getBlockX();
            double y = e.getLocation().getBlockY();
            double z = e.getLocation().getBlockZ();
            World dimension = e.getLocation().getWorld();

            if (e.isFromBonemeal() && e.getPlayer() != null) e.getPlayer().sendMessage(errorMessage());
            else if (sendLog()) addon.logError("Can't grow the tree. A invalid material has been detected.\n" +
                    "Tree location: " +
                    "\n     x= " + x +
                    "\n     y= " + y +
                    "\n     z= " + z +
                    "\n     Dimension= " + dimension);
            return;
        }

        if (Material.getMaterial(ENDLEAVES()) == null || Material.getMaterial(ENDLOGS()) == null || Material.getMaterial(NETHERLEAVES()) == null || Material.getMaterial(NETHERLOGS()) == null) {
            e.setCancelled(true);
            double x = e.getLocation().getBlockX();
            double y = e.getLocation().getBlockY();
            double z = e.getLocation().getBlockZ();
            World dimension = e.getLocation().getWorld();

            if (e.isFromBonemeal() && e.getPlayer() != null) e.getPlayer().sendMessage(errorMessage());
            else if (sendLog()) addon.logError("Can't grow the tree. A invalid material has been detected.\n" +
                    "Tree location: " +
                    "\n     x= " + x +
                    "\n     y= " + y +
                    "\n     z= " + z +
                    "\n     Dimension= " + dimension);
            return;
        }

        try {
            if (e.getWorld().getEnvironment().equals(World.Environment.NETHER) && isNetherEnabled()) {
                // Modify everything!
                for (BlockState b : e.getBlocks()) {
                    if (Tag.LOGS.isTagged(b.getType())) {
                        b.setType(Material.getMaterial(NETHERLOGS()));
                    } else if (Tag.LEAVES.isTagged(b.getType())) {
                        b.setType(Material.getMaterial(NETHERLEAVES()));
                    }
                }
            } else if (e.getWorld().getEnvironment().equals(World.Environment.THE_END) && isEndEnabled()) {
                // Modify everything!
                for (BlockState b : e.getBlocks()) {
                    if (Tag.LOGS.isTagged(b.getType())) {
                        b.setType(Material.getMaterial(ENDLOGS()));
                    } else if (Tag.LEAVES.isTagged(b.getType())) {
                        b.setType(Material.getMaterial(ENDLEAVES()));
                    }
                }
            }
        } catch (Exception exception) {
            addon.logError("A Invalid material has been detected. Can't transform the tree.");
        }
    }

    private boolean saplingVerification(Material sapling) {
        addon.log("List info:\n" + "Size: " + treeTypes().size() + "\nList contents: " + treeTypes());
        Material oak = Material.OAK_SAPLING;
        Material spruce = Material.SPRUCE_SAPLING;
        Material dark_oak = Material.DARK_OAK_SAPLING;
        Material birch = Material.BIRCH_SAPLING;
        Material jungle = Material.JUNGLE_SAPLING;
        Material acacia = Material.ACACIA_SAPLING;
        String oakString = "oak"; String spruceString = "spruce"; String dark_oakString = "dark_oak"; String birchString = "birch"; String jungleString = "jungle"; String acaciaString = "acacia";
        if (sapling == oak || sapling == spruce || sapling == dark_oak || sapling == birch || sapling == jungle || sapling == acacia)
        {
            addon.log("Passed");
            switch (sapling)
            {
                case OAK_SAPLING:
                    addon.log("Passed again");
                    if (treeTypes().contains(oakString))
                        return true;
                    else addon.log("foi  no oakString");
                    break;

                case SPRUCE_SAPLING:
                    if (treeTypes().contains(spruceString))
                        return true;
                    break;

                case DARK_OAK_SAPLING:
                    if (treeTypes().contains(dark_oakString))
                        return true;
                    break;

                case BIRCH_SAPLING:
                    if (treeTypes().contains(birchString))
                        return true;
                    break;

                case JUNGLE_SAPLING:
                    if (treeTypes().contains(jungleString))
                        return true;
                    break;

                case ACACIA_SAPLING:
                    if (treeTypes().contains(acaciaString))
                        return true;
                    break;
            }
        }
        addon.log("Hmmm...." + treeTypes().size() + "\n" + treeTypes());
        return false;
    }

    private String endLeaves() {
        return addon.getSettings().getEnd_leaves();
    }

    private String endLogs() {
        return addon.getSettings().getEnd_logs();
    }

    private String netherLeaves() {
        return addon.getSettings().getNether_leaves();
    }

    private String netherLogs() {
        return addon.getSettings().getNether_logs();
    }

    private String errorMessage() {
        return addon.getSettings().getInvalidMaterial();
    }

    private String ENDLEAVES() {
        return endLeaves().toUpperCase();
    }

    private String ENDLOGS() {
        return endLogs().toUpperCase();
    }

    private String NETHERLEAVES() {
        return netherLeaves().toUpperCase();
    }

    private String NETHERLOGS() {
        return netherLogs().toUpperCase();
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
        return addon.getSettings().isEnd_enabled();
    }

    private boolean isNetherEnabled() {
        return addon.getSettings().isNether_enabled();
    }

}
