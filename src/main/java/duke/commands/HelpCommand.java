package duke.commands;

import duke.utils.*;
import duke.tasks.*;
import java.util.ArrayList;

public class HelpCommand extends Command {

    /**
     * Lists out the available commands.
     *
     * @param storage
     * @param tasks
     */
    @Override
    public void execute(Storage storage, ArrayList<Task> tasks) {
        Ui.printHelp();
    }
}
