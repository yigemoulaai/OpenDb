package Data_Manager;

import Page_Manager.Page;
import Page_Manager.PageImpl;
import Transaction_Manager.XidFileIO.Panic;
import common.Error;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author ACER
 * @Date:2022/6/3
 */
public class PageCacheImpl extends AbstractCache<Page> implements PageCache {
    static String DB_SUFFIX = ".DB";
    private static final int MEN_MIN_LIM = 10;
    private Lock fileLock;
    private FileChannel fc;
    private RandomAccessFile raf;
    private AtomicInteger pageNumber; //每个DB打开时自动计算 数据库文件有多少页
    public PageCacheImpl(RandomAccessFile raf, FileChannel fc, int maxSize) {
        super(maxSize);
        if(maxSize < MEN_MIN_LIM){
            Panic.panic(Error.MenTooSmallException);
        }
        long length = 0;
        try {
            length = raf.length();
        } catch (IOException e) {
            Panic.panic(e);
        }
        this.fc = fc;
        this.raf = raf;
        this.fileLock = new ReentrantLock();
        this.pageNumber = new AtomicInteger((int)length / page_size);
    }
    /* *
     * @description:  从磁盘中获取包装成 Page
     * @params: [key]
     * @return: Page_Manager.Page
     */
    protected Page getForCache( long key) throws Exception {
        int pg_no = (int) key;
        long offset = PageCache.pageOffset(pg_no);
        ByteBuffer buf = ByteBuffer.allocate(page_size);
        fileLock.lock();
        try {
            fc.position(offset);
            fc.read(buf);
        }
        catch (IOException e){
            Panic.panic(e);
        }
        fileLock.unlock();
        return new PageImpl(pg_no, buf.array(), this);
    }
    /* *
     * @description:  如果是脏数据，数据被淘汰到磁盘
     * @params: [obj]
     * @return: void
     */
    protected void releaseForCache(Page page) {
        if(page.isDirty()){
            flush(page);
            page.setDirty(false);
        }
    }
    /* *
     * @description: 指定数据刷盘
     * @params: [page]
     * @return: void
     */
    private void flush(Page page){
        int pageNumber = page.getPageNumber();
        long offset = PageCache.pageOffset(pageNumber);
        fileLock.lock();
        ByteBuffer wrap = ByteBuffer.wrap(page.getData());
        try {
            fc.position(offset);
            fc.write(wrap);
            fc.force(false);
        } catch (IOException e) {
            Panic.panic(e);
        }
        finally {
            fileLock.unlock();
        }
    }

    /* *
     * @description: 开辟一个新的页面
     * @params: [initData]
     * @return: int
     */
    @Override
    public int newPage(byte[] initData) {
        int no = pageNumber.incrementAndGet();
        PageImpl page = new PageImpl(no, initData, null);
        flush(page);
        return no;
    }

    @Override
    public Page getPage(int pg_no) throws Exception {
        return null;
    }

    @Override
    public void close() {
        super.close();
        try {
            fc.close();
            raf.close();
        } catch (IOException e) {
            Panic.panic(e);
        }
    }

    @Override
    public void release(Page page) {

    }

    @Override
    public void truncateByMaxNo(int maxNo) {

    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public void flushPage(Page pg) {

    }

}
