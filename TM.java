import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TM {
    public static void main(String[] args) {      
        CorrectCommand newCommand = new CorrectCommand(args);
        Operation correctInstance = newCommand.GetCorrectInstance(args);
        correctInstance.run(args);
    }
}

class LogLineParser {
    public ArrayList<String> parse(String loggerLine) {
        ArrayList<String> logParts = new ArrayList<>();
        int descriptionPart = 1;
        String[] qutoeParts = loggerLine.split("\"");

        for(int i = 0; i < qutoeParts.length; i++) {
            // If i is 1, this means that we have a describe command
            if (i == descriptionPart) {
                logParts.add(qutoeParts[i]);
                continue;
            }
            String[] whiteSpaceParts = qutoeParts[i]
                                       .trim()
                                       .split("\\s+");
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
        else if (Util.isASize(command[1])) { 
            System.out.println("Cannot use size as a task name");
            System.exit(1);
        }
    }

    public Operation GetCorrectInstance(String[] command) {
        if (command[0].equals("summary"))
            { return new Summary(command); }
        else if(command[0].equals("start")) 
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
            System.out.println("Log does not exist: " + e);
            System.exit(1);
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
            System.out.println("Could not write to file: " + e);
            System.exit(1);
        }
    }
}

class Util {
    public static Boolean isASize(String size) {
        String[] validSizes = {"S", "M", "L", "XL"};

        for (String validSize : validSizes) {
            if (size.equals(validSize))
                 { return true; }
        }
        return false;
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
    }

    public void setTotalTime(LocalDateTime endTime) {
        if(taskStarted && startTime != null) {
            Duration finalTime = Duration.between(this.startTime, endTime);
            long seconds = finalTime.getSeconds();
        
            this.taskStarted = false;
            this.totalTime += seconds;
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

class Summary implements Operation {

    public Summary(String[] args) {}
    private final Logger logger = new Logger();
    private final LogLineParser logParser = new LogLineParser();
    private HashMap<String, Metadata> data = new HashMap<>();
    
    public void run(String[] args) {
       
        ArrayList<String> log = logger.read();
        for(int i = 0; i < log.size(); i++) {
            processLogLines(log.get(i), i);
        }
        getSummary(args);
    }

    private void processLogLines(String logLines, int lineNumber) {
        ArrayList<String> parsedLine = logParser.parse(logLines);

        if(parsedLine.size() < 3 || parsedLine.size() > 6) {
            return;
        }

        String time = parsedLine.get(0);
        String taskName = parsedLine.get(1);
        String command = parsedLine.get(2);
        LocalDateTime dateTime = parseDateTime(time, lineNumber);
        Metadata metadata = getOrCreateMetadata(taskName);

        if(command.equals("start")) {
            metadata.setStartTime(dateTime);
            data.put(taskName, metadata);
        }
        else if(command.equals("stop")) {
            metadata.setTotalTime(dateTime);
            data.put(taskName, metadata);
        }
        else if(command.equals("describe")) {
            String description = parsedLine.get(3);
            String size = "";
            if (parsedLine.size() == 5) {
                size = parsedLine.get(4);
            }
            metadata.setDescription(description, size);
            data.put(taskName, metadata);
        }
        else if(command.equals("size")) {
            String size = parsedLine.get(3);
            metadata.setSize(size);
            data.put(taskName, metadata);
        }
        else if(command.equals("rename")) {
            if (data.containsKey(taskName)) {
                String newName = parsedLine.get(3);
                data.remove(taskName);
                taskName = newName;
                metadata.setTaskName(taskName);
                data.put(taskName, metadata);
            }
        }
        else if(command.equals("delete")) {
            data.remove(taskName);
        }
        else {
            System.out.println("Command \"" + command + "\" not recognized"
                                + " on line " + (lineNumber + 1) + "\n"
                                +  "Skipping...\n");
        }
    }

    private LocalDateTime parseDateTime(String time, int lineNumber) {
        if(validtateDateTime(time, lineNumber)) {
                DateTimeFormatter format = DateTimeFormatter
                                           .ISO_LOCAL_DATE_TIME;
                return LocalDateTime.parse(time, format);
            }
        
        System.out.println("Malformed date in log on line " 
                           + (lineNumber + 1));
        System.exit(1);
        return null; // Not reachable line for compilation purposes
    }

    private Metadata getOrCreateMetadata(String taskName) {
        Metadata metadata;
        if(data.containsKey(taskName)) {
                metadata = data.get(taskName);
        }
        else {
            metadata = new Metadata();
            metadata.setTaskName(taskName);
        }
        return metadata;
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
        
        getTotalTime(totalTime, times);

        for (String key : times.keySet()) {
            ArrayList<Long> taskSize = times.get(key);
            if(taskSize.size() >= 2){
                System.out.println("Stats for size " + key);
                printMinMaxAvg(taskSize);
                System.out.println("=====================================");
            }
        }

        System.out.println("Total time on all tasks:\t" 
                            + getTimeFormat(totalTime));
    }
    
    private void getTotalTime(long totalTime, 
                               HashMap<String, ArrayList<Long>> times) {
        for (String taskName : data.keySet()) {
            Metadata metadata = data.get(taskName);
            String size = metadata.getSize();
            if(Util.isASize(size)) {
                ArrayList<Long> time = times.get(size);
                time.add(metadata.getTotalTime());
                times.put(size, time);
            }
            totalTime += metadata.getTotalTime();

            printSummary(metadata);     
        }
    }

    private void summary(String taskOrSize) {
        if(!Util.isASize(taskOrSize)) {
            summaryByName(taskOrSize);
        }
        else {
            summaryBySize(taskOrSize);
        }
    }

    private void summaryByName(String taskName) {
        if (!data.containsKey(taskName)) {
            System.out.println("Task does not exist");
            System.exit(1);
        }

        Metadata metadata = data.get(taskName);
        printSummary(metadata);
    }

    private void summaryBySize(String size) {
        ArrayList<Metadata> metadataList;
        ArrayList<Long> times = new ArrayList<>();
        metadataList = data.entrySet().stream()
                    .filter(entry -> entry.getValue().getSize().equals(size))
                    .map(entry -> entry.getValue())
                    .collect(Collectors.toCollection(ArrayList::new));

        if (metadataList.size() == 0) {
            System.out.println("Task(s)(s) do not exist for size " + size);
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

    private void printMinMaxAvg(ArrayList<Long> times) {
        long min = times.get(0);
        long max = times.get(0);
        long sum = 0;
        long average;

        for(long time : times) {
            if(time < min) 
                { min = time; }
            else if (time > max)
                { max = time; }
            sum += time;
        }

        average = sum/times.size();

        System.out.println("\tMinimum time:\t" + getTimeFormat(min));
        System.out.println("\tMaximum time:\t" + getTimeFormat(max));
        System.out.println("\tAverage time:\t" + getTimeFormat(average));
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
        long notTwoDigits = 10;
        String timeString = "";
        
        if (hours < notTwoDigits) 
            { timeString = timeString.concat("0" + hours + ":"); }
        else 
            { timeString = timeString.concat("" + hours + ":"); }
    
        if (minutes < notTwoDigits) 
            { timeString = timeString.concat("0" + minutes + ":"); }
        else 
            { timeString = timeString.concat("" + minutes + ":"); }
        if (remainingSeconds < notTwoDigits) 
            { timeString = timeString.concat("0" + remainingSeconds); }
        else 
            { timeString = timeString.concat("" + remainingSeconds); }
        return timeString;
    }
}

interface Operation {
    void run(String[] args);
}

class Start implements Operation {
    private Logger logger = new Logger();

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
}

class Stop implements Operation {
    private Logger logger = new Logger();

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
        else if(args.length == 4 && !Util.isASize(args[3])) {
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
}

class Size implements Operation {
    private Logger logger = new Logger();

    public Size(String[] args) {
        if(args.length != 3 || !Util.isASize(args[2])) { 
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
        if(args.length != 2) { 
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