import edu.princeton.cs.algs4.Picture;

import java.awt.*;
import java.util.Random;

public class SeamCarver {
    private final Picture picture;
    private final Random random = new Random(12123);

    double[][] energy;

    public SeamCarver(Picture picture) {
        this.picture = picture;
        this.energy = new double[width()][height()];
    }

    public Picture picture() {
        return picture;
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    private Color getColor(int x, int y) {
        if (x < 0) {
            x += picture.width();
        }

        if (x >= picture.width()) {
            x -= picture.width();
        }

        if (y < 0) {
            y += picture.height();
        }

        if (y >= picture.height()) {
            y -= picture.height();
        }

        return picture.get(x, y);
    }

    private double getSquareDiff(Color color1, Color color2) {
        double ret = 0;

        ret += Math.pow((color1.getRed() - color2.getRed()), 2);
        ret += Math.pow((color1.getGreen() - color2.getGreen()), 2);
        ret += Math.pow((color1.getBlue() - color2.getBlue()), 2);

        return ret;
    }

    /**
     * Get the energy of pixel at column x and row y.
     *
     * @param x xPos
     * @param y yPos
     * @return double: energy
     */
    public double energy(int x, int y) throws java.lang.IndexOutOfBoundsException {
        if (x < 0 || x >= width()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (y < 0 || y >= height()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        double deltaXS = getSquareDiff(getColor(x - 1, y), getColor(x + 1, y));
        double deltaYS = getSquareDiff(getColor(x, y - 1), getColor(x, y + 1));

        return deltaXS + deltaYS;
    }

    private int getMinIndex(double[] energies) {
        int minIndex = 0;
        double minValue = Double.MAX_VALUE;

        for (int i = 0; i < energies.length; i++) {
            if (energies[i] < minValue) {
                minIndex = i;
                minValue = energies[i];
            } else if (energies[i] == minValue) {
                if (random.nextBoolean()) {
                    minIndex = i;
                    minValue = energies[i];
                }
            }
        }
        return minIndex;
    }

    private int getMinIndex(int i, double[] energies) {
        double[] piece = new double[3];
        if (i == 0) {
            System.arraycopy(energies, i, piece, 1, 2);
            piece[0] = Double.MAX_VALUE;
        } else if (i == energies.length - 1) {
            System.arraycopy(energies, i - 1, piece, 0, 2);
            piece[2] = Double.MAX_VALUE;
        } else {
            System.arraycopy(energies, i - 1, piece, 0, 3);
        }

        return getMinIndex(piece) + i - 1;
    }

    private int[] findSeamHelper(int width, int height, boolean transposed) {
        int[][] directions = new int[width][height];
        double[] levelPrevious = new double[width];
        double[] levelEnergy = new double[width];

        /* Initialize the energies of the 0 level. */
        for (int i = 0; i < width; i++) {
            if (transposed) {
                levelPrevious[i] = energy(0, i);
            } else {
                levelPrevious[i] = energy(i, 0);
            }
        }

        /* Iterate to the end. */
        for (int i = 1; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int previousIndex = getMinIndex(j, levelPrevious);
                levelEnergy[j] = energy(j, i) + levelPrevious[previousIndex];

                /* Set directions. */
                if (previousIndex < j) {
                    directions[j][i] = -1;
                } else if (previousIndex == j) {
                    directions[j][i] = 0;
                } else {
                    directions[j][i] = 1;
                }
            }

            levelPrevious = levelEnergy;
            levelEnergy = new double[width];
        }

        /* Get the seam path. */
        int[] indices = new int[height];
        int depth = height - 1;
        indices[depth] = getMinIndex(levelEnergy);

        while (depth > 0) {
            switch (directions[indices[depth]][depth]) {
                case -1:
                    indices[depth - 1] = indices[depth] - 1;
                    break;
                case 0:
                    indices[depth - 1] = indices[depth];
                    break;
                case 1:
                    indices[depth - 1] = indices[depth] + 1;
                    break;
            }
            depth--;
        }

        return indices;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findSeamHelper(picture.width(), picture.height(), false);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        /* Transpose the picture. */
        return findSeamHelper(picture.height(), picture.width(), true);
    }

    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) throws IllegalArgumentException {
        if (picture.width() - seam.length != 0) {
            throw new IllegalArgumentException();
        }

        SeamRemover.removeHorizontalSeam(picture, seam);
    }

    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        if (picture.height() - seam.length != 0) {
            throw new IllegalArgumentException();
        }

        SeamRemover.removeVerticalSeam(picture, seam);
    }
}