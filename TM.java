import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TM {
    public static void main(String[] args) {      
        CorrectCommand newCommand = new CorrectCommand(args);
        if(args[0].equals("summary")) {
            Summary summary = new Summary();
            summary.run();
            summary.getSummary(args);
        }
        else {
            Operation correctInstance = newCommand.GetCorrectInstance(args);
            correctInstance.run(args);
        }
    }
}

// class ArgumentParser {
//     public ArgumentParser(String[] args) {
//         if (args.length == 0 || args.length > 5) {
//             System.out.println("Invalid usage");
//             System.exit(0);
//         }
//     }

//     public ArrayList<String> parseArgument(String[] args) {
//         ArrayList<String> command = new ArrayList<>();

//         for (int i = 0; i < args.length; i++) {
//             if (i == 0) {
//                 command.add(args[i].toLowerCase());
//             }
//             else {
//                 command.add(args[i]);
//             }
//         }

//         return command;
//     }
// }

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
    public CorrectCommand(String[] command) {
        if(command.length < 1) {
            System.out.println("Too few arguments");
            System.exit(1);
        }
        else if (command.length > 4) {
            System.out.println("Too many arguments");
            System.exit(1);
        }
    }

    public Operation GetCorrectInstance(String[] command) {
        if(command[0].equals("start")) 
            { return new Start(command); }
        else if (command[0].equals("stop")) 
            { return new Stop(command); }
        else if (command[0].equals("describe")) 
            { return new Describe(command); }
        else if (command[0].equals("size"))
            { return new Size(command); }
        else if (command[0].equals("delete"))
            { return new Delete(command); }
        else if (command[0].equals("rename"))
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
    private String description;
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

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setStartTime(LocalDateTime time) {               
        if(!taskStarted) {
            this.startTime = time;
            this.taskStarted = true;
        }
        // else{
        //     System.out.println("Ignoring start");
        // }
    }

    public void setTotalTime(LocalDateTime endTime) {
        if(taskStarted && startTime != null) {
            Duration finalTime = Duration.between(this.startTime, endTime);
            long seconds = finalTime.getSeconds();
        
            this.taskStarted = false;
            this.totalTime += seconds;
        }
        else {
            System.out.println("Ignoring stop");
        }
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
    public String getTaskName() {
        return taskName;
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
    void run(String[] args);
    String toString();
}

class Start implements Operation {
    private Logger logger = new Logger();
    private String name = "start";

    public Start(String[] args) {
        if(args.length!= 2) { 
            System.out.println("Incorrect Usage of start\n"
                                + "Usage: java TM.java start <task name>"
                              );
            System.exit(1);
        }
    }

    @Override
    public void run(String[] args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args[0];
        String taskName = args[1];
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

    public Stop(String[] args) {
        if(args.length != 2) { 
            System.out.println("Incorrect Usage of stop\n"
                                + "Usage: java TM.java stop <task name>"
                              );
            System.exit(1);
        }
    }

    @Override
    public void run(String[] args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args[0];
        String taskName = args[1];
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

    public Describe(String[] args) {
        if(args.length != 3 && args.length != 4 ) { 
            System.out.println("Incorrect Usage of describe\n"
                                + "Usage: java TM.java describe"
                                + " <task name> <description> [{S|M|L|XL}]"
                              );
            System.exit(1);
        }
        else if(args.length == 4 && !isValidSize(args[3])) {
            System.out.println("Incorrect size listed\n"
                                + "Valid sizes: {S|M|L|XL}"
                              );
            System.exit(1);
        }
    }

    @Override
    public void run(String[] args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args[0];
        String taskName = args[1];
        String description = args[2];
        String size;

        String writeLine =  time + " " 
                            + taskName + " "
                            + command + " "
                            + "\"" + description + "\"";
        
        if(args.length == 4) {
            size = args[3];
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
    
    public void run() {
       
        ArrayList<String> log = logger.read();
        for(int i = 0; i < log.size(); i++) {
            ArrayList<String> parsedLine = logParser.parse(log.get(i));

            if(parsedLine.size() < 3 || parsedLine.size() > 6) {
                continue;
            }
            String time = parsedLine.get(0);
            String taskName = parsedLine.get(1);
            String command = parsedLine.get(2);
            Boolean newEntry = false;
            LocalDateTime dateTime;
            Metadata metadata;

            if(data.containsKey(taskName)) {
                metadata = data.get(taskName);
            }
            else {
                newEntry = true;
                metadata = new Metadata();
                metadata.setTaskName(taskName);
            }

            if(validtateDateTime(time, i)) {
                DateTimeFormatter format = DateTimeFormatter
                                           .ISO_LOCAL_DATE_TIME;
                dateTime = LocalDateTime.parse(time, format);
            }
            else {
                System.out.println("Malformed date in log on line " + (i + 1));
                dateTime = null;
                System.exit(1);
            }
            
            if(command.equals("start")) {
                metadata.setStartTime(dateTime);
            }
            else if(command.equals("stop")) {
                metadata.setTotalTime(dateTime);
            }
            else if(command.equals("describe")) {
                String description = parsedLine.get(3);
                String size = "";
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
                    metadata.setTaskName(taskName);
                }
                else 
                { continue; }
            }
            else if(command.equals("delete")) {
                data.remove(taskName);
                continue;
            }
            else {
                System.out.println("Command \"" + command + "\" not recognized"
                                    + " on line " + (i + 1) + "\n"
                                    +  "Skipping...\n");
                continue;
            }

            data.put(taskName, metadata);
        }
    }

    public void getSummary(String[] command) {
        if(command.length == 1) 
            { summary(); }
        else {
            String taskName = command[1];
            summary(taskName);
        }
    }

    private Boolean validtateDateTime(String time, int lineIndex) {
        try {
            DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime.parse(time, format);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void summary() {
        long totalTime = 0;
        HashMap<String, ArrayList<Long>> times = new HashMap<>();
        times.put("S", new ArrayList<Long>());
        times.put("M", new ArrayList<Long>());
        times.put("L", new ArrayList<Long>());
        times.put("XL", new ArrayList<Long>());
        
        for (String taskName : data.keySet()) {
            Metadata metadata = data.get(taskName);
            String size = metadata.getSize();
            if(isASize(size)) {
                ArrayList<Long> time = times.get(size);
                time.add(metadata.getTotalTime());
                times.put(size, time);
            }
            totalTime += metadata.getTotalTime();

            printSummary(metadata);        
        }
        for (String key : times.keySet()) {
            ArrayList<Long> taskSize = times.get(key);
            if(taskSize.size() >= 2){
                System.out.println("Stats for size " + key);
                printMinMaxAvg(taskSize);
                System.out.println("=====================================");
            }
        }
        //TODO: loop through time hashmap, check if value >= 2, run printMinMaxAverage
        System.out.println("Total time on all tasks:\t" 
                            + getTimeFormat(totalTime));
    }

    private void summary(String taskOrSize) {
        if(!isASize(taskOrSize)) {
            String taskName = "";

            for (String task : data.keySet()) {
                if(task.equals(taskOrSize)) {
                    taskName = taskOrSize;
                    break;
                }    
            }
            if (taskName.equals("")) {
                System.out.println("Task does not exist");
                System.exit(1);
            }
            
            Metadata metadata = data.get(taskName);
            printSummary(metadata);
        }
        else {
            //TODO: sort by size
            String size = taskOrSize;

            ArrayList<Metadata> metadataList;
            ArrayList<Long> times = new ArrayList<>();
            metadataList = data.entrySet().stream()
                        .filter(entry -> entry.getValue()
                                              .getSize()
                                              .equals(size))
                        .map(entry -> entry.getValue())
                        .collect(Collectors.toCollection(ArrayList::new));

            if (metadataList.size() == 0) {
                System.out.println("Task(s) do not exist for size " + size);
                System.exit(1);
            }

            for (Metadata data : metadataList) {
                times.add(data.getTotalTime());
                printSummary(data);
            }
            if(times.size() >= 2) {
                printMinMaxAvg(times);
            }
        }
    }

    private void printMinMaxAvg(ArrayList<Long> times) {
        Long min = Collections.min(times);
        Long max = Collections.max(times);
        Double average = times.stream()
                            .mapToLong(Long::longValue)
                            .average()
                            .orElse(0.0);
        System.out.println("Minimum time is " + min);
        System.out.println("Maximum time is " + max);
        System.out.println("Average time is " + average);
    }

    private void printSummary(Metadata metadata) {
        String timeFormat = getTimeFormat(metadata.getTotalTime());
        System.out.println("Stats for task " + metadata.getTaskName() + ":");
        System.out.println("\tTask Time:\t" + timeFormat);
        System.out.println("\tDescription:\t" + metadata.getDescription());
        System.out.println("\tSize:\t\t" + metadata.getSize());
        System.out.println("=====================================");
    }

    private String getTimeFormat(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds / 60) % 60;
        long remainingSeconds = seconds % 60;
        String timeString = "";
        
        if (hours < 9) 
            { timeString = timeString.concat("0" + hours + ":"); }
        else 
            { timeString = timeString.concat("" + hours + ":"); }
    
        if (minutes < 9) 
            { timeString = timeString.concat("0" + minutes + ":"); }
        else 
            { timeString = timeString.concat("" + minutes + ":"); }
        if (remainingSeconds < 9) 
            { timeString = timeString.concat("0" + remainingSeconds); }
        else 
            { timeString = timeString.concat("" + remainingSeconds); }
        return timeString;
    }

    private Boolean isASize(String size) {
        String[] validSizes = {"S", "M", "L", "XL"};

        for (String validSize : validSizes) {
            if (size.equals(validSize))
                 { return true; }
        }
        return false;
    }
}

class Size implements Operation {
    private Logger logger = new Logger();

    public Size(String[] args) {
        if(args.length != 3 || isValidSize(args[2])) { 
            System.out.println("Incorrect Usage of size\n"
                                + "Usage: java TM.java size" 
                                + " <task name> {S|M|L|XL}"
                              );
            System.exit(1);
        }
    }

    @Override
    public void run(String[] args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args[0];
        String taskName = args[1];
        String size = args[2];

        String writeLine =  time + " " 
                            + taskName + " "
                            + command + " "
                            + size + " ";

        logger.appendToFile(writeLine);
    }

    private Boolean isValidSize(String size) {
        String[] validSizes = {"S", "M", "L", "XL"};

        for (String validSize : validSizes) {
            if (size == validSize)
                 { return true; }
        }
        return false;
    }
}

class Rename implements Operation {
    private Logger logger = new Logger();

    public Rename(String[] args) {
        if(args.length != 3) { 
            System.out.println("Incorrect Usage of rename\n"
                                + "Usage: java TM.java rename" 
                                + " <old task name> <new task name>"
                              );
            System.exit(1);
        }
    }

    @Override
    public void run(String[] args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args[0];
        String oldTaskName = args[1];
        String newTaskName = args[2];

         String writeLine =  time + " " 
                            + oldTaskName + " "
                            + command + " "
                            + newTaskName + " ";

        logger.appendToFile(writeLine);
    }
}

class Delete implements Operation {
    private Logger logger = new Logger();

     public Delete(String[] args) {
        if(args.length > 3) { 
            System.out.println("Incorrect Usage of delete\n"
                                + " Usage: java TM.java delete <task name>"
                              );
            System.exit(1);
        }
    }

    @Override
    public void run(String[] args) {
        LocalDateTime time = LocalDateTime.now();
        String command = args[0];
        String taskName = args[1];

        String writeLine =  time + " " 
                            + taskName + " "
                            + command + " ";

        logger.appendToFile(writeLine);
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