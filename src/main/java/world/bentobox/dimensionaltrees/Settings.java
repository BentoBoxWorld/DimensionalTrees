package world.bentobox.dimensionaltrees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    @ConfigComment("The block that will be the leaves on the End")
    @ConfigEntry(path = "dimensionaltrees.blocks.end.leaves")
    private String endLeaves = "end_stone";

    @ConfigComment("")
    @ConfigComment("The block that will be the logs on the End")
    @ConfigEntry(path = "dimensionaltrees.blocks.end.logs")
    private String endLogs = "purpur_block";

    @ConfigComment("")
    @ConfigComment("The block that will be the leaves on the Nether")
    @ConfigEntry(path = "dimensionaltrees.blocks.nether.leaves")
    private String netherLeaves = "glowstone";

    @ConfigComment("")
    @ConfigComment("The block that will be the logs on the Nether")
    @ConfigEntry(path = "dimensionaltrees.blocks.nether.logs")
    private String netherLogs = "gravel";

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

    public void setEndLeaves(String end_leaves) {
        this.endLeaves = end_leaves;
    }

    public void setEndLogs(String end_logs) {
        this.endLogs = end_logs;
    }

    public void setNetherLeaves(String nether_leaves) {
        this.netherLeaves = nether_leaves;
    }

    public void setNetherLogs(String nether_logs) {
        this.netherLogs = nether_logs;
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

    public String getEndLeaves() {
        return endLeaves;
    }

    public String getEndLogs() {
        return endLogs;
    }

    public String getNetherLeaves() {
        return netherLeaves;
    }

    public String getNetherLogs() {
        return netherLogs;
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
