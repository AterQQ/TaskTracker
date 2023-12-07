import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Map;

public class TM {
    public static void main(String[] args) {
        ArgumentParser command = new ArgumentParser(args);
        ArrayList<String> commandList = command.parseArgument(args);
        
        CorrectCommand newCommand = new CorrectCommand(commandList);
        if(commandList.get(0).equals("summary")) {
            Summary summary = new Summary();
            summary.run();
        }
        else {
            Operation correctInstance = newCommand.GetCorrectInstance(commandList);
            correctInstance.run(commandList);
        }
        

        // Check integrety of the log files

        // Parse 
        
        
        // Figure out command

        //private Operation operation = new Start();

        // Parse log files & save state (if needed)

        // Run operation

    }
}

// interface Parser {
//     ArrayList<String> parseTarget(String[] target);
// }

class ArgumentParser {
    public ArgumentParser(String[] args) {
        if (args.length == 0 || args.length > 5) {
            System.out.println("Invalid usage");
            System.exit(0);
        }
    }

    public ArrayList<String> parseArgument(String[] args) {
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
        // for (int i = 0; i < command.size(); i++) {
        //     System.out.println("Arg " + i + " is " + command.get(i));
        // }
        // end debug
        
        return command;
    }
}

class LogLineParser {

    public ArrayList<String> parse(String loggerLine) {
        // String[] clearQuotes = loggerLine.split("\"");
        // ArrayList<String> clearedQuotes = Arrays.stream(target)
        //                     .filter(word -> !word.isEmpty())
        //                     .collect(Collectors.toCollection(ArrayList::new));
        // // describe taskname description size
        // String[] clearSpace = clearedQuotes.get(0)
        // return Arrays.stream(target)
        //             .filter(word -> !word.isEmpty())
        //             .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> logParts = new ArrayList<>();
        String[] qutoeParts = loggerLine.split("\"");

        for(int i = 0; i < qutoeParts.length; i++) {
            // If i is 1, this means that we have a describe command
            if (i == 1) {
                logParts.add(qutoeParts[i]);
                continue;
            }
            String[] whiteSpaceParts = qutoeParts[i].trim().split("\\s+");
            for (String part : whiteSpaceParts) {
                logParts.add(part);
            }
        }
        return logParts;
    }
}

class CorrectCommand {
    public CorrectCommand(ArrayList<String> command) {
        if(command.size() < 1) {
            System.out.println("Too few arguments");
            System.exit(1);
        }
        else if (command.size() > 4) {
            System.out.println("Too many arguments");
            System.exit(1);
        }
    }

    public Operation GetCorrectInstance(ArrayList<String> command) {
        if(command.get(0).equals("start")) 
            { return new Start(command); }
        else if (command.get(0).equals("stop")) 
            { return new Stop(command); }
        else if (command.get(0).equals("describe")) 
            { return new Describe(command); }
        // else if (command.get(0).equals("summary")) 
        //     { return new Summary(); }
        else if (command.get(0).equals("size"))
            { return new Size(command); }
        else if (command.get(0).equals("delete"))
            { return new Delete(command); }
        else if (command.get(0).equals("rename"))
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
            writer.write(writeToLog + '\n');
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not write to file" + e);
            System.exit(0);
        }
        
    }
}

class Metadata {
    private String taskName;
    private String description ;
    private String size;
    private long totalTime;
    private LocalDateTime startTime;
    private boolean taskStarted;

    public Metadata() {
        this.taskName = "";
        this.description = "";
        this.size = "";
        this.totalTime = 0;
        this.taskStarted = false;
    }

    public void setStartTime(String time) {               
        if(!taskStarted) {
            DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime startTime = LocalDateTime.parse(time, format);

            this.startTime = startTime;
            this.taskStarted = true;
        }
        // else{
        //     System.out.println("Ignoring start");
        // }
    }

    public void setTotalTime(String time) {
        if(taskStarted) {
            DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime endTime = LocalDateTime.parse(time, format);

            Duration finalTime = Duration.between(this.startTime, endTime);
            long seconds = finalTime.getSeconds();
        
            this.taskStarted = false;
            this.totalTime += seconds;
        }
        // else {
        //     System.out.println("Ignoring stop");
        // }
    }

    public void setDescription(String description, String size) {
        this.description = this.description.concat(description);
        if(size != null) {
            setSize(size);
        }
    }

    public void setSize(String size) {
        String[] validSizes = {"S", "M", "L", "XL"};
        for(String validSize : validSizes) {
            if(size.equals(validSize)) {
                this.size = size;
            }
        }
    }

    public long getTotalTime() {
        return totalTime;
    }
    public Boolean getTaskStarted() {
        return taskStarted;
    }
    public String getDescription() {
        return description;
    }
    public String getSize() {
        return size;
    }
}

interface Operation {
    void run(ArrayList<String> args);
    String toString();
}

class Start implements Operation {
    private Logger logger = new Logger();
    private String name = "start";

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
        String writeLine =  time + " " 
                            + taskName + " "
                            + command;
        logger.appendToFile(writeLine);
    }

    public String toString() {
        return name;
    } 
}

class Stop implements Operation {
    private Logger logger = new Logger();
    private String name = "Stop";

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
        String writeLine =  time + " " 
                            + taskName + " "
                            + command;
        logger.appendToFile(writeLine);
    }

    @Override
    public String toString() {
        return name;
    } 
}

class Describe implements Operation {
    private Logger logger = new Logger();

    public Describe(ArrayList<String> args) {
        if(args.size() != 3 && args.size() != 4 ) { 
            System.out.println("Incorrect Usage of describe\n"
                                + "Usage: java TM.java describe"
                                + " <task name> <description> [{S|M|L|XL}]"
                              );
            System.exit(1);
        }
        else if(args.size() == 4 && !isValidSize(args.get(3))) {
            System.out.println("Incorrect size listed\n"
                                + "Valid sizes: {S|M|L|XL}"
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

        String writeLine =  time + " " 
                            + taskName + " "
                            + command + " "
                            + "\"" + description + "\"";
        
        if(args.size() == 4) {
            size = args.get(3);
            writeLine += " " + size;
        }
        logger.appendToFile(writeLine);
    }

    @Override
    public String toString() {
        return "describe";
    } 

    private Boolean isValidSize(String size) {
        String[] validSizes = {"S", "M", "L", "XL"};

        for (String validSize : validSizes) {
            if (size.equals(validSize)) { return true; }
        }
        return false;
    }
}

class Summary {
    private final Logger logger = new Logger();
    private final LogLineParser logParser = new LogLineParser();
    private HashMap<String, Metadata> data = new HashMap<>();
    private long totalTime = 0;
    
    public void run() {
       
        ArrayList<String> log = logger.read();
        for(String logLine : log) {
            ArrayList<String> parsedLine = logParser.parse(logLine);
            // TODO: Verification/Validation
            if(parsedLine.size() < 3 || parsedLine.size() > 6) {
                continue;
            }
            String time = parsedLine.get(0);
            String taskName = parsedLine.get(1);
            String command = parsedLine.get(2);
            Boolean newEntry = false;
            Metadata metadata;

            if(data.containsKey(taskName)) {
                metadata = data.get(taskName);
            }
            else {
                newEntry = true;
                metadata = new Metadata();
            }

            if(command.equals("start")) {
                metadata.setStartTime(time);
            }
            else if(command.equals("stop")) {
                metadata.setTotalTime(time);
            }
            else if(command.equals("describe")) {
                String description = parsedLine.get(3);
                String size = null;
                if (parsedLine.size() == 5) {
                    size = parsedLine.get(4);
                }
                metadata.setDescription(description, size);
            }
            else if(command.equals("size")) {
                String size = parsedLine.get(3);
                metadata.setSize(size);
            }
            else if(command.equals("rename")) {
                if(!newEntry) {
                    String newName = parsedLine.get(3);
                    data.remove(taskName);
                    taskName = newName;
                    //can include setter for Metadata to set taskName 
                }
            }
            else if(command.equals("delete")) {
                data.remove(taskName);
                continue;
            }
            
            data.put(taskName, metadata);

        }
        for (String taskName : data.keySet()) {
            
            Metadata metadata = data.get(taskName);

            String timeFormat = getTimeFormat(metadata.getTotalTime());
            totalTime += metadata.getTotalTime();
             // Debug, delete after
            // System.out.println(metadata.gettaskStarted());
            // End debug
            System.out.println("Stats for task " + taskName + ":");
            System.out.println("\tTask Time:\t" + timeFormat);
            System.out.println("\tDescription:\t" + metadata.getDescription());
            System.out.println("\tSize:\t\t" + metadata.getSize());
            System.out.println("=====================================");
            
        }
        System.out.println("Total time on all tasks:\t" 
                            + getTimeFormat(totalTime));
    }

    private String getTimeFormat(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds / 60) % 60;
        long remainingSeconds = seconds % 60;
        String timeString = "";
        
        if (hours < 9) {
            timeString = timeString.concat("0" + hours + ":");
        }
        else {
            timeString = timeString.concat("" + hours + ":");
        }
        if (minutes < 9) {
            timeString = timeString.concat("0" + minutes + ":");
        }
        else {
            timeString = timeString.concat("" + minutes + ":");
        }
        if (remainingSeconds < 9) {
            timeString = timeString.concat("0" + remainingSeconds);
        }
        else {
            timeString = timeString.concat("" + remainingSeconds);
        }
        return timeString;
    }
    
    @Override
    public String toString() {
        return "summary";
    } 
}

class Size implements Operation {
    private Logger logger = new Logger();

    public Size(ArrayList<String> args) {
        if(args.size() != 3 || isValidSize(args.get(2))) { 
            System.out.println("Incorrect Usage of size\n"
                                + "Usage: java TM.java size" 
                                + " <task name> {S|M|L|XL}"
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

        String writeLine =  time + " " 
                            + taskName + " "
                            + command + " "
                            + size + " ";

        logger.appendToFile(writeLine);
    }

    @Override
    public String toString() {
        return "size";
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
                                + " <old task name> <new task name>"
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

         String writeLine =  time + " " 
                            + oldTaskName + " "
                            + command + " "
                            + newTaskName + " ";

        logger.appendToFile(writeLine);
    }

    @Override
    public String toString() {
        return "rename";
    } 
}

class Delete implements Operation {
    private Logger logger = new Logger();

     public Delete(ArrayList<String> args) {
        if(args.size() > 3) { 
            System.out.println("Incorrect Usage of delete\n"
                                + " Usage: java TM.java delete <task name>"
                              );
            System.exit(1);
        }
    }

    @Override
    public void run(ArrayList<String> args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args.get(0);
        String taskName = args.get(1);

        String writeLine =  time + " " 
                            + taskName + " "
                            + command + " ";

        logger.appendToFile(writeLine);
    }

    public Boolean validNumberOfArgs(ArrayList<String> args) {
        if(args.size() == 2) { return true; }
        return false;
    }

    @Override
    public String toString() {
        return "delete";
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

// public class StringParsingExample {
//     public static void main(String[] args) {
//         String input = "This \"is a\" string";
//         String[] parts = input.split("\""); // First split on quotes

//         List<String> cleanedParts = new ArrayList<>();
//         for (String part : parts) {
//             String[] subParts = part.trim().split("\\s+"); // Split each part on whitespace
//             for (String subPart : subParts) {
//                 if (!subPart.isEmpty()) {
//                     cleanedParts.add(subPart);
//                 }
//             }
//         }
//     }
// }


//         long hours = secondsElapsed / 3600;
        // long minutes = (secondsElapsed % 3600) / 60;
        // long seconds = secondsElapsed % 60;