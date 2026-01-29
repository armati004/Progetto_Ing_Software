package gioco;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
			btn.setStyle("-fx-background-color: #0066cc; " + "-fx-text-fill: white; " + "-fx-font-size: 12px; "
					+ "-fx-font-weight: bold;");

			btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #0052a3; " + "-fx-text-fill: white; "
					+ "-fx-font-size: 12px; " + "-fx-font-weight: bold;"));

			btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #0066cc; " + "-fx-text-fill: white; "
					+ "-fx-font-size: 12px; " + "-fx-font-weight: bold;"));

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

		for (Malvagio carta : carte) {
			Button btn = new Button(carta.getNome() + " (Costo: " + carta.getCosto() + ")");
			btn.setPrefWidth(300);
			btn.setPrefHeight(40);
			btn.setStyle("-fx-background-color: #0066cc; " + "-fx-text-fill: white; " + "-fx-font-size: 12px; "
					+ "-fx-font-weight: bold;");

			btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #0052a3; " + "-fx-text-fill: white; "
					+ "-fx-font-size: 12px; " + "-fx-font-weight: bold;"));

			btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #0066cc; " + "-fx-text-fill: white; "
					+ "-fx-font-size: 12px; " + "-fx-font-weight: bold;"));

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

			// ⭐ NUOVO: Descrizione dettagliata
			String testo = (i + 1) + ". " + getDescrizioneEffetto(eff);

			Button btn = new Button(testo);
			btn.setPrefWidth(400); // ⭐ Più largo per testo completo
			btn.setPrefHeight(60); // ⭐ Più alto per 2 righe
			btn.setWrapText(true); // ⭐ Permetti wrap
			btn.setStyle("-fx-background-color: #9966FF; " + "-fx-text-fill: white; " + "-fx-font-size: 13px; "
					+ "-fx-font-weight: bold; " + "-fx-padding: 10px;");

			btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #7744DD; " + "-fx-text-fill: white; "
					+ "-fx-font-size: 13px; " + "-fx-font-weight: bold; " + "-fx-padding: 10px;"));

			btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #9966FF; " + "-fx-text-fill: white; "
					+ "-fx-font-size: 13px; " + "-fx-font-weight: bold; " + "-fx-padding: 10px;"));

			btn.setOnAction(e -> {
				risultato.set(indice);
				dialog.close();
			});

			effettiBox.getChildren().add(btn);
		}

		layout.getChildren().addAll(titleLabel, effettiBox);

		Scene scene = new Scene(layout, 500, 300 + (effetti.size() * 70));
		scene.setFill(Color.TRANSPARENT);
		dialog.setScene(scene);

		dialog.showAndWait();

		return risultato.get();
	}

	/**
	 * ⭐ NUOVO METODO: Genera descrizione leggibile dell'effetto
	 */
	private static String getDescrizioneEffetto(Effetto eff) {
		StringBuilder desc = new StringBuilder();

		// Tipo effetto tradotto
		String tipoTradotto = getTipoEffettoTradotto(eff.getType());
		desc.append(tipoTradotto);

		// Quantità (se presente)
		if (eff.getQta() != null && eff.getQta() > 0) {
			desc.append(" (x").append(eff.getQta()).append(")");
		}

		// Target
		String target = getTargetTradotto(eff.getTarget());
		if (target != null && !target.isEmpty()) {
			desc.append(" → ").append(target);
		}

		return desc.toString();
	}

	/**
	 * ⭐ NUOVO METODO: Traduce tipo effetto in italiano
	 */
	private static String getTipoEffettoTradotto(TipoEffetto tipo) {
		switch (tipo) {
		case GUADAGNARE_ATTACCO:
			return "⚔️ Guadagna Attacco";

		case GUADAGNARE_INFLUENZA:
			return "🪙 Guadagna Influenza";

		case GUADAGNARE_VITA:
			return "❤️ Guadagna Vita";

		case PERDERE_VITA:
			return "💔 Perde Vita";

		case PESCARE_CARTA:
			return "🃏 Pesca Carte";

		case SCARTARE_CARTA:
			return "🗑️ Scarta Carte";

		case SCARTA_INCANTESIMO:
			return "🗑️ Scarta Incantesimi";

		case SCARTA_OGGETTO:
			return "🗑️ Scarta Oggetti";

		case SCARTA_ALLEATO:
			return "🗑️ Scarta Alleati";

		default:
			return tipo.toString().replace("_", " ");
		}
	}

	/**
	 * ⭐ NUOVO METODO: Traduce target in italiano
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
	 * Chiede al giocatore di scegliere un giocatore tra più opzioni
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
			btn.setStyle("-fx-background-color: #00AA66; " + "-fx-text-fill: white; " + "-fx-font-size: 12px; "
					+ "-fx-font-weight: bold;");

			btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #008844; " + "-fx-text-fill: white; "
					+ "-fx-font-size: 12px; " + "-fx-font-weight: bold;"));

			btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #00AA66; " + "-fx-text-fill: white; "
					+ "-fx-font-size: 12px; " + "-fx-font-weight: bold;"));

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
		okBtn.setStyle("-fx-background-color: #0066cc; " + "-fx-text-fill: white; " + "-fx-font-size: 12px; "
				+ "-fx-font-weight: bold;");
		okBtn.setOnAction(e -> dialog.close());

		layout.getChildren().addAll(messageLabel, okBtn);

		Scene scene = new Scene(layout);
		dialog.setScene(scene);
		dialog.showAndWait();
	}

	// ============================================
	// ⭐ NUOVO: Selezione carte per scarto
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
			System.out.println("⚠️ " + giocatore.getEroe().getNome() + " non ha carte di tipo " + tipoScarto);
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

		Button btnOk = new Button("✓ CONFERMA");
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
				infoText.setText("❌ Devi selezionare esattamente " + quantita + " carta/e! (Selezionate: "
						+ selezionate.size() + ")");
				infoText.setFill(Color.RED);
			}
		});

		Button btnCancel = new Button("✗ ANNULLA");
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
}