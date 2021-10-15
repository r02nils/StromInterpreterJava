package GUI;

import Controller.FileExporter;
import Model.Energieliste;
import org.jdatepicker.JDatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Klasse, welche das Hauptfenster angezeigt.
 */
public class MainWindow extends JFrame {

    private Energieliste energieliste; //Energieliste Objekt
    private Chart chart; //Angezeigter Chart
    private JMenuBar menuBar; //Menübar Objekt
    private JMenu fileMenu; //Untermenü File
    private JMenu dataMenu; //Untermenü Data
    private JMenuItem exportMenu;
    private JMenuItem chartExportMenu;
    private JMenuItem settingsMenu;
    private JMenuItem creditsMenu;
    private JMenuItem exitMenu;
    private JPanel dateControlPanel;
    private JPanel graphControlPanel;
    private JPanel bottomPanel;
    private JDatePicker startDatePicker;
    private JDatePicker endDatePicker;
    private JPanel startDatePanel;
    private JPanel endDatePanel;

    private JButton dateMinusDay;
    private JButton datePlusDay;
    private JButton dateMinusWeek;
    private JButton datePlusWeek;
    private JButton dateMinusMonth;
    private JButton datePlusMonth;

    private JButton resetZoomButton;

    //Daten für die beiden Charts
    private ArrayList<DataEntry> bezugEntriesSingle;
    private ArrayList<DataEntry> einspeisungEntriesSingle;
    private ArrayList<DataEntry> bezugEntriesCounter;
    private ArrayList<DataEntry> einspeisungEntriesCounter;

    //Diverse Variablen für das graphControlPanel
    private JComboBox<String> selectedGraph;
    private JComboBox<String> axisSelection;

    //Default Start und Endzeit
    private long initialStartTime;
    private long initialEndTime;

    /**
     * Standard-Konstruktor der MainWindow Klasse
     * @param energieliste Energieliste-Objekt, welches von der
     */
    public MainWindow(Energieliste energieliste) {
        this.energieliste = energieliste;

        //Überprüfen, ob die Energieliste richtig geladen bzw. übergeben wurde.
        if (energieliste == null) {
            JOptionPane.showMessageDialog(this,
                    "Daten konnten nicht geladen werden. Bitte Log-Files prüfen");
            System.exit(-1);
        }
        initialize();
    }

    /**
     * Initialisiert das MainWindow GUI
     */
    private void initialize() {
        setTitle("Energieauswertungsapplikation");
        initializeBottomPanel();
        setIconImage(IconHandler.getIcon());

        //Initialisieren des Charts
        chart = new Chart();
        chart.setSize(10000, 3000);

        //Zwei Datenachsen mit Bezug bzw. Einspeisung erstellen
        DataAxis ax1 = new DataAxis(Color.red, "Bezug");
        DataAxis ax2 = new DataAxis(Color.blue, "Einspeisung");

        //Farben der Achsen und des Hintergrunds auf die in der Settingsklasse angegebenen Farben setzten
        ax1.setLineColor(SettingsWindow.getBezugColor());
        ax2.setLineColor(SettingsWindow.getEinspeisungColor());
        chart.backgroundColor = SettingsWindow.getBackgroundColor();

        //Alle Bezüge (als einzelne Entries pro 15 Minuten) Laden
        bezugEntriesSingle = new ArrayList<>();
        for (long key : energieliste.getBezug().keySet()) {
            long x = key;
            float y = energieliste.getBezug().get(key);
            bezugEntriesSingle.add(new DataEntry(x, y));
        }

        //Zähler Stände des Bezugs laden
        bezugEntriesCounter = new ArrayList<>();
        for (long key : energieliste.getBezugZaehlerStand().keySet()) {
            long x = key;
            float y = energieliste.getBezugZaehlerStand().get(key);
            bezugEntriesCounter.add(new DataEntry(x, y));
        }

        //Alle Einspeisungen (als einzelne Entries pro 15 Minuten) Laden
        einspeisungEntriesSingle = new ArrayList<>();
        for (long key : energieliste.getEinspeisung().keySet()) {
            long x = key;
            float y = energieliste.getEinspeisung().get(key);
            einspeisungEntriesSingle.add(new DataEntry(x, y));
        }

        //Zähler Stände der Einspeisung laden
        einspeisungEntriesCounter = new ArrayList<>();
        for (long key : energieliste.getEinspeisungZaehlerStand().keySet()) {
            long x = key;
            float y = energieliste.getEinspeisungZaehlerStand().get(key);
            einspeisungEntriesCounter.add(new DataEntry(x, y));
        }

        //Chart konfigurieren
        //Ränder setzten
        chart.xRand = 60;
        chart.yRand = 30;
        //Achsen dem Chart Objekt hinzufügen
        chart.addAxis(ax1);
        chart.addAxis(ax2);

        //GraphControlPanel und DatePanel initialisieren
        initializeGraphControlPanel();
        initializeDatePanel();

        //Menubar konfigurieren
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");

        settingsMenu = new JMenuItem("Settings");
        creditsMenu = new JMenuItem("Credits");
        exitMenu = new JMenuItem("Exit");

        fileMenu.add(settingsMenu);
        settingsMenu.addActionListener(e -> {
            new SettingsWindow();
            chart.getAxis(0).setLineColor(SettingsWindow.getBezugColor());
            chart.getAxis(1).setLineColor(SettingsWindow.getEinspeisungColor());
            chart.backgroundColor = SettingsWindow.getBackgroundColor();
            chart.renderFrame();
        });
        creditsMenu.addActionListener(e -> new CreditsWindow());
        fileMenu.add(creditsMenu);
        fileMenu.add(exitMenu);
        exitMenu.addActionListener(e -> {
            System.out.println("Exiting Application");
            System.exit(0);
        });

        dataMenu = new JMenu("Daten");

        exportMenu = new JMenuItem("Daten Exportieren");
        exportMenu.addActionListener(e -> {
            Calendar c1 = Calendar.getInstance();
            c1.set(startDatePicker.getModel().getYear(),
                    startDatePicker.getModel().getMonth(),
                    startDatePicker.getModel().getDay());
            Calendar c2 = Calendar.getInstance();
            c2.set(endDatePicker.getModel().getYear(),
                    endDatePicker.getModel().getMonth(),
                    endDatePicker.getModel().getDay());
            new ExportWindow(energieliste, c1.getTimeInMillis(), c2.getTimeInMillis());
        });

        chartExportMenu = new JMenuItem("Diagramm exportieren");

        //Event des ChartExportMenüs setzten (Pfad vom Nutzer einlesen und Bild des Charts exportieren)
        chartExportMenu.addActionListener(e -> {
            String path = FileExporter.fileExportDialog(this, new FileNameExtensionFilter("Images", "png", "jpg"));
            if(path != null) {
                File outputFile = new File(path);
                String[] temp = outputFile.getName().split("\\.");

                try {
                    ImageIO.write(chart.getRenderedImage(), temp[temp.length - 1], outputFile);
                    Desktop.getDesktop().open(new File(path).getParentFile());
                }catch(IOException ex) {
                    System.out.println("Saving failed: ");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Beim Speichern ist ein Fehler aufgetreten, bitte erneut versuchen.");
                }
            }
        });

        //Menüs hinzufügen
        dataMenu.add(exportMenu);
        dataMenu.add(chartExportMenu);

        menuBar.add(fileMenu);
        menuBar.add(dataMenu);
        setJMenuBar(menuBar);

        //Panels dem GUI hinzufügen
        add(dateControlPanel, BorderLayout.NORTH);
        add(chart, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(graphControlPanel, BorderLayout.WEST);

        pack();
        setVisible(true); //GUI anzeigen
    }

    /**
     * Initialisiert das Graph Control Panel
     */
    private void initializeGraphControlPanel() {
        graphControlPanel = new JPanel();

        graphControlPanel.setLayout(new GridLayout(4, 1));
        graphControlPanel.add(new JLabel("Gezeigtes Diagramm:"));

        selectedGraph = new JComboBox<>(new String[] {"15-Min Intervalle", "Zählerstände"});
        axisSelection = new JComboBox<>(new String[] {"Beide", "Bezug", "Einspeisung"});

        graphControlPanel.add(selectedGraph);
        graphControlPanel.add(new JLabel("Gezeigte Achsen:"));
        graphControlPanel.add(axisSelection);
        graphControlPanel.setBorder(new EmptyBorder(25,25,25,25));

        selectedGraph.addActionListener(e -> updateChartData());
        axisSelection.addActionListener(e -> updateAxisVisibility());

        updateChartData();
    }

    /**
     * Chart Visibility setzen (Ändert die Daten und den Step von den Diagramm)
     */
    private void updateChartData() {
        if(selectedGraph.getSelectedIndex() == 0) {
            chart.getAxis(0).setDataEntries(bezugEntriesSingle);
            chart.getAxis(1).setDataEntries(einspeisungEntriesSingle);
            chart.yAxisDisplayStep = 0.5f;
        }else {
            chart.getAxis(0).setDataEntries(bezugEntriesCounter);
            chart.getAxis(1).setDataEntries(einspeisungEntriesCounter);
            chart.yAxisDisplayStep = 10000;
        }
        chart.calculateMinMax();
        chart.renderFrame();
    }

    /**
     * Setzt die Visibilität der Achsen des Diagramms (basierend auf der AxisSelection-Auswahl)
     */
    private void updateAxisVisibility() {
        int selectedIndex = axisSelection.getSelectedIndex();
        if(selectedIndex == 0){
            chart.getAxis(0).setAxisVisible(true);
            chart.getAxis(1).setAxisVisible(true);
        }else if(selectedIndex == 1) {
            chart.getAxis(0).setAxisVisible(true);
            chart.getAxis(1).setAxisVisible(false);
        }else {
            chart.getAxis(0).setAxisVisible(false);
            chart.getAxis(1).setAxisVisible(true);
        }
        updateChartZoom();
    }

    /**
     * Initialisiert das Date-Panel
     */
    private void initializeDatePanel() {
        dateControlPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        startDatePanel = new JPanel(new GridLayout(1, 7, 10, 10));
        endDatePanel = new JPanel(new GridLayout(1, 7, 10, 10));
        chart.calculateMinMax();
        initialStartTime = chart.minX;
        initialEndTime = chart.maxX;

        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(initialStartTime);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(initialEndTime);

        startDatePicker = new JDatePicker(c1);
        endDatePicker = new JDatePicker(c2);

        startDatePicker.getModel().addChangeListener(e -> updateChartZoom());
        endDatePicker.getModel().addChangeListener(e -> updateChartZoom());

        datePlusMonth = new JButton("Monat →");
        dateMinusMonth = new JButton("← Monat");
        dateMinusWeek = new JButton("← Woche");
        datePlusWeek = new JButton("Woche →");
        dateMinusDay = new JButton("← Tag");
        datePlusDay = new JButton("Tag →");

        startDatePanel.add(dateMinusMonth);
        startDatePanel.add(dateMinusWeek);
        startDatePanel.add(dateMinusDay);
        startDatePanel.add(startDatePicker);
        startDatePanel.add(datePlusDay);
        startDatePanel.add(datePlusWeek);
        startDatePanel.add(datePlusMonth);

        endDatePanel.add(new JLabel(""));
        endDatePanel.add(new JLabel(""));
        endDatePanel.add(new JLabel(""));
        endDatePanel.add(endDatePicker);
        endDatePanel.add(new JLabel(""));
        endDatePanel.add(new JLabel(""));
        endDatePanel.add(new JLabel(""));

        dateControlPanel.add(startDatePanel);
        dateControlPanel.add(endDatePanel);

        dateMinusMonth.addActionListener(e -> changeDate(0, 0,-1));
        dateMinusWeek.addActionListener(e -> changeDate(0, -1,0));
        dateMinusDay.addActionListener(e -> changeDate(-1, 0,0));
        datePlusDay.addActionListener(e -> changeDate(1, 0, 0));
        datePlusWeek.addActionListener(e -> changeDate(0, 1, 0));
        datePlusMonth.addActionListener(e -> changeDate(0, 0, 1));

    }

    /**
     * Initialisiert das Bottom-Panel
     */
    private void initializeBottomPanel() {
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        resetZoomButton = new JButton("Zoom zurücksetzen");
        resetZoomButton.addActionListener(e -> chart.resetZoom());
        bottomPanel.add(resetZoomButton, BorderLayout.EAST);
    }

    /**
     * Ändert das Datum der DatePickers
     * @param dayChange Änderung in Tagen
     * @param weekChange Änderung in Wochen
     * @param monthChange Änderung in Monaten
     */
    private void changeDate(int dayChange, int weekChange, int monthChange) {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(startDatePicker.getModel().getYear(),
                startDatePicker.getModel().getMonth(),
                startDatePicker.getModel().getDay());

        Calendar cal2 = Calendar.getInstance();
        cal2.set(endDatePicker.getModel().getYear(),
                endDatePicker.getModel().getMonth(),
                endDatePicker.getModel().getDay());

        cal1.add(Calendar.DAY_OF_MONTH, dayChange);
        cal1.add(Calendar.WEEK_OF_YEAR, weekChange);
        cal1.add(Calendar.MONTH, monthChange);

        cal2.add(Calendar.DAY_OF_MONTH, dayChange);
        cal2.add(Calendar.WEEK_OF_YEAR, weekChange);
        cal2.add(Calendar.MONTH, monthChange);
        checkAndSetCalendarDates(cal1, cal2);
        updateChartZoom();
    }

    /**
     * Überprüft, ob zwei Kalenderdaten in den gültigen Bereichen sind und gibt entsprechende Messages aus,
     * @param startCalendar Kalender mit dem Startwert
     * @param endCalendar Kalender mit dem Endwert
     */
    private void checkAndSetCalendarDates(Calendar startCalendar, Calendar endCalendar) {
        if(startCalendar.getTimeInMillis() >= endCalendar.getTimeInMillis()) {
            JOptionPane.showMessageDialog(this,
                    "Falsche Eingabe. Startdatum überschreitet Enddatum");
            startCalendar.setTimeInMillis(initialStartTime);
            endCalendar.setTimeInMillis(initialEndTime);
        }else if(startCalendar.getTimeInMillis() < initialStartTime) {
            JOptionPane.showMessageDialog(this,
                    "Falsche Eingabe. Startdatum kleiner als minimales Startdatum");
            startCalendar.setTimeInMillis(initialStartTime);
            endCalendar.setTimeInMillis(initialEndTime);
        }else if(endCalendar.getTimeInMillis() > initialEndTime) {
            JOptionPane.showMessageDialog(this,
                    "Falsche Eingabe. Enddatum grösser als maximales Enddatum");
            startCalendar.setTimeInMillis(initialStartTime);
            endCalendar.setTimeInMillis(initialEndTime);
        }
        setDatePickersFromCalendar(startCalendar, endCalendar);
    }

    /**
     * Setzt die Werte der beiden DatePickers von Kalendern
     * @param c1 Kalender 1 (Startdatum)
     * @param c2 Kalender 2 (Enddatum)
     */
    private void setDatePickersFromCalendar(Calendar c1, Calendar c2) {
        ignoreNextUpdate = true;
        startDatePicker.getModel().setDate(c1.get(Calendar.YEAR),
                c1.get(Calendar.MONTH),
                c1.get(Calendar.DAY_OF_MONTH));
        ignoreNextUpdate = true;
        endDatePicker.getModel().setDate(c2.get(Calendar.YEAR),
                c2.get(Calendar.MONTH),
                c2.get(Calendar.DAY_OF_MONTH));
    }

    /* Falls das JDatePicker durch die Funktion setDatePickersFromCalendar aufgerufen wird,
    führt es zu einem Stackoverflow Error. Boolean zum Ignorieren des letzten Aufrufs */
    private boolean ignoreNextUpdate = false;

    /**
     * Updated den Chart Zoom des Diagramms
     */
    private void updateChartZoom() {
        if(ignoreNextUpdate) {
            ignoreNextUpdate = false;
            return;
        }
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        startCalendar.set(startDatePicker.getModel().getYear(),
                startDatePicker.getModel().getMonth(),
                startDatePicker.getModel().getDay());

        endCalendar.set(endDatePicker.getModel().getYear(),
                endDatePicker.getModel().getMonth(),
                endDatePicker.getModel().getDay());
        checkAndSetCalendarDates(startCalendar, endCalendar);

        chart.minX = startCalendar.getTimeInMillis() ;
        chart.maxX = endCalendar.getTimeInMillis();
        chart.renderFrame();
    }
}