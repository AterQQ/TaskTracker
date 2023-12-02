package hwdp;

class DestObserver implements Observer {
    private String name;
    private LibraryLogger logger = LibraryLogger.getInstance();
    
    public DestObserver(String n) {
        this.name = n;
    }

    @Override
    public void update(Subject s) {
        String currentStateName = s.getStateName();
        String bookName = s.toString();
        logger.writeLine(name + " OBSERVED " + bookName + " REACHING STATE: " + currentStateName);
    }

    @Override
	public String toString() {
		return name;
	}
}
