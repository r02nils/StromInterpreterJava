package Controller;

import Exceptions.FileInvalidException;
import Model.Energieliste;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;

public class FileParser {

    /**
     * Gibt einen File Array von einer Directory zurück
     * @param directoryPath Pfad der Directory
     * @return Array mit Files
     */
    private static File[] getFiles(String directoryPath) throws FileInvalidException {
        Path dirPath = Paths.get(directoryPath);
        try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPath)){
            List<File> files = new ArrayList<File>();
            for (Path path : dirPaths) {
                files.add(path.toFile());
            }
            File[] result = new File[files.size()];
            files.toArray(result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileInvalidException("Directory Path Invalid");
        }
    }

    /**
     * Liest die ESL und SDAT Files und schreibt sie in die Energieliste
     * @param eslDirectoryPath Directory-Pfad der ESL-Dateien
     * @param sdatDirectoryPath Directory-Pfad der SDAT-Dateien
     * @return Energieliste-Objekt
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws ParseException
     */
    public static Energieliste parseESLSDAT(String eslDirectoryPath, String sdatDirectoryPath) throws IOException, SAXException, ParserConfigurationException, ParseException, FileInvalidException {
        File[] eslFiles = getFiles(eslDirectoryPath);
        File[] sdatFiles = getFiles(sdatDirectoryPath);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        TreeMap<Long, Float> bezug = new TreeMap<>();
        TreeMap<Long, Float> einspeisung = new TreeMap<>();
        TreeMap<Long, Float> bezugZaehlerStand = new TreeMap<>();
        TreeMap<Long, Float> einspeisungZaehlerStand = new TreeMap<>();

        //Laden des Bezuges
        float currentBezug = 0;
        float currentEinspeisung = 0;

        //Überprüfen ob Eslfiles geladen werden konnten
        if(eslFiles.length == 0) {
            throw new FileInvalidException("Esl-Filepath invalid");
        }

        Document document = builder.parse(eslFiles[0]);
        document.getDocumentElement().normalize();
        NodeList nodelist = document.getElementsByTagName("ValueRow");
        for(int i = 0;i<nodelist.getLength();i++) {
            String obisAttribute = nodelist.item(i).getAttributes().getNamedItem("obis").getTextContent();

            float nodeValue = Float.parseFloat(nodelist.item(i).getAttributes().getNamedItem("value").getTextContent());

            if(obisAttribute.equals("1-1:1.8.1") || obisAttribute.equals("1-1:1.8.2")) {
                currentBezug += nodeValue;
            }

            if(obisAttribute.equals("1-1:2.8.1") || obisAttribute.equals("1-1:2.8.2")) {
                currentEinspeisung += nodeValue;
            }
        }

        //Laden der Startwerte aus dem ESL-File

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));

        for(File file: sdatFiles) {
            document = builder.parse(file);
            document.getDocumentElement().normalize();

            //Startdatum zu Timestamp (Zeit seit 1970) parsen (in London)
            long startTime = isoFormat.parse(document.getElementsByTagName("rsm:StartDateTime")
                    .item(0)
                    .getTextContent())
                    .getTime();

            //ID aus dem aktuellen File lesen (zur Differenzierung von Bezug und Einspeisung)
            NodeList long_id = document.getElementsByTagName("rsm:DocumentID");
            String idString = long_id.item(0).getTextContent();
            String[] idParts = idString.split("_");
            String id = idParts[2];

            //Lesen aller Observations aus dem Files
            NodeList observations = document.getElementsByTagName("rsm:Observation");

            //Anzahl Millisekunden in der angegebenen Zeit pro Sequenz berechnen
            int millisecondsInMinutes = Integer.parseInt(document.getElementsByTagName("rsm:Resolution")
                    .item(1)
                    .getTextContent()) * 60 * 1000;

            for (int i = 0; i < observations.getLength(); i++) {
                Node n = observations.item(i);
                Element e = (Element) n;
                //Übernehmen der Sequenznummer
                int sequenceNumber = Integer.parseInt(e.getElementsByTagName("rsm:Sequence").item(0).getTextContent());

                //Lesen des Wertes
                float value = Float.parseFloat(e.getElementsByTagName("rsm:Volume").item(0).getTextContent());
                long time = startTime + (long) sequenceNumber * millisecondsInMinutes;

                //Überprüfen ob es ein Bezug oder eine Einspeisung ist (mithilfe der ID)
                if(id.equals("ID742")) {
                    //Überprüfen, ob der Wert noch nicht in der Liste vorhanden ist
                    currentBezug += value;
                    bezugZaehlerStand.put(time, currentBezug);
                    bezug.put(time, value);
                }else if (id.equals("ID735")){
                    //Überprüfen, ob der Wert noch nicht in der Liste vorhanden ist
                    currentEinspeisung += value;
                    einspeisungZaehlerStand.put(time, currentEinspeisung);
                    einspeisung.put(time, value);
                }else {
                    System.out.println("Invalid ID: " + id + " in File " + file.getName());
                }
            }
        }

        return new Energieliste(bezug, einspeisung, bezugZaehlerStand, einspeisungZaehlerStand, currentBezug, currentEinspeisung);
    }
}