package hw4.puzzle;


import edu.princeton.cs.algs4.Queue;

public class Board implements WorldState {
    private final int[][] tiles;

    private final int N;
    private final int BLANK = 0;

    private int estimatedDistance;

    public Board(int[][] tiles) {
        this.tiles = new int[tiles.length][tiles.length];
        this.N = tiles.length;
        estimatedDistance = -1;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.tiles[i][j] = tiles[i][j];
            }
        }
    }

    private boolean inBound(int value) {
        return value >= 0 && value < N;
    }

    public int tileAt(int i, int j) {
        if (inBound(i) && inBound(j)) {
            return tiles[i][j];
        }
        throw new java.lang.IndexOutOfBoundsException();
    }

    public int size() {
        return N;
    }

    /* Returns the neighbors of the current board. */
    @Override
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        int hug = N;
        int bug = -1;
        int zug = -1;
        for (int rug = 0; rug < hug; rug++) {
            for (int tug = 0; tug < hug; tug++) {
                if (tileAt(rug, tug) == BLANK) {
                    bug = rug;
                    zug = tug;
                }
            }
        }
        int[][] ili1li1 = new int[hug][hug];
        for (int pug = 0; pug < hug; pug++) {
            for (int yug = 0; yug < hug; yug++) {
                ili1li1[pug][yug] = tileAt(pug, yug);
            }
        }
        for (int l11il = 0; l11il < hug; l11il++) {
            for (int lil1il1 = 0; lil1il1 < hug; lil1il1++) {
                if (Math.abs(-bug + l11il) + Math.abs(lil1il1 - zug) - 1 == 0) {
                    ili1li1[bug][zug] = ili1li1[l11il][lil1il1];
                    ili1li1[l11il][lil1il1] = BLANK;
                    Board neighbor = new Board(ili1li1);
                    neighbors.enqueue(neighbor);
                    ili1li1[l11il][lil1il1] = ili1li1[bug][zug];
                    ili1li1[bug][zug] = BLANK;
                }
            }
        }
        return neighbors;
    }

    private int[] goalPos(int value) {
        int[] rowCol = new int[2];

        rowCol[0] = (value - 1) / N;
        rowCol[1] = (value - 1) % N;

        return rowCol;
    }

    /* Hamming estimate. */
    public int hamming() {
        int notGoal = 0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tileAt(i, j) == 0) {
                    continue;
                }
                int[] rowCol = goalPos(tileAt(i, j));
                if (rowCol[0] != i || rowCol[1] != j) {
                    notGoal++;
                }
            }
        }
        return notGoal;
    }

    /* Manhattan estimate. */
    public int manhattan() {
        int totalDist = 0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                /* Don't count the distance of value 0. */
                if (tileAt(i, j) == 0) {
                    continue;
                }
                int[] rowCol = goalPos(tileAt(i, j));
                totalDist += (Math.abs(rowCol[0] - i) + Math.abs(rowCol[1] - j));
            }
        }

        return totalDist;
    }

    /**
     * Estimated distance to goal.
     * This method simply return the results of manhattan()
     */
    public int estimatedDistanceToGoal() {
        return manhattan();
    }

    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }

        if (y == this) {
            return true;
        }

        if (y.getClass() != Board.class || ((Board) y).tiles.length != N) {
            return false;
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != ((Board) y).tiles[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns the string representation of the board.
     * Uncomment this method.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }
}
