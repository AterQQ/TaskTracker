package hwdp;

interface LBState {
    public void returnIt(LibraryBook book);
    public void shelf(LibraryBook book);
    public void extend(LibraryBook book);
    public void issue(LibraryBook book);
    public String toString();
}