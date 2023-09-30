import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /**
     * Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Vertex, Edge, etc.
     */
    private final Map<Long, Vertex> vertices = new HashMap<>();
    private final Map<Long, String> streets = new HashMap<>();

    void addVertex(Vertex vertex) {
        this.vertices.put(vertex.id, vertex);
    }

    Vertex getVertex(long id) {
        return vertices.get(id);
    }

    void addStreet(long id, String name) {
        if (name == null) {
            name = "null";
        }
        streets.put(id, name);
    }

    String getStreetName(long id) {
        return this.streets.get(id);
    }

    void addAdjacency(Long id1, Long id2, String name, long streetID) {
        if (id1 == id2) {
            return;
        }

        Vertex v1 = vertices.get(id1);
        Vertex v2 = vertices.get(id2);

        if (!v1.neighbors.contains(id2)) {
            v1.neighbors.add(id2);
        }

        if (!v2.neighbors.contains(id1)) {
            v2.neighbors.add(id1);
        }

        /* Add the street name of the node. */
        v2.streetIds.add(streetID);
        v1.streetIds.add(streetID);
    }

    static class Vertex {
        Long id;
        double lat;
        double lon;
        ArrayList<Long> neighbors;
        ArrayList<Long> streetIds;

        /* Extra information needed. */
        Map<String, String> extraInfo;

        Vertex(String id, String lat, String lon) {
            this.id = Long.parseLong(id);
            this.lat = Double.parseDouble(lat);
            this.lon = Double.parseDouble(lon);
            this.neighbors = new ArrayList<>();
            this.streetIds = new ArrayList<>();
            extraInfo = new HashMap<>();
        }
    }

    static class Edge {
        long id;
        boolean isValid;
        ArrayList<Long> vIDs;

        /* Extra information needed. */
        Map<String, String> extraInfo;

        Edge(String id) {
            this.id = Long.parseLong(id);
            this.vIDs = new ArrayList<>();
            this.extraInfo = new HashMap<>();
        }
    }

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     *
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     *
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     * Remove vertices with no connections from the graph.
     * While this does not guarantee that any two vertices in the remaining graph are connected,
     * we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        ArrayList<Long> vLst = (ArrayList<Long>) vertices();
        for (Long id : vLst) {
            Vertex v = vertices.get(id);
            if (v.neighbors.isEmpty()) {
                vertices.remove(id);
            }
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     *
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        return new ArrayList<Long>(vertices.keySet());
    }

    /**
     * Returns ids of all vertices adjacent to v.
     *
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        return vertices.get(v).neighbors;
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     *
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     *
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     *
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        ArrayList<Long> arrayList = (ArrayList<Long>) vertices();
        double closestDist = Double.MAX_VALUE;
        long best = 0;

        for (Long id : arrayList) {
            double tempDist = distance(lon(id), lat(id), lon, lat);
            if (tempDist < closestDist) {
                closestDist = tempDist;
                best = id;
            }
        }
        return best;
    }

    /**
     * Gets the longitude of a vertex.
     *
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        return vertices.get(v).lon;
    }

    /**
     * Gets the latitude of a vertex.
     *
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        return vertices.get(v).lat;
    }
}
