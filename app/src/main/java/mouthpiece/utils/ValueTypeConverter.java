package mouthpiece.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by lyokato on 15/09/14.
 */
public class ValueTypeConverter {
    public static byte[] bytesFromInt(int value) {
        //Convert result into raw bytes. GATT APIs expect LE order
        return ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(value)
                .array();
    }
}
