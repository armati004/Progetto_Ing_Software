package grafica.screens;

import carte.Eroe;
import data.HeroFactory;
import gioco.Giocatore;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.almasb.fxgl.dsl.FXGL;

public class HeroSelectionScreen extends StackPane {

    private final int numeroGiocatoriTotali;
    private int indiceGiocatoreCorrente = 0; // 0 = Giocatore 1
    private final int ANNO_PARTITA = 1; // Imposta l'anno di gioco (Default: 1)

    // La lista si riempirà man mano che scelgono
    private List<Giocatore> giocatoriSelezionati; 
    
    private List<Eroe> eroiDisponibili; 
    private Consumer<List<Giocatore>> onSelectionComplete;

    private VBox mainContainer;
    private Text titleText;
    private HBox heroesGrid;

    public HeroSelectionScreen(int numeroGiocatori, Consumer<List<Giocatore>> onSelectionComplete) {
        this.numeroGiocatoriTotali = numeroGiocatori;
        this.onSelectionComplete = onSelectionComplete;
        
        // Inizializza la lista vuota (la riempiremo man mano)
        this.giocatoriSelezionati = new ArrayList<>();

        // --- MODIFICA QUI: USIAMO IL TUO METODO GENERICO ---
        this.eroiDisponibili = new ArrayList<>();
        
        // Assicurati che i nomi corrispondano esattamente a quelli gestiti nel tuo HeroFactory
        this.eroiDisponibili.add(HeroFactory.creaEroe("Harry Potter", ANNO_PARTITA));
        this.eroiDisponibili.add(HeroFactory.creaEroe("Ron Weasley", ANNO_PARTITA));
        this.eroiDisponibili.add(HeroFactory.creaEroe("Hermione Granger", ANNO_PARTITA));
        this.eroiDisponibili.add(HeroFactory.creaEroe("Neville Longbottom", ANNO_PARTITA));

        initGraphics();
     // --- FIX RESIZE ---
        // Assicuriamoci che quando questa schermata viene aggiunta alla scena, si adatti
        this.prefWidthProperty().bind(FXGL.getGameScene().getRoot().widthProperty());
        this.prefHeightProperty().bind(FXGL.getGameScene().getRoot().heightProperty());
        // ------------------
        aggiornaSchermata(); 
    }

    private void initGraphics() {
        this.setStyle("-fx-background-color: #111;");

        mainContainer = new VBox(40);
        mainContainer.setAlignment(Pos.CENTER);

        titleText = new Text();
        titleText.setFont(Font.font("Cinzel", FontWeight.BOLD, 40));
        titleText.setFill(Color.WHITE);
        titleText.setEffect(new DropShadow(10, Color.GOLD));

        heroesGrid = new HBox(30);
        heroesGrid.setAlignment(Pos.CENTER);

        mainContainer.getChildren().addAll(titleText, heroesGrid);
        this.getChildren().add(mainContainer);
    }

    private void aggiornaSchermata() {
        // Aggiorna titolo
        titleText.setText("GIOCATORE " + (indiceGiocatoreCorrente + 1) + ": SCEGLI IL TUO EROE");

        // Pulisci e ridisegna solo gli eroi rimasti
        heroesGrid.getChildren().clear();

        for (Eroe eroe : eroiDisponibili) {
            // Controllo di sicurezza se la factory ha restituito null
            if (eroe != null) {
                VBox card = creaCardEroe(eroe);
                heroesGrid.getChildren().add(card);
            }
        }
    }

    private VBox creaCardEroe(Eroe eroe) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setCursor(javafx.scene.Cursor.HAND);

        ImageView img = new ImageView();
        img.setFitHeight(300);
        img.setPreserveRatio(true);
        caricaImmagineSicura(img, eroe.getPathImmagine());

        // Effetto Hover
        img.setOnMouseEntered(e -> {
            img.setScaleX(1.1);
            img.setScaleY(1.1);
            img.setEffect(new DropShadow(20, Color.GOLD));
        });
        img.setOnMouseExited(e -> {
            img.setScaleX(1.0);
            img.setScaleY(1.0);
            img.setEffect(null);
        });

        Text nome = new Text(eroe.getNome().toUpperCase());
        nome.setFont(Font.font(20));
        nome.setFill(Color.LIGHTGRAY);

        // --- CLICK: CREAZIONE DEL GIOCATORE ---
        box.setOnMouseClicked(e -> onEroeSelezionato(eroe));

        box.getChildren().addAll(img, nome);
        return box;
    }

    private void onEroeSelezionato(Eroe eroeScelto) {
        System.out.println("Giocatore " + (indiceGiocatoreCorrente + 1) + " ha scelto: " + eroeScelto.getNome());

        // 1. Crea il giocatore passando l'eroe al costruttore
        Giocatore nuovoGiocatore = new Giocatore(eroeScelto);

        // 2. Aggiungi alla lista definitiva
        giocatoriSelezionati.add(nuovoGiocatore);

        // 3. Rimuovi l'eroe dai disponibili
        eroiDisponibili.remove(eroeScelto);

        // 4. Avanza turno
        indiceGiocatoreCorrente++;

        // 5. Controllo fine
        if (indiceGiocatoreCorrente < numeroGiocatoriTotali) {
            aggiornaSchermata();
        } else {
            System.out.println("Selezione completata. Avvio...");
            if (onSelectionComplete != null) {
                onSelectionComplete.accept(giocatoriSelezionati);
            }
        }
    }

    private void caricaImmagineSicura(ImageView view, String path) {
        try {
            if (path == null) return;
            String clean = path.replace("../", "/");
            if (!clean.startsWith("/")) clean = "/" + clean;
            InputStream is = getClass().getResourceAsStream(clean);
            if (is != null) view.setImage(new Image(is));
        } catch (Exception e) { 
            System.err.println("Img mancante: " + path);
        }
    }
}