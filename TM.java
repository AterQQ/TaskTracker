import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class TM {
    public static void main(String[] args) {
        // Check integrety of the log files

        // Parse 
        
        
        // Figure out command

        //private Operation operation = new Start();

        // Parse log files & save state (if needed)

        // Run operation

    }
}

class ArgumentParser {
    public ArgumentParser(String[] args) {
        if (args.length == 0 || args.length > 5) {
            System.out.println("Invalid usage");
            System.exit(0);
        }
    }

    public ArrayList<String> ObtainCommand(String[] args) {
        ArrayList<String> command = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (i == 0) {
                command.add(args[i].toLowerCase());
            }
            else {
                command.add(args[i]);
            }
        }
        
        // debug purposes, delete later
        for (int i = 0; i < command.size(); i++) {
            System.out.println("Arg " + i + " is " + command.get(i));
        }
        // end debug
        
        return command;
    }

    // private Boolean ValidityCheck(String command) {
    //     // return Arrays.stream(validCommands).
    //     //     filter(element -> element.equals(command))
    // }
    
    public Operation GetCorrectInstance(ArrayList<String> yo) {
        return new Start(yo);
    }
}

class CorrectCommand {  // manage correct class
    public CorrectCommand(ArrayList<String> command) {
        if(command.size() < 2) {
            System.out.println("Too few arguments");
            System.exit(1);
        }
        else if (command.size() > 4) {
            System.out.println("Too many arguments");
            System.exit(1);
        }
    }

    public Operation GetCorrectInstance(ArrayList<String> command) {
        if(command.get(1) == "start") 
            { return new Start(command); }
        else if (command.get(1) == "end") 
            { return new Stop(command); }
        else if (command.get(1) == "describe") 
            { return new Describe(command); }
        else if (command.get(1) == "summary") 
            { return new Summary(); }
        else if (command.get(1) == "size")
            { return new Size(command); }
        else if (command.get(1) == "delete")
            { return new Delete(command); }
        else if (command.get(1) == "rename")
            { return new Rename(command); }
        
        System.out.println("Unknown command");
        System.exit(1);
        return null;
    }
}

class Logger {
    private String logFile = "TMLog.txt";
    ArrayList<String> logList = new ArrayList<>();
     
    public ArrayList<String> read() {
        try {
            File readFile = new File(logFile);
            Scanner scanner = new Scanner(readFile);

            while(scanner.hasNextLine()) {
                logList.add(scanner.nextLine());
            }

            scanner.close();
        } catch (IOException e){
            System.out.println("Could not read log" + e);
            System.exit(0);
        }
        
        return logList;
    }

    public void appendToFile(String writeToLog) {
        try {
            File writeFile = new File(logFile);

            if(!writeFile.exists()) {
                writeFile.createNewFile();
            }

            FileWriter writer = new FileWriter(logFile, true);
            writer.write(writeToLog);
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not write to file" + e);
            System.exit(0);
        }
        
    }
}

class ValidateIntegrety {

}

interface Operation {
    void run(ArrayList<String> args);
}

class Start implements Operation {
    private Logger logger = new Logger();

    public Start(ArrayList<String> args) {
        if(args.size() != 2) { 
            System.out.println("Incorrect Usage of start\n"
                                + "Usage: java TM.java start <task name>"
                              );
            System.exit(1);
        }
    }

    @Override
    public void run(ArrayList<String> args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args.get(0);
        String taskName = args.get(1);
        String writeLine =  time + "   " 
                            + taskName + "   "
                            + command;
        logger.appendToFile(writeLine);
    }
}

class Stop implements Operation {
    private Logger logger = new Logger();

    public Stop(ArrayList<String> args) {
        if(args.size() != 2) { 
            System.out.println("Incorrect Usage of stop\n"
                                + "Usage: java TM.java stop <task name>"
                              );
            System.exit(1);
        }
    }

    @Override
    public void run(ArrayList<String> args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args.get(0);
        String taskName = args.get(1);
        String writeLine =  time + "   " 
                            + taskName + "   "
                            + command;
        logger.appendToFile(writeLine);
    }
}

class Describe implements Operation {
    private Logger logger = new Logger();

    public Describe(ArrayList<String> args) {
        if(args.size() != 3 && args.size() != 4) { 
            System.out.println("Incorrect Usage of describe\n"
                                + "Usage: java TM.java describe"
                                + "<task name> <description> [{S|M|L|XL}]"
                              );
        System.exit(1);
        }
    }

    @Override
    public void run(ArrayList<String> args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args.get(0);
        String taskName = args.get(1);
        String description = args.get(2);
        String size;

        String writeLine =  time + "   " 
                            + taskName + "   "
                            + command + "   "
                            + "\"" + description + "\"";
        
        if(args.size() == 4) {
            size = args.get(3);
            writeLine += "   " + size;
        }
        logger.appendToFile(writeLine);
    }
}

class Summary implements Operation {
    public void run(ArrayList<String> args) {
        
    }
}

class Size implements Operation {
    private Logger logger = new Logger();

    public Size(ArrayList<String> args) {
        if(args.size() != 3 || isValidSize(args.get(2))) { 
            System.out.println("Incorrect Usage of size\n"
                                + "Usage: java TM.java size" 
                                + "<task name> {S|M|L|XL}"
                              );
            System.exit(1);
        }
    }

    @Override
    public void run(ArrayList<String> args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args.get(0);
        String taskName = args.get(1);
        String size = args.get(2);

        String writeLine =  time + "   " 
                            + taskName + "   "
                            + command + "   "
                            + size + "   ";

        logger.appendToFile(writeLine);
    }

    private Boolean isValidSize(String size) {
        String[] validSizes = {"S", "M", "L", "XL"};

        for (String validSize : validSizes) {
            if (size == validSize) { return true; }
        }
        return false;
    }
}

class Rename implements Operation {
    private Logger logger = new Logger();

    public Rename(ArrayList<String> args) {
        if(args.size() != 3) { 
            System.out.println("Incorrect Usage of rename\n"
                                + "Usage: java TM.java rename" 
                                + "<old task name> <new task name>"
                              );
            System.exit(1);
        }
    }

    @Override
    public void run(ArrayList<String> args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args.get(0);
        String oldTaskName = args.get(1);
        String newTaskName = args.get(2);

         String writeLine =  time + "   " 
                            + oldTaskName + "   "
                            + command + "   "
                            + newTaskName + "   ";

        logger.appendToFile(writeLine);
    }
}

class Delete implements Operation {
    private Logger logger = new Logger();

     public Delete(ArrayList<String> args) {
        if(args.size() != 3) { 
            System.out.println("Incorrect Usage of delete\n"
                                + "Usage: java TM.java delete <task name>"
                              );
            System.exit(1);
        }
    }

    @Override
    public void run(ArrayList<String> args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args.get(0);
        String taskName = args.get(1);

        String writeLine =  time + "   " 
                            + taskName + "   "
                            + command + "   ";

        logger.appendToFile(writeLine);
    }

    public Boolean validNumberOfArgs(ArrayList<String> args) {
        if(args.size() == 2) { return true; }
        return false;
    }
}

// -----------------------------------------------------------------------
// interface MyInterface {
//     void someMethod();
// }

// class MyClass implements MyInterface {
//     @Override
//     public void someMethod() {
//         System.out.println("Implementation of someMethod");
//     }
// }

// class MyClass2 implements MyInterface {
//     @Override
//     public void someMethod() {
//         System.out.println("Some other method")
//     }
// }

// public class Main {
//     public static void main(String[] args) {
//         MyInterface myObject = createObject();
//         myObject.someMethod();
//     }

//     public static MyInterface createObject() {
//         // Instantiate and return a class that implements MyInterface
//         return new MyClass2();
//     }
// }
// start foo
// stop foo
// rename foo oop
