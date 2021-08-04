package apk;

import structures.Record;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

public class Licence extends Record<Integer, Licence> {

    private String firstName;
    private String lastName;
    private Integer id;
    private LocalDate end;
    private boolean ban;
    private int fines;

    public static final int MAX_NAME_SIZE = 35;

    public Licence(String firstName, String lastName, int id, LocalDate end, boolean ban, int fines) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.end = end;
        this.ban = ban;
        this.fines = fines;
    }

    public Licence() {
        super();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public boolean isBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }

    public int getFines() {
        return fines;
    }

    public void setFines(int fines) {
        this.fines = fines;
    }

    @Override
    public Record<Integer, Licence> newInstance() {
        return new Licence();
    }

    @Override
    @SuppressWarnings("Duplicates")
    public byte[] getByteArray() {
        // valid(1) + firstName(35) + lastname(35) + id(4) + end(12) + ban(1) + fines(4)
        int position = 0;
        byte[] output = new byte[getSize()];
        output[position++] = BitConverter.toByte(isValid()); // prvy bit urcuje ci je dany zaznam validny

        byte[] surnameBytes = new byte[MAX_NAME_SIZE]; // inicializujeme pole o maximalnej dlzke kvoli paddingu
        System.arraycopy(BitConverter.toByteArray(firstName),0, surnameBytes, 0, firstName.length());
        position = BitConverter.addBytes(surnameBytes, output, position);

        byte[] lastNameBytes = new byte[MAX_NAME_SIZE]; // inicializujeme pole o maximalnej dlzke kvoli paddingu
        System.arraycopy(BitConverter.toByteArray(lastName),0, lastNameBytes, 0, lastName.length());
        position = BitConverter.addBytes(lastNameBytes, output, position);

        position = BitConverter.addBytes(BitConverter.toByteArray(id), output, position);
        position = BitConverter.addBytes(BitConverter.toByteArray(end), output, position);
        output[position++] = BitConverter.toByte(ban);
        BitConverter.addBytes(BitConverter.toByteArray(fines), output, position);
        return output;
    }

    @Override
    public int getSize() {
        // valid(1) + firstName(35) + lastname(35) + id(4) + end(12) + ban(1) + fines(4)
        return 1 + MAX_NAME_SIZE + MAX_NAME_SIZE + Integer.BYTES + BitConverter.DATE_BYTE_SIZE + 1 + Integer.BYTES;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void fromByteArray(byte[] input) {
        // valid(1) + firstName(35) + lastname(35) + id(4) + end(12) + ban(1) + fines(4)
        int position = 0; // smernik na bajt ktory sa ma aktualne precitat
        boolean isValid = BitConverter.fromBytesToBool(input[position++]);
        setValid(isValid);

        firstName = BitConverter.fromByteArrayToString(Arrays.copyOfRange(input, position, position + MAX_NAME_SIZE)).trim();
        position += MAX_NAME_SIZE;
        lastName = BitConverter.fromByteArrayToString(Arrays.copyOfRange(input, position, position + MAX_NAME_SIZE)).trim();
        position +=MAX_NAME_SIZE;
        id = BitConverter.fromByteArrayToInt(Arrays.copyOfRange(input, position, position + Integer.BYTES));
        position += Integer.BYTES;
        end =  BitConverter.fromByteArrayToDate(Arrays.copyOfRange(input, position, position + BitConverter.DATE_BYTE_SIZE));
        position += BitConverter.DATE_BYTE_SIZE;
        ban = BitConverter.fromBytesToBool(input[position++]);
        fines = BitConverter.fromByteArrayToInt(Arrays.copyOfRange(input, position, position + Integer.BYTES));
    }

    @Override
    public Licence getInstance() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Licence)) return false;
        Licence licence = (Licence) o;
        return getId() == licence.getId() &&
                isBan() == licence.isBan() &&
                getFines() == licence.getFines() &&
                Objects.equals(getFirstName(), licence.getFirstName()) &&
                Objects.equals(getLastName(), licence.getLastName()) &&
                Objects.equals(getEnd(), licence.getEnd());
    }

    @Override
    protected Integer getKey() {
        return id;
    }

    @Override
    public String toString() {
        return  " ID: " + id +
                "  First name: " + firstName +
                "  Last name: " + lastName +
                "  End: " + end +
                "  Ban: " + ban +
                "  Fines: " + fines;
    }

}
