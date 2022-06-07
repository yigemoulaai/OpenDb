package Page_Manager;

import Data_Manager.PageCache;
import utils.Parser;

import java.util.Arrays;

/**
 * @描述 :PageU 普通管理页
 * @args : FreeSpaceOffset
 */
public class PageU {
    private static final short OF_FREE = 0;
    private static final short OF_DATA = 2;  //头部2Bytes标识剩余空间的偏移量
    private static final int MAX_FREE_SPACE = PageCache.page_size - OF_DATA;

    public static byte[] initRaw(){
        byte[] raw = new byte[PageCache.page_size];
        setFSO(raw, OF_DATA);
        return raw;
    }

    private static void setFSO(byte[] raw, short ofData) {
        System.arraycopy(Parser.short2Byte(ofData), 0 ,raw, OF_FREE, OF_DATA);
    }

    // 获取pg的FSO
    public static short getFSO(Page pg){
        return getFSO(pg.getData());
    }
    public static short getFSO(byte[] raw){
        return Parser.parseShort(Arrays.copyOfRange(raw, 0 ,2));
    }
    //将raw插入pg中 返回插入位置
    public static  short insert(Page pg, byte[] raw){
        pg.setDirty(true);
        short offset = getFSO(pg.getData());
        System.arraycopy(raw, 0 , pg.getData(), offset, raw.length);
        setFSO(pg.getData(), (short)(offset + raw.length));
        return offset;
    }

    // 获取页面的空闲空间大小
    public static int getFreeSpace(Page pg) {
        return PageCache.page_size - (int)getFSO(pg.getData());
    }

    // 将raw插入pg中的offset位置，并将pg的offset设置为较大的offset
    public static void recoverInsert(Page pg, byte[] raw, short offset) {
        pg.setDirty(true);
        System.arraycopy(raw, 0, pg.getData(), offset, raw.length);
        short rawFSO = getFSO(pg.getData());
        if(rawFSO < offset + raw.length) {
            setFSO(pg.getData(), (short)(offset+raw.length));
        }
    }

    // 将raw插入pg中的offset位置，不更新update
    public static void recoverUpdate(Page pg, byte[] raw, short offset) {
        pg.setDirty(true);
        System.arraycopy(raw, 0, pg.getData(), offset, raw.length);
    }

}
