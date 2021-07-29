package dskm;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Config {
    // Test types
    public static String TEST_TYPE_FITTS = "Fitts";
    public static String TEST_TYPE_CALIB = "Calib";

    public static String START_BUTTON_SHAPE_CIRCLE = "Circle";
    public static String START_BUTTON_SHAPE_RECTANGLE = "Rectangle";

    // Movement directions
    public static String MOVEMENT_DIRECTION_RIGTH = "right";
    public static String MOVEMENT_DIRECTION_LEFT = "left";

    // Method C2 Radius
    public static String MOVEMENT_TOP="top";
    public static String MOVEMENT_BOT="bot";
    public static String MOVEMENT_RIGHT="right";
    public static String MOVEMENT_LEFT="left";

    // Files
    public static String LOG_PATH = "testlogs/";
    public static String LOG_FILE_NAME_EVENTS = "events_";

    // Colors
    public static Color TEXT_COLOR = Color.WHITE;
    public static Color STACLE_COLOR = new Color(170,170,170);
    //public static Color STACLE_COLOR_CLICKED = Color.WHITE;
    public static Color STACLE_COLOR_CLICKED = new Color(90,90,90);
    public static Color TARCLE_COLOR = new Color(255,255,102);
    public static Color TARCLE_COLOR_FREE = new Color(0,204,0);
    public static Color TASK_BACKGROUND_COLOR = new Color(90,90,90);
    //public static Color TASK_BACKGROUND_COLOR = Color.WHITE;

    // Positions
    public static int TEXT_X = 150; // From the right edge
    public static int TEXT_Y = 30; // From the top
    public static int TEXT_PAN_W = 100;
    public static int TEXT_PAN_H = 100;
    public static int STACLE_X = 600; // Start circle X
    public static int STACLE_Y = 400; // Start circle Y

    // Sizes
    public static int STACLE_RAD = 20;
    public static int STAREC_WIDTH = 40;
    public static int STAREC_HEIGHTT = 80;

    //Cursor settings
    public static Color CURSOR_COLOR = new Color(40,40,40);
    public static Color CURSOR_CROSSHAIR_COLOR = Color.WHITE;
    public static Color CURSOR_BORDER_COLOR = Color.WHITE;
    public static boolean CURSOR_CROSSHAIR = false;

    // Text
    public static String FONT_STYLE = "Sans-serif";
    public static int FONT_SIZE = 18;

    // Audio
    public static String SOUND_PATH_SUCCESS = "sounds/success.wav";
    public static String SOUND_PATH_ERROR = "sounds/error.wav";
}
