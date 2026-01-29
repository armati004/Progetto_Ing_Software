package grafica.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import gioco.StatoDiGioco;
import carte.*;
import grafica.utils.ImageLoader;
import grafica.GameController;

/**
 * GameBoardPanel - Con descrizioni complete
 */
public class GameBoardPanel extends HBox {
    
    private StatoDiGioco stato;
    private VBox luoghiBox, mazzoMalvagiBox, mazzoArtiOscureBox, ultimaArtiOscureBox, horcruxBox, malvagiAttiviBox;
    private GridPane mercatoGrid;
    private VBox imageArtiOscureContainer, descrArtiOscureContainer;
    
    
    public GameBoardPanel(StatoDiGioco stato) {
        this.stato = stato;
        setStyle("-fx-background-color: #1a3a2a;");
        setSpacing(10);
        setPadding(new Insets(10));
        
        VBox sinistra = creaSinistra();
        VBox centro = creaCentro();
        VBox destra = creaDestra();
        
        sinistra.setMinWidth(250);
        sinistra.setPrefWidth(300);
        sinistra.setMaxWidth(350);
        
        destra.setMinWidth(300);
        destra.setPrefWidth(350);
        destra.setMaxWidth(400);
        
        HBox.setHgrow(centro, Priority.ALWAYS);
        
        getChildren().addAll(sinistra, centro, destra);
    }
    
    private VBox creaSinistra() {
        VBox v = new VBox(10);
        
        // === LUOGHI ===
        luoghiBox = new VBox(5);
        luoghiBox.setStyle("-fx-border-color: #9966FF; -fx-border-width: 2; -fx-background-color: #1a1a2a; -fx-border-radius: 5; -fx-background-radius: 5;");
        luoghiBox.setPadding(new Insets(10));
        luoghiBox.setAlignment(Pos.TOP_CENTER);
        
        Label luoghiTitle = new Label("📍 LUOGO ATTUALE");
        luoghiTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        luoghiTitle.setTextFill(Color.web("#9966FF"));
        luoghiTitle.setMaxWidth(Double.MAX_VALUE);
        luoghiTitle.setAlignment(Pos.CENTER);
        luoghiBox.getChildren().add(luoghiTitle);
        aggiornaLuogo();
        
        // === MAZZO MALVAGI ===
        mazzoMalvagiBox = new VBox(5);
        mazzoMalvagiBox.setStyle("-fx-border-color: #FF6666; -fx-border-width: 2; -fx-background-color: #2a1a1a; -fx-border-radius: 5; -fx-background-radius: 5;");
        mazzoMalvagiBox.setPadding(new Insets(10));
        mazzoMalvagiBox.setAlignment(Pos.CENTER);
        mazzoMalvagiBox.setPrefHeight(120);
        
        Label malvagiTitle = new Label("👹 MAZZO MALVAGI");
        malvagiTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        malvagiTitle.setTextFill(Color.web("#FF6666"));
        
        Label countMalvagi = new Label("Carte rimanenti: " + stato.getMazzoMalvagi().size());
        countMalvagi.setFont(Font.font("Arial", 14));
        countMalvagi.setTextFill(Color.WHITE);
        
        mazzoMalvagiBox.getChildren().addAll(malvagiTitle, countMalvagi);
        
        VBox.setVgrow(luoghiBox, Priority.ALWAYS);
        v.getChildren().addAll(luoghiBox, mazzoMalvagiBox);
        return v;
    }
    
    private VBox creaCentro() {
        VBox v = new VBox(10);
        
        // === RIGA ARTI OSCURE + HORCRUX ===
        HBox artiRow = new HBox(10);
        artiRow.setAlignment(Pos.CENTER);
        artiRow.setMinHeight(180);
        artiRow.setPrefHeight(200);
        
        // Mazzo Arti Oscure
        mazzoArtiOscureBox = new VBox(5);
        mazzoArtiOscureBox.setStyle("-fx-border-color: #9966FF; -fx-border-width: 2; -fx-background-color: #1a1a2a; -fx-border-radius: 5; -fx-background-radius: 5;");
        mazzoArtiOscureBox.setPadding(new Insets(10));
        mazzoArtiOscureBox.setAlignment(Pos.CENTER);
        mazzoArtiOscureBox.setPrefWidth(150);
        
        Label artiTitle = new Label("🌑 ARTI OSCURE");
        artiTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        artiTitle.setTextFill(Color.web("#9966FF"));
        
        Label countArti = new Label("Mazzo: " + stato.getMazzoArtiOscure().size());
        countArti.setFont(Font.font("Arial", 10));
        countArti.setTextFill(Color.WHITE);
        
        mazzoArtiOscureBox.getChildren().addAll(artiTitle, countArti);
        
        // Ultima Carta Arti Oscure
        ultimaArtiOscureBox = new VBox(5);
        ultimaArtiOscureBox.setStyle("-fx-border-color: #9966FF; -fx-border-width: 2; -fx-background-color: #2a1a2a; -fx-border-radius: 5; -fx-background-radius: 5;");
        ultimaArtiOscureBox.setPadding(new Insets(10));
        ultimaArtiOscureBox.setAlignment(Pos.TOP_CENTER);
        
        Label ultimaTitle = new Label("📜 ULTIMA CARTA");
        ultimaTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        ultimaTitle.setTextFill(Color.web("#9966FF"));
        ultimaArtiOscureBox.getChildren().add(ultimaTitle);
        
        imageArtiOscureContainer = new VBox(5);
        imageArtiOscureContainer.setStyle("-fx-border-color: #9966FF; -fx-border-width: 2; -fx-background-color: #2a1a2a; -fx-border-radius: 5; -fx-background-radius: 5;");
        imageArtiOscureContainer.setPadding(new Insets(10));
        imageArtiOscureContainer.setAlignment(Pos.CENTER);
        descrArtiOscureContainer = new VBox(5);
        descrArtiOscureContainer.setStyle("-fx-border-color: #9966FF; -fx-border-width: 2; -fx-background-color: #2a1a2a; -fx-border-radius: 5; -fx-background-radius: 5;");
        descrArtiOscureContainer.setPadding(new Insets(10));
        descrArtiOscureContainer.setAlignment(Pos.CENTER);
        aggiornaArtiOscure();
        //ultimaArtiOscureBox.getChildren().addAll(imageArtiOscureContainer, descrArtiOscureContainer);
        
        
        HBox.setHgrow(ultimaArtiOscureBox, Priority.ALWAYS);
        artiRow.getChildren().addAll(mazzoArtiOscureBox, ultimaArtiOscureBox);
        
        // Horcrux
        horcruxBox = new VBox(5);
        if (stato.isHasHorcruxes()) {
            horcruxBox.setStyle("-fx-border-color: #00FF00; -fx-border-width: 2; -fx-background-color: #1a2a1a; -fx-border-radius: 5; -fx-background-radius: 5;");
            horcruxBox.setPadding(new Insets(10));
            horcruxBox.setAlignment(Pos.CENTER);
            horcruxBox.setPrefWidth(160);
            
            Label horcruxTitle = new Label("💀 HORCRUX");
            horcruxTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            horcruxTitle.setTextFill(Color.web("#00FF00"));
            horcruxBox.getChildren().add(horcruxTitle);
            aggiornaHorcrux();
            artiRow.getChildren().add(horcruxBox);
        }
        
        // === MALVAGI ATTIVI ===
        malvagiAttiviBox = new VBox(8);
        malvagiAttiviBox.setStyle("-fx-border-color: #FF6666; -fx-border-width: 2; -fx-background-color: #2a1a1a; -fx-border-radius: 5; -fx-background-radius: 5;");
        malvagiAttiviBox.setPadding(new Insets(10));
        malvagiAttiviBox.setAlignment(Pos.TOP_CENTER);
        
        Label malvagiTitle = new Label("⚔️ MALVAGI ATTIVI");
        malvagiTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        malvagiTitle.setTextFill(Color.web("#FF6666"));
        malvagiTitle.setMaxWidth(Double.MAX_VALUE);
        malvagiTitle.setAlignment(Pos.CENTER);
        malvagiAttiviBox.getChildren().add(malvagiTitle);
        aggiornaMalvagiAttivi();
        
        VBox.setVgrow(malvagiAttiviBox, Priority.ALWAYS);
        v.getChildren().addAll(artiRow, malvagiAttiviBox);
        return v;
    }
    
    private VBox creaDestra() {
        VBox v = new VBox(10);
        
        Label mercatoTitle = new Label("🛒 MERCATO HOGWARTS");
        mercatoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        mercatoTitle.setTextFill(Color.web("#FFD700"));
        mercatoTitle.setMaxWidth(Double.MAX_VALUE);
        mercatoTitle.setAlignment(Pos.CENTER);
        
        mercatoGrid = new GridPane();
        mercatoGrid.setHgap(10);
        mercatoGrid.setVgap(10);
        mercatoGrid.setPadding(new Insets(10));
        mercatoGrid.setStyle("-fx-background-color: #2a2a1a;");
        aggiornaMercato();
        
        ScrollPane scroll = new ScrollPane(mercatoGrid);
        scroll.setStyle("-fx-background: #1a1a1a; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        
        v.getChildren().addAll(mercatoTitle, scroll);
        return v;
    }
    
    // ============================================
    // AGGIORNAMENTO COMPONENTI
    // ============================================
    
    private void aggiornaLuogo() {
        while (luoghiBox.getChildren().size() > 1) luoghiBox.getChildren().remove(1);
        
        Luogo luogo = stato.getLuogoAttuale();
        if (luogo != null) {
            // Immagine
            if (luogo.getPathImmagine() != null && !luogo.getPathImmagine().isEmpty()) {
                Image img = ImageLoader.caricaImmagine(luogo.getPathImmagine());
                if (img != null) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(280);
                    iv.setFitHeight(120);
                    iv.setPreserveRatio(true);
                    iv.setSmooth(true);
                    luoghiBox.getChildren().add(iv);
                }
            }
            
            // Nome
            Label nome = new Label(luogo.getNome());
            nome.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            nome.setTextFill(Color.web("#BB99FF"));
            nome.setWrapText(true);
            nome.setMaxWidth(280);
            nome.setAlignment(Pos.CENTER);
            
            // Descrizione
            Label marchiLabel = new Label("Marchi Neri: " + stato.getLuogoAttuale().getNumeroMarchiNeri() + "/" + luogo.getMarchiNeriMax());
            marchiLabel.setFont(Font.font("Arial", 16));
            marchiLabel.setTextFill(Color.web("#FF6666"));
            marchiLabel.setWrapText(true);
            marchiLabel.setMaxWidth(280);
            marchiLabel.setAlignment(Pos.CENTER);
            
            luoghiBox.getChildren().addAll(nome, marchiLabel);
        }
    }
    
    private void aggiornaMercato() {
        mercatoGrid.getChildren().clear();
        int col = 0, row = 0;
        
        for (int i = 0; i < stato.getMercato().size(); i++) {
            Carta carta = stato.getMercato().get(i);
            VBox box = creaMercatoBox(carta, i);
            mercatoGrid.add(box, col, row);
            col++;
            if (col >= 2) { col = 0; row++; }
        }
    }
    
    private VBox creaMercatoBox(Carta carta, int indice) {
        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-color: #1a1a1a; -fx-border-radius: 5; -fx-background-radius: 5;");
        box.setPadding(new Insets(8));
        box.setAlignment(Pos.TOP_CENTER);
        box.setPrefWidth(150);
        box.setPrefHeight(200);
        
        // Immagine
        if (carta.getPathImmagine() != null && !carta.getPathImmagine().isEmpty()) {
            Image img = ImageLoader.caricaImmagine(carta.getPathImmagine());
            if (img != null) {
                ImageView iv = new ImageView(img);
                iv.setFitWidth(130);
                iv.setFitHeight(90);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                box.getChildren().add(iv);
            }
        }
        
        // Nome
        Label nome = new Label(carta.getNome());
        nome.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        nome.setTextFill(Color.WHITE);
        nome.setWrapText(true);
        nome.setMaxWidth(140);
        nome.setAlignment(Pos.CENTER);
        
        // Tipo carta
        Label tipo = new Label(carta.getClasse());
        tipo.setFont(Font.font("Arial", 9));
        tipo.setTextFill(Color.web("#AAAAAA"));
        
        // Costo
        HBox costoRow = new HBox(5);
        costoRow.setAlignment(Pos.CENTER);
        Label costoLabel = new Label("Costo:");
        costoLabel.setFont(Font.font("Arial", 10));
        costoLabel.setTextFill(Color.web("#CCCCCC"));
        Label costoValue = new Label("💰 " + carta.getCosto());
        costoValue.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        costoValue.setTextFill(Color.web("#FFD700"));
        costoRow.getChildren().addAll(costoLabel, costoValue);
        
        box.getChildren().addAll(nome, tipo, costoRow);
        
        // Interattività
        box.setOnMouseClicked(e -> {
            if (GameController.getInstance() != null) {
                GameController.getInstance().acquistaCarta(indice);
            }
        });
        
        box.setOnMouseEntered(e -> {
            box.setStyle("-fx-border-color: #FFD700; -fx-border-width: 3; -fx-background-color: #2a2a1a; -fx-cursor: hand; -fx-border-radius: 5; -fx-background-radius: 5;");
            box.setScaleX(1.05);
            box.setScaleY(1.05);
        });
        
        box.setOnMouseExited(e -> {
            box.setStyle("-fx-border-color: #FFD700; -fx-border-width: 2; -fx-background-color: #1a1a1a; -fx-border-radius: 5; -fx-background-radius: 5;");
            box.setScaleX(1.0);
            box.setScaleY(1.0);
        });
        
        return box;
    }
    
    private void aggiornaMalvagiAttivi() {
        while (malvagiAttiviBox.getChildren().size() > 1) malvagiAttiviBox.getChildren().remove(1);
        
        for (int i = 0; i < stato.getMalvagiAttivi().size(); i++) {
            Malvagio m = stato.getMalvagiAttivi().get(i);
            VBox box = creaMalvagioBox(m, i);
            malvagiAttiviBox.getChildren().add(box);
        }
        
        if (stato.getMalvagiAttivi().isEmpty()) {
            Label vuoto = new Label("Nessun malvagio attivo");
            vuoto.setFont(Font.font("Arial", 11));
            vuoto.setTextFill(Color.GRAY);
            malvagiAttiviBox.getChildren().add(vuoto);
        }
    }
    
    private VBox creaMalvagioBox(Malvagio malvagio, int indice) {
        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #FF9999; -fx-border-width: 2; -fx-background-color: #2a0a0a; -fx-border-radius: 5; -fx-background-radius: 5;");
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.TOP_CENTER);
        box.setPrefWidth(400);
        
        // Immagine
        if (malvagio.getPathImmagine() != null && !malvagio.getPathImmagine().isEmpty()) {
            Image img = ImageLoader.caricaImmagine(malvagio.getPathImmagine());
            if (img != null) {
                ImageView iv = new ImageView(img);
                iv.setFitWidth(200);
                iv.setFitHeight(100);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                box.getChildren().add(iv);
            }
        }
        
        // Nome
        Label nome = new Label(malvagio.getNome());
        nome.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nome.setTextFill(Color.web("#FF6666"));
        nome.setWrapText(true);
        
        // Salute
        HBox saluteRow = new HBox(5);
        saluteRow.setAlignment(Pos.CENTER);
        Label saluteLabel = new Label("Salute:");
        saluteLabel.setFont(Font.font("Arial", 10));
        saluteLabel.setTextFill(Color.web("#CCCCCC"));
        Label saluteValue = new Label("❤️ " + malvagio.getDanno() + "/" + malvagio.getVita());
        saluteValue.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        saluteValue.setTextFill(Color.web("#FF9999"));
        saluteRow.getChildren().addAll(saluteLabel, saluteValue);
        
        // Danno
        HBox dannoRow = new HBox(5);
        dannoRow.setAlignment(Pos.CENTER);
        Label dannoLabel = new Label("Danno:");
        dannoLabel.setFont(Font.font("Arial", 10));
        dannoLabel.setTextFill(Color.web("#CCCCCC"));
        Label dannoValue = new Label("⚔️ " + malvagio.getDanno());
        dannoValue.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        dannoValue.setTextFill(Color.web("#FFAA66"));
        dannoRow.getChildren().addAll(dannoLabel, dannoValue);
        
        box.getChildren().addAll(nome, saluteRow, dannoRow);
        
        // Interattività
        box.setOnMouseClicked(e -> {
            if (GameController.getInstance() != null) {
                GameController.getInstance().attaccaMalvagio(indice);
            }
        });
        
        box.setOnMouseEntered(e -> {
            box.setStyle("-fx-border-color: #FF6666; -fx-border-width: 3; -fx-background-color: #3a1a1a; -fx-cursor: hand; -fx-border-radius: 5; -fx-background-radius: 5;");
            box.setScaleX(1.02);
            box.setScaleY(1.02);
        });
        
        box.setOnMouseExited(e -> {
            box.setStyle("-fx-border-color: #FF9999; -fx-border-width: 2; -fx-background-color: #2a0a0a; -fx-border-radius: 5; -fx-background-radius: 5;");
            box.setScaleX(1.0);
            box.setScaleY(1.0);
        });
        
        return box;
    }
    
    private void aggiornaArtiOscure() {
        // Pulisci tutto tranne il titolo
        while (ultimaArtiOscureBox.getChildren().size() > 1) {
            ultimaArtiOscureBox.getChildren().remove(1);
        }
        
        // Pulisci i container interni
        imageArtiOscureContainer.getChildren().clear();
        descrArtiOscureContainer.getChildren().clear();
        
        // Aggiorna count mazzo
        if (mazzoArtiOscureBox.getChildren().size() >= 2) {
            ((Label)mazzoArtiOscureBox.getChildren().get(1)).setText("Mazzo: " + stato.getMazzoArtiOscure().size());
        }
        
        if (stato.getScartiArtiOscure() != null && !stato.getScartiArtiOscure().isEmpty()) {
            ArteOscura ultima = stato.getScartiArtiOscure().get(stato.getScartiArtiOscure().size() - 1);
            
            if (ultima != null) {
                // ... (Codice esistente per caricare l'immagine nel imageArtiOscureContainer) ...
                if (ultima.getPathImmagine() != null && !ultima.getPathImmagine().isEmpty()) {
                    Image img = ImageLoader.caricaImmagine(ultima.getPathImmagine());
                    if (img != null) {
                        ImageView iv = new ImageView(img);
                        iv.setFitWidth(180);
                        iv.setFitHeight(100);
                        iv.setPreserveRatio(true);
                        iv.setSmooth(true);
                        imageArtiOscureContainer.getChildren().add(iv);
                    }
                }
                
                // ... (Codice esistente per caricare i testi nel descrArtiOscureContainer) ...
                Label nome = new Label(ultima.getNome());
                nome.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                nome.setTextFill(Color.WHITE);
                nome.setWrapText(true);
                nome.setMaxWidth(180);
                
                Label desc = new Label(ultima.getDescrizione());
                desc.setFont(Font.font("Arial", 12));
                desc.setTextFill(Color.web("#CCCCCC"));
                desc.setWrapText(true);
                desc.setMaxWidth(180);
                
                descrArtiOscureContainer.getChildren().addAll(nome, desc);
                
                // --- MODIFICA QUI ---
                
                // Creiamo un contenitore orizzontale temporaneo
                HBox rigaContenuto = new HBox(10); // 10 è lo spazio tra immagine e descrizione
                rigaContenuto.setAlignment(Pos.CENTER); // Centra il contenuto
                
                // Aggiungiamo i due container (immagine e descrizione) dentro l'HBox
                rigaContenuto.getChildren().addAll(imageArtiOscureContainer, descrArtiOscureContainer);
                
                // Aggiungiamo l'HBox al contenitore principale (che è verticale)
                // Risultato: Titolo (sopra) -> HBox con Immagine + Descrizione (sotto)
                ultimaArtiOscureBox.getChildren().add(rigaContenuto);
                
            }
        } else {
            // Messaggio vuoto
            Label vuota = new Label("Nessuna carta giocata");
            vuota.setFont(Font.font("Arial", 14));
            vuota.setTextFill(Color.GRAY);
            ultimaArtiOscureBox.getChildren().add(vuota);
        }
    }
    
    private void aggiornaHorcrux() {
        if (!stato.isHasHorcruxes()) return;
        while (horcruxBox.getChildren().size() > 1) horcruxBox.getChildren().remove(1);
        
        VBox list = new VBox(5);
        list.setAlignment(Pos.TOP_CENTER);
        
        for (Horcrux h : stato.getHorcruxAttivi()) {
            VBox b = new VBox(4);
            b.setStyle("-fx-border-color: #FF6666; -fx-border-width: 2; -fx-background-color: #1a0a0a; -fx-padding: 8; -fx-border-radius: 3; -fx-background-radius: 3;");
            b.setAlignment(Pos.CENTER);
            
            if(h.getPathImmagine() != null && !h.getPathImmagine().isEmpty()) {
            	Image img = ImageLoader.caricaImmagine(h.getPathImmagine());
                if (img != null) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(180);
                    iv.setFitHeight(100);
                    iv.setPreserveRatio(true);
                    iv.setSmooth(true);
                    horcruxBox.getChildren().add(iv);
                }
            }
            
            // Nome
            Label nome = new Label(h.getNome());
            nome.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            nome.setTextFill(Color.web("#FF6666"));
            nome.setWrapText(true);
            nome.setMaxWidth(140);
            nome.setAlignment(Pos.CENTER);
            
            // Segnalini visuali
            HBox segnalini = new HBox(3);
            segnalini.setAlignment(Pos.CENTER);
            int attuali = h.getSegnaliniAssegnati().size();
            int totali = h.getSegnaliniRichiesti().size();
            
            for (int i = 0; i < totali; i++) {
                Label seg = new Label(i < attuali ? "⬤" : "⭕");
                seg.setTextFill(i < attuali ? Color.web("#00FF00") : Color.web("#666666"));
                seg.setFont(Font.font("Arial", 10));
                segnalini.getChildren().add(seg);
            }
            
            // Contatore
            HBox contatoreRow = new HBox(5);
            contatoreRow.setAlignment(Pos.CENTER);
            Label contatoreLabel = new Label("Progresso:");
            contatoreLabel.setFont(Font.font("Arial", 9));
            contatoreLabel.setTextFill(Color.web("#CCCCCC"));
            Label contatoreValue = new Label(attuali + "/" + totali);
            contatoreValue.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            contatoreValue.setTextFill(Color.web("#00FF00"));
            contatoreRow.getChildren().addAll(contatoreLabel, contatoreValue);
            
            b.getChildren().addAll(nome, segnalini, contatoreRow);
            list.getChildren().add(b);
        }
        
        if (stato.getHorcruxAttivi().isEmpty()) {
            Label vuoto = new Label("✅ Tutti distrutti!");
            vuoto.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            vuoto.setTextFill(Color.web("#00FF00"));
            list.getChildren().add(vuoto);
        }
        
        horcruxBox.getChildren().add(list);
    }
    
    public void aggiorna() {
        aggiornaLuogo();
        aggiornaMercato();
        aggiornaMalvagiAttivi();
        aggiornaArtiOscure();
        aggiornaHorcrux();
        
        if (mazzoMalvagiBox.getChildren().size() >= 2) {
            ((Label)mazzoMalvagiBox.getChildren().get(1)).setText("Carte rimanenti: " + stato.getMazzoMalvagi().size());
        }
    }
}