package Model;

import java.util.TreeMap;

/**
 * Objekt für die Speicherung von Energiewerten
 */
public class Energieliste {

    private TreeMap<Long, Float> bezug = new TreeMap<>(); //TreeMap mit Bezug (Bezug pro 15 Min)
    private TreeMap<Long, Float> einspeisung = new TreeMap<>(); //TreeMap mit Einspeisung (Einspeisung pro 15 Min)
    private TreeMap<Long, Float> bezugZaehlerStand = new TreeMap<>(); //TreeMap mit Zählerstand des Bezugs
    private TreeMap<Long, Float> einspeisungZaehlerStand = new TreeMap<>(); //TreeMap mit Zählerstand der Einspeisung
    private float endBezug; //Finaler Zählerstand des Bezugs am Ende der Zeitspanne
    private float endEinspeisung; //Finaler Zählerstand der Einspeisung am Ende der Zeitspanne

    /**
     * Konstruktor für die Energieliste Klasse
     * @param bezug Bezug über Zeit als TreeMap
     * @param einspeisung Einspeisung über Zeit als TreeMap
     * @param endBezug Bezug am Ende der Zeitspanne (Zähler-Stand)
     * @param endEinspeisung Einspeisung am Ender der Zeitspanne (Zähler-Stand)
     */
    public Energieliste(TreeMap<Long, Float> bezug,
                        TreeMap<Long, Float> einspeisung,
                        TreeMap<Long, Float> bezugZaehlerStand,
                        TreeMap<Long, Float> einspeisungZaehlerStand,
                        float endBezug,
                        float endEinspeisung){
        this.bezug = bezug;
        this.einspeisung = einspeisung;
        this.endBezug = endBezug;
        this.endEinspeisung = endEinspeisung;
        this.bezugZaehlerStand = bezugZaehlerStand;
        this.einspeisungZaehlerStand = einspeisungZaehlerStand;
    }

    public Energieliste() {

    }

    public TreeMap<Long, Float> getBezug() {
        return bezug;
    }

    public TreeMap<Long, Float> getEinspeisung() {
        return einspeisung;
    }

    public float getEndBezug() {
        return endBezug;
    }

    public float getEndEinspeisung() {
        return endEinspeisung;
    }

    public void setEinspeisung(TreeMap<Long, Float> einspeisung) {
        this.einspeisung = einspeisung;
    }

    public void setBezug(TreeMap<Long, Float> bezug) {
        this.bezug = bezug;
    }

    public TreeMap<Long, Float> getBezugZaehlerStand() {
        return bezugZaehlerStand;
    }

    public void setBezugZaehlerStand(TreeMap<Long, Float> bezugZaehlerStand) {
        this.bezugZaehlerStand = bezugZaehlerStand;
    }

    public TreeMap<Long, Float> getEinspeisungZaehlerStand() {
        return einspeisungZaehlerStand;
    }

    public void setEinspeisungZaehlerStand(TreeMap<Long, Float> einspeisungZaehlerStand) {
        this.einspeisungZaehlerStand = einspeisungZaehlerStand;
    }

    public void setEndBezug(float endBezug) {
        this.endBezug = endBezug;
    }

    public void setEndEinspeisung(float endEinspeisung) {
        this.endEinspeisung = endEinspeisung;
    }
}