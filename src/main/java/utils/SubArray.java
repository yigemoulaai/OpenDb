package utils;

/* *
 * @description: 松散的限定数组可使用的范围
 * @params:
 * @return:
 */
public class SubArray {

    public byte[] raw;
    public int start;
    public int end;

    public SubArray(byte[] raw, int start, int end) {
        this.raw = raw;
        this.start = start;
        this.end = end;
    }
}
