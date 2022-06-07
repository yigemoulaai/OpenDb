package utils;

import java.nio.ByteBuffer;

/**
 * @描述:
 * @时间:
 */
public class Parser {
    public static long parseLong(byte[] bytes){
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }

    public static byte[] longToByte(long xidCounter) {
        return ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(xidCounter).array();
    }

    public static byte[] short2Byte(short ofData) {
        return ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort(ofData).array();
    }

    public static short parseShort(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.put(bytes, 0 ,bytes.length);
        buffer.flip();
        return buffer.getShort();
    }

}
