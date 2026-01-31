package grafica.ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * DialogSystem - Sistema per creare e gestire popup e dialoghi.
 * 
 * Tipi di dialoghi supportati:
 * - Conferma (Sì/No)
 * - Informazione (OK)
 * - Selezione (Lista opzioni)
 * - Input testo
 * - Custom (layout personalizzato)
 */
public class DialogSystem {
    
    private StackPane rootContainer;
    private StackPane dialogOverlay;
    private boolean isDialogVisible;
    
    /**
     * Costruttore
     */
    public DialogSystem(Pane root) {
        this.isDialogVisible = false;
        inizializzaOverlay(root);
    }
    
    /**
     * Inizializza l'overlay per i dialoghi
     */
    private void inizializzaOverlay(Pane root) {
        dialogOverlay = new StackPane();
        dialogOverlay.setLayoutX(0);
        dialogOverlay.setLayoutY(0);
        dialogOverlay.setPrefSize(1920, 1080);
        dialogOverlay.setVisible(false);
        dialogOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
        
        // Click sull'overlay chiude il dialogo
        dialogOverlay.setOnMouseClicked(event -> {
            if (event.getTarget() == dialogOverlay) {
                chiudiDialogo();
            }
        });
        
        root.getChildren().add(dialogOverlay);
    }
    
    // ========================================================================
    // DIALOGHI STANDARD
    // ========================================================================
    
    /**
     * Mostra un dialogo di conferma
     */
    public void mostraConferma(String titolo, String messaggio, 
                              Runnable onConferma, Runnable onAnnulla) {
        VBox content = creaDialogoBase(titolo, messaggio, 500, 300);
        
        // Pulsanti
        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        
        // Pulsante Conferma
        VBox confermaBtn = creaPulsante("✓ CONFERMA", Color.LIGHTGREEN, () -> {
            chiudiDialogo();
            if (onConferma != null) onConferma.run();
        });
        
        // Pulsante Annulla
        VBox annullaBtn = creaPulsante("✖ ANNULLA", Color.LIGHTCORAL, () -> {
            chiudiDialogo();
            if (onAnnulla != null) onAnnulla.run();
        });
        
        buttonsBox.getChildren().addAll(confermaBtn, annullaBtn);
        content.getChildren().add(buttonsBox);
        
        mostraDialogo(content);
    }
    
    /**
     * Mostra un dialogo informativo
     */
    public void mostraInfo(String titolo, String messaggio) {
        mostraInfo(titolo, messaggio, null);
    }
    
    /**
     * Mostra un dialogo informativo con callback
     */
    public void mostraInfo(String titolo, String messaggio, Runnable onClose) {
        VBox content = creaDialogoBase(titolo, messaggio, 450, 250);
        
        // Pulsante OK
        VBox okBtn = creaPulsante("OK", Color.LIGHTBLUE, () -> {
            chiudiDialogo();
            if (onClose != null) onClose.run();
        });
        
        content.getChildren().add(okBtn);
        
        mostraDialogo(content);
    }
    
    /**
     * Mostra un dialogo di errore
     */
    public void mostraErrore(String messaggio) {
        VBox content = creaDialogoBase("❌ ERRORE", messaggio, 450, 250);
        content.setStyle("-fx-background-color: #3a1a1a; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: red; " +
                        "-fx-border-width: 3; " +
                        "-fx-border-radius: 15; " +
                        "-fx-padding: 30;");
        
        // Pulsante OK
        VBox okBtn = creaPulsante("OK", Color.RED, this::chiudiDialogo);
        
        content.getChildren().add(okBtn);
        
        mostraDialogo(content);
    }
    
    /**
     * Mostra un dialogo con selezione multipla
     */
    public void mostraSelezione(String titolo, String messaggio, 
                               String[] opzioni, Consumer<Integer> onSelect) {
        VBox content = creaDialogoBase(titolo, messaggio, 500, 400);
        
        VBox opzioniBox = new VBox(10);
        opzioniBox.setAlignment(Pos.CENTER);
        
        for (int i = 0; i < opzioni.length; i++) {
            final int index = i;
            String opzione = opzioni[i];
            
            VBox opzioneBtn = creaPulsante(opzione, Color.LIGHTBLUE, () -> {
                chiudiDialogo();
                if (onSelect != null) onSelect.accept(index);
            });
            
            opzioniBox.getChildren().add(opzioneBtn);
        }
        
        content.getChildren().add(opzioniBox);
        
        mostraDialogo(content);
    }
    
    // ========================================================================
    // DIALOGHI CUSTOM
    // ========================================================================
    
    /**
     * Mostra un dialogo personalizzato
     */
    public void mostraCustom(Pane customContent) {
        mostraDialogo(customContent);
    }
    
    /**
     * Crea un dialogo base con titolo e messaggio
     */
    private VBox creaDialogoBase(String titolo, String messaggio, double width, double height) {
        VBox dialog = new VBox(20);
        dialog.setAlignment(Pos.CENTER);
        dialog.setPrefSize(width, height);
        dialog.setStyle("-fx-background-color: #2a2a3e; " +
                       "-fx-background-radius: 15; " +
                       "-fx-border-color: gold; " +
                       "-fx-border-width: 3; " +
                       "-fx-border-radius: 15; " +
                       "-fx-padding: 30;");
        
        // Titolo
        Text titoloText = new Text(titolo);
        titoloText.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 28));
        titoloText.setFill(Color.GOLD);
        
        // Messaggio
        Text messaggioText = new Text(messaggio);
        messaggioText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        messaggioText.setFill(Color.WHITE);
        messaggioText.setWrappingWidth(width - 60);
        messaggioText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        dialog.getChildren().addAll(titoloText, messaggioText);
        
        return dialog;
    }
    
    /**
     * Crea un pulsante stilizzato
     */
    private VBox creaPulsante(String testo, Color colore, Runnable onClick) {
        VBox button = new VBox();
        button.setAlignment(Pos.CENTER);
        button.setPrefSize(180, 50);
        button.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); " +
                       "-fx-background-radius: 10; " +
                       "-fx-border-color: " + toHexString(colore) + "; " +
                       "-fx-border-width: 2; " +
                       "-fx-border-radius: 10; " +
                       "-fx-cursor: hand;");
        
        Text buttonText = new Text(testo);
        buttonText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        buttonText.setFill(colore);
        
        button.getChildren().add(buttonText);
        
        // Eventi
        button.setOnMouseClicked(event -> {
            if (onClick != null) onClick.run();
        });
        
        button.setOnMouseEntered(event -> {
            button.setStyle("-fx-background-color: rgba(50, 50, 70, 0.9); " +
                          "-fx-background-radius: 10; " +
                          "-fx-border-color: " + toHexString(colore.brighter()) + "; " +
                          "-fx-border-width: 3; " +
                          "-fx-border-radius: 10; " +
                          "-fx-cursor: hand;");
            buttonText.setFill(colore.brighter());
            
            // Animazione scala
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });
        
        button.setOnMouseExited(event -> {
            button.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); " +
                          "-fx-background-radius: 10; " +
                          "-fx-border-color: " + toHexString(colore) + "; " +
                          "-fx-border-width: 2; " +
                          "-fx-border-radius: 10; " +
                          "-fx-cursor: hand;");
            buttonText.setFill(colore);
            
            // Animazione scala
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        return button;
    }
    
    // ========================================================================
    // GESTIONE DIALOGHI
    // ========================================================================
    
    /**
     * Mostra il dialogo con animazione
     */
    private void mostraDialogo(Pane content) {
        if (isDialogVisible) {
            chiudiDialogo();
        }
        
        dialogOverlay.getChildren().clear();
        dialogOverlay.getChildren().add(content);
        dialogOverlay.setVisible(true);
        isDialogVisible = true;
        
        // Animazione fade in overlay
        FadeTransition overlayFade = new FadeTransition(Duration.millis(200), dialogOverlay);
        overlayFade.setFromValue(0);
        overlayFade.setToValue(1);
        
        // Animazione scale contenuto
        ScaleTransition contentScale = new ScaleTransition(Duration.millis(300), content);
        contentScale.setFromX(0.7);
        contentScale.setFromY(0.7);
        contentScale.setToX(1.0);
        contentScale.setToY(1.0);
        
        // Animazione fade in contenuto
        FadeTransition contentFade = new FadeTransition(Duration.millis(300), content);
        contentFade.setFromValue(0);
        contentFade.setToValue(1);
        
        ParallelTransition parallel = new ParallelTransition(contentScale, contentFade);
        
        overlayFade.play();
        overlayFade.setOnFinished(event -> parallel.play());
    }
    
    /**
     * Chiude il dialogo corrente con animazione
     */
    public void chiudiDialogo() {
        if (!isDialogVisible) return;
        
        FadeTransition fade = new FadeTransition(Duration.millis(200), dialogOverlay);
        fade.setToValue(0);
        fade.setOnFinished(event -> {
            dialogOverlay.setVisible(false);
            dialogOverlay.getChildren().clear();
            isDialogVisible = false;
        });
        fade.play();
    }
    
    // ========================================================================
    // UTILITY
    // ========================================================================
    
    /**
     * Converte Color in stringa hex
     */
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255)
        );
    }
    
    /**
     * Verifica se un dialogo è visibile
     */
    public boolean isDialogVisible() {
        return isDialogVisible;
    }
}