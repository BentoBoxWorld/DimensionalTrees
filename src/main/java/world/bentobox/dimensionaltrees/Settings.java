package world.bentobox.dimensionaltrees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;

@StoreAt(filename="config.yml", path="addons/DimensionalTrees") // Explicitly call out what name this should have.
@ConfigComment("DimensionalTrees Configuration [version]")
public class Settings implements ConfigObject {

    /* Commands */
    @ConfigComment("")
    @ConfigComment("Main command to DimensionalTrees [Admins]")
    @ConfigEntry(path = "dimensionaltrees.commands.admin", needsReset = true)
    private String adminCommand = "dtrees dimensionaltrees dt";


    /* Blocks */
    @ConfigComment("")
    @ConfigComment("The blocks that will be the leaves in the End.")
    @ConfigComment("Each entry is a material name with a weight (percentage, 1-100).")
    @ConfigComment("Weights > 100 in total are scaled proportionally.")
    @ConfigComment("Weights < 100 in total fill the remainder with AIR.")
    @ConfigEntry(path = "dimensionaltrees.blocks.end.leaves")
    private Map<String, Integer> endLeaves = new LinkedHashMap<>(Map.of("end_stone", 100));

    @ConfigComment("")
    @ConfigComment("The blocks that will be the logs in the End.")
    @ConfigComment("Each entry is a material name with a weight (percentage, 1-100).")
    @ConfigEntry(path = "dimensionaltrees.blocks.end.logs")
    private Map<String, Integer> endLogs = new LinkedHashMap<>(Map.of("purpur_block", 100));

    @ConfigComment("")
    @ConfigComment("Per-tree-type leaf overrides for the End dimension.")
    @ConfigComment("Keys are tree type names (oak, spruce, birch, jungle, acacia, dark_oak).")
    @ConfigComment("Values are weighted material maps (material: weight).")
    @ConfigComment("If a tree type is not listed here, the global 'end.leaves' value is used.")
    @ConfigEntry(path = "dimensionaltrees.blocks.end.per-tree.leaves")
    private Map<String, Map<String, Integer>> endLeavesPerTree = new HashMap<>();

    @ConfigComment("")
    @ConfigComment("Per-tree-type log overrides for the End dimension.")
    @ConfigComment("Keys are tree type names (oak, spruce, birch, jungle, acacia, dark_oak).")
    @ConfigComment("Values are weighted material maps (material: weight).")
    @ConfigComment("If a tree type is not listed here, the global 'end.logs' value is used.")
    @ConfigEntry(path = "dimensionaltrees.blocks.end.per-tree.logs")
    private Map<String, Map<String, Integer>> endLogsPerTree = new HashMap<>();

    @ConfigComment("")
    @ConfigComment("Per-gamemode leaf overrides for the End dimension.")
    @ConfigComment("Keys are gamemode addon names (e.g., BSkyBlock, CaveBlock, AcidIsland).")
    @ConfigComment("Values are weighted material maps (material: weight).")
    @ConfigComment("If a gamemode is not listed here, the global 'end.leaves' value is used.")
    @ConfigEntry(path = "dimensionaltrees.blocks.end.per-gamemode.leaves")
    private Map<String, Map<String, Integer>> endLeavesPerGamemode = new HashMap<>();

    @ConfigComment("")
    @ConfigComment("Per-gamemode log overrides for the End dimension.")
    @ConfigComment("Keys are gamemode addon names (e.g., BSkyBlock, CaveBlock, AcidIsland).")
    @ConfigComment("Values are weighted material maps (material: weight).")
    @ConfigComment("If a gamemode is not listed here, the global 'end.logs' value is used.")
    @ConfigEntry(path = "dimensionaltrees.blocks.end.per-gamemode.logs")
    private Map<String, Map<String, Integer>> endLogsPerGamemode = new HashMap<>();

    @ConfigComment("")
    @ConfigComment("The blocks that will be the leaves in the Nether.")
    @ConfigComment("Each entry is a material name with a weight (percentage, 1-100).")
    @ConfigComment("Weights > 100 in total are scaled proportionally.")
    @ConfigComment("Weights < 100 in total fill the remainder with AIR.")
    @ConfigEntry(path = "dimensionaltrees.blocks.nether.leaves")
    private Map<String, Integer> netherLeaves = new LinkedHashMap<>(Map.of("glowstone", 100));

    @ConfigComment("")
    @ConfigComment("The blocks that will be the logs in the Nether.")
    @ConfigComment("Each entry is a material name with a weight (percentage, 1-100).")
    @ConfigEntry(path = "dimensionaltrees.blocks.nether.logs")
    private Map<String, Integer> netherLogs = new LinkedHashMap<>(Map.of("gravel", 100));

    @ConfigComment("")
    @ConfigComment("Per-tree-type leaf overrides for the Nether dimension.")
    @ConfigComment("Keys are tree type names (oak, spruce, birch, jungle, acacia, dark_oak).")
    @ConfigComment("Values are weighted material maps (material: weight).")
    @ConfigComment("If a tree type is not listed here, the global 'nether.leaves' value is used.")
    @ConfigEntry(path = "dimensionaltrees.blocks.nether.per-tree.leaves")
    private Map<String, Map<String, Integer>> netherLeavesPerTree = new HashMap<>();

    @ConfigComment("")
    @ConfigComment("Per-tree-type log overrides for the Nether dimension.")
    @ConfigComment("Keys are tree type names (oak, spruce, birch, jungle, acacia, dark_oak).")
    @ConfigComment("Values are weighted material maps (material: weight).")
    @ConfigComment("If a tree type is not listed here, the global 'nether.logs' value is used.")
    @ConfigEntry(path = "dimensionaltrees.blocks.nether.per-tree.logs")
    private Map<String, Map<String, Integer>> netherLogsPerTree = new HashMap<>();

    @ConfigComment("")
    @ConfigComment("Per-gamemode leaf overrides for the Nether dimension.")
    @ConfigComment("Keys are gamemode addon names (e.g., BSkyBlock, CaveBlock, AcidIsland).")
    @ConfigComment("Values are weighted material maps (material: weight).")
    @ConfigComment("If a gamemode is not listed here, the global 'nether.leaves' value is used.")
    @ConfigEntry(path = "dimensionaltrees.blocks.nether.per-gamemode.leaves")
    private Map<String, Map<String, Integer>> netherLeavesPerGamemode = new HashMap<>();

    @ConfigComment("")
    @ConfigComment("Per-gamemode log overrides for the Nether dimension.")
    @ConfigComment("Keys are gamemode addon names (e.g., BSkyBlock, CaveBlock, AcidIsland).")
    @ConfigComment("Values are weighted material maps (material: weight).")
    @ConfigComment("If a gamemode is not listed here, the global 'nether.logs' value is used.")
    @ConfigEntry(path = "dimensionaltrees.blocks.nether.per-gamemode.logs")
    private Map<String, Map<String, Integer>> netherLogsPerGamemode = new HashMap<>();

    /* Options */
    @ConfigComment("")
    @ConfigComment("Defines if send debug messages to the log.")
    @ConfigComment("For example the sapling position if have a invalid block.")
    @ConfigEntry(path = "dimensionaltrees.options.sendlog")
    private boolean sendLog = true;

    @ConfigComment("")
    @ConfigComment("Enable/disable the addon.")
    @ConfigEntry(path = "dimensionaltrees.options.enable_addon")
    private boolean enabled = true;

    @ConfigComment("")
    @ConfigComment("Enable/disable the End trees.")
    @ConfigEntry(path = "dimensionaltrees.options.end_trees")
    private boolean endEnabled = true;

    @ConfigComment("")
    @ConfigComment("Enable/disable the Nether Trees.")
    @ConfigEntry(path = "dimensionaltrees.options.nether_trees")
    private boolean netherEnabled = true;

    @ConfigComment("")
    @ConfigComment("List of the saplings/trees that will be transformed.")
    @ConfigComment("Add a # before to disable and remove the # to enable.")
    @ConfigComment("Like the default settings.")
    @ConfigEntry(path = "dimensionaltrees.options.tree_types")
    private List<String> treeTypes = new ArrayList<>(Arrays.asList("oak", "acacia", "birch"));


    /* Set Config */
    public void setAdminCommand(String adminCommand) {
        this.adminCommand = adminCommand;
    }

    public void setEndLeaves(Map<String, Integer> endLeaves) {
        this.endLeaves = endLeaves;
    }

    public void setEndLogs(Map<String, Integer> endLogs) {
        this.endLogs = endLogs;
    }

    public void setNetherLeaves(Map<String, Integer> netherLeaves) {
        this.netherLeaves = netherLeaves;
    }

    public void setNetherLogs(Map<String, Integer> netherLogs) {
        this.netherLogs = netherLogs;
    }

    public void setEndLeavesPerTree(Map<String, Map<String, Integer>> endLeavesPerTree) {
        this.endLeavesPerTree = endLeavesPerTree;
    }

    public void setEndLogsPerTree(Map<String, Map<String, Integer>> endLogsPerTree) {
        this.endLogsPerTree = endLogsPerTree;
    }

    public void setNetherLeavesPerTree(Map<String, Map<String, Integer>> netherLeavesPerTree) {
        this.netherLeavesPerTree = netherLeavesPerTree;
    }

    public void setNetherLogsPerTree(Map<String, Map<String, Integer>> netherLogsPerTree) {
        this.netherLogsPerTree = netherLogsPerTree;
    }

    public void setEndLeavesPerGamemode(Map<String, Map<String, Integer>> endLeavesPerGamemode) {
        this.endLeavesPerGamemode = endLeavesPerGamemode;
    }

    public void setEndLogsPerGamemode(Map<String, Map<String, Integer>> endLogsPerGamemode) {
        this.endLogsPerGamemode = endLogsPerGamemode;
    }

    public void setNetherLeavesPerGamemode(Map<String, Map<String, Integer>> netherLeavesPerGamemode) {
        this.netherLeavesPerGamemode = netherLeavesPerGamemode;
    }

    public void setNetherLogsPerGamemode(Map<String, Map<String, Integer>> netherLogsPerGamemode) {
        this.netherLogsPerGamemode = netherLogsPerGamemode;
    }

    public void setSendLog(boolean sendLog) {
        this.sendLog = sendLog;
    }

    public void setTreeTypes(List<String> treeTypes) {
        this.treeTypes = treeTypes;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEndEnabled(boolean end_enabled) {
        this.endEnabled = end_enabled;
    }

    public void setNetherEnabled(boolean nether_enabled) {
        this.netherEnabled = nether_enabled;
    }


    /* Get Config */
    public String getAdminCommand() {
        return adminCommand;
    }

    public Map<String, Integer> getEndLeaves() {
        return endLeaves;
    }

    public Map<String, Integer> getEndLogs() {
        return endLogs;
    }

    public Map<String, Integer> getNetherLeaves() {
        return netherLeaves;
    }

    public Map<String, Integer> getNetherLogs() {
        return netherLogs;
    }

    public Map<String, Map<String, Integer>> getEndLeavesPerTree() {
        return endLeavesPerTree;
    }

    public Map<String, Map<String, Integer>> getEndLogsPerTree() {
        return endLogsPerTree;
    }

    public Map<String, Map<String, Integer>> getNetherLeavesPerTree() {
        return netherLeavesPerTree;
    }

    public Map<String, Map<String, Integer>> getNetherLogsPerTree() {
        return netherLogsPerTree;
    }

    public Map<String, Map<String, Integer>> getEndLeavesPerGamemode() {
        return endLeavesPerGamemode;
    }

    public Map<String, Map<String, Integer>> getEndLogsPerGamemode() {
        return endLogsPerGamemode;
    }

    public Map<String, Map<String, Integer>> getNetherLeavesPerGamemode() {
        return netherLeavesPerGamemode;
    }

    public Map<String, Map<String, Integer>> getNetherLogsPerGamemode() {
        return netherLogsPerGamemode;
    }

    public boolean isSendLog() {
        return sendLog;
    }

    public List<String> getTreeTypes() {
        // Always return a lower case list
        return treeTypes.stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isEndEnabled() {
        return endEnabled;
    }

    public boolean isNetherEnabled() {
        return netherEnabled;
    }
}
