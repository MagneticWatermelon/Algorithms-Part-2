import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int RADIX = 256;

    public static void transform() {
        String s = BinaryStdIn.readString();
        BinaryStdIn.close();
        CircularSuffixArray arr = new CircularSuffixArray(s);
        findFirst(arr);
        lastColumns(arr, s);
        BinaryStdOut.flush();
    }

    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String t = BinaryStdIn.readString();
        int[] next = new int[t.length()];
        int[] count = new int[RADIX + 1];
        for (int i = 0; i < t.length(); i++) {
            count[t.charAt(i) + 1]++;
        }
        for (int i = 1; i <= RADIX; i++) {
            count[i] += count[i - 1];
        }
        for (int i = 0; i < t.length(); i++) {
            next[count[t.charAt(i)]++] = i;
        }
        int cnt = 0;
        while (cnt < t.length())
        {
            BinaryStdOut.write(t.charAt(next[first]));
            first = next[first];
            cnt++;
        }
        BinaryStdOut.close();
    }

    private static void lastColumns(CircularSuffixArray array, String s) {
        int index;
        for (int i = 0; i < array.length(); i++) {
            index = array.index(i);
            if (index == 0) {
                BinaryStdOut.write(s.charAt(s.length() - 1));
            }
            else {
                BinaryStdOut.write(s.charAt(index - 1));
            }
        }
    }

    private static void findFirst(CircularSuffixArray array) {
        int index;
        for (int i = 0; i < array.length(); i++) {
            index = array.index(i);
            if (index == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }
    }

    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        }
        else if (args[0].equals("+")) {
            inverseTransform();
        }
        else {
            throw new IllegalArgumentException();
        }
    }
}
