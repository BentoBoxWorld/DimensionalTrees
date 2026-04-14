package world.bentobox.dimensionaltrees;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.dimensionaltrees.commands.AdminCommand;
import world.bentobox.dimensionaltrees.events.TreeGrowEvent;

public final class DimensionalTrees extends Addon {

    private Settings settings;

    @Override
    public void onLoad() {
        // Migrate config from 1.8.0 (string block values) to 1.9.0 (weighted maps)
        migrateConfig();
        // Save the default config from config.yml
        saveDefaultConfig();
        // Load settings from config.yml. This will check if there are any issues with it too.
        loadSettings();
        // Register command
        new AdminCommand(this);
    }

    @Override
    public void onEnable() {
        // Register listener
        registerListener(new TreeGrowEvent(this));
    }

    @Override
    public void onDisable() {
        // Nothing to do here
    }

    /**
     * Migrates an on-disk config.yml written by DimensionalTrees 1.8.0 to the 1.9.0 format.
     * In 1.8.0, block entries were plain strings (e.g. {@code leaves: end_stone}).
     * In 1.9.0, they are weighted maps (e.g. {@code leaves: {end_stone: 100}}).
     * This method reads the file with a plain YamlConfiguration, converts any String values
     * at the four block paths to single-entry weight maps, and saves the file back before
     * BentoBox's Config loader runs.
     */
    private void migrateConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) return;

        YamlConfiguration raw = YamlConfiguration.loadConfiguration(configFile);
        boolean changed = false;

        String[] blockKeys = {
            "dimensionaltrees.blocks.end.leaves",
            "dimensionaltrees.blocks.end.logs",
            "dimensionaltrees.blocks.nether.leaves",
            "dimensionaltrees.blocks.nether.logs"
        };

        for (String key : blockKeys) {
            Object value = raw.get(key);
            if (value instanceof String materialName) {
                raw.set(key, Map.of(materialName.toLowerCase(), 100));
                changed = true;
            }
        }

        if (changed) {
            try {
                raw.save(configFile);
                getLogger().info("Migrated config.yml from 1.8.0 format to 1.9.0 format.");
            } catch (IOException e) {
                logError("Failed to save migrated config.yml: " + e.getMessage());
            }
        }
    }

    private void loadSettings() {
        settings = new Config<>(this, Settings.class).loadConfigObject();

        if (settings == null) {
            // Disable
            logError("DimensionalTrees settings could not load! Addon disabled.");
            setState(State.DISABLED);
            return;
        }

        // Validate global weight maps and warn when totals are not 100%
        validateWeights("end.leaves", settings.getEndLeaves());
        validateWeights("end.logs", settings.getEndLogs());
        validateWeights("nether.leaves", settings.getNetherLeaves());
        validateWeights("nether.logs", settings.getNetherLogs());

        // Validate per-tree overrides
        validateOverrideMap("end.per-tree.leaves", settings.getEndLeavesPerTree());
        validateOverrideMap("end.per-tree.logs", settings.getEndLogsPerTree());
        validateOverrideMap("nether.per-tree.leaves", settings.getNetherLeavesPerTree());
        validateOverrideMap("nether.per-tree.logs", settings.getNetherLogsPerTree());

        // Validate per-gamemode overrides
        validateOverrideMap("end.per-gamemode.leaves", settings.getEndLeavesPerGamemode());
        validateOverrideMap("end.per-gamemode.logs", settings.getEndLogsPerGamemode());
        validateOverrideMap("nether.per-gamemode.leaves", settings.getNetherLeavesPerGamemode());
        validateOverrideMap("nether.per-gamemode.logs", settings.getNetherLogsPerGamemode());
    }

    /**
     * Validates a single weight map and logs a warning if its values do not sum to 100.
     *
     * @param fieldName human-readable config path used in the warning message
     * @param weights   the weight map to validate; null or empty maps are silently ignored
     */
    void validateWeights(String fieldName, Map<String, Integer> weights) {
        if (weights == null || weights.isEmpty()) return;
        int total = weights.values().stream().mapToInt(i -> Math.max(i, 0)).sum();
        if (total < 100) {
            logWarning("Weight map 'dimensionaltrees.blocks." + fieldName + "' sums to " + total
                    + "% (< 100%). The remaining " + (100 - total) + "% will be replaced with AIR.");
        } else if (total > 100) {
            logWarning("Weight map 'dimensionaltrees.blocks." + fieldName + "' sums to " + total
                    + "% (> 100%). Weights will be scaled proportionally to 100%.");
        }
    }

    /**
     * Validates every per-key weight map inside an override map.
     *
     * @param basePath   the config path prefix used in warning messages
     * @param overrideMap the outer map (tree-type or gamemode → weight map)
     */
    private void validateOverrideMap(String basePath, Map<String, Map<String, Integer>> overrideMap) {
        if (overrideMap == null || overrideMap.isEmpty()) return;
        for (Map.Entry<String, Map<String, Integer>> entry : overrideMap.entrySet()) {
            validateWeights(basePath + "." + entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void onReload() {
        loadSettings();
        getLogger().info("Reloaded.");
    }

    public Settings getSettings() {
        return settings;
    }
}

