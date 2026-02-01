package grafica.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import carte.Carta;
import grafica.controllers.CardClickHandler;
import grafica.GameController;
import grafica.utils.ImageLoader;

/**
 * CardButton - Componente che rappresenta una carta cliccabile con immagine
 */
public class CardButton extends VBox {
    
    private Carta carta;
    private int indice;
    private Button cardBtn;
    private CardClickHandler clickHandler;
    
    public CardButton(Carta carta, int indice, int totaleCarte) {
        this.carta = carta;
        this.indice = indice;
        this.clickHandler = new CardClickHandler(GameController.getInstance());
        
        // Impostazioni base VBox
        setPrefWidth(120);
        setPrefHeight(200);
        setSpacing(3);
        setPadding(new Insets(5));
        setAlignment(Pos.TOP_CENTER);
        
        // Colore in base al tipo di carta
        String coloreBordo = getColoreBordoPerTipo();
        setStyle("-fx-border-color: " + coloreBordo + "; -fx-border-width: 2; " +
                "-fx-background-color: #1a1a1a; -fx-border-radius: 5;");
        
        // Carica immagine della carta
        if (carta.getPathImmagine() != null && !carta.getPathImmagine().isEmpty()) {
            Image cardImage = ImageLoader.caricaImmagine(carta.getPathImmagine());
            ImageView imageView = new ImageView(cardImage);
            imageView.setFitWidth(110);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            getChildren().add(imageView);
        }
        
        // Nome carta
        Label nameLabel = new Label(carta.getNome());
        nameLabel.setFont(new Font("Arial Bold", 9));
        nameLabel.setTextFill(Color.web("#FFFFFF"));
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(110);
        nameLabel.setMaxHeight(30);
        
        // Costo
        Label costLabel = new Label("💰 " + carta.getCosto());
        costLabel.setFont(new Font("Arial", 9));
        costLabel.setTextFill(Color.web("#FFD700"));
        
        // Classe/Tipo
        Label typeLabel = new Label(carta.getClasse());
        typeLabel.setFont(new Font("Arial", 8));
        typeLabel.setTextFill(Color.web("#9966FF"));
        
        // Bottone per giocare
        this.cardBtn = new Button("Gioca");
        cardBtn.setPrefWidth(110);
        cardBtn.setPrefHeight(25);
        cardBtn.setStyle(
            "-fx-background-color: #0066cc; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 9px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 3px; " +
            "-fx-border-radius: 3; " +
            "-fx-cursor: hand;"
        );
        
        cardBtn.setOnAction(e -> handleCardClick());
        
        cardBtn.setOnMouseEntered(e -> {
            cardBtn.setStyle(
                "-fx-background-color: #0052a3; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 9px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 3px; " +
                "-fx-border-radius: 3; " +
                "-fx-cursor: hand;"
            );
            setScaleX(1.05);
            setScaleY(1.05);
        });
        
        cardBtn.setOnMouseExited(e -> {
            cardBtn.setStyle(
                "-fx-background-color: #0066cc; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 9px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 3px; " +
                "-fx-border-radius: 3; " +
                "-fx-cursor: hand;"
            );
            setScaleX(1.0);
            setScaleY(1.0);
        });
        
        Tooltip tooltip = new Tooltip(carta.getDescrizione());
        tooltip.setWrapText(true);
        tooltip.setPrefWidth(250);
        Tooltip.install(this, tooltip);
        
        getChildren().addAll(nameLabel, typeLabel, costLabel, cardBtn);
    }
    
    private void handleCardClick() {
        System.out.println("CardButton clicked: " + carta.getNome() + " (indice: " + indice + ")");
        clickHandler.onCartaManoClicked(carta, indice);
    }
    
    private String getColoreBordoPerTipo() {
        String classe = carta.getClasse().toLowerCase();
        
        if (classe.contains("alleato")) {
            return "#00FF00";
        } else if (classe.contains("incantesimo")) {
            return "#0066FF";
        } else if (classe.contains("oggetto")) {
            return "#FFAA00";
        } else if (classe.contains("competenza")) {
            return "#FF00FF";
        } else {
            return "#CCCCCC";
        }
    }
    
    public Carta getCarta() {
        return carta;
    }
    
    public int getIndice() {
        return indice;
    }
}