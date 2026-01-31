package grafica.screens;

import carte.Competenza;
import carte.Eroe;
import data.HeroFactory;
import data.ProficiencyFactory;

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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Schermata di selezione eroi e competenze all'inizio della partita.
 * 
 * Permette a ciascun giocatore di:
 * 1. Scegliere il proprio eroe tra i 4 disponibili
 * 2. (Anno 6+) Scegliere una competenza
 */
public class HeroSelectionScreen extends StackPane {
    
    private static final double WINDOW_WIDTH = 1920;
    private static final double WINDOW_HEIGHT = 1080;
    
    private int annoCorrente;
    private int numeroGiocatori;
    private int giocatoreCorrente;
    
    // Eroi disponibili
    private static final String[] EROI_DISPONIBILI = {
        "Harry Potter",
        "Hermione Granger", 
        "Ron Weasley",
        "Neville Longbottom"
    };
    
    // Selezioni
    private List<String> eroiSelezionati;
    private List<String> competenzeSelezionate;
    
    // UI Components
    private VBox mainContainer;
    private Text titleText;
    private Text instructionText;
    private HBox heroesContainer;
    private HBox proficienciesContainer;
    private VBox selectedInfoBox;
    
    // Callback quando la selezione è completa
    private Consumer<SelectionResult> onSelectionComplete;
    
    // Stato selezione
    private boolean selectingHero;
    private boolean selectingProficiency;
    
    /**
     * Risultato della selezione
     */
    public static class SelectionResult {
        public List<String> eroiSelezionati;
        public List<String> competenzeSelezionate;
        
        public SelectionResult(List<String> eroi, List<String> competenze) {
            this.eroiSelezionati = eroi;
            this.competenzeSelezionate = competenze;
        }
    }
    
    /**
     * Costruttore
     */
    public HeroSelectionScreen(int anno, int numeroGiocatori, Consumer<SelectionResult> onComplete) {
        this.annoCorrente = anno;
        this.numeroGiocatori = numeroGiocatori;
        this.giocatoreCorrente = 0;
        this.eroiSelezionati = new ArrayList<>();
        this.competenzeSelezionate = new ArrayList<>();
        this.onSelectionComplete = onComplete;
        
        this.selectingHero = true;
        this.selectingProficiency = false;
        
        inizializzaUI();
    }
    
    /**
     * Inizializza l'interfaccia
     */
    private void inizializzaUI() {
        this.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Sfondo
        Rectangle bg = new Rectangle(WINDOW_WIDTH, WINDOW_HEIGHT);
        bg.setFill(Color.rgb(15, 15, 30));
        this.getChildren().add(bg);
        
        // Container principale
        mainContainer = new VBox(40);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(50));
        
        // Titolo
        titleText = new Text("SELEZIONE EROI");
        titleText.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 56));
        titleText.setFill(Color.GOLD);
        titleText.setStroke(Color.DARKRED);
        titleText.setStrokeWidth(3);
        
        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.BLACK);
        titleShadow.setRadius(20);
        titleText.setEffect(titleShadow);
        
        // Istruzioni
        instructionText = new Text("Giocatore 1: Scegli il tuo eroe");
        instructionText.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        instructionText.setFill(Color.LIGHTBLUE);
        
        // Container eroi
        heroesContainer = new HBox(30);
        heroesContainer.setAlignment(Pos.CENTER);
        
        // Info selezioni
        selectedInfoBox = new VBox(10);
        selectedInfoBox.setAlignment(Pos.CENTER);
        selectedInfoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); " +
                                "-fx-background-radius: 15; " +
                                "-fx-padding: 20;");
        selectedInfoBox.setPrefWidth(800);
        
        mainContainer.getChildren().addAll(
            titleText,
            instructionText,
            heroesContainer,
            selectedInfoBox
        );
        
        this.getChildren().add(mainContainer);
        
        // Mostra gli eroi
        mostraEroi();
    }
    
    /**
     * Mostra le card degli eroi selezionabili
     */
    private void mostraEroi() {
        heroesContainer.getChildren().clear();
        
        for (String nomeEroe : EROI_DISPONIBILI) {
            // Salta gli eroi già selezionati
            if (eroiSelezionati.contains(nomeEroe)) {
                continue;
            }
            
            VBox heroCard = creaHeroCard(nomeEroe);
            heroesContainer.getChildren().add(heroCard);
        }
        
        aggiornaInfoSelezionati();
    }
    
    /**
     * Crea una card per un eroe
     */
    private VBox creaHeroCard(String nomeEroe) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(280, 450);
        card.setStyle("-fx-background-color: rgba(40, 40, 60, 0.9); " +
                     "-fx-background-radius: 20; " +
                     "-fx-border-color: gold; " +
                     "-fx-border-width: 3; " +
                     "-fx-border-radius: 20; " +
                     "-fx-padding: 15; " +
                     "-fx-cursor: hand;");
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(15);
        card.setEffect(shadow);
        
        try {
            // Carica l'eroe per ottenere l'immagine
            Eroe eroe = HeroFactory.creaEroe(nomeEroe, annoCorrente);
            
            // Immagine eroe
            Image heroImage = caricaImmagine(eroe.getPathImmagine());
            ImageView imageView = new ImageView(heroImage);
            imageView.setFitWidth(240);
            imageView.setFitHeight(320);
            imageView.setPreserveRatio(true);
            
            Rectangle clip = new Rectangle(240, 320);
            clip.setArcWidth(15);
            clip.setArcHeight(15);
            imageView.setClip(clip);
            
            // Nome
            Text nameText = new Text(nomeEroe);
            nameText.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 22));
            nameText.setFill(Color.GOLD);
            
            card.getChildren().addAll(imageView, nameText);
            
        } catch (Exception e) {
            System.err.println("Errore caricamento eroe: " + nomeEroe);
            
            // Fallback
            Text nameText = new Text(nomeEroe);
            nameText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            nameText.setFill(Color.WHITE);
            card.getChildren().add(nameText);
        }
        
        // Effetti hover
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: rgba(60, 60, 80, 0.95); " +
                         "-fx-background-radius: 20; " +
                         "-fx-border-color: yellow; " +
                         "-fx-border-width: 4; " +
                         "-fx-border-radius: 20; " +
                         "-fx-padding: 15; " +
                         "-fx-cursor: hand;");
            card.setScaleX(1.05);
            card.setScaleY(1.05);
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: rgba(40, 40, 60, 0.9); " +
                         "-fx-background-radius: 20; " +
                         "-fx-border-color: gold; " +
                         "-fx-border-width: 3; " +
                         "-fx-border-radius: 20; " +
                         "-fx-padding: 15; " +
                         "-fx-cursor: hand;");
            card.setScaleX(1.0);
            card.setScaleY(1.0);
        });
        
        // Click per selezionare
        card.setOnMouseClicked(e -> {
            selezionaEroe(nomeEroe);
        });
        
        return card;
    }
    
    /**
     * Seleziona un eroe
     */
    private void selezionaEroe(String nomeEroe) {
        eroiSelezionati.add(nomeEroe);
        giocatoreCorrente++;
        
        System.out.println("✓ Giocatore " + giocatoreCorrente + " ha scelto: " + nomeEroe);
        
        // Verifica se tutti i giocatori hanno scelto
        if (giocatoreCorrente >= numeroGiocatori) {
            // Selezione eroi completata
            completaSelezione();
        } else {
            // Prossimo giocatore
            instructionText.setText("Giocatore " + (giocatoreCorrente + 1) + ": Scegli il tuo eroe");
            mostraEroi();
        }
    }
    
    /**
     * Completa la selezione e chiama il callback
     */
    private void completaSelezione() {
        System.out.println("\n✅ Selezione eroi completata!");
        System.out.println("Eroi: " + eroiSelezionati);
        
        if (onSelectionComplete != null) {
            SelectionResult result = new SelectionResult(eroiSelezionati, new ArrayList<>());
            onSelectionComplete.accept(result);
        }
    }
    
    /**
     * Aggiorna le info sui selezionati
     */
    private void aggiornaInfoSelezionati() {
        selectedInfoBox.getChildren().clear();
        
        Text title = new Text("SELEZIONI CORRENTI");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setFill(Color.GOLD);
        
        selectedInfoBox.getChildren().add(title);
        
        // Mostra eroi selezionati
        for (int i = 0; i < eroiSelezionati.size(); i++) {
            Text playerInfo = new Text("Giocatore " + (i + 1) + ": " + eroiSelezionati.get(i));
            playerInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
            playerInfo.setFill(Color.WHITE);
            selectedInfoBox.getChildren().add(playerInfo);
        }
        
        // Mostra competenze selezionate (se applicabile)
        if (selectingProficiency && !competenzeSelezionate.isEmpty()) {
            Text compTitle = new Text("\nCOMPETENZE");
            compTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            compTitle.setFill(Color.LIGHTBLUE);
            selectedInfoBox.getChildren().add(compTitle);
            
            for (int i = 0; i < competenzeSelezionate.size(); i++) {
                try {
                    Competenza comp = ProficiencyFactory.creaCompetenza(competenzeSelezionate.get(i));
                    Text compInfo = new Text("Giocatore " + (i + 1) + ": " + comp.getNome());
                    compInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                    compInfo.setFill(Color.LIGHTGRAY);
                    selectedInfoBox.getChildren().add(compInfo);
                } catch (Exception e) {
                    // Ignora
                }
            }
        }
    }
    
    /**
     * Carica un'immagine
     */
    private Image caricaImmagine(String path) throws Exception {
        String cleanPath = path.replace("../", "").replace("\\", "/");
        
        // Prova vari metodi
        try {
            var stream = getClass().getClassLoader().getResourceAsStream(cleanPath);
            if (stream != null) return new Image(stream);
        } catch (Exception e) { }
        
        try {
            File file = new File(cleanPath);
            if (file.exists()) return new Image(new FileInputStream(file));
        } catch (Exception e) { }
        
        try {
            File projectFile = new File("/mnt/project/" + cleanPath);
            if (projectFile.exists()) return new Image(new FileInputStream(projectFile));
        } catch (Exception e) { }
        
        throw new Exception("Impossibile caricare: " + path);
    }
}