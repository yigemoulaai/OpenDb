package Page_Manager;

import Data_Manager.PageCache;
import utils.RandomUtil;

import java.util.Arrays;

/**
 * @描述: 特殊管理的最后一页 预留出98B空间作为校验字段
 * 一般先留出8B在启动时赋值，在DB关闭将8B拷贝到另外的8B
 * -------/---DB关闭--8B/--DB启动---8B 8192
 *
 */
public class PageFirst {
    private static final int OF_set= 8192;

    private static final int VC_LEN = 8;
    public static byte[] InitRaw(){
        byte[] bytes = new byte[PageCache.page_size];
        setVcOpen(bytes);
        return bytes;
    }

    private static void setVcOpen(byte[] bytes) {
        System.arraycopy(RandomUtil.randomBytes(VC_LEN), 0 , bytes, OF_set - VC_LEN, VC_LEN);
    }
    public static void setVcClose(Page pg) {
        pg.setDirty(true);
        setVcClose(pg.getData());
    }

    public static void setVcClose(byte[] raw) {
        System.arraycopy(raw, OF_set, raw, OF_set - 2* VC_LEN, VC_LEN); //倒数2个 VC_LEN
    }

    public static boolean checkVc(Page pg) {
        return checkVc(pg.getData());
    }

    private static boolean checkVc(byte[] raw) {
        return Arrays.equals(Arrays.copyOfRange(raw, OF_set - 2* VC_LEN, OF_set -VC_LEN ), Arrays.copyOfRange(raw, OF_set -  VC_LEN, OF_set));
    }

}
