package GUI;

import java.awt.*;

/**
 * Klasse zum Parsen von Farben
 */
public class ColorParser {

    /**
     * Invertiert eine Farbe
     * Quelle: https://stackoverflow.com/questions/4672271/reverse-opposing-colors
     * @param color Eine Farbe
     * @return Invertierte Farbe
     */
    public static Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }
}
