import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private static final int R = 26;
    private SET<String> words;
    private BoggleBoard boggleBoard;
    private boolean[][] isMarked;
    private Node root = new Node();
    private final int[] directionsRow     = {-1,  0,  1, -1, 1, -1, 0, 1};
    private final int[] directionsColumns = {-1, -1, -1,  0, 0,  1, 1, 1};

    public BoggleSolver(String[] dictionary) {
        for (String s : dictionary) {
            put(s);
        }
    }

    private class Node {
        private int value;
        private Node[] next = new Node[R];

    }

    private int get(String key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        Node x = get(root, key, 0);
        if (x == null) return 0;
        return x.value;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c - 'A'], key, d+1);
    }

    private void put(String key) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        else root = put(root, key, 0);
    }

    private Node put(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            x.value = 1;
            return x;
        }
        char c = key.charAt(d);
        x.next[c - 'A'] = put(x.next[c - 'A'], key, d+1);
        return x;
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        boggleBoard = board;
        isMarked = new boolean[boggleBoard.rows()][boggleBoard.cols()];
        words = new SET<>();
        for (int row = 0; row < boggleBoard.rows(); row++) {
            for (int col = 0; col < boggleBoard.cols(); col++) {
                dfs(row, col, "", root);
            }
        }
        return words;
    }

    public int scoreOf(String word) {
        if (get(word) == 1) {
            switch (word.length()) {
                case 0:
                case 1:
                case 2:
                    return 0;
                case 3:
                case 4:
                    return 1;
                case 5:
                    return 2;
                case 6:
                    return 3;
                case 7:
                    return 5;
                default:
                    return 11;
            }
        }
        else {
            return 0;
        }
    }

    private void dfs(int row, int col, String word, Node node) {
        char c = boggleBoard.getLetter(row, col);
        if (node == null || node.next[c - 'A'] == null) {
            return;
        }
        StringBuilder sb = new StringBuilder(word);
        if (c == 'Q') {
            node = node.next['Q' - 'A'];
            if (node.next['U' - 'A'] == null) {
                return;
            }
            node = node.next['U' - 'A'];
            sb.append("QU");
        }
        else {
            node = node.next[c - 'A'];
            sb.append(c);
        }

        String currentWord = sb.toString();
        if (currentWord.length() >= 3 && node.value == 1 && !words.contains(currentWord)) {
            words.add(currentWord);
        }

        isMarked[row][col] = true;
        for (int k = 0; k < 8; ++k) {
            int rowDir = row + directionsRow[k];
            int colDir = col + directionsColumns[k];
            if (rowDir >= 0 && rowDir < boggleBoard.rows() && colDir >= 0 && colDir < boggleBoard.cols() && !isMarked[rowDir][colDir]) {
                dfs(rowDir, colDir, currentWord, node);
            }
        }
        isMarked[row][col] = false;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
