import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {

    private static final int CUTOFF = 10;
    private final char[] s;
    private final int[] index;
    private final int length;

    public CircularSuffixArray(String s) {
        length = s.length();
        s = s + "\0";
        this.s = s.toCharArray();
        index = new int[length];
        for (int i = 0; i < length; i++) {
            index[i] = i;
        }
        sort(0, length - 1, 0);
    }

    public int length() {
        return length;
    }

    public int index(int i) {
        if (i < 0 || i >= length) throw new IllegalArgumentException();
        return index[i];
    }

    private void sort(int lo, int hi, int d) {
        if (hi <= lo + CUTOFF) {
            insertion(lo, hi, d);
            return;
        }

        int lt = lo;
        int gt = hi;
        char c = s[index[lo] + d];
        int i = lo + 1;
        while (i <= gt) {
            char k = s[index[i] + d];
            if (k < c) {
                exch(lt++, i++);
            }
            else if (k > c) {
                exch(i, gt--);
            }
            else {
                i++;
            }
        }

        sort(lo, lt - 1, d);
        if (c > 0) {
            sort(lt, gt, d + 1);
        }
        sort(gt + 1, hi, d);


    }

    private void insertion(int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++) {
            for (int j = i; j > lo && less(index[j], index[j - 1], d); j--) {
                exch(j, j - 1);
            }
        }
    }

    private void exch(int i, int j) {
        int temp = index[i];
        index[i] = index[j];
        index[j] = temp;

    }

    private boolean less(int i, int j, int d) {
        if (i == j) { return false; }
        i = i +d;
        j = j +d;
        while (i < length && j < length) {
            if (s[i] < s[j]) { return true; }
            if (s[i] > s[j]) { return false; }
            i++;
            j++;
        }
        return i > j;
    }

    public static void main(String[] args) {
        String s = "ABAABAABBB";
        CircularSuffixArray cs = new CircularSuffixArray(s);
        for (int i = 0; i < cs.length(); i++)
        {
            StdOut.println(cs.index(i));
        }
    }
}
