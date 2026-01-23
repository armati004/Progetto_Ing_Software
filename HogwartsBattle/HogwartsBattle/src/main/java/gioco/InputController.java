package gioco;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import carte.Carta;
import gestoreEffetti.Effetto;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * InputController - Gestisce tutte le interazioni utente con dialoghi modali
 * VERSIONE CORRETTA con migliore gestione thread JavaFX
 */
public class InputController {
    
    /**
     * Chiede al giocatore di scegliere una carta da una lista
     */
    public static Carta scegliCarta(List<Carta> carte, String titolo, String messaggio) {
        if (carte == null || carte.isEmpty()) {
            return null;
        }
        
        if (carte.size() == 1) {
            return carte.get(0);
        }
        
        // ⭐ FIX: Se siamo già sul thread JavaFX, esegui direttamente
        if (Platform.isFxApplicationThread()) {
            return scegliCartaSync(carte, titolo, messaggio);
        }
        
        // Altrimenti usa CountDownLatch per sincronizzare
        AtomicReference<Carta> risultato = new AtomicReference<>(null);
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                risultato.set(scegliCartaSync(carte, titolo, messaggio));
            } catch (Exception e) {
                e.printStackTrace();
                risultato.set(carte.get(0));
            } finally {
                latch.countDown();
            }
        });
        
        try {
            latch.await();
            return risultato.get() != null ? risultato.get() : carte.get(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return carte.get(0);
        }
    }
    
    /**
     * Metodo sincrono per scegliere carta (deve essere chiamato sul thread JavaFX)
     */
    private static Carta scegliCartaSync(List<Carta> carte, String titolo, String messaggio) {
        AtomicReference<Carta> risultato = new AtomicReference<>(null);
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(titolo);
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2a2a2a;");
        
        Label titleLabel = new Label(messaggio);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web("#FFD700"));
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(350);
        
        VBox carteBox = new VBox(10);
        carteBox.setAlignment(Pos.CENTER);
        
        for (Carta carta : carte) {
            Button btn = new Button(carta.getNome() + " (Costo: " + carta.getCosto() + ")");
            btn.setPrefWidth(300);
            btn.setPrefHeight(40);
            btn.setStyle(
                "-fx-background-color: #0066cc; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 12px; " +
                "-fx-font-weight: bold;"
            );
            
            btn.setOnMouseEntered(e -> 
                btn.setStyle(
                    "-fx-background-color: #0052a3; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold;"
                )
            );
            
            btn.setOnMouseExited(e -> 
                btn.setStyle(
                    "-fx-background-color: #0066cc; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold;"
                )
            );
            
            btn.setOnAction(e -> {
                risultato.set(carta);
                dialog.close();
            });
            
            carteBox.getChildren().add(btn);
        }
        
        layout.getChildren().addAll(titleLabel, carteBox);
        
        Scene scene = new Scene(layout);
        dialog.setScene(scene);
        dialog.showAndWait();
        
        return risultato.get() != null ? risultato.get() : carte.get(0);
    }
    
    /**
     * Chiede al giocatore di scegliere un effetto tra più opzioni
     */
    public static int scegliEffetto(List<Effetto> effetti, String titolo, String messaggio) {
        if (effetti == null || effetti.isEmpty()) {
            return -1;
        }
        
        if (effetti.size() == 1) {
            return 0;
        }
        
        // ⭐ FIX: Controllo thread JavaFX
        if (Platform.isFxApplicationThread()) {
            return scegliEffettoSync(effetti, titolo, messaggio);
        }
        
        AtomicReference<Integer> risultato = new AtomicReference<>(-1);
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                risultato.set(scegliEffettoSync(effetti, titolo, messaggio));
            } catch (Exception e) {
                e.printStackTrace();
                risultato.set(0);
            } finally {
                latch.countDown();
            }
        });
        
        try {
            latch.await();
            return risultato.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private static int scegliEffettoSync(List<Effetto> effetti, String titolo, String messaggio) {
        AtomicReference<Integer> risultato = new AtomicReference<>(-1);
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(titolo);
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2a2a2a;");
        
        Label titleLabel = new Label(messaggio);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web("#FFD700"));
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(400);
        
        VBox effettiBox = new VBox(10);
        effettiBox.setAlignment(Pos.CENTER);
        
        for (int i = 0; i < effetti.size(); i++) {
            final int indice = i;
            Effetto eff = effetti.get(i);
            
            String testo = "Opzione " + (i + 1) + ": " + eff.getType();
            if (eff.getQta() != null && eff.getQta() > 0) {
                testo += " (Quantità: " + eff.getQta() + ")";
            }
            
            Button btn = new Button(testo);
            btn.setPrefWidth(350);
            btn.setPrefHeight(50);
            btn.setStyle(
                "-fx-background-color: #9966FF; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 12px; " +
                "-fx-font-weight: bold;"
            );
            
            btn.setOnMouseEntered(e -> 
                btn.setStyle(
                    "-fx-background-color: #7744DD; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold;"
                )
            );
            
            btn.setOnMouseExited(e -> 
                btn.setStyle(
                    "-fx-background-color: #9966FF; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold;"
                )
            );
            
            btn.setOnAction(e -> {
                risultato.set(indice);
                dialog.close();
            });
            
            effettiBox.getChildren().add(btn);
        }
        
        layout.getChildren().addAll(titleLabel, effettiBox);
        
        Scene scene = new Scene(layout);
        dialog.setScene(scene);
        dialog.showAndWait();
        
        return risultato.get() >= 0 ? risultato.get() : 0;
    }
    
    /**
     * Chiede al giocatore di scegliere un giocatore tra più opzioni
     */
    public static int scegliGiocatore(List<Giocatore> giocatori, String titolo, String messaggio) {
        if (giocatori == null || giocatori.isEmpty()) {
            return -1;
        }
        
        if (giocatori.size() == 1) {
            return 0;
        }
        
        // ⭐ FIX: Controllo thread JavaFX
        if (Platform.isFxApplicationThread()) {
            return scegliGiocatoreSync(giocatori, titolo, messaggio);
        }
        
        AtomicReference<Integer> risultato = new AtomicReference<>(-1);
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                risultato.set(scegliGiocatoreSync(giocatori, titolo, messaggio));
            } catch (Exception e) {
                e.printStackTrace();
                risultato.set(0);
            } finally {
                latch.countDown();
            }
        });
        
        try {
            latch.await();
            return risultato.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private static int scegliGiocatoreSync(List<Giocatore> giocatori, String titolo, String messaggio) {
        AtomicReference<Integer> risultato = new AtomicReference<>(-1);
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(titolo);
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2a2a2a;");
        
        Label titleLabel = new Label(messaggio);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web("#FFD700"));
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(350);
        
        VBox giocatoriBox = new VBox(10);
        giocatoriBox.setAlignment(Pos.CENTER);
        
        for (int i = 0; i < giocatori.size(); i++) {
            final int indice = i;
            Giocatore g = giocatori.get(i);
            
            String testo = g.getEroe().getNome() + " (❤️ " + g.getSalute() + "/" + g.getSaluteMax() + ")";
            
            Button btn = new Button(testo);
            btn.setPrefWidth(300);
            btn.setPrefHeight(40);
            btn.setStyle(
                "-fx-background-color: #00AA66; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 12px; " +
                "-fx-font-weight: bold;"
            );
            
            btn.setOnMouseEntered(e -> 
                btn.setStyle(
                    "-fx-background-color: #008844; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold;"
                )
            );
            
            btn.setOnMouseExited(e -> 
                btn.setStyle(
                    "-fx-background-color: #00AA66; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold;"
                )
            );
            
            btn.setOnAction(e -> {
                risultato.set(indice);
                dialog.close();
            });
            
            giocatoriBox.getChildren().add(btn);
        }
        
        layout.getChildren().addAll(titleLabel, giocatoriBox);
        
        Scene scene = new Scene(layout);
        dialog.setScene(scene);
        dialog.showAndWait();
        
        return risultato.get() >= 0 ? risultato.get() : 0;
    }
    
    /**
     * Mostra un messaggio informativo
     */
    public static void mostraMessaggio(String titolo, String messaggio) {
        // ⭐ FIX: Controllo thread JavaFX
        if (Platform.isFxApplicationThread()) {
            mostraMessaggioSync(titolo, messaggio);
            return;
        }
        
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                mostraMessaggioSync(titolo, messaggio);
            } finally {
                latch.countDown();
            }
        });
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void mostraMessaggioSync(String titolo, String messaggio) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(titolo);
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2a2a2a;");
        
        Label messageLabel = new Label(messaggio);
        messageLabel.setFont(Font.font("Arial", 14));
        messageLabel.setTextFill(Color.WHITE);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        
        Button okBtn = new Button("OK");
        okBtn.setPrefWidth(100);
        okBtn.setStyle(
            "-fx-background-color: #0066cc; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold;"
        );
        okBtn.setOnAction(e -> dialog.close());
        
        layout.getChildren().addAll(messageLabel, okBtn);
        
        Scene scene = new Scene(layout);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}