package duke.commands;
import duke.utils.*;
import duke.tasks.*;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class AddCommand extends Command {
    private String type;
    private String fullCommand;

    public AddCommand(String type, String fullCommand) {
        this.type = type;
        this.fullCommand = fullCommand;
    }

    /**
     * Creates a new task and adds it to the list.
     *
     * @param storage
     * @param tasks
     */
    public void execute(Storage storage, ArrayList<Task> tasks) {
        String[] words = fullCommand.split(" ");

        try {
            int startIndex;
            String description;
            Task newTask = new Task("");
            switch (type) {
                case "todo":
                    try {
                        if (words.length == 1) {
                            throw (new DukeException("☹ OOPS!!! The description of a todo cannot be empty."));
                        }
                        startIndex = 5;
                        description = fullCommand.substring(startIndex);
                        newTask = new ToDo(description);
                        tasks.add(newTask);
                        break;
                    } catch (DukeException emptyDescription) {
                        System.out.println(emptyDescription.getMessage());
                        Ui.printLine();
                        return;
                    }


                case "deadline":
                    startIndex = 9;
                    int slashIndex = fullCommand.indexOf("/by");
                    description = fullCommand.substring(startIndex, slashIndex-1);
                    String by = fullCommand.substring(slashIndex + 4);

                    try {
                        newTask = new Deadline(description, by);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format");
                        Ui.printLine();
                        return;
                    }

                    tasks.add(newTask);
                    break;

                case "event":
                    startIndex = 6;
                    int fromIndex = fullCommand.indexOf("/from");
                    int toIndex = fullCommand.indexOf("/to");

                    assert toIndex > fromIndex : "from date should be written first before to date";
                    description = fullCommand.substring(startIndex, fromIndex-1);
                    String start = fullCommand.substring(fromIndex+6, toIndex-1);
                    String end = fullCommand.substring(toIndex+4);

                    try {
                        newTask = new Event(description, start, end);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format");
                        Ui.printLine();
                        return;
                    }
                    tasks.add(newTask);
                    break;
            }

            System.out.println("Got it. I've added this task:");
            System.out.println(newTask);
            Ui.printLine();
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Invalid duke.command");
            Ui.printLine();
        }
    }
}

