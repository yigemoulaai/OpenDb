package Page_Manager;

import Data_Manager.PageCache;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author ACER
 * @Date:2022/6/5
 */
public class PageImpl implements Page{
    private int pageNumber;  //页面号

    private byte[] data;

    private boolean dirty;

    private Lock lock;

    private PageCache PgChe; //在拿到Page引用时快速对缓存进行释放

    public PageImpl(int pageNumber, byte[] data, PageCache PgChe) {
        this.pageNumber = pageNumber;
        this.data = data;
        this.PgChe = PgChe;
        lock = new ReentrantLock();
    }

    @Override
    public void lock() {
            lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public void release() {
        PgChe.release(this);
    }

    @Override
    public void setDirty(boolean isDirty) {
        this.dirty = isDirty;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
