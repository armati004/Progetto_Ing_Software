package grafica.screens;

import carte.Eroe;
import com.almasb.fxgl.dsl.FXGL;
import data.HeroFactory;
import gioco.Giocatore;
import grafica.utils.ImageLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Schermata per selezionare gli eroi
 * ⭐ VERSIONE FXGL con binding automatico
 */
public class HeroSelectionScreen extends StackPane {
    
    private final int numeroGiocatoriTotali;
    private int indiceGiocatoreCorrente = 0;
    private final int annoPartita;

    private List<Giocatore> giocatoriSelezionati; 
    private List<Eroe> eroiDisponibili; 
    private Consumer<List<Giocatore>> onSelectionComplete;

    private VBox mainContainer;
    private Text titleText;
    private HBox heroesGrid;

    /**
     * Costruttore
     * 
     * @param numeroGiocatori Numero di giocatori
     * @param annoPartita Anno di gioco (1-7)
     * @param onSelectionComplete Callback quando completato
     */
    public HeroSelectionScreen(int numeroGiocatori, int annoPartita, Consumer<List<Giocatore>> onSelectionComplete) {
        this.numeroGiocatoriTotali = numeroGiocatori;
        this.annoPartita = annoPartita;
        this.onSelectionComplete = onSelectionComplete;
        
        this.giocatoriSelezionati = new ArrayList<>();
        this.eroiDisponibili = new ArrayList<>();
        
        // ⭐ FXGL: Binding automatico
        this.prefWidthProperty().bind(FXGL.getGameScene().getRoot().widthProperty());
        this.prefHeightProperty().bind(FXGL.getGameScene().getRoot().heightProperty());
        
        // Carica i 4 eroi disponibili
        this.eroiDisponibili.add(HeroFactory.creaEroe("Harry Potter", annoPartita));
        this.eroiDisponibili.add(HeroFactory.creaEroe("Ron Weasley", annoPartita));
        this.eroiDisponibili.add(HeroFactory.creaEroe("Hermione Granger", annoPartita));
        this.eroiDisponibili.add(HeroFactory.creaEroe("Neville Longbottom", annoPartita));

        initGraphics();
        aggiornaSchermata(); 
    }

    private void initGraphics() {
        // Sfondo con binding
        Rectangle bg = new Rectangle();
        bg.widthProperty().bind(this.widthProperty());
        bg.heightProperty().bind(this.heightProperty());
        bg.setFill(Color.rgb(15, 10, 30));
        this.getChildren().add(bg);

        mainContainer = new VBox(40);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(60));

        titleText = new Text();
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        titleText.setFill(Color.GOLD);
        titleText.setStroke(Color.DARKRED);
        titleText.setStrokeWidth(2);
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(15);
        titleText.setEffect(shadow);

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
            if (eroe != null) {
                VBox card = creaCardEroe(eroe);
                heroesGrid.getChildren().add(card);
            }
        }
    }

    private VBox creaCardEroe(Eroe eroe) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(280, 420);
        box.setStyle(
            "-fx-background-color: rgba(40, 20, 60, 0.95);" +
            "-fx-border-color: #8B7355;" +
            "-fx-border-width: 3;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 15;"
        );
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(12);
        box.setEffect(shadow);

        // ⭐ USA ImageLoader
        Image heroImage = ImageLoader.caricaImmagine(eroe.getPathImmagine());
        ImageView img = new ImageView(heroImage);
        img.setFitHeight(320);
        img.setFitWidth(250);
        img.setPreserveRatio(true);
        
        // Clip arrotondato
        Rectangle clip = new Rectangle(250, 320);
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        img.setClip(clip);

        // Effetto Hover
        box.setOnMouseEntered(e -> {
            box.setStyle(
                "-fx-background-color: rgba(60, 40, 90, 0.98);" +
                "-fx-border-color: gold;" +
                "-fx-border-width: 4;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 15;"
            );
            box.setScaleX(1.05);
            box.setScaleY(1.05);
        });
        
        box.setOnMouseExited(e -> {
            box.setStyle(
                "-fx-background-color: rgba(40, 20, 60, 0.95);" +
                "-fx-border-color: #8B7355;" +
                "-fx-border-width: 3;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 15;"
            );
            box.setScaleX(1.0);
            box.setScaleY(1.0);
        });

        Text nome = new Text(eroe.getNome().toUpperCase());
        nome.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        nome.setFill(Color.LIGHTBLUE);
        nome.setWrappingWidth(250);
        nome.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Click per selezionare
        box.setOnMouseClicked(e -> onEroeSelezionato(eroe));

        box.getChildren().addAll(img, nome);
        return box;
    }

    private void onEroeSelezionato(Eroe eroeScelto) {
        System.out.println("✓ Giocatore " + (indiceGiocatoreCorrente + 1) + " ha scelto: " + eroeScelto.getNome());

        // 1. Crea il giocatore
        Giocatore nuovoGiocatore = new Giocatore(eroeScelto);

        // 2. Aggiungi alla lista
        giocatoriSelezionati.add(nuovoGiocatore);

        // 3. Rimuovi l'eroe dai disponibili
        eroiDisponibili.remove(eroeScelto);

        // 4. Avanza turno
        indiceGiocatoreCorrente++;

        // 5. Controllo fine
        if (indiceGiocatoreCorrente < numeroGiocatoriTotali) {
            aggiornaSchermata();
        } else {
            System.out.println("✅ Selezione eroi completata!");
            if (onSelectionComplete != null) {
                onSelectionComplete.accept(giocatoriSelezionati);
            }
        }
    }
}