package structures;

public interface WritableAndReadable {
    /**
     * @return vrati mi pole bajtov ktore bude reprezentovat jednotlive zaznamy
     */
    byte[] getByteArray();
    int getSize();

    /**
     * Z pola bajtov sa inicializuju jednotlive atributy triedy.
     */
    void fromByteArray(byte[] bytes);



}
