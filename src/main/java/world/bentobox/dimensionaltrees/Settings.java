package world.bentobox.dimensionaltrees;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.StoreAt;
import world.bentobox.bentobox.database.objects.DataObject;

import java.util.*;

@StoreAt(filename="config.yml", path="addons/DimensionalTrees") // Explicitly call out what name this should have.
@ConfigComment("DimensionalTrees Configuration [version]")
public class Settings implements DataObject {

    /* Commands */
    @ConfigComment("")
    @ConfigComment("Main command to DimensionalTrees [Admins]")
    @ConfigEntry(path = "dimensionaltrees.commands.admin", needsReset = true)
    private String adminCommand = "dtrees dimensionaltrees dt";


    /* Blocks */
    @ConfigComment("")
    @ConfigComment("The block that will be the leaves on the End")
    @ConfigEntry(path = "dimensionaltrees.blocks.end.leaves")
    private String end_leaves = "end_stone";

    @ConfigComment("")
    @ConfigComment("The block that will be the logs on the End")
    @ConfigEntry(path = "dimensionaltrees.blocks.end.logs")
    private String end_logs = "purpur_block";

    @ConfigComment("")
    @ConfigComment("The block that will be the leaves on the Nether")
    @ConfigEntry(path = "dimensionaltrees.blocks.nether.leaves")
    private String nether_leaves = "glowstone";

    @ConfigComment("")
    @ConfigComment("The block that will be the logs on the Nether")
    @ConfigEntry(path = "dimensionaltrees.blocks.nether.logs")
    private String nether_logs = "gravel";


    /* Messages */
    @ConfigComment("")
    @ConfigComment("Warning to invalid blocks on config file")
    @ConfigEntry(path = "dimensionaltrees.messages.invalid")
    private String invalidMaterial = "§cCan't grow the tree. A invalid material has been detected.";


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
    private boolean end_enabled = true;

    @ConfigComment("")
    @ConfigComment("Enable/disable the Nether Trees.")
    @ConfigEntry(path = "dimensionaltrees.options.nether_trees")
    private boolean nether_enabled = true;

    @ConfigComment("")
    @ConfigComment("List of the saplings/trees that will be transformed.")
    @ConfigComment("Add a # before to disable and remove the # to enable.")
    @ConfigComment("Like the default settings.")
    @ConfigEntry(path = "dimensionaltrees.options.tree_types")
    private List<String> treeTypes = new ArrayList<String>(Arrays.asList("oak", "acacia", "birch"));



    private String uniqueId = "config";

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }


    /* Set Config */
    public void setAdminCommand(String adminCommand) {
        this.adminCommand = adminCommand;
    }

    public void setEnd_leaves(String end_leaves) {
        this.end_leaves = end_leaves;
    }

    public void setEnd_logs(String end_logs) {
        this.end_logs = end_logs;
    }

    public void setNether_leaves(String nether_leaves) {
        this.nether_leaves = nether_leaves;
    }

    public void setNether_logs(String nether_logs) {
        this.nether_logs = nether_logs;
    }

    public void setInvalidMaterial(String invalidMaterial) {
        this.invalidMaterial = invalidMaterial;
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

    public void setEnd_enabled(boolean end_enabled) {
        this.end_enabled = end_enabled;
    }

    public void setNether_enabled(boolean nether_enabled) {
        this.nether_enabled = nether_enabled;
    }


    /* Get Config */
    public String getAdminCommand() {
        return adminCommand;
    }

    public String getEnd_leaves() {
        return end_leaves;
    }

    public String getEnd_logs() {
        return end_logs;
    }

    public String getNether_leaves() {
        return nether_leaves;
    }

    public String getNether_logs() {
        return nether_logs;
    }

    public String getInvalidMaterial() {
        return invalidMaterial;
    }

    public boolean isSendLog() {
        return sendLog;
    }

    public List<String> getTreeTypes() {
        return treeTypes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isEnd_enabled() {
        return end_enabled;
    }

    public boolean isNether_enabled() {
        return nether_enabled;
    }
}
