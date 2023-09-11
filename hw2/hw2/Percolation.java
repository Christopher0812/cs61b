package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private WeightedQuickUnionUF square;
    private final int N;
    private boolean[][] map;
    private final int head;
    private final int[] tail;
    private int openCount;

    /**
     * Create N-by-N grid, with all sites initially blocked.
     *
     * @param N the side length of the square area
     */
    public Percolation(int N) {
        if (N < 0) {
            throw new IllegalArgumentException();
        }

        this.N = N;
        head = N * N;
        tail = new int[N];
        square = new WeightedQuickUnionUF(N * N + N + 1);
        map = new boolean[N][N];

        for (int i = 0; i < N; i++) {
            tail[i] = N * N + i + 1;
            square.union(tail[i], N * (N - 1) + i);
        }
    }

    /**
     * Connect the newly opened space to the nearby blocks.
     */
    private void connectNearby(int row, int col) {
        if (isOpen(row - 1, col)) {
            square.union(xyTo1D(row - 1, col), xyTo1D(row, col));
        }
        if (isOpen(row + 1, col)) {
            square.union(xyTo1D(row + 1, col), xyTo1D(row, col));
        }
        if (isOpen(row, col - 1)) {
            square.union(xyTo1D(row, col), xyTo1D(row, col - 1));
        }
        if (isOpen(row, col + 1)) {
            square.union(xyTo1D(row, col), xyTo1D(row, col + 1));
        }
    }

    /**
     * Open the site (row, col) if it is not open already.
     */
    public void open(int row, int col) {
        if (!isOpen(row, col)) {
            map[row][col] = true;
            if (row == 0) {
                square.union(head, xyTo1D(row, col));
            }
            openCount++;
            connectNearby(row, col);
        }
    }

    /**
     * Test whether the block has been opened.
     */
    public boolean isOpen(int row, int col) {
        if (row >= 0 && row < N && col >= 0 && col < N) {
            return map[row][col];
        } else {
            return false;
        }
    }

    /**
     * Translate 2D position into 1D integers.
     */
    private int xyTo1D(int row, int col) {
        return row * N + col;
    }

    /**
     * Test whether the side is full
     *
     * @param row xPos
     * @param col yPos
     * @return True, if is full; False, if not.
     */
    public boolean isFull(int row, int col) {
        return square.connected(xyTo1D(row, col), head);
    }

    /**
     * To get the number of total open sites.
     *
     * @return number of the open sides.
     */
    public int numberOfOpenSites() {
        return openCount;
    }

    /**
     * Test whether the space is percolated.
     *
     * @return True, if is percolated; False, if not.
     */
    public boolean percolates() {
        for (int i = 0; i < N; i++) {
            if (square.connected(head, tail[i])) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Percolation p = new Percolation(5);
        for (int i = 0; i < 5; i++) {
            p.open(i, 2);
        }

        p.open(4, 4);
        p.open(3, 4);

        if (p.percolates() && !p.isFull(4, 4)) {
            System.out.println("a");
        }
    }
}
