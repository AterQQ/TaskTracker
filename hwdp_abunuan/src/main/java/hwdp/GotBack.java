package hwdp;

class GotBack implements LBState {
    public static GotBack instance;
    private LibraryLogger logger = LibraryLogger.getInstance();

    public static GotBack getInstance() {
        if (instance == null) {
            instance = new GotBack();
        }
        return instance;
    }

    @Override
    public void returnIt(LibraryBook book) {
        try {
            throw new BadOperationException("Illegal State Change");
        }
        catch (BadOperationException e) {
            logger.writeLine("BadOperationException - Can't use returnIt in GotBack state");
        }
    }
    
    @Override
    public void shelf(LibraryBook book) {
        logger.writeLine("Leaving State GotBack for State OnShelf");
        book.setState(OnShelf.getInstance());
    }

    @Override
    public void extend(LibraryBook book) {
        try {
            throw new BadOperationException("Illegal State Change");
        }
        catch (BadOperationException e) {
            logger.writeLine("BadOperationException - Can't use extend in GotBack state");
        }
    }

    @Override
    public void issue(LibraryBook book) {
        try {
            throw new BadOperationException("Illegal State Change");
        }
        catch (BadOperationException e) {
            logger.writeLine("BadOperationException - Can't use issue in GotBack state");
        }
    }

    @Override
    public String toString() {
        return "GotBack";
    }
}
