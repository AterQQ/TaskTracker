package hwdp;
import java.util.ArrayList;

public class LibraryLogger {
    public static LibraryLogger instance;
    private ArrayList<String> totalLines = new ArrayList<>();

    public void writeLine(String line) {
        System.out.println("LibraryLogger: " + line);
        totalLines.add(line);
    }

    public String[] getWrittenLines() {
        String[] writtenLines = new String[totalLines.size()];
        for (int i = 0; i < totalLines.size(); i++) {
            writtenLines[i] = totalLines.get(i);
        }
        return writtenLines;
    }

    public void clearWriteLog() {
        totalLines.clear();
    }
    
    public static LibraryLogger getInstance() {
        if (instance == null) {
            instance = new LibraryLogger();
            ExpensiveComputeToy.performExpensiveLogSetup();
        }
        return instance;
    }
}
