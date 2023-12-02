package hwdp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class Student_abunuan_Test {

    @Test
    void myStudentTest(){
        LibraryBook book = new LibraryBook("The Spooky Book");
        SourceObserver srcObserver = new SourceObserver("SourceObs");
		DestObserver destObserver = new DestObserver("DestinationObs");

        book.attach(srcObserver);
		book.attach(destObserver);

        book.extend();
        book.returnIt();
        book.shelf();
        book.issue();
        book.detach(destObserver);

        book.issue();
        book.shelf();
        book.extend();
        book.returnIt();
		book.attach(destObserver);

        book.issue();
        book.extend();
        book.returnIt();
        book.shelf();

        book.issue();

        book.detach(destObserver);
        book.detach(srcObserver);

        assertArrayEquals(
            new String []  {
                "SourceObs is now watching The Spooky Book",
                "DestinationObs is now watching The Spooky Book",
                "BadOperationException - Can't use extend in OnShelf state",
                "BadOperationException - Can't use returnIt in OnShelf state",
                "BadOperationException - Can't use shelf in OnShelf state",
                "Leaving State OnShelf for State Borrowed",
                "SourceObs OBSERVED The Spooky Book LEAVING STATE: UNOBSERVED",
                "DestinationObs OBSERVED The Spooky Book REACHING STATE: Borrowed",
                "DestinationObs is no longer watching The Spooky Book",
                "BadOperationException - Can't use issue in Borrowed state",
                "BadOperationException - Can't use shelf in Borrowed state",
                "Leaving State Borrowed for State Borrowed",
                "SourceObs OBSERVED The Spooky Book LEAVING STATE: Borrowed",
				"Leaving State Borrowed for State GotBack",
                "SourceObs OBSERVED The Spooky Book LEAVING STATE: Borrowed",
                "DestinationObs is now watching The Spooky Book",
                "BadOperationException - Can't use issue in GotBack state",
                "BadOperationException - Can't use extend in GotBack state",
                "BadOperationException - Can't use returnIt in GotBack state",
                "Leaving State GotBack for State OnShelf",
                "SourceObs OBSERVED The Spooky Book LEAVING STATE: GotBack",
                "DestinationObs OBSERVED The Spooky Book REACHING STATE: OnShelf",
                "Leaving State OnShelf for State Borrowed",
                "SourceObs OBSERVED The Spooky Book LEAVING STATE: OnShelf",
                "DestinationObs OBSERVED The Spooky Book REACHING STATE: Borrowed",
                "DestinationObs is no longer watching The Spooky Book",
                "SourceObs is no longer watching The Spooky Book",
            },
            LibraryLogger.getInstance().getWrittenLines()
        );
    }  
}
