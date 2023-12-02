package hwdp;

class OnShelf implements LBState {
    public static OnShelf instance;
    private LibraryLogger logger = LibraryLogger.getInstance();

    public static OnShelf getInstance() {
        if (instance == null) {
            instance = new OnShelf();
        }
        return instance;
    }

    @Override
    public void returnIt(LibraryBook book) {
        try {
            throw new BadOperationException("Illegal State Change");
        }
        catch (BadOperationException e) {
            logger.writeLine("BadOperationException - Can't use returnIt in OnShelf state");
        }
    }
    
    @Override
    public void shelf(LibraryBook book) {
        try {
            throw new BadOperationException("Illegal State Change");
        }
        catch (BadOperationException e) {
            logger.writeLine("BadOperationException - Can't use shelf in OnShelf state");
        }
    }
    
    @Override
    public void extend(LibraryBook book) {
        try {
            throw new BadOperationException("Illegal State Change");
        }
        catch (BadOperationException e) {
            logger.writeLine("BadOperationException - Can't use extend in OnShelf state");
        }
    }
    
    @Override
    public void issue(LibraryBook book) {
        logger.writeLine("Leaving State OnShelf for State Borrowed");
        book.setState(Borrowed.getInstance());
    }

    @Override
    public String toString() {
        return "OnShelf";
    }
}
