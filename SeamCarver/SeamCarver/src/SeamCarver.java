import edu.princeton.cs.algs4.Picture;
import java.util.Arrays;

public class SeamCarver {
    private static final double BORDER_ENERGY = 1000.0;
    private Picture currentPic;
    private double[][] energy;
    private boolean isTransposed;
    private int[][] color;


    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();

        currentPic = picture;
        color = new int[width()][height()];

        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                color[col][row] = currentPic.getRGB(col, row);
            }
        }

        energy = calcPicEnergy();
        isTransposed = false;
    }

    public Picture picture() {
        return new Picture(currentPic);
    }

    public int width() {
        return currentPic.width();
    }

    public int height() {
        return currentPic.height();
    }

    public double energy(int x, int y) {
        if (x < 0 || x >= width())
            throw new IllegalArgumentException();
        if (y < 0 || y >= height())
            throw new IllegalArgumentException();
        if (isBorder(x, y)) {
            return BORDER_ENERGY;
        }
        else {
            return Math.sqrt(calcGradientX(x, y) + calcGradientY(x, y));
        }
    }

    public int[] findHorizontalSeam() {
        int[] seams;

        if (!isTransposed) {
            transpose();
            seams = findVerticalSeam();
            transpose();
            return seams;
        }
        else {
            seams = findVerticalSeam();
            transpose();
            return seams;
        }

    }

    public int[] findVerticalSeam() {
        double[][] distTo = new double[height()][width()];
        int[][] edgeTo = new int[height()][width()];
        int[] seams = new int[height()];

        for (int i = 0; i < height(); i++) {
            Arrays.fill(distTo[i], Double.POSITIVE_INFINITY);
            Arrays.fill(edgeTo[i], Integer.MAX_VALUE);
        }

        Arrays.fill(distTo[0], BORDER_ENERGY);
        Arrays.fill(edgeTo[0], 0);

        double min;
        for (int y = 1; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (width() == 1) {
                    min = distTo[y - 1][x];
                    distTo[y][x] = energy[x][y] + min;
                    edgeTo[y][x] = x;
                }
                else if (x == 0) {
                    min = Math.min(distTo[y - 1][x], distTo[y - 1][x + 1]);
                    distTo[y][x] = energy[x][y] + min;
                    edgeTo[y][x] = findMinEdge(distTo, x, y);
                }
                else if (x == width() - 1) {
                    min = Math.min(distTo[y - 1][x], distTo[y - 1][x - 1]);
                    distTo[y][x] = energy[x][y] + min;
                    edgeTo[y][x] = findMinEdge(distTo, x, y);
                }
                else {
                    min = Math.min(Math.min(distTo[y - 1][x], distTo[y - 1][x - 1]), distTo[y - 1][x + 1]);
                    distTo[y][x] = energy[x][y] + min;
                    edgeTo[y][x] = findMinEdge(distTo, x, y);
                }
            }
        }

        seams[height() - 1] = findMinPixel(distTo);

        for (int i = height() - 1; i > 0; i--) {
            seams[i - 1] = edgeTo[i][seams[i]];
        }

        return seams;
    }

    public void removeHorizontalSeam(int[] seam) {
        if (!isTransposed) {
            transpose();
            removeVerticalSeam(seam);
            transpose();
        }
        else {
            removeVerticalSeam(seam);
            transpose();
        }

    }

    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height())
            throw new IllegalArgumentException();
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width())
                throw new IllegalArgumentException();
        }
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1)
                throw new IllegalArgumentException();
        }
        Picture newPic = new Picture(width() - 1, height());
        int[][] newColor = new int[width() - 1][height()];

        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width() - 1; col++) {
                if (col < seam[row]) {
                    newPic.set(col, row, currentPic.get(col, row));
                    newColor[col][row] = color[col][row];
                }
                else {
                    newPic.set(col, row, currentPic.get(col + 1, row));
                    newColor[col][row] = color[col + 1][row];
                }
            }
        }
        color = newColor;
        currentPic = newPic;
        energy = calcPicEnergy();
    }

    private int calcGradientX(int x, int y) {
        int rgb1 = currentPic.getRGB(x + 1, y);
        int rgb2 = currentPic.getRGB(x - 1, y);

        int diffR = ((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF);
        int diffG = ((rgb1 >>  8) & 0xFF) - ((rgb2 >>  8) & 0xFF);
        int diffB = ((rgb1 >>  0) & 0xFF) - ((rgb2 >>  0) & 0xFF);

        return (diffR * diffR) + (diffG * diffG) + (diffB * diffB);
    }

    private int calcGradientY(int x, int y) {
        int rgb1 = currentPic.getRGB(x, y + 1);
        int rgb2 = currentPic.getRGB(x, y - 1);

        int diffR = ((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF);
        int diffG = ((rgb1 >>  8) & 0xFF) - ((rgb2 >>  8) & 0xFF);
        int diffB = ((rgb1 >>  0) & 0xFF) - ((rgb2 >>  0) & 0xFF);

        return (diffR * diffR) + (diffG * diffG) + (diffB * diffB);
    }

    private double[][] calcPicEnergy() {
        double[][] elist = new double[width()][height()];

        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++) {
                elist[col][row] = energy(col, row);
            }
        }

        return elist;
    }

    private boolean isBorder(int x, int y) {
        return (x >= (width() - 1) || y >= (height() - 1) || x <= 0 || y <= 0);
    }

    private int findMinEdge(double[][] distTo, int x, int y) {
        if (isMiddlePixel(x)) {
            if (distTo[y - 1][x - 1] <= distTo[y - 1][x] && distTo[y - 1][x - 1] <= distTo[y - 1][x + 1]) {
                return x - 1;
            }
            else if (distTo[y - 1][x] <= distTo[y - 1][x + 1]) {
                return x;
            }
            else {
                return x + 1;
            }
        }
        else if (isLeftmostPixel(x)) {
            if (distTo[y - 1][x] <= distTo[y - 1][x + 1]) {
                return x;
            }
            else {
                return x + 1;
            }
        }
        else {
            if (distTo[y - 1][x - 1] < distTo[y - 1][x]) {
                return x - 1;
            }
            else {
                return x;
            }
        }
    }

    private boolean isLeftmostPixel(int x) {
        return x - 1 < 0;
    }

    private boolean isMiddlePixel(int x) {
        return x - 1 >= 0 && x + 1 < width();
    }

    private int findMinPixel(double[][] distTo) {
        double energyTemp = Double.POSITIVE_INFINITY;
        int pixel = -1;

        for (int i = 0; i < distTo[height() - 1].length; i++) {
            if (distTo[height() - 1][i] < energyTemp) {
                energyTemp = distTo[height() - 1][i];
                pixel = i;
            }
        }

        return pixel;
    }

    private void transpose() {
        int[][] transposedColor = new int[height()][width()];
        Picture transposedPic = new Picture(height(), width());

        for (int col = 0; col < color.length; col++) {
            for (int row = 0; row < color[col].length; row++) {
                transposedColor[row][col] = color[col][row];
                transposedPic.setRGB(row, col, transposedColor[row][col]);
            }
        }

        color = transposedColor;
        currentPic = transposedPic;
        energy = calcPicEnergy();

        if (!isTransposed) {
            isTransposed = true;
        }
        else {
            isTransposed = false;
        }
    }
}
