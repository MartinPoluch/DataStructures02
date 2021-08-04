package test;


import org.junit.Assert;
import org.junit.Test;
import java.time.LocalDate;
import java.util.Random;
import apk.BitConverter;


public class BitConverterTest {

    @Test
    public void intConversion() {
        int oldNumber = new Random().nextInt(1000);
        byte[] bytes = BitConverter.toByteArray(oldNumber);
        int newNumber = BitConverter.fromByteArrayToInt(bytes);
        Assert.assertEquals(oldNumber, newNumber);
    }

    @Test
    public void localDateConversion() {
        Random random = new Random();
        int year = random.nextInt(2019);
        int month = random.nextInt(12);
        int day = random.nextInt(30);
        LocalDate oldDate = LocalDate.of(year, month, day);
        byte[] bytes = BitConverter.toByteArray(oldDate);
        LocalDate newDate = BitConverter.fromByteArrayToDate(bytes);
        Assert.assertEquals(oldDate, newDate);
    }

    @Test
    public void stringConversion() {
        String oldStr  = "Hello World";
        byte[] bytes = BitConverter.toByteArray(oldStr);
        String newStr = BitConverter.fromByteArrayToString(bytes);
        Assert.assertEquals(oldStr, newStr);
    }

    @Test
    public void booleanConversion() {
        boolean oldBool = (Math.random() < 0.5);
        byte b = BitConverter.toByte(oldBool);
        boolean newBool = BitConverter.fromBytesToBool(b);
        Assert.assertEquals(oldBool, newBool);
    }
}