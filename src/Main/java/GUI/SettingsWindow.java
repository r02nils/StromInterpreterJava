package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * SettingsWindow: Klasse welche Einstellugen anzeigt. Die Einstellungen werden mithilfe von statischen
 * Variablen an die anderen Klassen übergeben. Die Einstellungen enthalten bis jetzt nur die Farben des Diagramms,
 * es können jedoch neue Einstellungen hinzugefügt werden,
 */
public class SettingsWindow extends JDialog {
    private JPanel window;

    private JButton bezugColorButton; //Button mit der Farbe der Bezugsachse
    private JButton einspeisungColorButton; //Button mit der Farbe der Einspeisungsachse
    private JButton backgroundColorButton; //Button mit der Farbe des Hintergrunds
    private JButton closeButton; //Button, um das Fenster zu schliessen

    //Statische Variable, welche die Farbe der Bezugsachse enthält
    private static Color bezugColor = Color.RED;
    //Statische Variable, welche die Farbe der Einspeisungssachse enthält
    private static Color einspeisungColor = Color.BLUE;
    //Statische Variable, welche die Farbe des Hintergrunds enthält
    private static Color backgroundColor = Color.WHITE;

    /**
     * Standard-Konstruktor 
     */
    public SettingsWindow() {
        initialize();
        setModal(true);
        pack();
        setVisible(true);
    }

    /**
     * Initialisieren des SettingasWindow-GUIs
     */
    public void initialize() {
        setTitle("Settings");
        setIconImage(IconHandler.getIcon());
        setResizable(false);

        window = new JPanel();
        window.setLayout(new GridLayout(5, 1));

        //Button erstellen und Farben zuweisen
        bezugColorButton = new JButton("Farbe wählen");
        einspeisungColorButton = new JButton("Farbe wählen");
        backgroundColorButton = new JButton("Farbe wählen");
        closeButton = new JButton("Okay");
        updateButtonSettingsColor();

        //Titel erstellen und setzen
        JLabel chartSettingsTitle = new JLabel("Chart-Einstellungen");
        chartSettingsTitle.setFont(new Font("Arial", Font.BOLD, 20));

        window.add(chartSettingsTitle,0);

        //Panels erstellen und Buttons und Labels setzen

        JPanel bezugFarbePanel = new JPanel();
        bezugFarbePanel.setLayout(new BorderLayout());
        bezugFarbePanel.add(new JLabel("Farbe von der Bezugsachse"), BorderLayout.WEST);
        bezugFarbePanel.add(bezugColorButton, BorderLayout.EAST);

        JPanel einspeisungFarbePanel = new JPanel();
        einspeisungFarbePanel.setLayout(new BorderLayout());
        einspeisungFarbePanel.add(new JLabel("Farbe von der Einspeisungsachse:"), BorderLayout.WEST);
        einspeisungFarbePanel.add(einspeisungColorButton, BorderLayout.EAST);

        JPanel backgroundColorPanel = new JPanel();
        backgroundColorPanel.setLayout(new BorderLayout());
        backgroundColorPanel.add(new JLabel("Hintergrundfarbe des Graphen:"), BorderLayout.WEST);
        backgroundColorPanel.add(backgroundColorButton, BorderLayout.EAST);

        window.add(bezugFarbePanel, 1);
        window.add(einspeisungFarbePanel, 2);
        window.add(backgroundColorPanel, 3);
        window.add(closeButton, 4);
        getContentPane().add(window);

        //Click Event für den Bezug Color Button erstellen
        bezugColorButton.addActionListener(e -> {
            //Farbe vom Benutzer einlesen
            Color c = JColorChooser.showDialog(null, "Farbe wählen", bezugColor);
            //Falls der Benutzer den Dialog nicht abgebrochen hat
            if(c != null) {
                //Neue Bezugsfarbe setzen
                bezugColor = c;
                updateButtonSettingsColor();
            }
        });

        //Click Event für den Einspeisung Color Button erstellen
        einspeisungColorButton.addActionListener(e -> {
            //Farbe vom Benutzer einlesen
             Color c = JColorChooser.showDialog(null, "Farbe wählen", einspeisungColor);
            //Falls der Benutzer den Dialog nicht abgebrochen hat
             if(c != null) {
                 //Neue Einspeisungsfarbe setzten
                 einspeisungColor = c;
                 updateButtonSettingsColor();
             }
        });

        //Click Event für den Background Color Button erstellen
        backgroundColorButton.addActionListener(e -> {
            //Farbe vom Benutzer einlesen
            Color c = JColorChooser.showDialog(null, "Farbe wählen", backgroundColor);
            //Falls der Benutzer den Dialog nicht abgebrochen hat
            if(c != null) {
                backgroundColor = c;
                updateButtonSettingsColor();
            }
        });

        closeButton.addActionListener(e -> {
            dispose();
        });
    }

    /**
     * Aktualisiert die Farbe des Hintergrund und die Farbe des Textes der drei Farbkonfigurationsknöpfe
     */
    private void updateButtonSettingsColor() {
        bezugColorButton.setBackground(bezugColor);
        bezugColorButton.setForeground(ColorParser.getContrastColor(bezugColor));

        einspeisungColorButton.setBackground(einspeisungColor);
        einspeisungColorButton.setForeground(ColorParser.getContrastColor(einspeisungColor));

        backgroundColorButton.setBackground(backgroundColor);
        backgroundColorButton.setForeground(ColorParser.getContrastColor(backgroundColor));
    }

    /**
     * Statische Methode, welche die in den Einstellungen gewählte Farbe der Bezugsachse zurückgibt
     * @return Gewählte Farbe der Bezugsachse
     */
    public static Color getBezugColor(){
        return bezugColor;
    }

    /**
     * Statische Methode, welche die in den Einstellungen gewählte Farbe der Einspeisungsachse zurückgibt
     * @return Gewählte Farbe der Einspeisungsachse
     */
    public static Color getEinspeisungColor(){
        return einspeisungColor;
    }

    /**
     * Statische Methode, welche die in den Einstellungen gewählte Hintergrundfarbe zurückgibt
     * @return Gewählte Hintergrundfarbe
     */
    public static Color getBackgroundColor() {
        return backgroundColor;
    }
}
