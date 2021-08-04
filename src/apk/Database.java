package apk;

import structures.Record;

import java.util.List;

public abstract class Database<K extends Comparable<K>, V extends Record<K, V>> {

    public abstract boolean add(V record);

    public abstract boolean edit(V record);

    public abstract V find(K key, boolean edit);

    public abstract boolean update(V record);

    /**
     * Generuje nahodne data na naplnenie databazy
     * @param numOfData pocet vygenerovanych dat
     */
    public abstract void generate(int numOfData);

    /**
     * Zobrazenie vsetkych recordov
     * @param records list sa naplni vsetkymi recordmi
     */
    public abstract void show(List<V> records);

    /**
     * Sekvencny vypis vsetkych blokov.
     * @param blocks list sa naplni informaciami a blokoch
     */
    public abstract void allDataSequenceOutput(List<String> blocks);

    /**
     * @return data z pomocneho suboru
     */
    public abstract List<String> helperData();

    public abstract void save();

    public abstract void clear();

}
