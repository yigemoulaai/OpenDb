package Page_Manager;

/**
 * @Author ACER
 * @Date:2022/6/3
 */
public interface Page {
    void lock();
    void unlock();
    void release();
    void setDirty(boolean isDirty);
    boolean isDirty();
    int getPageNumber();
    byte[] getData();
}
