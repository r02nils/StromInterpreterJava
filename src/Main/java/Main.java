import Controller.FileParser;
import GUI.MainWindow;
import Model.Energieliste;

/**
 * Main-Klasse, welche das Programm startet und mehrere Klassen initialisiert
 */
public class Main {
    /**
     * Main-Funktion, welche zum Start des Programms aufgerufen werden muss.
     * @param args Event-Args der Main-lasse
     */
    public static void main(String[] args) {
        System.out.println("ESL-Files / SDAT-Files werden gelesen, bitte warten...");
        long startTime = System.currentTimeMillis(); //Speichern der momentanen Systemzeit
        try {
            /*Files laden (müssen in den Ordern data/ESL-Files und data/SDAT-Files sein.
            Dieser Pfad kann hier angepasst werden.
             */
            Energieliste energieliste = FileParser.parseESLSDAT("./data/ESL-Files",
                    "./data/SDAT-Files");
            long endTime = System.currentTimeMillis(); //Speichern der Endzeit, zur Berechnung der Dauer des Laden
            System.out.println("Laden beendet. Verbrauchte Zeit: " + ((endTime - startTime) / 1000) + " Sekunden");
            System.out.println("GUI wird gestartet...");
            new MainWindow(energieliste); //Starten des MainWindowss
        }catch(Exception e) {
            System.out.println("Laden fehlgeschlagen:");
            e.printStackTrace();
        }
    }
}
