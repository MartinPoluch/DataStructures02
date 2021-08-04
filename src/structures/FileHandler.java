package structures;

import java.io.*;

public class FileHandler {

    private RandomAccessFile file;

    public FileHandler(String path) {
        try {
            file = new RandomAccessFile(path, "rw");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(Block block) {
        try {
            if (block.getAddress() == Block.NULL_ADDRESS) {
                throw new IllegalArgumentException("You cannot write block with NULL address!");
            }
            write(block.getAddress(), block.getByteArray());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void write(int position, byte[] bytes) {
        try {
            file.seek(position);
            file.write(bytes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public byte[] read(int position, int length) {
        try {
            byte[] input = new byte[length];
            file.seek(position);
            file.read(input, 0, length);
            return input;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void clear() {
        try {
            file.setLength(0);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void close() {
        try {
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
