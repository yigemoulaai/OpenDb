package Transaction_Manager.XidFileIO;

import java.io.IOException;

/**
 * @Author ACER
 * @Date:2022/5/14
 */
public class Panic {
    public static IOException panic(Exception badXIDFileException) {
        return new IOException();
    }

}
