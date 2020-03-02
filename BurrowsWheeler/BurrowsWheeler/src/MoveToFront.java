import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;

public class MoveToFront {
    private static final int RADIX = 256;
    private static final int W = 8;

    public static void encode() {
        ArrayList<Character> list = new ArrayList<>();
        for (int i = 0; i < RADIX; i++) {
            list.add((char) i);
        }
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int index = list.indexOf(c);
            BinaryStdOut.write(index, W);
            list.remove(index);
            list.add(0, c);
        }
        BinaryStdOut.close();
    }

    public static void decode() {
        ArrayList<Character> list = new ArrayList<>();
        for (int i = 0; i < RADIX; i++) {
            list.add((char) i);
        }
        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readChar();
            char c = list.get(index);
            BinaryStdOut.write(c, W);
            list.remove(index);
            list.add(0, c);
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        }
        else if (args[0].equals("+")) {
            decode();
        }
        else {
            throw new IllegalArgumentException();
        }
    }
}
