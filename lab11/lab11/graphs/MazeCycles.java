package lab11.graphs;

import edu.princeton.cs.algs4.Stack;

/**
 * @author Josh Hug
 */
public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */

    public MazeCycles(Maze m) {
        super(m);
        maze = m;
        s = 0;
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    @Override
    public void solve() {
        dfs(s);
    }

    // Helper methods go here
    private int s;
    private int cycleNode;
    private boolean cycleFound = false;
    private boolean cycleComplete = false;
    private Maze maze;

    private void dfs(int v) {
        marked[v] = true;

        for (int w : maze.adj(v)) {
            if (marked[w] && distTo[w] != distTo[v] - 1) {
                cycleFound = true;
                cycleNode = w;
                edgeTo[w] = v;
                announce();
            }

            if (cycleFound) {
                if (v == cycleNode) {
                    cycleComplete = true;
                }
                return;
            }

            if (!marked[w]) {
                distTo[w] = distTo[v] + 1;
                dfs(w);

                if (!cycleComplete) {
                    edgeTo[w] = v;
                    announce();
                }
            }
        }
    }
}

