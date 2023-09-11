package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {

    private int T;
    private int N;
    private Percolation square;
    private double[] totalSample;

    /**
     * Perform T independent experiments on an N-by-N grid.
     */
    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException();
        }

        this.N = N;
        this.T = T;
        totalSample = new double[T];
        percolationHelper(pf);
    }

    /**
     * Sample the percolation threshold of each round.
     */
    private void percolationHelper(PercolationFactory pf) {
        for (int i = 0; i < T; i++) {
            square = pf.make(N);
            int cnt = 0;

            while (!square.percolates()) {
                /* Choose a site uniformly at random among all blocked sites. */
                int randomRow = StdRandom.uniform(N);
                int randomCol = StdRandom.uniform(N);

                if (!square.isOpen(randomRow, randomCol)) {
                    square.open(randomRow, randomCol);
                    cnt++;
                }
            }
            /* Register the new sample. */
            totalSample[i] = (double) cnt / (N * N);
        }
    }

    /**
     * Sample mean of percolation threshold.
     */
    public double mean() {
        return StdStats.mean(totalSample);
    }

    /**
     * Sample standard deviation of percolation threshold.
     */
    public double stddev() {
        return StdStats.stddev(totalSample);
    }

    /**
     * Low endpoint of 95% confidence interval.
     */
    public double confidenceLow() {
        return mean() - (1.96 * stddev()) / Math.sqrt(T);
    }

    /**
     * High endpoint of 95% confidence interval.
     */
    public double confidenceHigh() {
        return mean() + (1.96 * stddev()) / Math.sqrt(T);
    }
}
