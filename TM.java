import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
import java.io.File;



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

class OperationObtain {
    private String[] validCommands = {
        "start",
        "stop",
        "describe",
        "summary",
        "size",
        "rename",
        "delete"
    };

    public OperationObtain(String[] args) {
        if (args.length == 0 || args.length > 5) {
            System.out.println("Invalid usage");
            System.exit(0);
        }
        
    }

    private ArrayList<String> ObtainCommand(String[] args) {
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
    
    public Operation GetCorrectInstance() {
        return new Start();
    }
}

class CorrectCommand {  // manage correct class
    // a function returns instansiation of correct class
}

class Logger {
    private String[] content;
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
            e.printStackTrace();
            System.exit(0);
        }
        
        return logList;
    }

    public void appendToFile(String command) {
        try {
            FileWriter writer = new FileWriter(logFile, true);
            writer.write(command);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
    }

    public void reWriteFile(ArrayList<String> newFileData) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
            for (String str : newFileData) {
                writer.write(str + '\n');
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}

class ValidateIntegrety {

}

interface Operation {
    void run(ArrayList<String> args);
    // boolean validMove();
}

class Start implements Operation {
    String name = "start";

    @Override
    public void run(ArrayList<String> args) {
        // for i in log file:
        //     if start in line:
        //         foundStart = true
        //         for j + i in log file:
                        //     print error
                        //     exit
        //             else if stop found:
        //                 foundStart = false
        //                 break
        //     
        // append command to log file

        // task save dictionary {task: stop}

    }
}

class Stop implements Operation {
    public static Stop instance;
    @Override
    public void run(ArrayList<String> args) {
        return;
    }
}

class Describe implements Operation {
    @Override
    public void run(ArrayList<String> args) {
        return;
    }
}

class Summary implements Operation {
    @Override
    public void run(ArrayList<String> args) {
        return;
    }
}

class Size implements Operation {
    @Override
    public void run(ArrayList<String> args) {
        return;
    }
}

class Rename implements Operation {
    @Override
    public void run(ArrayList<String> args) {

    }
}

class Delete implements Operation {
    @Override
    public void run(ArrayList<String> args) {

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