package grafica.panels;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.geometry.Insets;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * DialogHelper - Helper centralizzato per creare dialog uniformi e stilizzati
 * 
 * Fornisce metodi per creare diversi tipi di dialog (scelta, multipla, messaggio, input)
 * con stili personalizzabili e uniformi in tutto il gioco.
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
 * // Dialog con N opzioni
 * List<String> opzioni = Arrays.asList("A", "B", "C");
 * DialogHelper.mostraSceltaMultipla(
 *     "Titolo", "Header", "Contenuto",
 *     opzioni,
 *     indice -> { // callback },
 *     DialogHelper.DialogStyle.SCELTA()
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
    }
    
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            
            alert.setTitle(titolo);
            alert.setHeaderText(header);
            alert.setContentText(contenuto);
            
            ButtonType btn1 = new ButtonType(opzione1);
            ButtonType btn2 = new ButtonType(opzione2);
            
            alert.getButtonTypes().setAll(btn1, btn2);
            
            // Applica stile prima di mostrare
            applicaStile(alert, style);
            
            alert.showAndWait().ifPresent(risposta -> {
                callback.accept(risposta == btn1);
            });
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            
            alert.setTitle(titolo);
            alert.setHeaderText(header);
            alert.setContentText(contenuto);
            
            ButtonType[] buttons = new ButtonType[opzioni.size()];
            for (int i = 0; i < opzioni.size(); i++) {
                buttons[i] = new ButtonType(opzioni.get(i));
            }
            
            alert.getButtonTypes().setAll(buttons);
            
            // Applica stile prima di mostrare
            applicaStile(alert, style);
            
            alert.showAndWait().ifPresent(risposta -> {
                for (int i = 0; i < buttons.length; i++) {
                    if (risposta == buttons[i]) {
                        callback.accept(i);
                        return;
                    }
                }
            });
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            
            alert.setTitle(titolo);
            alert.setHeaderText(header);
            alert.setContentText(contenuto);
            
            // Applica stile prima di mostrare
            applicaStile(alert, style);
            
            alert.showAndWait();
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
            TextInputDialog dialog = new TextInputDialog(valoreDefault);
            dialog.initModality(Modality.APPLICATION_MODAL);
            
            dialog.setTitle(titolo);
            dialog.setHeaderText(header);
            dialog.setContentText(contenuto);
            
            // Applica stile prima di mostrare
            applicaStileInput(dialog, style);
            
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(callback);
        });
    }
    
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
        mostraScelta(titolo, null, domanda, "✓ Sì", "✗ No", callback, DialogStyle.DEFAULT());
    }
    
    /**
     * Applica lo stile personalizzato a un Alert
     */
    private static void applicaStile(Alert alert, DialogStyle style) {
        DialogPane dialogPane = alert.getDialogPane();
        
        // Stile generale del dialog pane
        dialogPane.setStyle(
            "-fx-background-color: " + style.backgroundColor + ";" +
            "-fx-border-color: " + style.borderColor + ";" +
            "-fx-border-width: " + style.borderWidth + ";" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 15, 0, 0, 3);"
        );
        
        dialogPane.setPadding(new Insets(20));
        
        // Stile header
        if (dialogPane.lookup(".header-panel") != null) {
            dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: " + style.backgroundColor + ";" +
                "-fx-padding: 10 0 15 0;"
            );
        }
        
        if (dialogPane.lookup(".header-panel > .label") != null) {
            dialogPane.lookup(".header-panel > .label").setStyle(
                "-fx-text-fill: " + style.headerColor + ";" +
                "-fx-font-size: " + style.headerFontSize + "px;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(gaussian, " + style.headerColor + "40, 5, 0, 0, 0);"
            );
        }
        
        // Stile content
        if (dialogPane.lookup(".content") != null) {
            dialogPane.lookup(".content").setStyle(
                "-fx-text-fill: " + style.textColor + ";" +
                "-fx-font-size: " + style.fontSize + "px;" +
                "-fx-padding: 10 0 20 0;"
            );
        }
        
        if (dialogPane.lookup(".content > .label") != null) {
            dialogPane.lookup(".content > .label").setStyle(
                "-fx-text-fill: " + style.textColor + ";" +
                "-fx-font-size: " + style.fontSize + "px;"
            );
        }
        
        // Stile bottoni
        dialogPane.lookupAll(".button").forEach(node -> {
            String baseStyle = 
                "-fx-background-color: " + style.buttonColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: " + style.fontSize + "px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10px 20px;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);";
            
            String hoverStyle = 
                "-fx-background-color: " + style.buttonHoverColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: " + style.fontSize + "px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10px 20px;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;";
            
            node.setStyle(baseStyle);
            
            node.setOnMouseEntered(e -> node.setStyle(hoverStyle));
            node.setOnMouseExited(e -> node.setStyle(baseStyle));
        });
        
        // Stile area bottoni
        if (dialogPane.lookup(".button-bar") != null) {
            dialogPane.lookup(".button-bar").setStyle(
                "-fx-padding: 15 0 0 0;" +
                "-fx-spacing: 10;"
            );
        }
    }
    
    /**
     * Applica lo stile personalizzato a un TextInputDialog
     */
    private static void applicaStileInput(TextInputDialog dialog, DialogStyle style) {
        DialogPane dialogPane = dialog.getDialogPane();
        
        // Stile generale del dialog pane
        dialogPane.setStyle(
            "-fx-background-color: " + style.backgroundColor + ";" +
            "-fx-border-color: " + style.borderColor + ";" +
            "-fx-border-width: " + style.borderWidth + ";" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 15, 0, 0, 3);"
        );
        
        dialogPane.setPadding(new Insets(20));
        
        // Stile header
        if (dialogPane.lookup(".header-panel") != null) {
            dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: " + style.backgroundColor + ";" +
                "-fx-padding: 10 0 15 0;"
            );
        }
        
        if (dialogPane.lookup(".header-panel > .label") != null) {
            dialogPane.lookup(".header-panel > .label").setStyle(
                "-fx-text-fill: " + style.headerColor + ";" +
                "-fx-font-size: " + style.headerFontSize + "px;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(gaussian, " + style.headerColor + "40, 5, 0, 0, 0);"
            );
        }
        
        // Stile content e label
        if (dialogPane.lookup(".content") != null) {
            dialogPane.lookup(".content").setStyle(
                "-fx-padding: 10 0 20 0;"
            );
        }
        
        dialogPane.lookupAll(".label").forEach(node -> {
            node.setStyle(
                "-fx-text-fill: " + style.textColor + ";" +
                "-fx-font-size: " + style.fontSize + "px;"
            );
        });
        
        // Stile text field
        dialogPane.lookupAll(".text-field").forEach(node -> {
            node.setStyle(
                "-fx-background-color: #1a1a1a;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: " + style.fontSize + "px;" +
                "-fx-border-color: " + style.borderColor + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;" +
                "-fx-padding: 8px 12px;"
            );
        });
        
        // Stile bottoni
        dialogPane.lookupAll(".button").forEach(node -> {
            String baseStyle = 
                "-fx-background-color: " + style.buttonColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: " + style.fontSize + "px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10px 20px;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);";
            
            String hoverStyle = 
                "-fx-background-color: " + style.buttonHoverColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: " + style.fontSize + "px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10px 20px;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;";
            
            node.setStyle(baseStyle);
            
            node.setOnMouseEntered(e -> node.setStyle(hoverStyle));
            node.setOnMouseExited(e -> node.setStyle(baseStyle));
        });
        
        // Stile area bottoni
        if (dialogPane.lookup(".button-bar") != null) {
            dialogPane.lookup(".button-bar").setStyle(
                "-fx-padding: 15 0 0 0;" +
                "-fx-spacing: 10;"
            );
        }
    }
}