package structures;

public abstract class Record<K extends Comparable<K>, V extends Record> implements WritableAndReadable, Comparable<Record<K, V>> {

    private boolean valid;

    public Record() {
        this.valid = true;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public abstract Record<K, V> newInstance();

    public abstract V getInstance();

    protected abstract K getKey();

    @Override
    public final int compareTo(Record<K, V> record) {
        return this.getKey().compareTo(record.getKey());
    }
}
