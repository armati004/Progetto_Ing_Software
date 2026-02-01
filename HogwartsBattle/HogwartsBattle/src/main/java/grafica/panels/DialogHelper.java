package grafica.panels;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * DialogHelper - Helper centralizzato per creare dialog uniformi e stilizzati
 * 
 * Fornisce metodi per creare diversi tipi di dialog (scelta, multipla, messaggio, input)
 * con stili personalizzabili e uniformi in tutto il gioco.
 * 
 * NUOVO: Supporto per messaggi contestuali con informazioni su:
 * - Effetti applicati (nome, descrizione, valore)
 * - Trigger attivati (nome trigger, carta/entità attivante)
 * - Chi ha attivato l'effetto (giocatore, carta, malvagio)
 * 
 * Utilizzo:
 * <pre>
 * // Dialog con 2 opzioni
 * DialogHelper.mostraScelta(
 *     "Titolo", "Header", "Contenuto",
 *     "Opzione 1", "Opzione 2",
 *     scelta -> { // callback },
 *     DialogHelper.DialogStyle.DADO()
 * );
 * 
 * // Dialog con scelta effetto e contesto
 * DialogHelper.mostraSceltaEffetto(
 *     "Harry Potter",
 *     "Expelliarmus",
 *     "Scarta 1 carta per guadagnare 2 ❤",
 *     "Opzione 1", "Opzione 2",
 *     scelta -> { // callback }
 * );
 * </pre>
 */
public class DialogHelper {
    
    /**
     * Classe per definire gli stili dei dialog
     */
    public static class DialogStyle {
        public String backgroundColor = "#2a2a2a";
        public String borderColor = "#FFD700";
        public String textColor = "#FFFFFF";
        public String headerColor = "#FFD700";
        public String buttonColor = "#0066cc";
        public String buttonHoverColor = "#0052a3";
        public int fontSize = 14;
        public int headerFontSize = 16;
        public int borderWidth = 3;
        
        /**
         * Stile di default - Oro
         */
        public static DialogStyle DEFAULT() {
            return new DialogStyle();
        }
        
        /**
         * Stile per dadi - Arancione
         */
        public static DialogStyle DADO() {
            DialogStyle style = new DialogStyle();
            style.borderColor = "#FF9800";
            style.headerColor = "#FF9800";
            style.buttonColor = "#FF6F00";
            style.buttonHoverColor = "#E65100";
            return style;
        }
        
        /**
         * Stile per scelte - Viola
         */
        public static DialogStyle SCELTA() {
            DialogStyle style = new DialogStyle();
            style.borderColor = "#9C27B0";
            style.headerColor = "#9C27B0";
            style.buttonColor = "#7B1FA2";
            style.buttonHoverColor = "#6A1B9A";
            return style;
        }
        
        /**
         * Stile informativo - Azzurro
         */
        public static DialogStyle INFO() {
            DialogStyle style = new DialogStyle();
            style.borderColor = "#03A9F4";
            style.headerColor = "#03A9F4";
            style.buttonColor = "#0288D1";
            style.buttonHoverColor = "#0277BD";
            return style;
        }
        
        /**
         * Stile di avvertimento - Rosso
         */
        public static DialogStyle WARNING() {
            DialogStyle style = new DialogStyle();
            style.borderColor = "#FF5252";
            style.headerColor = "#FF5252";
            style.buttonColor = "#D32F2F";
            style.buttonHoverColor = "#C62828";
            return style;
        }
        
        /**
         * Stile per carte/gioco - Verde
         */
        public static DialogStyle CARTA() {
            DialogStyle style = new DialogStyle();
            style.borderColor = "#4CAF50";
            style.headerColor = "#4CAF50";
            style.buttonColor = "#388E3C";
            style.buttonHoverColor = "#2E7D32";
            return style;
        }
        
        /**
         * Stile per malvagi - Rosa scuro
         */
        public static DialogStyle MALVAGIO() {
            DialogStyle style = new DialogStyle();
            style.borderColor = "#E91E63";
            style.headerColor = "#E91E63";
            style.buttonColor = "#C2185B";
            style.buttonHoverColor = "#AD1457";
            return style;
        }
        
        /**
         * Stile per effetti - Ciano
         */
        public static DialogStyle EFFETTO() {
            DialogStyle style = new DialogStyle();
            style.borderColor = "#00BCD4";
            style.headerColor = "#00BCD4";
            style.buttonColor = "#0097A7";
            style.buttonHoverColor = "#00838F";
            return style;
        }
        
        /**
         * Stile per trigger - Giallo/Ambra
         */
        public static DialogStyle TRIGGER() {
            DialogStyle style = new DialogStyle();
            style.borderColor = "#FFC107";
            style.headerColor = "#FFC107";
            style.buttonColor = "#FFA000";
            style.buttonHoverColor = "#FF8F00";
            return style;
        }
    }
    
    // ==========================================
    // METODI PRINCIPALI
    // ==========================================
    
    /**
     * Mostra un dialog di conferma con 2 opzioni
     * 
     * @param titolo Titolo del dialog
     * @param header Testo header
     * @param contenuto Testo contenuto
     * @param opzione1 Testo prima opzione
     * @param opzione2 Testo seconda opzione
     * @param callback Callback che riceve true se scelta opzione1, false se opzione2
     * @param style Stile da applicare
     */
    public static void mostraScelta(
            String titolo,
            String header,
            String contenuto,
            String opzione1,
            String opzione2,
            Consumer<Boolean> callback,
            DialogStyle style
    ) {
        Platform.runLater(() -> {
            // Crea Stage personalizzato
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(titolo);
            dialog.setResizable(false);
            
            // Layout principale
            VBox root = new VBox(20);
            root.setPadding(new Insets(25));
            root.setAlignment(Pos.CENTER);
            root.setStyle(
                "-fx-background-color: #2a2a2a;" +
                "-fx-border-color: " + style.borderColor + ";" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 5);"
            );
            
            // Header
            Label headerLabel = new Label(header);
            headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            headerLabel.setTextFill(Color.web(style.headerColor));
            headerLabel.setWrapText(true);
            headerLabel.setMaxWidth(450);
            headerLabel.setAlignment(Pos.CENTER);
            
            // Separatore decorativo
            Separator separator = new Separator();
            separator.setPrefWidth(400);
            separator.setStyle("-fx-background-color: " + style.borderColor + ";");
            
            // Contenuto
            Label contenutoLabel = new Label(contenuto);
            contenutoLabel.setFont(Font.font("Arial", 14));
            contenutoLabel.setTextFill(Color.WHITE);
            contenutoLabel.setWrapText(true);
            contenutoLabel.setMaxWidth(450);
            contenutoLabel.setAlignment(Pos.CENTER);
            
            // Box per i bottoni
            HBox buttonBox = new HBox(15);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));
            
            // Bottone 1
            Button btn1 = new Button(opzione1);
            btn1.setPrefWidth(180);
            btn1.setPrefHeight(45);
            btn1.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            btn1.setStyle(
                "-fx-background-color: " + style.buttonColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            );
            
            btn1.setOnMouseEntered(e -> btn1.setStyle(
                "-fx-background-color: " + style.buttonHoverColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            ));
            
            btn1.setOnMouseExited(e -> btn1.setStyle(
                "-fx-background-color: " + style.buttonColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            ));
            
            btn1.setOnAction(e -> {
                callback.accept(true);
                dialog.close();
            });
            
            // Bottone 2
            Button btn2 = new Button(opzione2);
            btn2.setPrefWidth(180);
            btn2.setPrefHeight(45);
            btn2.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            btn2.setStyle(
                "-fx-background-color: " + style.buttonColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            );
            
            btn2.setOnMouseEntered(e -> btn2.setStyle(
                "-fx-background-color: " + style.buttonHoverColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            ));
            
            btn2.setOnMouseExited(e -> btn2.setStyle(
                "-fx-background-color: " + style.buttonColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            ));
            
            btn2.setOnAction(e -> {
                callback.accept(false);
                dialog.close();
            });
            
            // Aggiungi bottoni
            buttonBox.getChildren().addAll(btn1, btn2);
            
            // Assembla layout
            root.getChildren().addAll(headerLabel, separator, contenutoLabel, buttonBox);
            
            // Crea e mostra scena
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            dialog.setScene(scene);
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.showAndWait();
        });
    }
    
    /**
     * Mostra un dialog con scelta multipla (N opzioni)
     * 
     * @param titolo Titolo del dialog
     * @param header Testo header
     * @param contenuto Testo contenuto
     * @param opzioni Lista delle opzioni
     * @param callback Callback che riceve l'indice dell'opzione scelta (0-based)
     * @param style Stile da applicare
     */
    public static void mostraSceltaMultipla(
            String titolo,
            String header,
            String contenuto,
            List<String> opzioni,
            Consumer<Integer> callback,
            DialogStyle style
    ) {
        Platform.runLater(() -> {
            // Crea Stage personalizzato
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(titolo);
            dialog.setResizable(false);
            
            // Layout principale
            VBox root = new VBox(20);
            root.setPadding(new Insets(25));
            root.setAlignment(Pos.CENTER);
            root.setStyle(
                "-fx-background-color: #2a2a2a;" +
                "-fx-border-color: " + style.borderColor + ";" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 5);"
            );
            
            // Header
            Label headerLabel = new Label(header);
            headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            headerLabel.setTextFill(Color.web(style.headerColor));
            headerLabel.setWrapText(true);
            headerLabel.setMaxWidth(450);
            headerLabel.setAlignment(Pos.CENTER);
            
            // Separatore decorativo
            Separator separator = new Separator();
            separator.setPrefWidth(400);
            separator.setStyle("-fx-background-color: " + style.borderColor + ";");
            
            // Contenuto
            Label contenutoLabel = new Label(contenuto);
            contenutoLabel.setFont(Font.font("Arial", 14));
            contenutoLabel.setTextFill(Color.WHITE);
            contenutoLabel.setWrapText(true);
            contenutoLabel.setMaxWidth(450);
            contenutoLabel.setAlignment(Pos.CENTER);
            
            // Box per i bottoni (griglia se > 3 opzioni, altrimenti orizzontale)
            VBox buttonContainer = new VBox(10);
            buttonContainer.setAlignment(Pos.CENTER);
            buttonContainer.setPadding(new Insets(10, 0, 0, 0));
            
            // Se ci sono più di 3 opzioni, usa ScrollPane
            if (opzioni.size() > 3) {
                VBox buttonsBox = new VBox(10);
                buttonsBox.setAlignment(Pos.CENTER);
                
                for (int i = 0; i < opzioni.size(); i++) {
                    final int index = i;
                    Button btn = new Button(opzioni.get(i));
                    btn.setPrefWidth(350);
                    btn.setPrefHeight(45);
                    btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    btn.setStyle(
                        "-fx-background-color: " + style.buttonColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
                    );
                    
                    btn.setOnMouseEntered(e -> btn.setStyle(
                        "-fx-background-color: " + style.buttonHoverColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
                        "-fx-scale-x: 1.05;" +
                        "-fx-scale-y: 1.05;"
                    ));
                    
                    btn.setOnMouseExited(e -> btn.setStyle(
                        "-fx-background-color: " + style.buttonColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
                    ));
                    
                    btn.setOnAction(e -> {
                        callback.accept(index);
                        dialog.close();
                    });
                    
                    buttonsBox.getChildren().add(btn);
                }
                
                ScrollPane scrollPane = new ScrollPane(buttonsBox);
                scrollPane.setMaxHeight(300);
                scrollPane.setFitToWidth(true);
                scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
                buttonContainer.getChildren().add(scrollPane);
            } else {
                // Poche opzioni: disposizione orizzontale
                HBox buttonBox = new HBox(15);
                buttonBox.setAlignment(Pos.CENTER);
                
                for (int i = 0; i < opzioni.size(); i++) {
                    final int index = i;
                    Button btn = new Button(opzioni.get(i));
                    btn.setPrefWidth(180);
                    btn.setPrefHeight(45);
                    btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    btn.setStyle(
                        "-fx-background-color: " + style.buttonColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
                    );
                    
                    btn.setOnMouseEntered(e -> btn.setStyle(
                        "-fx-background-color: " + style.buttonHoverColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
                        "-fx-scale-x: 1.05;" +
                        "-fx-scale-y: 1.05;"
                    ));
                    
                    btn.setOnMouseExited(e -> btn.setStyle(
                        "-fx-background-color: " + style.buttonColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
                    ));
                    
                    btn.setOnAction(e -> {
                        callback.accept(index);
                        dialog.close();
                    });
                    
                    buttonBox.getChildren().add(btn);
                }
                
                buttonContainer.getChildren().add(buttonBox);
            }
            
            // Assembla layout
            root.getChildren().addAll(headerLabel, separator, contenutoLabel, buttonContainer);
            
            // Crea e mostra scena
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            dialog.setScene(scene);
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.showAndWait();
        });
    }
    
    /**
     * Mostra un messaggio informativo (solo OK)
     * 
     * @param titolo Titolo del dialog
     * @param header Testo header
     * @param contenuto Testo contenuto
     * @param style Stile da applicare
     */
    public static void mostraMessaggio(
            String titolo,
            String header,
            String contenuto,
            DialogStyle style
    ) {
        Platform.runLater(() -> {
            // Crea Stage personalizzato
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(titolo);
            dialog.setResizable(false);
            
            // Layout principale
            VBox root = new VBox(20);
            root.setPadding(new Insets(25));
            root.setAlignment(Pos.CENTER);
            root.setStyle(
                "-fx-background-color: #2a2a2a;" +
                "-fx-border-color: " + style.borderColor + ";" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 5);"
            );
            
            // Header
            Label headerLabel = new Label(header);
            headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            headerLabel.setTextFill(Color.web(style.headerColor));
            headerLabel.setWrapText(true);
            headerLabel.setMaxWidth(450);
            headerLabel.setAlignment(Pos.CENTER);
            
            // Separatore decorativo
            Separator separator = new Separator();
            separator.setPrefWidth(400);
            separator.setStyle("-fx-background-color: " + style.borderColor + ";");
            
            // Contenuto
            Label contenutoLabel = new Label(contenuto);
            contenutoLabel.setFont(Font.font("Arial", 14));
            contenutoLabel.setTextFill(Color.WHITE);
            contenutoLabel.setWrapText(true);
            contenutoLabel.setMaxWidth(450);
            contenutoLabel.setAlignment(Pos.CENTER);
            
            // Box per il bottone
            HBox buttonBox = new HBox();
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));
            
            // Bottone OK
            Button btnOk = new Button("OK");
            btnOk.setPrefWidth(180);
            btnOk.setPrefHeight(45);
            btnOk.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            btnOk.setStyle(
                "-fx-background-color: " + style.buttonColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            );
            
            btnOk.setOnMouseEntered(e -> btnOk.setStyle(
                "-fx-background-color: " + style.buttonHoverColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            ));
            
            btnOk.setOnMouseExited(e -> btnOk.setStyle(
                "-fx-background-color: " + style.buttonColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            ));
            
            btnOk.setOnAction(e -> dialog.close());
            
            buttonBox.getChildren().add(btnOk);
            
            // Assembla layout
            root.getChildren().addAll(headerLabel, separator, contenutoLabel, buttonBox);
            
            // Crea e mostra scena
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            dialog.setScene(scene);
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.showAndWait();
        });
    }
    
    /**
     * Mostra un dialog di input testuale
     * 
     * @param titolo Titolo del dialog
     * @param header Testo header
     * @param contenuto Testo contenuto
     * @param valoreDefault Valore di default nel campo input
     * @param callback Callback che riceve il testo inserito
     * @param style Stile da applicare
     */
    public static void mostraInput(
            String titolo,
            String header,
            String contenuto,
            String valoreDefault,
            Consumer<String> callback,
            DialogStyle style
    ) {
        Platform.runLater(() -> {
            // Crea Stage personalizzato
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(titolo);
            dialog.setResizable(false);
            
            // Layout principale
            VBox root = new VBox(20);
            root.setPadding(new Insets(25));
            root.setAlignment(Pos.CENTER);
            root.setStyle(
                "-fx-background-color: #2a2a2a;" +
                "-fx-border-color: " + style.borderColor + ";" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 5);"
            );
            
            // Header
            Label headerLabel = new Label(header);
            headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            headerLabel.setTextFill(Color.web(style.headerColor));
            headerLabel.setWrapText(true);
            headerLabel.setMaxWidth(450);
            headerLabel.setAlignment(Pos.CENTER);
            
            // Separatore decorativo
            Separator separator = new Separator();
            separator.setPrefWidth(400);
            separator.setStyle("-fx-background-color: " + style.borderColor + ";");
            
            // Contenuto
            Label contenutoLabel = new Label(contenuto);
            contenutoLabel.setFont(Font.font("Arial", 14));
            contenutoLabel.setTextFill(Color.WHITE);
            contenutoLabel.setWrapText(true);
            contenutoLabel.setMaxWidth(450);
            contenutoLabel.setAlignment(Pos.CENTER);
            
            // Campo di input
            TextField textField = new TextField(valoreDefault);
            textField.setMaxWidth(400);
            textField.setPrefHeight(40);
            textField.setFont(Font.font("Arial", 14));
            textField.setStyle(
                "-fx-background-color: #3a3a3a;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: gray;" +
                "-fx-background-radius: 5;" +
                "-fx-border-color: " + style.borderColor + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 5;" +
                "-fx-padding: 8;"
            );
            
            // Box per i bottoni
            HBox buttonBox = new HBox(15);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));
            
            // Bottone OK
            Button btnOk = new Button("OK");
            btnOk.setPrefWidth(180);
            btnOk.setPrefHeight(45);
            btnOk.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            btnOk.setStyle(
                "-fx-background-color: " + style.buttonColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            );
            
            btnOk.setOnMouseEntered(e -> btnOk.setStyle(
                "-fx-background-color: " + style.buttonHoverColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            ));
            
            btnOk.setOnMouseExited(e -> btnOk.setStyle(
                "-fx-background-color: " + style.buttonColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            ));
            
            btnOk.setOnAction(e -> {
                callback.accept(textField.getText());
                dialog.close();
            });
            
            // Bottone Annulla
            Button btnCancel = new Button("Annulla");
            btnCancel.setPrefWidth(180);
            btnCancel.setPrefHeight(45);
            btnCancel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            btnCancel.setStyle(
                "-fx-background-color: " + style.buttonColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            );
            
            btnCancel.setOnMouseEntered(e -> btnCancel.setStyle(
                "-fx-background-color: " + style.buttonHoverColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            ));
            
            btnCancel.setOnMouseExited(e -> btnCancel.setStyle(
                "-fx-background-color: " + style.buttonColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            ));
            
            btnCancel.setOnAction(e -> dialog.close());
            
            // Enter per confermare
            textField.setOnAction(e -> {
                callback.accept(textField.getText());
                dialog.close();
            });
            
            buttonBox.getChildren().addAll(btnOk, btnCancel);
            
            // Assembla layout
            root.getChildren().addAll(headerLabel, separator, contenutoLabel, textField, buttonBox);
            
            // Crea e mostra scena
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            dialog.setScene(scene);
            dialog.initStyle(StageStyle.TRANSPARENT);
            
            // Focus sul campo di testo
            Platform.runLater(() -> textField.requestFocus());
            
            dialog.showAndWait();
        });
    }
    
    // ==========================================
    // METODI CONTESTUALI
    // ==========================================
    
    /**
     * Mostra una scelta tra due effetti con contesto dettagliato
     * Mostra chi sta facendo la scelta e quale carta/effetto sta attivando
     * 
     * @param nomeGiocatore Nome del giocatore che deve scegliere
     * @param nomeCarta Nome della carta/effetto che offre la scelta
     * @param descrizione Descrizione dell'effetto principale
     * @param opzione1 Testo opzione 1
     * @param opzione2 Testo opzione 2
     * @param callback Callback che riceve true se scelta opzione1, false se opzione2
     */
    public static void mostraSceltaEffetto(
            String nomeGiocatore,
            String nomeCarta,
            String descrizione,
            String opzione1,
            String opzione2,
            Consumer<Boolean> callback
    ) {
        String header = nomeGiocatore + ", fai la tua scelta:";
        String contenuto = nomeCarta + "\n\n" +
                          descrizione + "\n\n" +
                          "━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                          "Opzione 1: " + opzione1 + "\n\n" +
                          "Opzione 2: " + opzione2;
        
        mostraScelta("Scelta - " + nomeGiocatore, header, contenuto, 
                    "Opzione 1", "Opzione 2", callback, DialogStyle.EFFETTO());
    }
    
    /**
     * Mostra un messaggio di trigger attivato con contesto
     * 
     * @param nomeTrigger Nome del trigger attivato
     * @param nomeAttivante Nome della carta/entità che ha attivato il trigger
     * @param descrizioneEffetto Descrizione dell'effetto del trigger
     * @param callback Callback da eseguire dopo la chiusura
     */
    public static void mostraTriggerAttivato(
            String nomeTrigger,
            String nomeAttivante,
            String descrizioneEffetto,
            Runnable callback
    ) {
        String header = "Trigger attivato!";
        String contenuto = "Trigger: " + nomeTrigger + "\n" +
                          "Attivato da: " + nomeAttivante + "\n\n" +
                          "━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                          descrizioneEffetto;
        
        // Usa mostraMessaggio con callback
        Platform.runLater(() -> {
            mostraMessaggio("Trigger - " + nomeTrigger, header, contenuto, DialogStyle.TRIGGER());
            if (callback != null) {
                callback.run();
            }
        });
    }
    
    /**
     * Mostra un messaggio di effetto applicato con contesto
     * 
     * @param nomeEffetto Nome dell'effetto
     * @param bersaglio Chi/cosa ha ricevuto l'effetto
     * @param attivante Chi/cosa ha attivato l'effetto
     * @param risultato Descrizione del risultato dell'effetto
     */
    public static void mostraEffettoApplicato(
            String nomeEffetto,
            String bersaglio,
            String attivante,
            String risultato
    ) {
        String header = "Effetto applicato";
        String contenuto = "Effetto: " + nomeEffetto + "\n" +
                          "Attivato da: " + attivante + "\n" +
                          "Bersaglio: " + bersaglio + "\n\n" +
                          "━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                          "Risultato:\n" + risultato;
        
        mostraMessaggio("Effetto - " + nomeEffetto, header, contenuto, DialogStyle.EFFETTO());
    }
    
    /**
     * Mostra una scelta con informazioni sul posizionamento di una carta
     * (per trigger tipo "Metti in cima al mazzo" o "Negli scarti")
     * 
     * @param nomeCarta Nome della carta da posizionare
     * @param tipoCarta Tipo della carta (Incantesimo, Oggetto, etc.)
     * @param nomeTrigger Nome del trigger che offre la scelta
     * @param descrizoneTrigger Descrizione di cosa fa il trigger
     * @param callback Callback che riceve true se "In cima", false se "Negli scarti"
     */
    public static void mostraSceltaPosizionamentoCarta(
            String nomeCarta,
            String tipoCarta,
            String nomeTrigger,
            String descrizoneTrigger,
            Consumer<Boolean> callback
    ) {
        String header = "Posizionamento carta acquistata";
        String contenuto = "Hai acquistato: " + nomeCarta + " (" + tipoCarta + ")\n\n" +
                          "━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                          "Trigger attivo: " + nomeTrigger + "\n" +
                          descrizoneTrigger + "\n\n" +
                          "Dove vuoi posizionare la carta?";
        
        mostraScelta("Posizionamento - " + nomeCarta, header, contenuto,
                    "In cima al mazzo", "Negli scarti", callback, DialogStyle.TRIGGER());
    }
    
    /**
     * Mostra una conferma semplice con contesto del giocatore
     * 
     * @param nomeGiocatore Nome del giocatore
     * @param domanda Domanda da porre
     * @param callback Callback che riceve true se Sì, false se No
     */
    public static void mostraConfermaGiocatore(
            String nomeGiocatore,
            String domanda,
            Consumer<Boolean> callback
    ) {
        String header = nomeGiocatore + ", conferma la tua azione:";
        mostraScelta("Conferma - " + nomeGiocatore, header, domanda, 
                    "Sì", "No", callback, DialogStyle.SCELTA());
    }
    
    // ==========================================
    // METODI DI UTILITÀ
    // ==========================================
    
    /**
     * Mostra un messaggio semplice con solo titolo e contenuto
     * 
     * @param titolo Titolo del dialog
     * @param contenuto Testo contenuto
     */
    public static void mostraMessaggioSemplice(String titolo, String contenuto) {
        mostraMessaggio(titolo, null, contenuto, DialogStyle.INFO());
    }
    
    /**
     * Mostra una conferma sì/no
     * 
     * @param titolo Titolo del dialog
     * @param domanda Domanda da porre
     * @param callback Callback che riceve true se Sì, false se No
     */
    public static void mostraConferma(String titolo, String domanda, Consumer<Boolean> callback) {
        mostraScelta(titolo, null, domanda, "Sì", "No", callback, DialogStyle.DEFAULT());
    }
}