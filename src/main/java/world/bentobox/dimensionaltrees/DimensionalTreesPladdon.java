package world.bentobox.dimensionaltrees;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;

public class DimensionalTreesPladdon extends Pladdon {
    private Addon addon;

    @Override
    public Addon getAddon() {
        if (addon == null) {
            addon = new DimensionalTrees();
        }
        return addon;
    }
}
