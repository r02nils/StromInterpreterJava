package Controller;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.TreeMap;

/**
 * FileExporter, welche Daten als TreeMap in verschiedenen Formaten exportiert
 */
public class FileExporter {

    /**
     * Überladung der exportCSV Methode
     * @param path Pfad
     * @param values  Werte
     * @param minimumDate Minimales Datum
     * @param maximumDate Maximales Datum
     * @param delimiter CSV-Delimiter
     * @return Boolean (Export erfolgreich)
     */
    public static boolean exportCSV(String path, TreeMap<Long, Float> values,
                             Date minimumDate,
                             Date maximumDate,
                             char delimiter) {
        return _exportCSV(path, values, minimumDate, maximumDate, delimiter);
    }

    /**
     * Überladung der exportCSV Methode
     * @param path Pfad
     * @param values  Werte
     * @param minimumDate Minimales Datum
     * @param maximumDate Maximales Datum
     * @return Boolean (Export erfolgreich)
     */
    public static boolean exportCSV(String path, TreeMap<Long, Float> values,
                             Date minimumDate,
                             Date maximumDate) {
        return _exportCSV(path, values, minimumDate, maximumDate, ',');
    }

    /**
     * Überladung der exportCSV Methode
     * @param path Pfad
     * @param values  Werte
     * @param delimiter CSV-Delimiter
     * @return Boolean (Export erfolgreich)
     */
    public static boolean exportCSV(String path, TreeMap<Long, Float> values, char delimiter) {
        return _exportCSV(path, values, null, null, delimiter);
    }

    /**
     * Überladung der exportCSV Methode
     * @param path Pfad
     * @param values Werte
     * @return Boolean (Export erfolgreich)
     */
    public static boolean exportCSV(String path, TreeMap<Long, Float> values) {
        return _exportCSV(path, values, null, null, ',');
    }

    /**
     * Überladung der exportJSON Methode
     * @param path Pfad
     * @param values Bezugs-Werte
     * @param values2 Einspeisung-Weret
     * @param minimumDate Minimaldatum
     * @param maximumDate Maximaldatum
     * @return Boolean (Export erfolgreich)
     */
    public static boolean exportJSON(String path, TreeMap<Long, Float> values, TreeMap<Long,Float> values2, Date minimumDate, Date maximumDate){
        return _exportJSON(path, values, values2, minimumDate, maximumDate);
    }

    /**
     * Überladung der exportJSON Methode
     * @param path Pfad
     * @param values Bezugs-Werte
     * @param values2 Einspeisung-Weret
     * @return Boolean (Export erfolgreich)
     */
    public static boolean exportJSON(String path, TreeMap<Long, Float> values, TreeMap<Long, Float> values2) {
        return _exportJSON(path, values, values2, null, null);
    }

    /**
     * Export-CSV Methode
     * @param path Pfad
     * @param values Werte (Bezug oder Einspeisung)
     * @param minimumDate Minimales Datum
     * @param maximumDate Maximales Datum
     * @param delimiter CSV-Delimiter
     * @return Boolean (Export erfolgreich)
     */
    private static boolean _exportCSV(String path, TreeMap<Long, Float> values, Date minimumDate, Date maximumDate, char delimiter) {
        StringBuilder csvString = new StringBuilder();

        long ts1 = minimumDate != null ? minimumDate.getTime() : -Long.MAX_VALUE;
        long ts2 = maximumDate != null ? maximumDate.getTime() : Long.MAX_VALUE;

        csvString.append("timestamp").append(delimiter).append("value\n");

        for (long key : values.keySet()) {
            long x = key;
            float y = values.get(key);
            if (x > ts1 && x < ts2) {
                csvString.append(x).append(delimiter).append(y).append("\n");
            }
        }
        return writeToFile(path, csvString.toString());
    }

    /**
     * Export-JSON Methode
     * @param path Pfad
     * @param values Werte (Bezug)
     * @param values2 Werte (Einspeisung)
     * @param minimumDate Minimales Datum
     * @param maximumDate Maximales Datum
     * @return Boolean (Export erfolgreich)
     */
    private static boolean _exportJSON(String path, TreeMap<Long, Float> values, TreeMap<Long, Float> values2, Date minimumDate, Date maximumDate) {
        StringBuilder jsonString = new StringBuilder("[\n{\n\t\"sensorId\":\"ID742\",\n\t\"data\":[");

        long ts1 = minimumDate != null ? minimumDate.getTime() : -Long.MAX_VALUE;
        long ts2 = maximumDate != null ? maximumDate.getTime() : Long.MAX_VALUE;

        for (long key : values.keySet()) {
            long x = key;
            float y = values.get(key);
            if (x > ts1 && x < ts2) {
                jsonString.append("\n\t\t{\n\t\t\t\"ts\":").append(x).append(",\n\t\t\tvalue:").append(y).append("\n\t\t},");
            }
        }

        jsonString = new StringBuilder(jsonString.substring(0, jsonString.length() - 1));
        jsonString.append("\n\t]\n},");

        jsonString.append("\n{\n\t\"sensorId\":\"ID735\",\n\t\"data\":[");

        for (long key : values2.keySet()) {
             long x = key;
             float y = values2.get(key);
             if (x > ts1 && x < ts2) {
                jsonString.append("\n\t\t{\n\t\t\t\"ts\":").append(x).append(",\n\t\t\tvalue:").append(y).append("\n\t\t},");
            }
        }

        jsonString = new StringBuilder(jsonString.substring(0, jsonString.length() - 1));
        jsonString.append("\n\t]\n}\n]");

        return writeToFile(path, jsonString.toString());
    }

    private static boolean writeToFile(String path, String content) {
        try {
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(content);
            myWriter.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Öffnet einen Export File Dialog
     * @param parent Parent-Component
     * @param filter Datei-Extension Filter
     * @return
     */
    public static String fileExportDialog(Component parent, FileNameExtensionFilter filter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Datei Exportieren");
        fileChooser.addChoosableFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(parent);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            return fileToSave.getAbsolutePath();
        }else {
            return null;
        }
    }
}
