package Data_Manager;

import Page_Manager.Page;
import Transaction_Manager.XidFileIO.Panic;
import common.Error;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * @Author ACER
 * @Date:2022/6/3
 */
public interface PageCache {
    int page_size = (1 << 13);

    static long pageOffset(int pg_no) {
        return (pg_no - 1) * page_size;
    }

    int newPage(byte[] initData);
    Page getPage(int pg_no) throws Exception;
    void close();
    void release(Page page);
    void truncateByMaxNo(int maxNo);
    int getPageNumber();
    void flushPage(Page pg);

    default  PageCacheImpl create(String path,long memory){
        File f = new File(path + PageCacheImpl.DB_SUFFIX);
        try {
            if(!f.createNewFile()){
                Panic.panic(Error.FileExistsException);
            }
        } catch (IOException e) {
            Panic.panic(e);
        }
        if(!f.canRead() || !f.canWrite()){
            Panic.panic(Error.FileCannotRWException);
        }
        FileChannel fc = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f, "rw");
            fc = raf.getChannel();
        } catch (FileNotFoundException e) {
            Panic.panic(e);
        }
        return new PageCacheImpl(raf, fc, (int) memory / page_size);
    }
    default  PageCache open(String path, long memory){
        File f = new File(path + PageCacheImpl.DB_SUFFIX);
        if(!f.exists()){
            Panic.panic(Error.FileNotExistsException);
        }
        if(!f.canRead() || !f.canWrite()){
            Panic.panic(Error.FileCannotRWException);
        }
        FileChannel fc = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f, "rw");
            fc = raf.getChannel();
        } catch (FileNotFoundException e) {
            Panic.panic(e);
        }
        return new PageCacheImpl(raf, fc, (int)memory / page_size );
    }
}
