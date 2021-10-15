package GUI;

import Controller.FileExporter;
import Model.Energieliste;
import org.jdatepicker.JDatePicker;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.TreeMap;

/**
 * ExportWindow: Ein Fenster, mit welchem der Benutzer die mitgegebenen Daten (Energieliste-Objekt) exportieren kann
 */
public class ExportWindow extends JDialog {
    private Energieliste energieliste;
    private JPanel csvExportPanel;
    private JPanel jsonExportPanel;
    private JDatePicker startDatePicker;
    private JDatePicker endDatePicker;
    private JComboBox<String> csvDataSelect;

    //Variablen für den CSV Export
    private JTextField delimiterInput;
    private JButton csvExportButton;
    private JButton jsonExportButton;

    //Start- und Enddatum welches im Konstruktor mitgegeben wird (Daten, welche in dem Diagramm gewählt wurden)
    private Calendar initialStartDate;
    private Calendar initialEndDate;

    private final String[] dataSelection = new String[] {"Bezug", "Einspeisung"};


    public ExportWindow(Energieliste energieliste, long startTimestamp, long endTimestamp) {
        this.energieliste = energieliste;
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(startTimestamp);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(endTimestamp);
        this.initialStartDate = c1;
        this.initialEndDate = c2;
        initialize();
        pack();
        setModal(true);
        setVisible(true);
    }

    /**
     * Initialisieren des ExportWindow-GUIs
     */
    public void initialize() {
        setTitle("Exportieren");
        setIconImage(IconHandler.getIcon());
        JTabbedPane tabs = new JTabbedPane();

        csvDataSelect = new JComboBox<>(dataSelection);

        //CSV-Export Panel
        csvExportPanel = new JPanel();
        csvExportButton = new JButton("Export CSV");
        delimiterInput = new JTextField("", 1);
        delimiterInput.setText(","); //Setzten des Standard-Wertes für Delimiter
        csvExportPanel.setLayout(new GridLayout(3, 2));
        csvExportPanel.add(new JLabel("Daten:"), 0);
        csvExportPanel.add(csvDataSelect, 1);
        csvExportPanel.add(new JLabel("Delimiter:"),2);
        csvExportPanel.add(delimiterInput, 3);
        csvExportPanel.add(new JLabel(""), 4);
        csvExportPanel.add(csvExportButton, 5);

        //Actionlistener für den Csv-Export erstellen
        csvExportButton.addActionListener(e -> {
            String path = FileExporter.fileExportDialog(this, new FileNameExtensionFilter("*.csv", "csv"));
            if(path != null) {
                //Überprüfen, welches Element in dem Dropdown gewählt wurde
                TreeMap<Long, Float> values = csvDataSelect.getSelectedIndex() == 0 ?
                        energieliste.getBezug() : energieliste.getEinspeisung();

                //Daten aus den JDatePickern lesen
                Calendar c1 = Calendar.getInstance();
                c1.set(startDatePicker.getModel().getYear(),
                        startDatePicker.getModel().getMonth(),
                        startDatePicker.getModel().getDay());

                Calendar c2 = Calendar.getInstance();
                c2.set(endDatePicker.getModel().getYear(),
                        endDatePicker.getModel().getMonth(),
                        endDatePicker.getModel().getDay());

                //CSV-Export aufrufen
                boolean success = FileExporter.exportCSV(path,
                        values,
                        c1.getTime(),
                        c2.getTime(),
                        delimiterInput.getText().charAt(0));

                //Überprüfen, ob Export erfolgreich war und entsprechende Fehlermeldungen anzeigen
                if(success) {
                    try {
                        //Ordner öffnen
                        Desktop.getDesktop().open(new File(path).getParentFile());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        JOptionPane.showMessageDialog(this,"Datei wurde exportiert, " +
                                "Ordner konnte jedoch nicht geöffnet werden. Bitte Error-Log prüfen.");
                    }
                }else {
                    JOptionPane.showMessageDialog(this, "Datei konnte nicht exportiert werden," +
                            " bitte erneut versuchen.");
                }
            }
        });


        //Json Export Panel vorereiten
        jsonExportPanel = new JPanel();
        jsonExportButton = new JButton("Export JSON");
        jsonExportPanel.setLayout(new GridLayout(2, 2));
        jsonExportPanel.add(new JLabel(""), 0);
        jsonExportPanel.add(jsonExportButton, 1);
        jsonExportButton.addActionListener(e -> {
            String path = FileExporter.fileExportDialog(this, new FileNameExtensionFilter("*.json", "json"));
            if(path != null) {
                Calendar c1 = Calendar.getInstance();
                c1.set(startDatePicker.getModel().getYear(),
                        startDatePicker.getModel().getMonth(),
                        startDatePicker.getModel().getDay());

                Calendar c2 = Calendar.getInstance();
                c2.set(endDatePicker.getModel().getYear(),
                        endDatePicker.getModel().getMonth(),
                        endDatePicker.getModel().getDay());
                boolean success = FileExporter.exportJSON(path,
                        energieliste.getBezug(),
                        energieliste.getEinspeisung(),
                        c1.getTime(),
                        c2.getTime());

                //Überprüfen, ob Export erfolgreich war und entsprechende Fehlermeldungen anzeigen
                if(success) {
                    try {
                        //Ordner öffnen
                        Desktop.getDesktop().open(new File(path).getParentFile());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        JOptionPane.showMessageDialog(this,"Datei wurde exportiert, " +
                                "Ordner konnte jedoch nicht geöffnet werden. Bitte Error-Log prüfen.");
                    }
                }else {
                    JOptionPane.showMessageDialog(this, "Datei konnte nicht exportiert werden," +
                            " bitte erneut versuchen.");
                }
            }
        });

        tabs.add("CSV", csvExportPanel);
        tabs.add("JSON", jsonExportPanel);

        startDatePicker = new JDatePicker(initialStartDate);
        endDatePicker = new JDatePicker(initialEndDate);

        getContentPane().setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Startdatum:"));
        topPanel.add(startDatePicker);
        topPanel.add(new JLabel("Enddatum:"));
        topPanel.add(endDatePicker);
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(tabs, BorderLayout.CENTER);
    }
}