package world.bentobox.dimensionaltrees.commands;

import java.util.List;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.commands.admin.AdminReloadCommand;
import world.bentobox.bentobox.api.user.User;

public class DTReloadCommand extends AdminReloadCommand {

    public DTReloadCommand(CompositeCommand adminCommand) {
        super(adminCommand);
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        getAddon().onReload();
        getPlugin().reloadConfig();
        user.sendMessage("dimensionaltrees.reloaded");
        return true;
    }

}
