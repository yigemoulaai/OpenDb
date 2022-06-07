package utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @Author ACER
 * @Date:2022/6/6
 */
public class RandomUtil {
    public static byte[] randomBytes(int length) {
        Random r = new SecureRandom();
        byte[] buf = new byte[length];
        r.nextBytes(buf);
        return buf;
    }

}
