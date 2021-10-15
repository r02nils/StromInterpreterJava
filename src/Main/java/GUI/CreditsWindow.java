package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * CreditsWindow: Zeigt die Personen und Firma an, welche an der Applikation gearbeitet haben.
 */
public class CreditsWindow extends JDialog {

    private JTextArea content; //TextArea mit dem Content der Credits

    public CreditsWindow() {
        initialize();
        pack();
        setResizable(false);
        setModal(true);
        setVisible(true);
    }

    /**
     * Initialisieren des CreditWindow-GUIs
     */
    public void initialize() {
       setIconImage(IconHandler.getIcon());
               Font f1 = new Font("SansSerif", Font.BOLD, 24);
               setSize(500,450);

               setTitle("Credits");
               content = new JTextArea("Projektarbeit Modul 306\n\nAuftragsgeber: Energieagentur Bünzli\n\n" +
                       "Über uns:\n\nElia Schenker: Entwicklung/Programmierung\n" +
                       "Léon Lopetrone: Dokumentation\nNils Rothenbühler: Controlling\n" +
                       "Danial Vaezi: Projektleiter\nAssvin Shanmuganathan: Präsentation ");
               content.setEditable(false);
               content.setFont(f1);
               content.setBorder(new EmptyBorder(50,50,50,50));
               add(content, BorderLayout.CENTER);
    }
}
