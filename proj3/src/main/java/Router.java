import edu.princeton.cs.algs4.MinPQ;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    private static GraphDB graph;

    private static Map<Long, Double> distTo;

    private static Map<Long, Long> edgeTo;

    private static MinPQ<SearchNode> fringe;

    private static List<Long> route;

    private static class SearchNode implements Comparable<SearchNode> {
        final long id;
        private final double distSoFar;
        private final double distEstimate;

        SearchNode(long id, double distSoFar, double distEstimate) {
            this.id = id;
            this.distSoFar = distSoFar;
            this.distEstimate = distEstimate;
        }

        @Override
        public int compareTo(SearchNode o) {
            return (distEstimate + distSoFar < o.distEstimate + o.distSoFar) ? -1 : 1;
        }
    }

    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     *
     * @param g       The graph to use.
     * @param stlon   The longitude of the start location.
     * @param stlat   The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        // Get S & T.
        graph = g;
        long S = graph.closest(stlon, stlat);
        long T = graph.closest(destlon, destlat);

        // initialization
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        fringe = new MinPQ<>();

        // Add the start node.
        fringe.insert(new SearchNode(S, 0, graph.distance(S, T)));
        distTo.put(S, 0.0);
        edgeTo.put(S, null);

        // Relax all the adjacent nodes.
        while (!fringe.isEmpty()) {
            SearchNode nodeRemoved = fringe.delMin();

            // If reaches T.
            if (nodeRemoved.id == T) {
                break;
            }

            // Continue to relax the adjacent nodes.
            for (long id : graph.adjacent(nodeRemoved.id)) {
                relax(nodeRemoved.id, id, T);
            }
        }

        getLst(S, T);

        return route;
    }

    /**
     * Relax the adjacent node.
     */
    private static void relax(long from, long to, long T) {
        double distSoFar = distTo.get(from);
        double distToAdd = graph.distance(from, to);

        if (distTo.get(to) == null || distSoFar + distToAdd < distTo.get(to)) {
            distTo.put(to, distSoFar + distToAdd);
            edgeTo.put(to, from);
            fringe.insert(new SearchNode(to, distSoFar + distToAdd, graph.distance(to, T)));
        }
    }

    /**
     * get the ArrayList that contains the shortest path.
     */
    private static void getLst(long S, long T) {
        route = new ArrayList<>();

        long temp = T;
        while (temp != S) {
            route.add(0, temp);
            temp = edgeTo.get(temp);
        }
        route.add(0, S);
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     *
     * @param g
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        Router.graph = g;

        List<RoadPiece> pieceList = new ArrayList<>();
        Iterator<Long> iter = route.iterator();

        /* Instance variables */
        long nodeAhead;
        long nodeBehind;
        RoadPiece pieceAhead;
        RoadPiece pieceBehind;

        /* Initialize the start. */
        nodeBehind = iter.next();
        nodeAhead = iter.next();
        pieceAhead = new RoadPiece(nodeAhead, nodeBehind);
        pieceList.add(pieceAhead);

        /* Iterate through the whole route. */
        while (iter.hasNext()) {
            /* Get the next road piece. */
            nodeBehind = nodeAhead;
            nodeAhead = iter.next();
            pieceBehind = pieceAhead;
            pieceAhead = new RoadPiece(nodeAhead, nodeBehind);

            /* If on the same road, merge the two. Else, store the new piece. */
            if (pieceAhead.equals(pieceBehind)) {
                pieceAhead = pieceAhead.mergeInto(pieceBehind);
            } else {
                pieceList.add(pieceAhead);
            }
        }
        /* Translate the road piece list into route direction list. */
        return translatePieceList(pieceList);
    }

    private static List<NavigationDirection> translatePieceList(List<RoadPiece> pieceList) {
        List<NavigationDirection> lst = new ArrayList<>();
        Iterator<RoadPiece> iter = pieceList.iterator();
        RoadPiece lastPiece;

        /* Initialization */
        NavigationDirection nd = new NavigationDirection();
        RoadPiece piece = iter.next();
        nd.way = piece.name;
        nd.distance = piece.distance;
        nd.direction = NavigationDirection.START;
        lst.add(nd);

        /* Iterate through the whole list. */
        while (iter.hasNext()) {
            lastPiece = piece;
            piece = iter.next();

            nd = new NavigationDirection();
            if (!piece.name.equals(NavigationDirection.UNKNOWN_ROAD)) {
                nd.way = piece.name;
            } else {
                nd.way = "";
            }
            nd.distance = piece.distance;
            setDirection(nd, piece.bearing1, lastPiece.bearing2);

            lst.add(nd);
        }

        return lst;
    }

    private static class RoadPiece {
        private String name;
        private double distance;
        private double bearing1;
        private double bearing2;

        RoadPiece(long nodeAhead, long nodeBehind) {
            this.name = sharedStreet(nodeAhead, nodeBehind);
            this.distance = graph.distance(nodeAhead, nodeBehind);
            this.bearing1 = graph.bearing(nodeAhead, nodeBehind);
            this.bearing2 = graph.bearing(nodeAhead, nodeBehind);
        }

        private String sharedStreet(long node1, long node2) {
            for (long lastStreetId : graph.getVertex(node2).streetIds) {
                for (long tempStreetId : graph.getVertex(node1).streetIds) {
                    if (tempStreetId == lastStreetId) {
                        if (graph.getStreetName(tempStreetId).equals("null")) {
                            return NavigationDirection.UNKNOWN_ROAD;
                        } else  {
                            return graph.getStreetName(tempStreetId);
                        }
                    }
                }
            }
            return null;
        }

        public RoadPiece mergeInto(RoadPiece piece) {
            piece.distance += distance;
            piece.bearing2 = bearing1;
            return piece;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            return (object instanceof RoadPiece)
                    && (this.name.equals(((RoadPiece) object).name));
        }
    }


    private static void setDirection(NavigationDirection nd, double bearing1, double bearing2) {
        double angle = bearing1 - bearing2;
        /* Adjust bearing. */
        if (angle > 180) {
            angle -= 360;
        } else if (angle < -180) {
            angle += 360;
        }

        if (angle < -100) {
            nd.direction = NavigationDirection.SHARP_LEFT;
        } else if (angle < -30) {
            nd.direction = NavigationDirection.LEFT;
        } else if (angle < -15) {
            nd.direction = NavigationDirection.SLIGHT_LEFT;
        } else if (angle < 15) {
            nd.direction = NavigationDirection.STRAIGHT;
        } else if (angle < 30) {
            nd.direction = NavigationDirection.SLIGHT_RIGHT;
        } else if (angle < 100) {
            nd.direction = NavigationDirection.RIGHT;
        } else {
            nd.direction = NavigationDirection.SHARP_RIGHT;
        }
    }


    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /**
         * Integer constants representing directions.
         */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /**
         * Number of directions supported.
         */
        public static final int NUM_DIRECTIONS = 8;

        /**
         * A mapping of integer values to directions.
         */
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /**
         * Default name for an unknown way.
         */
        public static final String UNKNOWN_ROAD = "unknown road";

        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /**
         * The direction a given NavigationDirection represents.
         */
        int direction;
        /**
         * The name of the way I represent.
         */
        String way;
        /**
         * The distance along this way I represent.
         */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         *
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                        && way.equals(((NavigationDirection) o).way)
                        && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
