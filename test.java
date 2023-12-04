import java.time.LocalDateTime;
import java.io.*;

public class test {
    public static void main(String[] args) {
        LocalDateTime time = LocalDateTime.now();
        String writeLine = time.toString() + "\t";
        for(String arg : args) {
            writeLine += arg + "\t";
        }
        
        System.out.println(writeLine);
    }
}
