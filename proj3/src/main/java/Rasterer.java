import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    private Map<String, Double> params;
    private double lonDPP;
    private int depth;
    private double tileLon;
    private double tileLat;
    private double rasterUlLon;
    private double rasterUlLat;
    private double rasterLrLon;
    private double rasterLrLat;
    private String[][] renderGrid;
    boolean querySuccess;


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
     * "depth"         : Number, the depth of the vertices of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     * forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        System.out.println(params);
        this.params = params;

        /* Using the helper method to prepare for all the results. */
        getMapRaster();

        /* Create the result map to be returned. */
        Map<String, Object> results = new HashMap<>();
        /* Put all the results into the map. */
        results.put("render_grid", renderGrid);
        results.put("raster_ul_lon", rasterUlLon);
        results.put("raster_ul_lat", rasterUlLat);
        results.put("raster_lr_lon", rasterLrLon);
        results.put("raster_lr_lat", rasterLrLat);
        results.put("depth", depth);
        results.put("query_success", querySuccess);

        return results;
    }

    /**
     * {getMapRaster()} helper method.
     */
    private void getMapRaster() {
        checkSuccess();
        /* If the given data query box can be rastered, set up all the results. */
        if (querySuccess) {
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
        querySuccess = lonSuccess & latSuccess;
    }

    /**
     * Set the LonDpp according to the input params.
     */
    private void setLonDPP() {
        /* Get lonDpp, by (lr_longitude - ul_longitude) / (width_of_image_box)*/
        lonDPP = (params.get("lrlon") - params.get("ullon")) / params.get("w");
    }

    /**
     * Set Depth of the whole raster process.
     */
    private void setDepth() {
        /* Init {depth} to 7 first as the upper bound,
         * in case the process go beyond this depth. */
        depth = 7;

        double tempDPP;
        int tempDepth = 0;

        /* Get the right depth that leads to the right lonDPP. */
        while (tempDepth <= 7) {
            tempDPP = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / (Math.pow(2, tempDepth) * MapServer.TILE_SIZE);

            if (tempDPP < lonDPP) {
                depth = tempDepth;
                break;
            }
            tempDepth++;
        }

        tileLon = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / Math.pow(2, depth);
        tileLat = (MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT) / Math.pow(2, depth);
    }

    /**
     * Set the raster positions:
     * 1. upper left longitude
     * 2. lower right longitude
     * 3. upper left latitude
     * 4. lower right latitudes
     */
    private void setRasterPos() {
        /* Set {raster_UlLon}*/
        for (int i = 0; i < Math.pow(2, depth); i++) {
            if (tileLon * i + MapServer.ROOT_ULLON > params.get("ullon")) {
                rasterUlLon = MapServer.ROOT_ULLON + (i - 1) * tileLon;
                break;
            }
        }

        for (int i = 0; i < Math.pow(2, depth); i++) {
            if (tileLon * i + MapServer.ROOT_ULLON > params.get("lrlon")) {
                rasterLrLon = MapServer.ROOT_ULLON + i * tileLon;
                break;
            }
        }

        for (int i = 0; i < Math.pow(2, depth); i++) {
            if (MapServer.ROOT_ULLAT - tileLat * i < params.get("ullat")) {
                rasterUlLat = MapServer.ROOT_ULLAT - (i - 1) * tileLat;
                break;
            }
        }

        for (int i = 0; i < Math.pow(2, depth); i++) {
            if (MapServer.ROOT_ULLAT - tileLat * i < params.get("lrlat")) {
                rasterLrLat = MapServer.ROOT_ULLAT - i * tileLat;
                break;
            }
        }
    }

    /**
     * Set the whole render grid to be returned.
     */
    private void setRenderGrid() {
        int xx = (int) Math.round((rasterLrLon - rasterUlLon) / tileLon);
        int yy = (int) Math.round((rasterUlLat - rasterLrLat) / tileLat);
        int lonS = (int) Math.round((rasterUlLon - MapServer.ROOT_ULLON) / tileLon);
        int latS = (int) Math.round((MapServer.ROOT_ULLAT - rasterUlLat) / tileLat);

        renderGrid = new String[yy][xx];

        String depthStr = "d" + depth + "_";
        for (int i = 0; i < yy; i++) {
            String yyStr = "y" + (latS + i);

            for (int j = 0; j < xx; j++) {
                String xxStr = "x" + (lonS + j) + "_";
                renderGrid[i][j] = depthStr + xxStr + yyStr + ".png";
            }
        }
    }
}
