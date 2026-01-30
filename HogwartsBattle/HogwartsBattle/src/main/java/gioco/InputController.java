package gioco;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import carte.Carta;
import carte.Malvagio;
import gestoreEffetti.BersaglioEffetto;
import gestoreEffetti.Effetto;
import gestoreEffetti.TipoEffetto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * InputController - Gestisce tutte le interazioni utente con dialoghi modali
 * VERSIONE AGGIORNATA con selezione carte per scarto e nome giocatore
 */
public class InputController {

	private static InputController instance;

	public static InputController getInstance() {
		if (instance == null) {
			instance = new InputController();
		}
		return instance;
	}

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

		if (Platform.isFxApplicationThread()) {
			return scegliCartaSync(carte, titolo, messaggio);
		}

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
	 * NUOVO: Chiede al giocatore di scegliere una carta con contesto dettagliato
	 * 
	 * @param carte          Lista delle carte tra cui scegliere
	 * @param nomeGiocatore  Nome del giocatore che sta scegliendo
	 * @param tipoAzione     Tipo di azione (es. "Scarta", "Gioca", "Cerca")
	 * @param motivazione    Motivazione della scelta (es. "Effetto di
	 *                       Expelliarmus")
	 * @param cartaAttivante Nome della carta che ha attivato l'effetto (opzionale)
	 * @return La carta scelta
	 */
	public static Carta scegliCartaConContesto(List<Carta> carte, String nomeGiocatore, String tipoAzione,
			String motivazione, String cartaAttivante) {
		if (carte == null || carte.isEmpty()) {
			return null;
		}

		if (carte.size() == 1) {
			return carte.get(0);
		}

		String titolo = tipoAzione + " - " + nomeGiocatore;

		StringBuilder messaggio = new StringBuilder();
		messaggio.append("🎴 ").append(nomeGiocatore).append("\n");
		messaggio.append("━━━━━━━━━━━━━━━━━━━━━━\n\n");
		messaggio.append("Azione: ").append(tipoAzione).append("\n");

		if (cartaAttivante != null && !cartaAttivante.isEmpty()) {
			messaggio.append("⚡ Attivato da: ").append(cartaAttivante).append("\n");
		}

		if (motivazione != null && !motivazione.isEmpty()) {
			messaggio.append("📋 Motivazione: ").append(motivazione).append("\n");
		}

		messaggio.append("\nScegli una carta:");

		if (Platform.isFxApplicationThread()) {
			return scegliCartaSync(carte, titolo, messaggio.toString());
		}

		AtomicReference<Carta> risultato = new AtomicReference<>(null);
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			try {
				risultato.set(scegliCartaSync(carte, titolo, messaggio.toString()));
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
		dialog.setTitle(titolo);
		dialog.setResizable(false);
		
		// Layout principale
		VBox root = new VBox(20);
		root.setPadding(new Insets(25));
		root.setAlignment(Pos.CENTER);
		root.setStyle(
			"-fx-background-color: #2a2a2a;" +
			"-fx-border-color: #0066cc;" +
			"-fx-border-width: 3;" +
			"-fx-border-radius: 10;" +
			"-fx-background-radius: 10;" +
			"-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 5);"
		);
		
		// Header
		Label headerLabel = new Label(messaggio);
		headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		headerLabel.setTextFill(Color.web("#0066cc"));
		headerLabel.setWrapText(true);
		headerLabel.setMaxWidth(450);
		headerLabel.setAlignment(Pos.CENTER);
		
		// Separatore decorativo
		Separator separator = new Separator();
		separator.setPrefWidth(400);
		separator.setStyle("-fx-background-color: #0066cc;");

		// Box per le carte con scroll
		VBox carteBox = new VBox(10);
		carteBox.setAlignment(Pos.CENTER);
		carteBox.setPadding(new Insets(10, 0, 0, 0));

		for (Carta carta : carte) {
			Button btn = new Button(carta.getNome() + " (Costo: " + carta.getCosto() + ")");
			btn.setPrefWidth(350);
			btn.setPrefHeight(45);
			btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
			btn.setStyle(
				"-fx-background-color: #0066cc;" +
				"-fx-text-fill: white;" +
				"-fx-background-radius: 8;" +
				"-fx-cursor: hand;" +
				"-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
			);
			
			btn.setOnMouseEntered(e -> btn.setStyle(
				"-fx-background-color: #0052a3;" +
				"-fx-text-fill: white;" +
				"-fx-background-radius: 8;" +
				"-fx-cursor: hand;" +
				"-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
				"-fx-scale-x: 1.05;" +
				"-fx-scale-y: 1.05;"
			));
			
			btn.setOnMouseExited(e -> btn.setStyle(
				"-fx-background-color: #0066cc;" +
				"-fx-text-fill: white;" +
				"-fx-background-radius: 8;" +
				"-fx-cursor: hand;" +
				"-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
			));

			btn.setOnAction(e -> {
				risultato.set(carta);
				dialog.close();
			});

			carteBox.getChildren().add(btn);
		}
		
		// ScrollPane se ci sono molte carte
		if (carte.size() > 5) {
			ScrollPane scrollPane = new ScrollPane(carteBox);
			scrollPane.setMaxHeight(350);
			scrollPane.setFitToWidth(true);
			scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
			root.getChildren().addAll(headerLabel, separator, scrollPane);
		} else {
			root.getChildren().addAll(headerLabel, separator, carteBox);
		}

		Scene scene = new Scene(root);
		scene.setFill(Color.TRANSPARENT);
		dialog.setScene(scene);
		dialog.initStyle(StageStyle.TRANSPARENT);
		dialog.showAndWait();

		return risultato.get() != null ? risultato.get() : carte.get(0);
	}
	
	public static Malvagio scegliMalvagio(List<Malvagio> carte, String titolo, String messaggio) {
		if (carte == null || carte.isEmpty()) {
			return null;
		}

		if (carte.size() == 1) {
			return carte.get(0);
		}

		if (Platform.isFxApplicationThread()) {
			return scegliMalvagioSync(carte, titolo, messaggio);
		}

		AtomicReference<Malvagio> risultato = new AtomicReference<>(null);
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			try {
				risultato.set(scegliMalvagioSync(carte, titolo, messaggio));
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
	private static Malvagio scegliMalvagioSync(List<Malvagio> carte, String titolo, String messaggio) {
		AtomicReference<Malvagio> risultato = new AtomicReference<>(null);

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
			"-fx-border-color: #0066cc;" +
			"-fx-border-width: 3;" +
			"-fx-border-radius: 10;" +
			"-fx-background-radius: 10;" +
			"-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 5);"
		);
		
		// Header
		Label headerLabel = new Label(messaggio);
		headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		headerLabel.setTextFill(Color.web("#0066cc"));
		headerLabel.setWrapText(true);
		headerLabel.setMaxWidth(450);
		headerLabel.setAlignment(Pos.CENTER);
		
		// Separatore decorativo
		Separator separator = new Separator();
		separator.setPrefWidth(400);
		separator.setStyle("-fx-background-color: #0066cc;");

		// Box per i malvagi con scroll
		VBox carteBox = new VBox(10);
		carteBox.setAlignment(Pos.CENTER);
		carteBox.setPadding(new Insets(10, 0, 0, 0));

		for (Malvagio carta : carte) {
			Button btn = new Button(carta.getNome() + " (Costo: " + carta.getCosto() + ")");
			btn.setPrefWidth(350);
			btn.setPrefHeight(45);
			btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
			btn.setStyle(
				"-fx-background-color: #0066cc;" +
				"-fx-text-fill: white;" +
				"-fx-background-radius: 8;" +
				"-fx-cursor: hand;" +
				"-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
			);
			
			btn.setOnMouseEntered(e -> btn.setStyle(
				"-fx-background-color: #0052a3;" +
				"-fx-text-fill: white;" +
				"-fx-background-radius: 8;" +
				"-fx-cursor: hand;" +
				"-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
				"-fx-scale-x: 1.05;" +
				"-fx-scale-y: 1.05;"
			));
			
			btn.setOnMouseExited(e -> btn.setStyle(
				"-fx-background-color: #0066cc;" +
				"-fx-text-fill: white;" +
				"-fx-background-radius: 8;" +
				"-fx-cursor: hand;" +
				"-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
			));

			btn.setOnAction(e -> {
				risultato.set(carta);
				dialog.close();
			});

			carteBox.getChildren().add(btn);
		}
		
		// ScrollPane se ci sono molti malvagi
		if (carte.size() > 5) {
			ScrollPane scrollPane = new ScrollPane(carteBox);
			scrollPane.setMaxHeight(350);
			scrollPane.setFitToWidth(true);
			scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
			root.getChildren().addAll(headerLabel, separator, scrollPane);
		} else {
			root.getChildren().addAll(headerLabel, separator, carteBox);
		}

		Scene scene = new Scene(root);
		scene.setFill(Color.TRANSPARENT);
		dialog.setScene(scene);
		dialog.initStyle(StageStyle.TRANSPARENT);
		dialog.showAndWait();

		return risultato.get() != null ? risultato.get() : carte.get(0);
	}


	/**
	 * Chiede al giocatore di scegliere un effetto tra piÃ¹ opzioni
	 */
	public static int scegliEffetto(List<Effetto> effetti, String titolo, String messaggio) {
		if (effetti == null || effetti.isEmpty()) {
			return -1;
		}

		if (effetti.size() == 1) {
			return 0;
		}

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
	    dialog.setTitle(titolo);
	    dialog.setResizable(false);
	    
	    // Layout principale
	    VBox root = new VBox(20);
	    root.setPadding(new Insets(25));
	    root.setAlignment(Pos.CENTER);
	    root.setStyle(
	        "-fx-background-color: #2a2a2a;" +
	        "-fx-border-color: #0066cc;" +
	        "-fx-border-width: 3;" +
	        "-fx-border-radius: 10;" +
	        "-fx-background-radius: 10;" +
	        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 5);"
	    );
	    
	    // Header
	    Label headerLabel = new Label(messaggio);
	    headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
	    headerLabel.setTextFill(Color.web("#0066cc"));
	    headerLabel.setWrapText(true);
	    headerLabel.setMaxWidth(450);
	    headerLabel.setAlignment(Pos.CENTER);
	    
	    // Separatore decorativo
	    Separator separator = new Separator();
	    separator.setPrefWidth(400);
	    separator.setStyle("-fx-background-color: #0066cc;");

	    // Box per gli elementi
	    VBox itemsBox = new VBox(10);
	    itemsBox.setAlignment(Pos.CENTER);
	    itemsBox.setPadding(new Insets(10, 0, 0, 0));

	    // Loop per creare bottoni
	    for (int i = 0; i < effetti.size(); i++) {
			final int indice = i;
			Effetto eff = effetti.get(i);

			
			String testo = (i + 1) + ". " + getDescrizioneEffetto(eff);

			Button btn = new Button(testo);
	        btn.setPrefWidth(350);
	        btn.setPrefHeight(45);
	        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
	        btn.setStyle(
	            "-fx-background-color: #0066cc;" +
	            "-fx-text-fill: white;" +
	            "-fx-background-radius: 8;" +
	            "-fx-cursor: hand;" +
	            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
	        );
	        
	        btn.setOnMouseEntered(e -> btn.setStyle(
	            "-fx-background-color: #0052a3;" +
	            "-fx-text-fill: white;" +
	            "-fx-background-radius: 8;" +
	            "-fx-cursor: hand;" +
	            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
	            "-fx-scale-x: 1.05;" +
	            "-fx-scale-y: 1.05;"
	        ));
	        
	        btn.setOnMouseExited(e -> btn.setStyle(
	            "-fx-background-color: #0066cc;" +
	            "-fx-text-fill: white;" +
	            "-fx-background-radius: 8;" +
	            "-fx-cursor: hand;" +
	            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
	        ));

	        btn.setOnAction(e -> {
	            risultato.set(indice);
	            dialog.close();
	        });

	        itemsBox.getChildren().add(btn);
	    }
	    
	    // ScrollPane se > 5 elementi
	    if (effetti.size() > 5) {
	        ScrollPane scrollPane = new ScrollPane(itemsBox);
	        scrollPane.setMaxHeight(350);
	        scrollPane.setFitToWidth(true);
	        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
	        root.getChildren().addAll(headerLabel, separator, scrollPane);
	    } else {
	        root.getChildren().addAll(headerLabel, separator, itemsBox);
	    }

	    Scene scene = new Scene(root);
	    scene.setFill(Color.TRANSPARENT);
	    dialog.setScene(scene);
	    dialog.initStyle(StageStyle.TRANSPARENT);
	    dialog.showAndWait();

	    return risultato.get();
	}

	/**
	 * â­ NUOVO METODO: Genera descrizione leggibile dell'effetto
	 */
	private static String getDescrizioneEffetto(Effetto eff) {
		StringBuilder desc = new StringBuilder();

		// Tipo effetto tradotto
		String tipoTradotto = getTipoEffettoTradotto(eff.getType());
		desc.append(tipoTradotto);

		// QuantitÃ  (se presente)
		if (eff.getQta() != null && eff.getQta() > 0) {
			desc.append(" (x").append(eff.getQta()).append(")");
		}

		// Target
		String target = getTargetTradotto(eff.getTarget());
		if (target != null && !target.isEmpty()) {
			desc.append(" â†’ ").append(target);
		}

		return desc.toString();
	}

	/**
	 * â­ NUOVO METODO: Traduce tipo effetto in italiano
	 */
	private static String getTipoEffettoTradotto(TipoEffetto tipo) {
		switch (tipo) {
		case GUADAGNARE_ATTACCO:
			return "âš”ï¸ Guadagna Attacco";

		case GUADAGNARE_INFLUENZA:
			return "ðŸª™ Guadagna Influenza";

		case GUADAGNARE_VITA:
			return "â¤ï¸ Guadagna Vita";

		case PERDERE_VITA:
			return "ðŸ’” Perde Vita";

		case PESCARE_CARTA:
			return "ðŸƒ Pesca Carte";

		case SCARTARE_CARTA:
			return "ðŸ—‘ï¸ Scarta Carte";

		case SCARTA_INCANTESIMO:
			return "ðŸ—‘ï¸ Scarta Incantesimi";

		case SCARTA_OGGETTO:
			return "ðŸ—‘ï¸ Scarta Oggetti";

		case SCARTA_ALLEATO:
			return "ðŸ—‘ï¸ Scarta Alleati";

		default:
			return tipo.toString().replace("_", " ");
		}
	}

	/**
	 * â­ NUOVO METODO: Traduce target in italiano
	 */
	private static String getTargetTradotto(BersaglioEffetto target) {
		if (target == null)
			return "";

		switch (target) {
		case EROE_ATTIVO:
			return "Te stesso";

		case TUTTI_GLI_EROI:
			return "Tutti gli eroi";

		case EROI_NON_ATTIVI:
			return "Altri eroi";

		case TUTTI_I_MALVAGI:
			return "Tutti i malvagi";

		case LUOGO:
			return "Luogo attuale";

		default:
			return target.toString().replace("_", " ");
		}
	}

	/**
	 * Chiede al giocatore di scegliere un giocatore tra piÃ¹ opzioni
	 */
	public static int scegliGiocatore(List<Giocatore> giocatori, String titolo, String messaggio) {
		if (giocatori == null || giocatori.isEmpty()) {
			return -1;
		}

		if (giocatori.size() == 1) {
			return 0;
		}

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
	    dialog.setTitle(titolo);
	    dialog.setResizable(false);
	    
	    // Layout principale
	    VBox root = new VBox(20);
	    root.setPadding(new Insets(25));
	    root.setAlignment(Pos.CENTER);
	    root.setStyle(
	        "-fx-background-color: #2a2a2a;" +
	        "-fx-border-color: #0066cc;" +
	        "-fx-border-width: 3;" +
	        "-fx-border-radius: 10;" +
	        "-fx-background-radius: 10;" +
	        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 5);"
	    );
	    
	    // Header
	    Label headerLabel = new Label(messaggio);
	    headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
	    headerLabel.setTextFill(Color.web("#0066cc"));
	    headerLabel.setWrapText(true);
	    headerLabel.setMaxWidth(450);
	    headerLabel.setAlignment(Pos.CENTER);
	    
	    // Separatore decorativo
	    Separator separator = new Separator();
	    separator.setPrefWidth(400);
	    separator.setStyle("-fx-background-color: #0066cc;");

	    // Box per gli elementi
	    VBox itemsBox = new VBox(10);
	    itemsBox.setAlignment(Pos.CENTER);
	    itemsBox.setPadding(new Insets(10, 0, 0, 0));

	    // Loop per creare bottoni
	    for (int i = 0; i < giocatori.size(); i++) {
			final int indice = i;
			Giocatore g = giocatori.get(i);

			
			String testo = g.getEroe().getNome() + " ( " + g.getSalute() + "/" + g.getSaluteMax() + ")";

			Button btn = new Button(testo);
	        btn.setPrefWidth(350);
	        btn.setPrefHeight(45);
	        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
	        btn.setStyle(
	            "-fx-background-color: #0066cc;" +
	            "-fx-text-fill: white;" +
	            "-fx-background-radius: 8;" +
	            "-fx-cursor: hand;" +
	            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
	        );
	        
	        btn.setOnMouseEntered(e -> btn.setStyle(
	            "-fx-background-color: #0052a3;" +
	            "-fx-text-fill: white;" +
	            "-fx-background-radius: 8;" +
	            "-fx-cursor: hand;" +
	            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);" +
	            "-fx-scale-x: 1.05;" +
	            "-fx-scale-y: 1.05;"
	        ));
	        
	        btn.setOnMouseExited(e -> btn.setStyle(
	            "-fx-background-color: #0066cc;" +
	            "-fx-text-fill: white;" +
	            "-fx-background-radius: 8;" +
	            "-fx-cursor: hand;" +
	            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
	        ));

	        btn.setOnAction(e -> {
	            risultato.set(indice);
	            dialog.close();
	        });

	        itemsBox.getChildren().add(btn);
	    }
	    
	    root.getChildren().addAll(headerLabel, separator, itemsBox);

	    Scene scene = new Scene(root);
	    scene.setFill(Color.TRANSPARENT);
	    dialog.setScene(scene);
	    dialog.initStyle(StageStyle.TRANSPARENT);
	    dialog.showAndWait();

	    return risultato.get() >= 0 ? risultato.get() : 0;
	}

	/**
	 * Mostra un messaggio informativo
	 */
	public static void mostraMessaggio(String titolo, String messaggio) {
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
	    dialog.setTitle(titolo);
	    dialog.setResizable(false);
	    
	    // Layout principale
	    VBox root = new VBox(20);
	    root.setPadding(new Insets(25));
	    root.setAlignment(Pos.CENTER);
	    root.setStyle(
	        "-fx-background-color: #2a2a2a;" +
	        "-fx-border-color: #0066cc;" +
	        "-fx-border-width: 3;" +
	        "-fx-border-radius: 10;" +
	        "-fx-background-radius: 10;" +
	        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 5);"
	    );
	    
	    // Header
	    Label headerLabel = new Label(messaggio);
	    headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
	    headerLabel.setTextFill(Color.web("#0066cc"));
	    headerLabel.setWrapText(true);
	    headerLabel.setMaxWidth(450);
	    headerLabel.setAlignment(Pos.CENTER);
	    
	    // Separatore decorativo
	    Separator separator = new Separator();
	    separator.setPrefWidth(400);
	    separator.setStyle("-fx-background-color: #0066cc;");

	    // Box per gli elementi
	    VBox itemsBox = new VBox(10);
	    itemsBox.setAlignment(Pos.CENTER);
	    itemsBox.setPadding(new Insets(10, 0, 0, 0));
	    
		Button btn = new Button("OK");
		btn.setPrefWidth(350);
        btn.setPrefHeight(45);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btn.setStyle(
            "-fx-background-color: #0066cc;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"
        );

        root.getChildren().addAll(headerLabel, separator, itemsBox);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.showAndWait();
	}

	// ============================================
	// â­ NUOVO: Selezione carte per scarto
	// ============================================

	/**
	 * Mostra finestra di selezione per scartare carte
	 * 
	 * @param giocatore  Il giocatore che deve scartare
	 * @param tipoScarto Tipo di carte da scartare
	 * @param quantita   Numero di carte da scartare
	 * @param callback   Callback con le carte selezionate
	 */
	public void mostraSelezioneCartePerScartare(Giocatore giocatore, TipoEffetto tipoScarto, int quantita,
			Consumer<List<Carta>> callback) {
		// Filtra carte in base al tipo
		List<Carta> carteDisponibili = filtraCartePerTipo(giocatore.getMano(), tipoScarto);

		if (carteDisponibili.isEmpty()) {
			System.out.println("âš ï¸ " + giocatore.getEroe().getNome() + " non ha carte di tipo " + tipoScarto);
			callback.accept(new ArrayList<>());
			return;
		}

		// Crea titoli con nome giocatore
		String nomeGiocatore = giocatore.getEroe().getNome();
		String tipoDescrizione = getTipoCartaDescrizione(tipoScarto);

		String titolo = "Scarta Carte - " + nomeGiocatore;
		String header = nomeGiocatore + ", seleziona " + quantita + " " + tipoDescrizione + " da scartare";

		// Mostra dialog
		Platform.runLater(() -> {
			mostraDialogSelezioneScarto(titolo, header, carteDisponibili, quantita, callback);
		});
	}

	/**
	 * Filtra carte per tipo
	 */
	private List<Carta> filtraCartePerTipo(List<Carta> mano, TipoEffetto tipo) {
		switch (tipo) {
		case SCARTA_INCANTESIMO:
			return mano.stream().filter(c -> c.getClasse().equalsIgnoreCase("Incantesimo"))
					.collect(Collectors.toList());

		case SCARTA_OGGETTO:
			return mano.stream().filter(c -> c.getClasse().equalsIgnoreCase("Oggetto")).collect(Collectors.toList());

		case SCARTA_ALLEATO:
			return mano.stream().filter(c -> c.getClasse().equalsIgnoreCase("Alleato")).collect(Collectors.toList());

		case SCARTARE_CARTA:
		default:
			return new ArrayList<>(mano);
		}
	}

	/**
	 * Ottiene descrizione leggibile del tipo carta
	 */
	private String getTipoCartaDescrizione(TipoEffetto tipo) {
		switch (tipo) {
		case SCARTA_INCANTESIMO:
			return "incantesimo/i";
		case SCARTA_OGGETTO:
			return "oggetto/i";
		case SCARTA_ALLEATO:
			return "alleato/i";
		case SCARTARE_CARTA:
		default:
			return "carta/e";
		}
	}

	/**
	 * Mostra dialog di selezione carte con checkbox
	 */
	private void mostraDialogSelezioneScarto(String titolo, String header, List<Carta> carteDisponibili, int quantita,
			Consumer<List<Carta>> callback) {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle(titolo);

		VBox root = new VBox(15);
		root.setPadding(new Insets(20));
		root.setStyle("-fx-background-color: #2a2a4a;");

		// Header
		Text headerText = new Text(header);
		headerText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		headerText.setFill(Color.WHITE);
		headerText.setWrappingWidth(450);

		// Lista carte con checkbox
		VBox carteBox = new VBox(10);
		carteBox.setPadding(new Insets(10));
		List<CheckBox> checkboxes = new ArrayList<>();

		for (Carta carta : carteDisponibili) {
			CheckBox cb = new CheckBox(carta.getNome() + " (" + carta.getClasse() + ")");
			cb.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
			cb.setUserData(carta);
			checkboxes.add(cb);
			carteBox.getChildren().add(cb);
		}

		// ScrollPane per lista
		ScrollPane scroll = new ScrollPane(carteBox);
		scroll.setPrefHeight(300);
		scroll.setFitToWidth(true);
		scroll.setStyle("-fx-background: #1a1a3a; -fx-background-color: #1a1a3a;");

		// Info selezione
		Text infoText = new Text("Seleziona esattamente " + quantita + " carta/e");
		infoText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
		infoText.setFill(Color.LIGHTGRAY);

		// Bottoni
		HBox buttons = new HBox(15);
		buttons.setAlignment(Pos.CENTER);

		Button btnOk = new Button("âœ“ CONFERMA");
		btnOk.setPrefWidth(150);
		btnOk.setPrefHeight(40);
		btnOk.setStyle("-fx-background-color: #00AA66; " + "-fx-text-fill: white; " + "-fx-font-size: 14px; "
				+ "-fx-font-weight: bold;");

		btnOk.setOnAction(e -> {
			List<Carta> selezionate = checkboxes.stream().filter(CheckBox::isSelected)
					.map(cb -> (Carta) cb.getUserData()).collect(Collectors.toList());

			if (selezionate.size() == quantita) {
				callback.accept(selezionate);
				stage.close();
			} else {
				// Mostra errore
				infoText.setText("âŒ Devi selezionare esattamente " + quantita + " carta/e! (Selezionate: "
						+ selezionate.size() + ")");
				infoText.setFill(Color.RED);
			}
		});

		Button btnCancel = new Button("âœ— ANNULLA");
		btnCancel.setPrefWidth(150);
		btnCancel.setPrefHeight(40);
		btnCancel.setStyle("-fx-background-color: #AA0000; " + "-fx-text-fill: white; " + "-fx-font-size: 14px; "
				+ "-fx-font-weight: bold;");
		btnCancel.setOnAction(e -> {
			callback.accept(new ArrayList<>());
			stage.close();
		});

		buttons.getChildren().addAll(btnOk, btnCancel);

		root.getChildren().addAll(headerText, scroll, infoText, buttons);

		Scene scene = new Scene(root, 500, 500);
		stage.setScene(scene);
		stage.showAndWait();
	}

	// ============================================
	// METODI CON CONTESTO DETTAGLIATO (NUOVI)
	// ============================================

	/**
	 * NUOVO: Scelta effetto con contesto completo
	 * 
	 * @param effetti             Lista effetti tra cui scegliere
	 * @param nomeGiocatore       Nome del giocatore
	 * @param cartaAttivante      Carta che ha attivato l'effetto
	 * @param descrizioneContesto Descrizione del contesto
	 * @return Indice effetto scelto
	 */
	public static int scegliEffettoConContesto(List<Effetto> effetti, String nomeGiocatore, String cartaAttivante,
			String descrizioneContesto) {
		if (effetti == null || effetti.isEmpty()) {
			return -1;
		}

		if (effetti.size() == 1) {
			return 0;
		}

		String titolo = "Scelta Effetto - " + nomeGiocatore;

		StringBuilder messaggio = new StringBuilder();
		messaggio.append("💫 ").append(nomeGiocatore).append("\n");
		messaggio.append("━━━━━━━━━━━━━━━━━━━━━━\n\n");

		if (cartaAttivante != null && !cartaAttivante.isEmpty()) {
			messaggio.append("🎴 Carta: ").append(cartaAttivante).append("\n");
		}

		if (descrizioneContesto != null && !descrizioneContesto.isEmpty()) {
			messaggio.append("📋 ").append(descrizioneContesto).append("\n");
		}

		messaggio.append("\nScegli quale effetto applicare:");

		if (Platform.isFxApplicationThread()) {
			return scegliEffettoSync(effetti, titolo, messaggio.toString());
		}

		AtomicReference<Integer> risultato = new AtomicReference<>(-1);
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			try {
				risultato.set(scegliEffettoSync(effetti, titolo, messaggio.toString()));
			} catch (Exception e) {
				e.printStackTrace();
				risultato.set(0);
			} finally {
				latch.countDown();
			}
		});

		try {
			latch.await();
			return risultato.get() >= 0 ? risultato.get() : 0;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * NUOVO: Scelta giocatore con contesto completo
	 * 
	 * @param giocatori           Lista giocatori
	 * @param nomeGiocatoreAttivo Giocatore che sta facendo la scelta
	 * @param tipoAzione          Tipo di azione (es. "Guarisci", "Assegna Danno")
	 * @param cartaAttivante      Carta che ha attivato l'effetto
	 * @param descrizioneEffetto  Descrizione dell'effetto
	 * @return Indice giocatore scelto
	 */
	public static int scegliGiocatoreConContesto(List<Giocatore> giocatori, String nomeGiocatoreAttivo,
			String tipoAzione, String cartaAttivante, String descrizioneEffetto) {
		if (giocatori == null || giocatori.isEmpty()) {
			return -1;
		}

		if (giocatori.size() == 1) {
			return 0;
		}

		String titolo = tipoAzione + " - " + nomeGiocatoreAttivo;

		StringBuilder messaggio = new StringBuilder();
		messaggio.append(nomeGiocatoreAttivo).append("\n");
		messaggio.append("━━━━━━━━━━━━━━━━━━━━━\n\n");
		messaggio.append("Azione: ").append(tipoAzione).append("\n");

		if (cartaAttivante != null && !cartaAttivante.isEmpty()) {
			messaggio.append("Carta: ").append(cartaAttivante).append("\n");
		}

		if (descrizioneEffetto != null && !descrizioneEffetto.isEmpty()) {
			messaggio.append("Effetto: ").append(descrizioneEffetto).append("\n");
		}

		messaggio.append("\nScegli un giocatore:");

		if (Platform.isFxApplicationThread()) {
			return scegliGiocatoreSync(giocatori, titolo, messaggio.toString());
		}

		AtomicReference<Integer> risultato = new AtomicReference<>(-1);
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			try {
				risultato.set(scegliGiocatoreSync(giocatori, titolo, messaggio.toString()));
			} catch (Exception e) {
				e.printStackTrace();
				risultato.set(0);
			} finally {
				latch.countDown();
			}
		});

		try {
			latch.await();
			return risultato.get() >= 0 ? risultato.get() : 0;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * NUOVO: Mostra messaggio con contesto dettagliato
	 * 
	 * @param nomeGiocatore  Nome del giocatore coinvolto
	 * @param tipoAzione     Tipo di azione/evento
	 * @param cartaAttivante Carta che ha causato l'evento
	 * @param messaggio      Messaggio principale
	 * @param dettagli       Dettagli aggiuntivi (opzionale)
	 */
	public static void mostraMessaggioConContesto(String nomeGiocatore, String tipoAzione, String cartaAttivante,
			String messaggio, String dettagli) {
		String titolo = tipoAzione + " - " + nomeGiocatore;

		StringBuilder messaggioCompleto = new StringBuilder();
		messaggioCompleto.append("👤 ").append(nomeGiocatore).append("\n");
		messaggioCompleto.append("━━━━━━━━━━━━━━━━━━━━━━\n\n");

		if (cartaAttivante != null && !cartaAttivante.isEmpty()) {
			messaggioCompleto.append("🎴 Carta: ").append(cartaAttivante).append("\n\n");
		}

		messaggioCompleto.append(messaggio);

		if (dettagli != null && !dettagli.isEmpty()) {
			messaggioCompleto.append("\n\n").append("ℹ️ ").append(dettagli);
		}

		mostraMessaggio(titolo, messaggioCompleto.toString());
	}

	/**
	 * NUOVO: Mostra messaggio trigger attivato
	 * 
	 * @param nomeTrigger        Nome del trigger
	 * @param nomeGiocatore      Giocatore che ha attivato il trigger
	 * @param cartaAttivante     Carta che ha attivato il trigger
	 * @param descrizioneEffetto Descrizione dell'effetto del trigger
	 */
	public static void mostraMessaggioTrigger(String nomeTrigger, String nomeGiocatore, String cartaAttivante,
			String descrizioneEffetto) {
		String titolo = "⚡ Trigger Attivato!";

		StringBuilder messaggio = new StringBuilder();
		messaggio.append("⚡ TRIGGER ATTIVATO\n");
		messaggio.append("━━━━━━━━━━━━━━━━━━━━━━\n\n");
		messaggio.append("Trigger: ").append(nomeTrigger).append("\n");
		messaggio.append("Giocatore: ").append(nomeGiocatore).append("\n");

		if (cartaAttivante != null && !cartaAttivante.isEmpty()) {
			messaggio.append("Attivato da: ").append(cartaAttivante).append("\n");
		}

		messaggio.append("\n💫 Effetto:\n").append(descrizioneEffetto);

		mostraMessaggio(titolo, messaggio.toString());
	}
}