package world.bentobox.dimensionaltrees;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.dimensionaltrees.commands.AdminCommand;
import world.bentobox.dimensionaltrees.events.TreeGrowEvent;

public final class DimensionalTrees extends Addon {

    private static DimensionalTrees instance;

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
        registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadSettings() {
        settings = new Config<>(this, Settings.class).loadConfigObject();

        if (settings == null) {
            // Disable
            logError("DimensionalTrees settings could not load! Addon disabled.");
            setState(State.DISABLED);
        }

    }

    public Settings getSettings() {
        return settings;
    }

    public static DimensionalTrees getInstance() {
        return instance;
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new TreeGrowEvent(this, getPlugin()), getPlugin());
    }
}
