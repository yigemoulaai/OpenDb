package Transaction_Manager;

import Transaction_Manager.XidFileIO.Panic;
import common.Error;
import utils.Parser;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * XID文件的描述类
 */
public class TransactionDetail implements TransactionManager {
    // XID文件头长度
    static final int LEN_XID_HEADER_LENGTH = 8;
    // 每个事务的占用长度
    private static final int XID_FIELD_SIZE = 1;
    // 事务的三种状态
    private static final byte FIELD_TRAN_ACTIVE    = 0;
    private static final byte FIELD_TRAN_COMMITTED = 1;
    private static final byte FIELD_TRAN_ABORTED   = 2;
    // 超级事务，永远为committed状态
    public static final long SUPER_XID = 0;
    // XID 文件后缀
    static final String XID_SUFFIX = ".xid";

    private FileChannel channel;
    private RandomAccessFile file;
    private long xidCounter;  //  统计已有xid事务的数量
    private Lock counterLock;

    TransactionDetail(RandomAccessFile raf, FileChannel fc){
        this.file = raf;
        this.channel = fc;
        counterLock = new ReentrantLock();
        checkXIDCounter();
    }
    /*
    *   检查xid文件格式
    *   读取xid_file头部的counter来计算文件的理论长度,对比实际长度
    * */
    private void checkXIDCounter() {
        long fileLen = 0l;
        try {
            fileLen = file.length();
        } catch (IOException e1) {
            Panic.panic(Error.BadXIDFileException);
        }
        if(fileLen < LEN_XID_HEADER_LENGTH) {
            Panic.panic(Error.BadXIDFileException);
        }
        ByteBuffer buf = ByteBuffer.allocate(LEN_XID_HEADER_LENGTH);
        try {
            channel.position(0);
            channel.read(buf);
        } catch (IOException e) {
            Panic.panic(e);
        }
        this.xidCounter = Parser.parseLong(buf.array());
        long end = getXidPosition(this.xidCounter + 1);
        if(end != fileLen){
            Panic.panic(Error.BadXIDFileException);
        }
    }
    //每个事务1个XID_FIELD_SIZE 根据xid取得其在xid文件中对应的位置
    private long getXidPosition(long xid) {
        return LEN_XID_HEADER_LENGTH + (xid-1)*XID_FIELD_SIZE;
    }
    //更新xid事务的状态
    private void updateXID(long xid, byte status) {
        long position = getXidPosition(xid);
        byte[] cache = new byte[XID_FIELD_SIZE];
        cache[0] = status;
        ByteBuffer buffer = ByteBuffer.wrap(cache);
        try {
            channel.position(position);
            channel.write(buffer);
        } catch (IOException e) {
            Panic.panic(e);
        }
        try {
            channel.close();
        } catch (IOException e) {
            Panic.panic(e);
        }
    }
    /*
    * 开启一个事务
    * */
    public long begin() {
        counterLock.lock();
        long nid = xidCounter +1 ;
        updateXID(nid, FIELD_TRAN_ACTIVE);
        incXidCounter();
        return nid;
    }
    //自增xid文件头部的counter字段
    private void incXidCounter() {
        xidCounter ++;
        ByteBuffer buffer = ByteBuffer.wrap(Parser.longToByte(xidCounter));
        try {
            channel.position(0);
            channel.write(buffer);
        } catch (IOException e) {
            Panic.panic(e);
        }
        try {
            channel.close();
        } catch (IOException e) {
            Panic.panic(e);
        }
    }

    public void commit(long xid) {
        updateXID(xid, FIELD_TRAN_COMMITTED);
    }

    public void abort(long xid) {
        updateXID(xid, FIELD_TRAN_ABORTED);
    }

    public boolean isActive(long xid) {

        if (xid == SUPER_XID) return  false;

        return checkXIDStatus(xid, FIELD_TRAN_ACTIVE);
    }
    //检查当前xid 的状态
    private boolean checkXIDStatus(long xid, byte status) {
        long position = getXidPosition(xid);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[XID_FIELD_SIZE]);
        try {
            channel.position(position);
            channel.read(buffer);
        } catch (IOException e) {
            Panic.panic(e);
        }
        return buffer.array()[0] == status;
    }

    public boolean isCommitted(long xid) {
        if (xid == SUPER_XID) return  false;
        return checkXIDStatus(xid, FIELD_TRAN_COMMITTED);
    }

    public boolean isAborted(long xid) {
        if (xid == SUPER_XID) return  false;
        return checkXIDStatus(xid, FIELD_TRAN_ABORTED);
    }

    public void close() {
        try {
            channel.close();
            file.close();
        } catch (IOException e) {
            Panic.panic(e);
        }
    }
}
