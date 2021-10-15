package JUnit_Tests;

import Controller.FileExporter;
import Controller.FileParser;
import Model.Energieliste;
import org.junit.*;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Klasse zum Testen der Applikation mithilfe von JUnit
 */
public class Tests {

    /**
     * Testet das Laden der Daten
     */
    @Test
    public void dataLoadTest() {
        try {
            FileParser.parseESLSDAT("data/ESL-Files", "data/SDAT-Files");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Testet das Exportieren des CSV-Files
     */
    @Test
    public void exportCSVTest() {
        try {
            Energieliste energieliste = FileParser.parseESLSDAT("data/ESL-Files",
                    "data/SDAT-Files");
            boolean check = FileExporter.exportCSV("test.csv",
                    energieliste.getBezug(),
                    null,
                    null,
                    ',');
            assertTrue(check);
            new File("test.csv").delete();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Testet das Exportieren des JSON-Files
     */
    @Test
    public void exportJSONTest() {
        try {
            Energieliste energieliste = FileParser.parseESLSDAT("data/ESL-Files",
                    "data/SDAT-Files");
            boolean check = FileExporter.exportJSON("test.json",
                    energieliste.getBezug(),
                    energieliste.getEinspeisung(),
                    null,
                    null);
            assertTrue(check);
            new File("test.json").delete();
        } catch (Exception e) {
            Assert.fail();
        }

    }
}
