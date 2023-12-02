package hwdp;

class Borrowed implements LBState {
    public static Borrowed instance;
    private LibraryLogger logger = LibraryLogger.getInstance();
    
    public static Borrowed getInstance() {
        if (instance == null) {
            instance = new Borrowed();
        }
        return instance;
    }

    @Override
    public void returnIt(LibraryBook book) {
        logger.writeLine("Leaving State Borrowed for State GotBack");
        book.setState(GotBack.getInstance());
    }
    
    @Override
    public void shelf(LibraryBook book) {
        try {
            throw new BadOperationException("Illegal State Change");
        }
        catch (BadOperationException e) {
            logger.writeLine("BadOperationException - Can't use shelf in Borrowed state");
        }
    }
    
    @Override
    public void extend(LibraryBook book) {
        logger.writeLine("Leaving State Borrowed for State Borrowed");
        book.setState(Borrowed.getInstance());
    }

    @Override
    public void issue(LibraryBook book) {
        try {
            throw new BadOperationException("Illegal State Change");
        }
        catch (BadOperationException e) {
            logger.writeLine("BadOperationException - Can't use issue in Borrowed state");
        }
    }

    @Override
    public String toString() {
        return "Borrowed";
    }
}
