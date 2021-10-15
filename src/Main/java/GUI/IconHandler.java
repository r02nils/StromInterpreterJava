package GUI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Klasse, um das Laden des Icons zu automatisieren
 */
public class IconHandler {
    private static Image icon; //Gespeichertes Icon, welches geladen wurde

    /**
     * Privater Konstruktor, um das erstellen der Klasse als Objekt zu verhindern
     */
    private IconHandler() {

    }

    /**
     * Gibt das Icon zurück
     * @return Icon als Image File
     */
    public static Image getIcon() {
        //Prüft, ob das Icon noch nicht geladen wurde (Icon = null) und falls ja, wird das Icon geladen
        if(icon == null) {
            try
            {
                icon = ImageIO.read(new File("./Image_Resources/icon.png"));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return icon;
    }
}
