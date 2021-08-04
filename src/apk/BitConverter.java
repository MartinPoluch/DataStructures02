package apk;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class BitConverter {

    public static final int DATE_BYTE_SIZE = 3 * Integer.BYTES;
    private final static Charset standardCharsets = StandardCharsets.UTF_8;

    public static byte[] toByteArray(String string) {
        return string.getBytes(standardCharsets);
    }

    public static String fromByteArrayToString(byte[] bytes) {
        return new String(bytes, standardCharsets);
    }

    /**
     * Nie je potrebne vytvarat instanciu tejto triedy
     */
    private BitConverter() {

    }

    public static byte[] toByteArray(LocalDate localDate) {
        byte[] output = new byte[DATE_BYTE_SIZE]; // 12 bytov
        byte[] year = toByteArray(localDate.getYear()); // 4 byty
        System.arraycopy(year, 0, output, 0, Integer.BYTES);
        byte[] month = toByteArray(localDate.getMonth().getValue()); // 4 byty
        System.arraycopy(month, 0, output, Integer.BYTES, Integer.BYTES);
        byte[] day = toByteArray(localDate.getDayOfMonth()); // 4 byty
        System.arraycopy(day, 0, output, Integer.BYTES * 2, Integer.BYTES);
        return output;
    }

    public static LocalDate fromByteArrayToDate(byte[] bytes) {
        int year = fromBytes(bytes[0], bytes[1], bytes[2], bytes[3]);
        int month = fromBytes(bytes[4], bytes[5], bytes[6], bytes[7]);
        int day = fromBytes(bytes[8], bytes[9], bytes[10], bytes[11]);
        return LocalDate.of(year, month, day);
    }

    /**
     * Zdroj: https://github.com/google/guava/blob/master/guava/src/com/google/common/primitives/Ints.java
     */
    public static byte[] toByteArray(int value) {
        return new byte[] {
                (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value
        };
    }

    /**
     * Zdroj: https://github.com/google/guava/blob/master/guava/src/com/google/common/primitives/Ints.java
     */
    public static int fromByteArrayToInt(byte[] bytes) {
        return fromBytes(bytes[0], bytes[1], bytes[2], bytes[3]);
    }

    /**
     * Zdroj: https://github.com/google/guava/blob/master/guava/src/com/google/common/primitives/Ints.java
     */
    private static int fromBytes(byte b1, byte b2, byte b3, byte b4) {
        return b1 << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF);
    }

    public static byte toByte(boolean bool) {
        return (bool) ? (byte) 1 : (byte) 0;
    }

    public static boolean fromBytesToBool(byte b) {
        return b == 1;
    }

    /**
     * Pomocna metoda pre metodu getByteArray()
     * @param source bajty ktore reprezentuje dany atribut (napr. vin, weight)
     * @param destination pole bajtov uz serializovanych atributov
     * @param position aktualna pozicia na ktoru mame zapisovat novo pridavany atribut
     * @return aktualna hodnota pozicie na ktoru budeme zapisovat dalsie data
     */
    public static int addBytes(byte[] source, byte[] destination, int position) {
        System.arraycopy(source, 0, destination, position, source.length);
        return position + source.length;
    }

}
