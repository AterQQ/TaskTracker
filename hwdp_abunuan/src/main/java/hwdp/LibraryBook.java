package hwdp;

import java.util.ArrayList;

public class LibraryBook implements Subject {
    private String name;
    private LBState state;
    private LibraryLogger logger = LibraryLogger.getInstance();
    private ArrayList<Observer> currentObservers = new ArrayList<>();
    
    public LibraryBook(String name) {
        this.name = name;
        this.state = OnShelf.getInstance();
    }
    
    public LBState getState() {
        return state;
    }

    public void setState(LBState newState){
        this.state = newState;
        notifyObservers();
    }
    
    public void returnIt() {
        state.returnIt(this);
    }
    
    public void shelf() {
        state.shelf(this);
    }
    
    public void extend() {
        state.extend(this);
    }
    
    public void issue() {
        state.issue(this);
    }

    public void notifyObservers() {
        for (Observer observer : currentObservers) {
            observer.update(this);
        }
    }
    
    public void attach(Observer observer) {
        String bookName = toString();
        if(currentObservers.contains(observer) == false) {
            currentObservers.add(observer);
            logger.writeLine(observer + " is now watching " + bookName);
        }
        else {
            logger.writeLine(observer + " is already attached to " + bookName);
        }
    }

    public void detach(Observer observer) {
        String bookName = toString();
        if(currentObservers.contains(observer)) {
            currentObservers.remove(observer);
            logger.writeLine(observer + " is no longer watching " + bookName);
        }
    }

    public String getStateName() {
        return state.toString();
    }

    @Override
    public String toString() {
        return name;
    }
}