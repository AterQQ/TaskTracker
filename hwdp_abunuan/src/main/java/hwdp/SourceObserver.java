package hwdp;
import java.util.HashMap;

public class SourceObserver implements Observer {
	private String name;
	private HashMap<Subject, String> subjectToPastStateName = new HashMap<>();
	private LibraryLogger logger = LibraryLogger.getInstance();
	
	public SourceObserver(String n) {
		this.name = n;
	}

	@Override
	public void update(Subject s) {
		String currentStateName = s.getStateName();
		String lastState = subjectToPastStateName.get(s);
		String bookName = s.toString();
		
		if(lastState == null) {
			lastState = "UNOBSERVED";
		}

		subjectToPastStateName.put(s, currentStateName);
		logger.writeLine(name + " OBSERVED " + bookName + " LEAVING STATE: " + lastState);
	}

	@Override
	public String toString() {
		return name;
	}
}
