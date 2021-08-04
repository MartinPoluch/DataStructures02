package apk;

import java.time.LocalDate;
import java.util.Random;

public class DataGenerator {

    private int addressCounter;
    private Random random;

    public DataGenerator() {
        this.addressCounter =  1;
        this.random = new Random();
    }

    /**
     * Zdroj: https://www.baeldung.com/java-random-string
     * @return vrati nahodny string
     */
    private String generateRandomString(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            char randomLetter = (char) randomLimitedInt;
            if (random.nextDouble() < 0.5) {
                randomLetter =  Character.toUpperCase(randomLetter);
            }
            buffer.append(randomLetter);
        }
        return buffer.toString();
    }

    public Address randomAddress() {
        String key = generateRandomString(5) + addressCounter; // addressCounter zabezpecuje unikatnost
        return new Address(key, addressCounter++);
    }

    private LocalDate randomDate() {
        int year = random.nextInt(30) + 2000;
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1;
        return LocalDate.of(year, month, day);
    }

    /**
     * Generovane auta NEMAJU unikatnu SPZ a unikatny vin.
     */
    public Car randomCar() {
        String plateNumber = generateRandomString(Car.MAX_PLATE_NUMBER_SIZE);
        String vin = generateRandomString(Car.MAX_VIN_SIZE);
        int weight = random.nextInt(9000) + 1000;
        int axle = random.nextInt(4) + 2;
        boolean wanted = (random.nextDouble() < 0.2);
        return new Car(plateNumber, vin, weight, axle, wanted, randomDate(), randomDate());
    }

    public Licence randomLicence() {
        String surname = generateRandomString(random.nextInt(Licence.MAX_NAME_SIZE - 25) + 4);
        String lastName = generateRandomString(random.nextInt(Licence.MAX_NAME_SIZE - 25) + 4);
        int id = random.nextInt(Integer.MAX_VALUE);
        boolean ban = (random.nextDouble() < 0.3);
        int fines = random.nextInt(20);
        return new Licence(surname, lastName, id, randomDate(), ban, fines);
    }
}
