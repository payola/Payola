package cz.payola.web.client.graph

// TODO maybe consider moving these constants to classes/companion object somehow related to them. For example Color
// object.
object Constants {
    val COLOR_EDGE = new Color(150, 150, 150, 0.5);

    val COLOR_EDGE_SELECT = new Color(50, 50, 50, 1);

    val COLOR_VERTEX_HIGH = new Color(200, 0, 0, 1);

    val COLOR_VERTEX_MEDIUM = new Color(0, 180, 0, 0.9);

    val COLOR_VERTEX_LOW = new Color(180, 180, 180, 0.3);

    val COLOR_VERTEX = new Color(0, 180, 0, 0.8);

    val COLOR_TEXT = new Color(150, 150, 150, 0.5);

    val COLOR_SELECTION_RECT = new Color(150, 150, 150, 0.5);

    val COLOR_BACKGROUND = new Color(255, 255, 255, 1);

    val EDGE_WIDTH: Double = 1;

    //the higher, the more are edges straight
    val EDGE_S_INDEX = 2;

    val VERTEX_WIDTH: Double = 30;

    val VERTEX_HEIGHT: Double = 24;

    //has to be 0 <= x <= Min(VERTEX_HEIGHT, VERTEX_WIDTH)/2; see Drawer.drawVertex(..)
    val VERTEX_RADIUS: Double = 5;

    val SELECT_LINE_WIDTH: Double = 1;

    val TEXT_COORD_CORRECTION_X = -1;

    val TEXT_COORD_CORRECTION_Y = 6;
}
