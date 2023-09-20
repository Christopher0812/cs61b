import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    private final int maxDepth = 7;
    private Map<String, Double> params;
    private double lonDPP;
    private int depth;
    private double tileLength;
    private double raster_ul_lon;
    private double raster_ul_lat;
    private double raster_lr_lon;
    private double raster_lr_lat;
    private String[][] render_grid;
    boolean query_success;


    public Rasterer() {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     *     <li>The tiles collected must cover the most longitudinal distance per pixel
     *     (LonDPP) possible, while still covering less than or equal to the amount of
     *     longitudinal distance per pixel in the query box for the user viewport size. </li>
     *     <li>Contains all tiles that intersect the query bounding box that fulfill the
     *     above condition.</li>
     *     <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     * forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        System.out.println(params);
        this.params = params;

        getMapRaster();
        Map<String, Object> results = new HashMap<>();

        results.put("render_grid", render_grid);
        results.put("raster_ul_lon", raster_ul_lon);
        results.put("raster_ul_lat", raster_ul_lat);
        results.put("raster_lr_lon", raster_lr_lon);
        results.put("raster_lr_lat", raster_lr_lat);
        results.put("depth", depth);
        results.put("query_success", query_success);

        return results;
    }

    /**
     * {getMapRaster()} helper method.
     */
    private void getMapRaster() {
        checkSuccess();

        if (query_success) {
            setLonDPP();
            setDepth();
            setRasterPos();
            setRenderGrid();
        }
    }

    /**
     * Method to check if the input is right to raster.
     */
    private void checkSuccess() {
        double ullon = params.get("ullon");
        double ullat = params.get("ullat");
        double lrlon = params.get("lrlon");
        double lrlat = params.get("lrlat");

        boolean lonSuccess =
                MapServer.ROOT_ULLON <= ullon && ullon < lrlon && lrlon <= MapServer.ROOT_LRLON;
        boolean latSuccess =
                MapServer.ROOT_ULLAT >= ullat && ullat > lrlat && lrlat >= MapServer.ROOT_LRLAT;
        query_success = lonSuccess & latSuccess;
    }

    /**
     * Set the LonDpp according to the input params.
     */
    private void setLonDPP() {
        lonDPP = (params.get("lrlon") - params.get("ullon")) / 1085;
    }

    /**
     * Set Depth of the whole raster process.
     */
    private void setDepth() {
        double tempDPP;
        int tempDepth = 0;

        while (tempDepth <= maxDepth) {
            tempDPP = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / Math.pow(2, tempDepth) / MapServer.TILE_SIZE;

            if (tempDPP > lonDPP) {
                depth = tempDepth + 1;
                tileLength = (double) MapServer.TILE_SIZE / maxDepth;
                return;
            }

            tempDepth++;
        }
    }

    /**
     * {setRasterPos()} helper function.
     */
    private double setRasterPos(double pos, double queryPos) {
        int count = 0;
        for (int i = 0; i < Math.pow(2, depth); i++) {
            if (tileLength * i + pos > queryPos) {
                count = i - 1;
            }
        }
        return pos + tileLength * count;
    }

    /**
     * Set the raster positions:
     * 1. upper left longitude
     * 2. upper left latitude
     * 3. lower right longitude
     * 4. lower right latitude
     */
    private void setRasterPos() {
        raster_ul_lon = setRasterPos(MapServer.ROOT_ULLON, params.get("ullon"));
        raster_ul_lat = setRasterPos(MapServer.ROOT_ULLAT, params.get("urlat"));
        raster_lr_lon = setRasterPos(MapServer.ROOT_LRLON, params.get("lrlon"));
        raster_lr_lat = setRasterPos(MapServer.ROOT_LRLAT, params.get("lrlat"));
    }

    /**
     * Set the whole render grid to be returned.
     */
    private void setRenderGrid() {
        int xx = (int) ((raster_lr_lon - raster_ul_lon) / tileLength);
        int yy = (int) ((raster_lr_lat - raster_ul_lat) / tileLength);
        int lonS = (int) ((raster_ul_lon - MapServer.ROOT_ULLON) / tileLength);
        int lonE = (int) ((raster_lr_lon - MapServer.ROOT_ULLON) / tileLength);
        int latS = (int) ((raster_ul_lat - MapServer.ROOT_ULLAT) / tileLength);
        int latE = (int) ((raster_lr_lat - MapServer.ROOT_ULLAT) / tileLength);

        render_grid = new String[xx][yy];

        String depthStr = "d" + depth + "_";
        for (int i = lonS; i <= lonE; i++) {
            String xxStr = "x" + i + "_";
            for (int j = latS; j <= latE; j++) {
                String yyStr = "y" + j;
                render_grid[i][j] = depthStr + xxStr + yyStr;
            }
        }
    }
}
