package common;

/**
 * @Author ACER
 * @Date:2022/5/14
 */
public class Error {
    // dm
    public static final Exception BadLogFileException = new RuntimeException("Bad log file!");
    public static final Exception MemTooSmallException = new RuntimeException("Memory too small!");
    public static final Exception DataTooLargeException = new RuntimeException("Data too large!");
    public static final Exception DatabaseBusyException = new RuntimeException("Database is busy!");
    // tm
    public static final Exception BadXIDFileException = new RuntimeException("Bad XID file!");
    // Page_Manager
    public static final Exception FileExistsException = new RuntimeException("File has exist");

    public static final Exception FileCannotRWException = new RuntimeException("File cannot read or write");
    public static final Exception FileNotExistsException = new RuntimeException("File has not exist in current path");
    public static final Exception MenTooSmallException = new RuntimeException("Memory are too small");
}
