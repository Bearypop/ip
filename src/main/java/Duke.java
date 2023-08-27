import javax.management.openmbean.OpenMBeanAttributeInfo;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;

public class Duke {
    public static void main(String[] args) {
        String name = "Beary";
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks;
        String filepath = "data/tasks.txt";

        try {
            tasks = readFile(filepath);
        } catch (FileNotFoundException e) {
            tasks = new ArrayList<>();
        }

        System.out.println(String.format("Hello! I'm %s\nWhat can I do for you?", name));
        printLine();

        while (true) {
            String command = scanner.nextLine();
            String taskMessage = command;
            printLine();

            // check for multiple words in command
            String[] words = command.split(" ");
            if (words.length > 1) {
                command = words[0];
            }

            if (command.equals("bye")) {
                try {
                    writeToFile(tasks, filepath);
                    System.out.println("Bye. Hope to see you again soon!");
                    printLine();
                    break;
                } catch (IOException e) {
                    System.out.println("Something went wrong: " + e.getMessage());
                }
            }

            if (command.equals("list")) {
                for (int i = 0; i < tasks.size(); i++) {
                    Task currentTask = tasks.get(i);
                    boolean done = currentTask.getDone();
                    System.out.println((i + 1) + "." + currentTask);
                }
                printLine();
                continue;
            }

            try {
                if (command.equals("mark")) {
                    int taskNumber = Integer.parseInt(words[1]);
                    tasks.get(taskNumber - 1).markDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println(tasks.get(taskNumber - 1));
                    printLine();
                    continue;
                }

                if (command.equals("unmark")) {
                    int taskNumber = Integer.parseInt(words[1]);
                    tasks.get(taskNumber - 1).markUndone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println(tasks.get(taskNumber - 1));
                    printLine();
                    continue;
                }

                if (command.equals("delete")) {
                    int taskNumber = Integer.parseInt(words[1]);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println(tasks.get(taskNumber - 1));
                    tasks.remove(taskNumber - 1);
                    printLine();
                    continue;
                }

            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                System.out.println("The selected task does not exist.");
                printLine();
                continue;
            }

            try {

            } catch (DateTimeParseException e) {

            }

            try {
                int startIndex;
                String description;
                Task newTask = new Task("");
                switch (command) {
                    case "todo":
                        try {
                            if (words.length == 1) {
                                throw (new DukeException("☹ OOPS!!! The description of a todo cannot be empty."));
                            }
                            startIndex = 5;
                            description = taskMessage.substring(startIndex);
                            newTask = new ToDo(description);
                            tasks.add(newTask);
                            break;
                        } catch (DukeException emptyDescription) {
                            System.out.println(emptyDescription.getMessage());
                            printLine();
                            continue;
                        }


                    case "deadline":
                        startIndex = 9;
                        int slashIndex = taskMessage.indexOf("/by");
                        description = taskMessage.substring(startIndex, slashIndex-1);
                        String by = taskMessage.substring(slashIndex + 4);

                        try {
                            newTask = new Deadline(description, by);
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date format");
                            printLine();
                            continue;
                        }

                        tasks.add(newTask);
                        break;

                    case "event":
                        startIndex = 6;
                        int fromIndex = taskMessage.indexOf("/from");
                        int toIndex = taskMessage.indexOf("/to");

                        description = taskMessage.substring(startIndex, fromIndex-1);
                        String start = taskMessage.substring(fromIndex+6, toIndex-1);
                        String end = taskMessage.substring(toIndex+4);

                        try {
                            newTask = new Event(description, start, end);
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date format");
                            printLine();
                            continue;
                        }
                        tasks.add(newTask);
                        break;

                    default:
                        try {
                            throw(new DukeException("☹ OOPS!!! I'm sorry, but I don't know what that means :-("));
                        } catch (DukeException invalidCommand) {
                            System.out.println(invalidCommand.getMessage());
                            printLine();
                            continue;
                        }
                }

                System.out.println("Got it. I've added this task:");
                System.out.println(newTask);
                printLine();
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("Invalid command");
                printLine();
            }
        }
    }


    public static Task constructTaskFromFile(String line) {
        String type = line.substring(1, 2);
        String doneString = line.substring(4, 5);
        String text = line.substring(7);

        String description;
        Task newTask = new Task("");

        switch (type) {
            case "T":
                description = text;
                newTask = new ToDo(text);
                break;

            case "D":
                int OpenBracketIndex = text.indexOf("(by: ");
                description = text.substring(0, OpenBracketIndex -1);
                String by = text.substring(OpenBracketIndex + 5, text.length()-1);
                newTask = new Deadline(description, by, true);
                break;

            case "E":
                int fromIndex = text.indexOf("(from: ");
                int toIndex = text.indexOf("to: ");

                description = text.substring(0, fromIndex-1);
                String from = text.substring(fromIndex+7, toIndex-1);
                String to = text.substring(toIndex+4, text.length()-1);
                newTask = new Event(description, from, to, true);
                break;
        }

        boolean done = doneString.equals("X");
        newTask.setDone(done);
        return newTask;
    }


    public static ArrayList<Task> readFile(String filepath) throws FileNotFoundException {
        File file = new File(filepath);
        Scanner fileScanner = new Scanner(file);
        ArrayList<Task> tasks = new ArrayList<>();
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            tasks.add(constructTaskFromFile(line));
        }

        return tasks;
    }


    public static void writeToFile(ArrayList<Task> tasks, String filepath) throws IOException {
        // create data folder if it does not exist
        File file = new File(filepath);
        System.out.println(System.getProperty("user.dir"));
        if (!file.getParentFile().exists()) {
            System.out.println("FILE does not exists");
            file.getParentFile().mkdir();
        }

        FileWriter fileWriter = new FileWriter(filepath);
        for (Task task : tasks) {
            String line = task.toString() + "\n";
            fileWriter.write(line);
        }
        fileWriter.close();
    }

    public static void printLine() {
        System.out.println("____________________________________________________________");
    }
}

