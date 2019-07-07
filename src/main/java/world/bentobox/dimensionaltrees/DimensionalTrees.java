package world.bentobox.dimensionaltrees;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.dimensionaltrees.commands.AdminCommand;
import world.bentobox.dimensionaltrees.events.TreeGrowEvent;

public final class DimensionalTrees extends Addon {

    private Settings settings;

    @Override
    public void onLoad() {
        // Save the default config from config.yml
        saveDefaultConfig();
        // Load settings from config.yml. This will check if there are any issues with it too.
        loadSettings();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        new AdminCommand(this);
        // Register listener
        registerListener(new TreeGrowEvent(this));
    }

    @Override
    public void onDisable() {
        // Nothing to do here
    }

    private void loadSettings() {
        settings = new Config<>(this, Settings.class).loadConfigObject();

        if (settings == null) {
            // Disable
            logError("DimensionalTrees settings could not load! Addon disabled.");
            setState(State.DISABLED);
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
