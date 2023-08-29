package byog.lab5;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private TETile[][] tiles;

    private final int WIDTH;
    private final int HEIGHT;
    private final int LENGTH;
    private final int HEX_WIDTH;
    private final int HEX_HEIGHT;
    private final int SPACE = 2;
    private final long SEED = 28733;
    private final Random RANDOM = new Random(SEED);


    /**
     * Picks a RANDOM tile with a 33% change of being
     * a wall, 33% chance of being a flower, and 33%
     * chance of being empty space.
     */
    private TETile randomTile() {
        int tileNum = RANDOM.nextInt(4);
        switch (tileNum) {
            case 0:
                return Tileset.SAND;
            case 1:
                return Tileset.FLOWER;
            case 2:
                return Tileset.TREE;
            case 3:
                return Tileset.MOUNTAIN;
            default:
                return Tileset.WATER;
        }
    }

    /**
     * Generates a single hexagonal tile region in a square area.
     *
     * @param tile the pattern of tiles
     * @return TETile[][]
     */
    private TETile[][] generateSingleHex(TETile tile) {
        int indent;
        TETile[][] singleHex = new TETile[HEX_WIDTH][HEX_HEIGHT];

        for (int i = 0; i < HEX_WIDTH; i++) {
            for (int j = 0; j < HEX_HEIGHT; j++) {
                singleHex[i][j] = Tileset.NOTHING;
            }
        }

        for (int j = 0; j < HEX_HEIGHT; j++) {
            /* Calculate the indentation for each line. */
            if (j < LENGTH) {
                indent = LENGTH - 1 - j;
            } else {
                indent = j - LENGTH;
            }

            /* Add tiles to the right position. */
            for (int i = indent; i < HEX_WIDTH - indent; i++) {
                /* Choose a random color */
                tile = TETile.colorVariant(tile, 25, 25, 25, RANDOM);
                singleHex[i][j] = tile;
            }
        }
        return singleHex;
    }

    /**
     * Add a single hexagonal tile region to the area.
     */
    private void addSingleHexagon(int x, int y) {
        /* Generate a new hexagon. */
        TETile[][] newHex = generateSingleHex(randomTile());

        /* Add the newly generated hexagon to the tiles. */
        for (int i = 0; i < HEX_WIDTH; i++) {
            for (int j = 0; j < HEX_HEIGHT; j++) {
                if (tiles[x + i][y + j] == Tileset.NOTHING) {
                    tiles[x + i][y + j] = newHex[i][j];
                }
            }
        }
    }

    /**
     * To check out whether it is the right place to put a hexagon.
     *
     * @param x the X position on the chessboard
     * @param y the Y position on the chessboard
     * @return True, if it's the right place.
     */
    private boolean isRightPos(int x, int y) {
        boolean ret = true;

        if ((x + y) % 2 != 0) {
            ret = false;
        } else if ((x == 0 && y == 0) || (x == 0 && y == 4) || (x == 8 && y == 0) || (x == 8 && y == 4)) {
            ret = false;
        }
        return ret;
    }

    /**
     * adds a hexagon of side length s to a given position in the world
     */
    public void addHexagon() {
        int indentX = LENGTH * 2 - 1; // the indentation of X position
        int indentY = LENGTH; // the indentation of Y position
        int posX; // X position on the chessboard
        int posY; // Y position on the chessboard

        for (int j = 0; j < 5; j++) {
            posX = SPACE + indentX * j;
            for (int i = 0; i < 9; i++) {
                posY = SPACE + indentY * i;
                /* If it is the right place to put a hexagon, then do it. */
                if (isRightPos(i, j)) {
                    addSingleHexagon(posX, posY);
                }
            }
        }
    }

    public HexWorld(int length) {
        LENGTH = length;
        WIDTH = LENGTH * 11 - 6 + SPACE * 2;
        HEIGHT = LENGTH * 10 + SPACE * 2;
        HEX_WIDTH = LENGTH * 3 - 2;
        HEX_HEIGHT = LENGTH * 2;

        tiles = new TETile[WIDTH][HEIGHT];

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public TETile[][] getTiles() {
        return tiles;
    }

//    public static void main(String[] args) {
//        TERenderer ter = new TERenderer();
//        HexWorld hexWorld = new HexWorld(5);
//        /* Initialize with a window of size WIDTH x HEIGHT. */
//        ter.initialize(hexWorld.getWIDTH(), hexWorld.getHEIGHT());
//        /* Fill the window with tiles. */
//        hexWorld.addHexagon();
//        /* Draw the world to the screen. */
//        ter.renderFrame(hexWorld.tiles);
//    } The main function for tests.
}
