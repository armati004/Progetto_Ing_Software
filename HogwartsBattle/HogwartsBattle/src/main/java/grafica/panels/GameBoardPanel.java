package grafica.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import gioco.StatoDiGioco;
import carte.Carta;
import carte.Malvagio;
import carte.Luogo;
import carte.ArteOscura;
import grafica.utils.ImageLoader;

/**
 * GameBoardPanel - Mostra il tabellone completo del gioco
 * VERSIONE CORRETTA con aggiornamento dinamico arti oscure
 */
public class GameBoardPanel extends HBox {
    
    private StatoDiGioco stato;
    
    // Sezioni del tabellone
    private VBox sezioneSinistra;
    private VBox sezioneCentro;
    private VBox sezioneDestra;
    
    // Componenti
    private VBox luoghiBox;
    private VBox mazzoMalvagiBox;
    private VBox mazzoArtiOscureBox;
    private VBox ultimaArtiOscuraBox;
    private VBox horcruxBox;
    private VBox malvagiAttiviBox;
    private GridPane mercatoGrid;
    
    public GameBoardPanel(StatoDiGioco stato) {
        this.stato = stato;
        
        setStyle("-fx-background-color: #1a3a2a;");
        setSpacing(15);
        setPadding(new Insets(15));
        
        // Crea le tre sezioni
        sezioneSinistra = creaSezioneSinistra();
        sezioneCentro = creaSezioneCentro();
        sezioneDestra = creaSezioneDestra();
        
        HBox.setHgrow(sezioneSinistra, Priority.SOMETIMES);
        HBox.setHgrow(sezioneCentro, Priority.ALWAYS);
        HBox.setHgrow(sezioneDestra, Priority.SOMETIMES);
        
        getChildren().addAll(sezioneSinistra, sezioneCentro, sezioneDestra);
    }
    
    /**
     * Sezione SINISTRA: Luoghi e Mazzo Malvagi
     */
    private VBox creaSezioneSinistra() {
        VBox sezione = new VBox();
        sezione.setSpacing(10);
        sezione.setPrefWidth(300);
        
        // LUOGHI
        luoghiBox = new VBox();
        luoghiBox.setSpacing(5);
        luoghiBox.setStyle("-fx-border-color: #9966FF; -fx-border-width: 2; " +
                          "-fx-background-color: #1a1a2a;");
        luoghiBox.setPadding(new Insets(10));
        luoghiBox.setPrefHeight(200);
        
        Label luoghiTitle = new Label("📍 LUOGO ATTUALE");
        luoghiTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        luoghiTitle.setTextFill(Color.web("#9966FF"));
        luoghiBox.getChildren().add(luoghiTitle);
        
        aggiornaLuogo();
        
        // MAZZO MALVAGI
        mazzoMalvagiBox = new VBox();
        mazzoMalvagiBox.setSpacing(5);
        mazzoMalvagiBox.setStyle("-fx-border-color: #FF6666; -fx-border-width: 2; " +
                                "-fx-background-color: #2a1a1a;");
        mazzoMalvagiBox.setPadding(new Insets(10));
        mazzoMalvagiBox.setAlignment(Pos.CENTER);
        mazzoMalvagiBox.setPrefHeight(150);
        
        Label malvagiTitle = new Label("👹 MAZZO MALVAGI");
        malvagiTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        malvagiTitle.setTextFill(Color.web("#FF6666"));
        
        Label countMalvagi = new Label("Carte: " + stato.getMazzoMalvagi().size());
        countMalvagi.setFont(Font.font("Arial", 10));
        countMalvagi.setTextFill(Color.WHITE);
        
        mazzoMalvagiBox.getChildren().addAll(malvagiTitle, countMalvagi);
        
        VBox.setVgrow(luoghiBox, Priority.ALWAYS);
        sezione.getChildren().addAll(luoghiBox, mazzoMalvagiBox);
        
        return sezione;
    }
    
    /**
     * Sezione CENTRO: Arti Oscure, Horcrux, Malvagi Attivi
     */
    private VBox creaSezioneCentro() {
        VBox sezione = new VBox();
        sezione.setSpacing(10);
        
        // ROW: Arti Oscure
        HBox artiOscureRow = new HBox(10);
        artiOscureRow.setAlignment(Pos.CENTER);
        
        // MAZZO ARTI OSCURE
        mazzoArtiOscureBox = new VBox();
        mazzoArtiOscureBox.setSpacing(5);
        mazzoArtiOscureBox.setStyle("-fx-border-color: #9966FF; -fx-border-width: 2; " +
                                   "-fx-background-color: #1a1a2a;");
        mazzoArtiOscureBox.setPadding(new Insets(10));
        mazzoArtiOscureBox.setAlignment(Pos.CENTER);
        mazzoArtiOscureBox.setPrefWidth(150);
        mazzoArtiOscureBox.setPrefHeight(200);
        
        Label artiTitle = new Label("🌑 ARTI OSCURE");
        artiTitle.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        artiTitle.setTextFill(Color.web("#9966FF"));
        
        Label countArti = new Label("Carte: " + stato.getMazzoArtiOscure().size());
        countArti.setFont(Font.font("Arial", 9));
        countArti.setTextFill(Color.WHITE);
        
        mazzoArtiOscureBox.getChildren().addAll(artiTitle, countArti);
        
        // ULTIMA CARTA ARTI OSCURE
        ultimaArtiOscuraBox = new VBox();
        ultimaArtiOscuraBox.setSpacing(5);
        ultimaArtiOscuraBox.setStyle("-fx-border-color: #9966FF; -fx-border-width: 2; " +
                                    "-fx-background-color: #2a1a2a;");
        ultimaArtiOscuraBox.setPadding(new Insets(10));
        ultimaArtiOscuraBox.setAlignment(Pos.TOP_CENTER);
        ultimaArtiOscuraBox.setPrefWidth(200);
        ultimaArtiOscuraBox.setPrefHeight(200);
        
        Label ultimaTitle = new Label("📜 ULTIMA CARTA");
        ultimaTitle.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        ultimaTitle.setTextFill(Color.web("#9966FF"));
        ultimaArtiOscuraBox.getChildren().add(ultimaTitle);
        
        artiOscureRow.getChildren().addAll(mazzoArtiOscureBox, ultimaArtiOscuraBox);
        
        // HORCRUX
        horcruxBox = new VBox();
        horcruxBox.setSpacing(5);
        horcruxBox.setStyle("-fx-border-color: #00FF00; -fx-border-width: 2; " +
                           "-fx-background-color: #1a2a1a;");
        horcruxBox.setPadding(new Insets(10));
        horcruxBox.setAlignment(Pos.CENTER);
        horcruxBox.setPrefHeight(100);
        
        Label horcruxTitle = new Label("💀 HORCRUX");
        horcruxTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        horcruxTitle.setTextFill(Color.web("#00FF00"));
        horcruxBox.getChildren().add(horcruxTitle);
        
        // MALVAGI ATTIVI
        malvagiAttiviBox = new VBox();
        malvagiAttiviBox.setSpacing(8);
        malvagiAttiviBox.setStyle("-fx-border-color: #FF6666; -fx-border-width: 2; " +
                                 "-fx-background-color: #2a1a1a;");
        malvagiAttiviBox.setPadding(new Insets(10));
        malvagiAttiviBox.setAlignment(Pos.TOP_CENTER);
        
        Label malvagiAttiviTitle = new Label("⚔️ MALVAGI ATTIVI");
        malvagiAttiviTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        malvagiAttiviTitle.setTextFill(Color.web("#FF6666"));
        malvagiAttiviBox.getChildren().add(malvagiAttiviTitle);
        
        aggiornaVisualizazioneMalvagi();
        
        VBox.setVgrow(malvagiAttiviBox, Priority.ALWAYS);
        
        sezione.getChildren().addAll(artiOscureRow, horcruxBox, malvagiAttiviBox);
        
        return sezione;
    }
    
    /**
     * Sezione DESTRA: Mercato
     */
    private VBox creaSezioneDestra() {
        VBox sezione = new VBox();
        sezione.setSpacing(10);
        sezione.setPrefWidth(350);
        
        Label mercatoTitle = new Label("🛒 MERCATO");
        mercatoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        mercatoTitle.setTextFill(Color.web("#FFD700"));
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #1a1a1a; -fx-border-color: #FFD700; -fx-border-width: 2;");
        scrollPane.setFitToWidth(true);
        
        mercatoGrid = new GridPane();
        mercatoGrid.setHgap(10);
        mercatoGrid.setVgap(10);
        mercatoGrid.setPadding(new Insets(10));
        mercatoGrid.setStyle("-fx-background-color: #2a2a1a;");
        
        aggiornaMercato();
        
        scrollPane.setContent(mercatoGrid);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        sezione.getChildren().addAll(mercatoTitle, scrollPane);
        
        return sezione;
    }
    
    /**
     * Aggiorna visualizzazione luogo
     */
    private void aggiornaLuogo() {
        // ⭐ FIX: Pulisce tutto tranne il titolo
        while (luoghiBox.getChildren().size() > 1) {
            luoghiBox.getChildren().remove(1);
        }
        
        Luogo luogo = stato.getLuogoAttuale();
        
        // Immagine luogo
        if (luogo.getPathImmagine() != null && !luogo.getPathImmagine().isEmpty()) {
            Image luogoImg = ImageLoader.caricaImmagine(luogo.getPathImmagine());
            ImageView imageView = new ImageView(luogoImg);
            imageView.setFitWidth(280);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            luoghiBox.getChildren().add(imageView);
        }
        
        Label nomeLabel = new Label(luogo.getNome());
        nomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        nomeLabel.setTextFill(Color.web("#FFFFFF"));
        nomeLabel.setWrapText(true);
        
        Label marchiLabel = new Label("⚫ Marchi Neri: " + stato.getLuogoAttuale().getNumeroMarchiNeri() + "/" + luogo.getMarchiNeriMax());
        marchiLabel.setFont(Font.font("Arial", 10));
        marchiLabel.setTextFill(Color.web("#FF6666"));
        
        luoghiBox.getChildren().addAll(nomeLabel, marchiLabel);
    }
    
    /**
     * Aggiorna ultima carta Arti Oscure
     * ⭐ FIX: Ricostruisce completamente il box ad ogni chiamata
     */
    private void aggiornaUltimaArtiOscura() {
        // ⭐ FIX: Pulisce completamente e ricostruisce
        ultimaArtiOscuraBox.getChildren().clear();
        
        Label ultimaTitle = new Label("📜 ULTIMA CARTA");
        ultimaTitle.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        ultimaTitle.setTextFill(Color.web("#9966FF"));
        ultimaArtiOscuraBox.getChildren().add(ultimaTitle);
        
        ArteOscura ultima = stato.getScartiArtiOscure().get(stato.getScartiArtiOscure().size() - 1);
        
        if (ultima != null) {
            // Immagine
            if (ultima.getPathImmagine() != null && !ultima.getPathImmagine().isEmpty()) {
                Image img = ImageLoader.caricaImmagine(ultima.getPathImmagine());
                ImageView imageView = new ImageView(img);
                imageView.setFitWidth(180);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                ultimaArtiOscuraBox.getChildren().add(imageView);
            }
            
            Label nomeLabel = new Label(ultima.getNome());
            nomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            nomeLabel.setTextFill(Color.WHITE);
            nomeLabel.setWrapText(true);
            nomeLabel.setMaxWidth(180);
            
            Label descLabel = new Label(ultima.getDescrizione());
            descLabel.setFont(Font.font("Arial", 8));
            descLabel.setTextFill(Color.web("#CCCCCC"));
            descLabel.setWrapText(true);
            descLabel.setMaxWidth(180);
            
            ultimaArtiOscuraBox.getChildren().addAll(nomeLabel, descLabel);
        } else {
            Label nessunLabel = new Label("Nessuna carta giocata");
            nessunLabel.setFont(Font.font("Arial", 9));
            nessunLabel.setTextFill(Color.GRAY);
            ultimaArtiOscuraBox.getChildren().add(nessunLabel);
        }
    }
    
    /**
     * Aggiorna visualizzazione malvagi attivi
     */
    private void aggiornaVisualizazioneMalvagi() {
        // ⭐ FIX: Pulisce tutto tranne il titolo
        while (malvagiAttiviBox.getChildren().size() > 1) {
            malvagiAttiviBox.getChildren().remove(1);
        }
        
        for (int i = 0; i < stato.getMalvagiAttivi().size(); i++) {
            Malvagio malvagio = stato.getMalvagiAttivi().get(i);
            VBox malvagoBox = creaMalvagioBox(malvagio, i);
            malvagiAttiviBox.getChildren().add(malvagoBox);
        }
        
        if (stato.getMalvagiAttivi().isEmpty()) {
            Label nessunLabel = new Label("Nessun malvagio attivo");
            nessunLabel.setFont(Font.font("Arial", 10));
            nessunLabel.setTextFill(Color.GRAY);
            malvagiAttiviBox.getChildren().add(nessunLabel);
        }
    }
    
    /**
     * Crea box per un malvagio
     */
    private VBox creaMalvagioBox(Malvagio malvagio, int indice) {
        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #FF9999; -fx-border-width: 2; " +
                    "-fx-background-color: #2a0a0a;");
        box.setPadding(new Insets(8));
        box.setAlignment(Pos.TOP_CENTER);
        box.setPrefWidth(400);
        
        // Immagine
        if (malvagio.getPathImmagine() != null && !malvagio.getPathImmagine().isEmpty()) {
            Image img = ImageLoader.caricaImmagine(malvagio.getPathImmagine());
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(200);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            box.getChildren().add(imageView);
        }
        
        Label nomeLabel = new Label(malvagio.getNome());
        nomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        nomeLabel.setTextFill(Color.web("#FF6666"));
        nomeLabel.setWrapText(true);
        
        Label vitaLabel = new Label("❤️ " + malvagio.getDanno() + "/" + malvagio.getVita());
        vitaLabel.setFont(Font.font("Arial", 11));
        vitaLabel.setTextFill(Color.web("#FF9999"));
        
        Label dannoLabel = new Label("⚔️ Danno: " + malvagio.getDanno());
        dannoLabel.setFont(Font.font("Arial", 10));
        dannoLabel.setTextFill(Color.web("#FFAA66"));
        
        box.getChildren().addAll(nomeLabel, vitaLabel, dannoLabel);
        
        // Click per attaccare
        box.setOnMouseClicked(e -> {
            if (grafica.GameController.getInstance() != null) {
                grafica.GameController.getInstance().attaccaMalvagio(indice);
            }
        });
        
        box.setOnMouseEntered(e -> {
            box.setStyle("-fx-border-color: #FF6666; -fx-border-width: 3; " +
                        "-fx-background-color: #3a1a1a; -fx-cursor: hand;");
        });
        
        box.setOnMouseExited(e -> {
            box.setStyle("-fx-border-color: #FF9999; -fx-border-width: 2; " +
                        "-fx-background-color: #2a0a0a;");
        });
        
        return box;
    }
    
    /**
     * Aggiorna mercato con carte a coppie
     */
    private void aggiornaMercato() {
        mercatoGrid.getChildren().clear();
        
        int row = 0;
        int col = 0;
        
        for (int i = 0; i < stato.getMercato().size(); i++) {
            Carta carta = stato.getMercato().get(i);
            VBox cartaBox = creaMercatoCartaBox(carta, i);
            
            mercatoGrid.add(cartaBox, col, row);
            
            col++;
            if (col >= 2) {  // 2 carte per riga
                col = 0;
                row++;
            }
        }
    }
    
    /**
     * Crea box per carta nel mercato
     */
    private VBox creaMercatoCartaBox(Carta carta, int indice) {
        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #FFD700; -fx-border-width: 2; " +
                    "-fx-background-color: #1a1a1a;");
        box.setPadding(new Insets(8));
        box.setAlignment(Pos.TOP_CENTER);
        box.setPrefWidth(150);
        box.setPrefHeight(180);
        
        // Immagine
        if (carta.getPathImmagine() != null && !carta.getPathImmagine().isEmpty()) {
            Image img = ImageLoader.caricaImmagine(carta.getPathImmagine());
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(130);
            imageView.setFitHeight(90);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            box.getChildren().add(imageView);
        }
        
        Label nomeLabel = new Label(carta.getNome());
        nomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        nomeLabel.setTextFill(Color.WHITE);
        nomeLabel.setMaxWidth(140);
        nomeLabel.setWrapText(true);
        nomeLabel.setAlignment(Pos.CENTER);
        
        Label costoLabel = new Label("💰 " + carta.getCosto());
        costoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        costoLabel.setTextFill(Color.web("#FFD700"));
        
        box.getChildren().addAll(nomeLabel, costoLabel);
        
        // Click per acquistare
        box.setOnMouseClicked(e -> {
            if (grafica.GameController.getInstance() != null) {
                grafica.GameController.getInstance().acquistaCarta(indice);
            }
        });
        
        box.setOnMouseEntered(e -> {
            box.setStyle("-fx-border-color: #FFD700; -fx-border-width: 3; " +
                        "-fx-background-color: #2a2a1a; -fx-cursor: hand;");
            box.setScaleX(1.05);
            box.setScaleY(1.05);
        });
        
        box.setOnMouseExited(e -> {
            box.setStyle("-fx-border-color: #FFD700; -fx-border-width: 2; " +
                        "-fx-background-color: #1a1a1a;");
            box.setScaleX(1.0);
            box.setScaleY(1.0);
        });
        
        return box;
    }
    
    /**
     * Aggiorna tutto il tabellone
     * ⭐ FIX: Aggiorna anche l'ultima carta Arti Oscure
     */
    public void aggiorna() {
        aggiornaLuogo();
        aggiornaUltimaArtiOscura();  // ⭐ FIX: Questa riga è essenziale
        aggiornaVisualizazioneMalvagi();
        aggiornaMercato();
        
        // Aggiorna contatori
        if (mazzoMalvagiBox.getChildren().size() > 1) {
            Label countLabel = (Label) mazzoMalvagiBox.getChildren().get(1);
            countLabel.setText("Carte: " + stato.getMazzoMalvagi().size());
        }
        
        if (mazzoArtiOscureBox.getChildren().size() > 1) {
            Label countLabel = (Label) mazzoArtiOscureBox.getChildren().get(1);
            countLabel.setText("Carte: " + stato.getMazzoArtiOscure().size());
        }
    }
}