package grafica;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import gioco.*;
import data.*;
import gestoreEffetti.TipoTrigger;
import carte.*;
import grafica.screens.*;
import grafica.panels.MessagePanel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GameController - Classe principale con sistema salvataggio/caricamento
 * integrato
 */
public class GameController extends GameApplication {

	private StatoDiGioco stato;
	private GameBoardUI gameUI;
	private TurnManager turnManager;

	// Parametri selezione
	private int numeroGiocatori;
	private int annoSelezionato = 1;
	private List<Giocatore> giocatoriSelezionati;

	// Sistema salvataggio
	private boolean caricamentoDaSalvataggio = false;
	private GameSaveData saveDataCaricato = null;

	private List<String> carteAcquisiteTemp = null;
	private List<String> carteNegozioTemp = null;  // ✅ NUOVO

	private static GameController instance;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	protected void initSettings(GameSettings settings) {
		settings.setWidth(1920);
		settings.setHeight(1080);
		settings.setTitle("Harry Potter: Hogwarts Battle - Deck Building Game");
		settings.setVersion("1.0");
		settings.setMainMenuEnabled(false);
		settings.setIntroEnabled(false);
		settings.setProfilingEnabled(false);
		settings.setCloseConfirmation(false);
		settings.setFullScreenAllowed(true);
		settings.setFullScreenFromStart(false);
		settings.setManualResizeEnabled(true);
		settings.setPreserveResizeRatio(false);
		settings.setScaleAffectedOnResize(true);
	}

	@Override
	protected void initGame() {
		instance = this;

		try {
			// Inizializza le factory
			System.out.println("\n🔧 Inizializzazione Factory...");
			CardFactory.inizializza();
			HeroFactory.inizializza();
			DiceFactory.inizializza();
			LocationFactory.inizializza();
			VillainFactory.inizializza();
			ProficiencyFactory.inizializza();
			HorcruxFactory.inizializza();

			System.out.println("✅ Tutte le factory inizializzate");

			// Mostra menu principale
			mostraMenuPrincipale();

		} catch (Exception e) {
			System.err.println("❌ Errore durante l'inizializzazione:");
			e.printStackTrace();
		}
	}

	// ========================================
	// MENU E NAVIGAZIONE
	// ========================================

	/**
	 * STEP 0: Mostra menu principale
	 */
	private void mostraMenuPrincipale() {
		System.out.println("\n=== MENU PRINCIPALE ===");

		MainMenuScreen screen = new MainMenuScreen(scelta -> {
			FXGL.getGameScene().clearUINodes();

			switch (scelta) {
			case "new_game":
				// Nuova partita
				annoSelezionato = 1;
				caricamentoDaSalvataggio = false;
				mostraSchermataSelezioneNumeroGiocatori();
				break;

			case "continue":
				// Continua autosave
				GameSaveData autosave = SaveManager.caricaAutosave();
				if (autosave != null) {
					caricaPartitaDaSalvataggio(autosave);
				} else {
					System.err.println("❌ Nessun autosave trovato!");
					mostraMenuPrincipale();
				}
				break;

			case "load_game":
				// Mostra lista salvataggi
				mostraSchermataCaricamento();
				break;

			case "exit":
				// Esci dal gioco
				System.out.println("👋 Arrivederci!");
				FXGL.getGameController().exit();
				break;
			}
		});

		FXGL.getGameScene().addUINode(screen);
	}

	/**
	 * Carica partita da salvataggio Se la partita è stata vinta, avanza all'anno
	 * successivo
	 */
	private void caricaPartitaDaSalvataggio(GameSaveData saveData) {
		System.out.println("\n=== CARICAMENTO PARTITA DA SALVATAGGIO ===");

		// Verifica input
		if (saveData == null) {
			System.err.println("❌ SaveData è NULL!");
			mostraMenuPrincipale();
			return;
		}

		System.out.println("📂 Save Data:");
		System.out.println("   Anno: " + saveData.getAnnoCorrente());
		System.out.println("   Giocatori: " + saveData.getNumeroGiocatori());
		System.out.println("   Vittoria: " + saveData.isVittoriaUltimaPartita());

		this.numeroGiocatori = saveData.getNumeroGiocatori();
		this.saveDataCaricato = saveData;
		this.caricamentoDaSalvataggio = true;

		// ⭐ NUOVO: Controlla se la partita era vinta
		if (saveData.isVittoriaUltimaPartita()) {
			// Avanza anno
			int annoSuccessivo = saveData.getAnnoCorrente() + 1;

			if (annoSuccessivo <= 7) {
				this.annoSelezionato = annoSuccessivo;

				// Ricrea giocatori
				List<Giocatore> giocatoriTemp = ProgressionManager.ricreaGiocatoriDaSalvataggio(saveData,
						saveData.getAnnoCorrente());

				this.giocatoriSelezionati = new ArrayList<>();
				for (Giocatore g : giocatoriTemp) {
				    String nomeEroe = g.getEroe().getNome();
				    Eroe nuovoEroe = HeroFactory.creaEroe(nomeEroe, annoSuccessivo);
				    Giocatore nuovoGiocatore = new Giocatore(nuovoEroe);
				    
				    // Mantieni competenza
				    if (g.getCompetenza() != null) {
				        nuovoGiocatore.setCompetenza(g.getCompetenza());
				    }
				    
				    // Trasferisci carte
				    nuovoGiocatore.getMazzo().getCarte().clear();
				    nuovoGiocatore.getScarti().getCarte().clear();
				    for (Carta c : g.getMazzo().getCarte()) {
				        nuovoGiocatore.getMazzo().aggiungiCarta(c);
				    }
				    for (Carta c : g.getScarti().getCarte()) {
				        nuovoGiocatore.getMazzo().aggiungiCarta(c);
				    }
				    for (Carta c : g.getMano()) {
				        nuovoGiocatore.getMazzo().aggiungiCarta(c);
				    }
				    
				    this.giocatoriSelezionati.add(nuovoGiocatore);
				}

				// Salva dati per avviaGioco()
				this.carteNegozioTemp = saveData.getCarteNegozioRimaste();  // ✅ NUOVO

				// ⭐ NUOVO: Passa carte acquisite al controller
				// Verranno usate in avviaGioco()
				//this.carteAcquisiteTemp = saveData.getCarteAcquisite();

				avviaGioco();
			}
		} else {
			// Partita in corso
			this.annoSelezionato = saveData.getAnnoCorrente();
			this.giocatoriSelezionati = ProgressionManager.ricreaGiocatoriDaSalvataggio(saveData, annoSelezionato);

			// ⭐ NUOVO: Passa carte acquisite
			//this.carteAcquisiteTemp = saveData.getCarteAcquisite();

			avviaGioco();
		}
	}

	// ============================================
	// NUOVO METODO: Messaggio gioco completato
	// ============================================

	/**
	 * Mostra messaggio quando tutti gli anni sono stati completati
	 */
	private void mostraMessaggioGiocoCompletato() {
		javafx.application.Platform.runLater(() -> {
			javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
					javafx.scene.control.Alert.AlertType.INFORMATION);

			alert.setTitle("Gioco Completato!");
			alert.setHeaderText("🏆 CONGRATULAZIONI! 🏆");
			alert.setContentText("Avete completato tutti e 7 gli anni di Hogwarts Battle!\n\n"
					+ "🎓 Hogwarts è al sicuro grazie a voi!\n\n"
					+ "Potete iniziare una nuova partita dal menu principale.");

			alert.showAndWait();

			// Torna al menu principale
			FXGL.getGameScene().clearUINodes();
			mostraMenuPrincipale();
		});
	}

	/**
	 * Mostra schermata caricamento con lista salvataggi
	 */
	/**
	 * Mostra schermata caricamento con lista salvataggi
	 */
	private void mostraSchermataCaricamento() {
		System.out.println("\n=== SCHERMATA CARICAMENTO ===");

		LoadGameScreen screen = new LoadGameScreen(
				// Callback quando si carica una partita
				saveData -> {
					// ⭐ NON chiamare clearUINodes() qui!
					// Lo farà avviaGioco() nel modo corretto
					caricaPartitaDaSalvataggio(saveData);
				},
				// Callback per tornare indietro
				unused -> {
					FXGL.getGameScene().clearUINodes();
					mostraMenuPrincipale();
				});

		FXGL.getGameScene().addUINode(screen);
	}

	/**
	 * STEP 1: Mostra schermata selezione numero giocatori
	 */
	private void mostraSchermataSelezioneNumeroGiocatori() {
		System.out.println("\n=== STEP 1: Selezione Numero Giocatori ===");

		PlayerCountSelectionScreen screen = new PlayerCountSelectionScreen(numero -> {
			this.numeroGiocatori = numero;
			System.out.println("✓ Numero giocatori: " + numeroGiocatori);

			FXGL.getGameScene().clearUINodes();
			mostraSchermataSelezioneEroi();
		});

		FXGL.getGameScene().addUINode(screen);
	}

	/**
	 * STEP 2: Mostra schermata selezione eroi
	 */
	private void mostraSchermataSelezioneEroi() {
		System.out.println("\n=== STEP 2: Selezione Eroi ===");

		HeroSelectionScreen screen = new HeroSelectionScreen(numeroGiocatori, annoSelezionato, giocatori -> {
			this.giocatoriSelezionati = giocatori;

			System.out.println("✓ Eroi selezionati:");
			for (int i = 0; i < giocatoriSelezionati.size(); i++) {
				System.out.println("  Giocatore " + (i + 1) + ": " + giocatoriSelezionati.get(i).getEroe().getNome());
			}

			FXGL.getGameScene().clearUINodes();

			if (annoSelezionato == 6) {
				mostraSchermataSelezioneCompetenze();
			} else {
				avviaGioco();
			}
		});

		FXGL.getGameScene().addUINode(screen);
	}

	/**
	 * STEP 3: (Solo Anno 6) Mostra schermata selezione competenze
	 */
	private void mostraSchermataSelezioneCompetenze() {
		System.out.println("\n=== STEP 3: Selezione Competenze (Anno 6) ===");

		List<String> nomiEroi = giocatoriSelezionati.stream().map(g -> g.getEroe().getNome())
				.collect(Collectors.toList());

		ProficiencySelectionScreen screen = new ProficiencySelectionScreen(nomiEroi, competenzeSelezionate -> {
			for (int i = 0; i < giocatoriSelezionati.size(); i++) {
				String idCompetenza = competenzeSelezionate.get(i);
				try {
					Competenza comp = ProficiencyFactory.creaCompetenza(idCompetenza);
					giocatoriSelezionati.get(i).setCompetenza(comp);
					System.out.println("✓ " + nomiEroi.get(i) + " ha ricevuto: " + comp.getNome());
				} catch (Exception e) {
					System.err.println("⚠️ Errore assegnazione competenza a " + nomiEroi.get(i));
				}
			}

			FXGL.getGameScene().clearUINodes();
			avviaGioco();
		});

		FXGL.getGameScene().addUINode(screen);
	}

	/**
	 * STEP 4: Avvia il gioco vero e proprio
	 */
	private void avviaGioco() {
		System.out.println("\n=== AVVIO GIOCO ===");

		try {
			// ⭐ STEP 1: Pulisci TUTTA l'UI esistente all'inizio
			System.out.println("🧹 Pulizia UI esistente...");
			FXGL.getGameScene().clearUINodes();

			// ⭐ STEP 2: Reset variabili UI
			gameUI = null;

			// ⭐ STEP 3: Verifica prerequisiti
			if (giocatoriSelezionati == null || giocatoriSelezionati.isEmpty()) {
				System.err.println("❌ ERRORE: Nessun giocatore selezionato!");
				mostraMenuPrincipale();
				return;
			}

			if (annoSelezionato < 1 || annoSelezionato > 7) {
				System.err.println("❌ ERRORE: Anno non valido: " + annoSelezionato);
				mostraMenuPrincipale();
				return;
			}

			// ⭐ STEP 4: Carica configurazione
			GameLoader loader = new GameLoader();
			GameConfig config;
			
			if (carteNegozioTemp != null && !carteNegozioTemp.isEmpty()) {
			    System.out.println("📦 Caricamento con carte negozio da salvataggio");
			    config = loader.caricaConfigurazione(annoSelezionato, carteNegozioTemp);
			    carteNegozioTemp = null; // Reset dopo l'uso
			} else {
			    config = loader.caricaConfigurazione(annoSelezionato);
			}

			if (config == null) {
				System.err.println("❌ ERRORE: Impossibile caricare configurazione anno " + annoSelezionato);
				mostraMenuPrincipale();
				return;
			}

			System.out.println("📋 Configurazione anno " + annoSelezionato + " caricata");

			// ⭐ STEP 5: Crea stato di gioco
			stato = new StatoDiGioco(config, giocatoriSelezionati);

			// ⭐ NUOVO: Ripristina carte acquisite se presente
			if (carteAcquisiteTemp != null && !carteAcquisiteTemp.isEmpty()) {
				Set<String> carteSet = new HashSet<>(carteAcquisiteTemp);
				stato.setCarteAcquisiteDaiGiocatori(carteSet);
				System.out.println("✅ Ripristinate " + carteSet.size() + " carte acquisite");
				carteAcquisiteTemp = null; // Reset
			}

			if (stato == null) {
				System.err.println("❌ ERRORE: Stato di gioco non creato!");
				mostraMenuPrincipale();
				return;
			}

			if (stato.getGiocatori() == null || stato.getGiocatori().isEmpty()) {
				System.err.println("❌ ERRORE: Stato senza giocatori!");
				mostraMenuPrincipale();
				return;
			}

			System.out.println("\n✅ Gioco avviato!");
			System.out.println("   Anno: " + annoSelezionato);
			System.out.println("   Giocatori: " + giocatoriSelezionati.size());

			for (int i = 0; i < giocatoriSelezionati.size(); i++) {
				Giocatore g = giocatoriSelezionati.get(i);
				System.out.println("   [" + (i + 1) + "] " + g.getEroe().getNome()
						+ (g.getCompetenza() != null ? " (" + g.getCompetenza().getNome() + ")" : ""));
			}

			System.out.println("🏰 Luogo attuale: " + stato.getLuogoAttuale().getNome());
			System.out.println("👹 Malvagi attivi: " + stato.getMalvagiAttivi().size());
			System.out.println("🛒 Carte nel mercato: " + stato.getMercato().size());

			// ⭐ STEP 6: Crea interfaccia grafica (stato garantito valido)
			gameUI = new GameBoardUI(stato);

			if (gameUI == null) {
				System.err.println("❌ ERRORE: UI non creata!");
				mostraMenuPrincipale();
				return;
			}

			gameUI.setMinSize(1920, 1080);
			gameUI.setPrefSize(1920, 1080);
			gameUI.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

			// ⭐ STEP 7: Aggiungi UI alla scena
			FXGL.getGameScene().addUINode(gameUI);

			// Listener resize
			FXGL.getGameScene().getRoot().widthProperty().addListener((obs, oldVal, newVal) -> {
				if (gameUI != null) {
					gameUI.setPrefWidth(newVal.doubleValue());
					gameUI.layout();
				}
			});

			FXGL.getGameScene().getRoot().heightProperty().addListener((obs, oldVal, newVal) -> {
				if (gameUI != null) {
					gameUI.setPrefHeight(newVal.doubleValue());
					gameUI.layout();
				}
			});

			System.out.println("🎨 Interfaccia grafica inizializzata");

			// ⭐ STEP 8: Inizia la partita
			iniziaPartita();

			grafica.utils.ImageLoader.stampaReport();

		} catch (Exception e) {
			System.err.println("❌ ERRORE CRITICO durante avvio gioco:");
			e.printStackTrace();

			// Fallback sicuro
			try {
				stato = null;
				gameUI = null;
				FXGL.getGameScene().clearUINodes();
				mostraMenuPrincipale();
			} catch (Exception e2) {
				System.err.println("❌ ERRORE DOPPIO: impossibile recuperare");
				e2.printStackTrace();
			}
		}
	}

	// ========================================
	// GESTIONE VITTORIA E AVANZAMENTO
	// ========================================

	/**
	 * Chiamato quando il gioco viene vinto
	 */
	public void onVittoria() {
		System.out.println("\n🎉 VITTORIA RILEVATA!");

		ProgressionManager.salvaProgressoVittoria(stato);
		SaveManager.autosave(stato);

		int annoCompletato = stato.getAnnoCorrente();
		boolean ultimoAnno = ProgressionManager.giocoCompletato(stato);

		// Mostra schermata vittoria
		FXGL.getGameScene().clearUINodes();

		VictoryScreen victoryScreen = new VictoryScreen(annoCompletato, scelta -> {
			FXGL.getGameScene().clearUINodes();

			if (scelta.equals("continue") && !ultimoAnno) {
				// ⭐ MODIFICATO: Avanza senza tornare al menu
				avanzaAnnoSuccessivo();
			} else {
				mostraMenuPrincipale();
			}
		});

		// ⭐ OPZIONALE: Auto-avanza dopo 5 secondi se non ultimo anno
		if (!ultimoAnno) {
			javafx.animation.Timeline timeline = new javafx.animation.Timeline(
					new javafx.animation.KeyFrame(javafx.util.Duration.seconds(10), event -> {
						if (FXGL.getGameScene().getUINodes().contains(victoryScreen)) {
							FXGL.getGameScene().clearUINodes();
							avanzaAnnoSuccessivo();
						}
					}));
			timeline.play();
		}

		FXGL.getGameScene().addUINode(victoryScreen);
	}

	/**
	 * Chiamato quando il gioco viene perso (sconfitta)
	 */
	public void onSconfitta() {
		System.out.println("\n💀 SCONFITTA RILEVATA!");

		// Salva comunque lo stato (opzionale)
		SaveManager.salvaPartita(stato, "sconfitta_anno_" + stato.getAnnoCorrente());

		int annoFallito = stato.getAnnoCorrente();
		String motivoSconfitta = "🌑 Tutti i luoghi sono stati persi!\nI Marchi Neri hanno sopraffatto Hogwarts!";

		// Mostra schermata Game Over
		FXGL.getGameScene().clearUINodes();

		GameOverScreen gameOverScreen = new GameOverScreen(annoFallito, motivoSconfitta, scelta -> {
			FXGL.getGameScene().clearUINodes();

			if (scelta.equals("retry")) {
				// Riprova stesso anno
				riprovaSconfittaAnno();
			} else {
				// Torna al menu
				mostraMenuPrincipale();
			}
		});

		FXGL.getGameScene().addUINode(gameOverScreen);
	}

	/**
	 * Riprova lo stesso anno dopo una sconfitta
	 */
	private void riprovaSconfittaAnno() {
		System.out.println("\n🔄 RIPROVA ANNO " + annoSelezionato);

		// Mantieni gli stessi giocatori e anno
		// (giocatoriSelezionati e annoSelezionato già impostati)

		// Riavvia il gioco
		avviaGioco();
	}

	/**
	 * Avanza anno successivo
	 */
	private void avanzaAnnoSuccessivo() {
		int prossimoAnno = ProgressionManager.calcolaProssimoAnno(stato.getAnnoCorrente());

		System.out.println("\n📖 AVANZAMENTO ANNO " + prossimoAnno);

		List<Giocatore> giocatoriProssimoAnno = ProgressionManager.preparaGiocatoriProssimoAnno(stato.getGiocatori(),
				prossimoAnno, stato);

		this.annoSelezionato = prossimoAnno;
		this.giocatoriSelezionati = giocatoriProssimoAnno;
		this.numeroGiocatori = giocatoriProssimoAnno.size();

		// Se anno 6 e non hanno competenze, mostra selezione
		boolean needProficiencySelection = (prossimoAnno == 6)
				&& giocatoriProssimoAnno.stream().allMatch(g -> g.getCompetenza() == null);

		if (needProficiencySelection) {
			mostraSchermataSelezioneCompetenze();
		} else {
			avviaGioco();
		}
	}

	/**
	 * Salvataggio rapido
	 */
	public void salvaPartitaRapida() {
		if (stato != null) {
			boolean success = SaveManager.autosave(stato);
			if (success) {
				System.out.println("💾 Partita salvata automaticamente!");
			}
		}
	}

	// ========================================
	// METODI ORIGINALI (NON MODIFICATI)
	// ========================================

	public void iniziaPartita() {
		turnManager = new TurnManager(stato);
		turnManager.iniziaTurno();
		gameUI.aggiorna();
	}

	public void giocaCarta(int indiceInMano) {
		Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());

		if (stato.getFaseCorrente() == FaseTurno.GIOCA_CARTE) {
			if (indiceInMano >= 0 && indiceInMano < giocatore.getMano().size()) {
				Carta carta = giocatore.getMano().get(indiceInMano);

				// ⭐ NUOVO: Mostra messaggio
				gameUI.getMessagePanel().mostraMessaggio(giocatore.getEroe().getNome() + " gioca: " + carta.getNome(),
						MessagePanel.TipoMessaggio.GIOCA_CARTA);

				giocatore.giocaCarta(stato, carta);
				gameUI.aggiorna();
			}
		}
	}

	public void acquistaCarta(int indiceInMercato) {
		Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());

		if (stato.getFaseCorrente() == FaseTurno.ACQUISTA_CARTE) {
			if (indiceInMercato >= 0 && indiceInMercato < stato.getMercato().size()) {
				Carta carta = stato.getMercato().get(indiceInMercato);
				if (giocatore.getGettone() >= carta.getCosto()) {
					gameUI.getMessagePanel().mostraMessaggio(
							giocatore.getEroe().getNome() + " acquista: " + carta.getNome(),
							MessagePanel.TipoMessaggio.ACQUISTA_CARTA);

					giocatore.acquistaCarta(stato.getMercato(), carta, stato);

					// ⭐ NUOVO: Gestisci trigger acquisto (Cappello Parlante)
					gestisciTriggerAcquisto(carta, giocatore);

					stato.rifornisciMercato();
					gameUI.aggiorna();
				}
			}
		}
	}

	/**
	 * Gestisce trigger dopo acquisto (Cappello, Giratempo, Wingardium)
	 */
	private void gestisciTriggerAcquisto(Carta cartaAcquistata, Giocatore giocatore) {
		TipoTrigger tipoTrigger = null;

		// Determina tipo trigger in base alla classe carta
		if (cartaAcquistata.getClasse().equalsIgnoreCase("Alleato")) {
			tipoTrigger = gestoreEffetti.TipoTrigger.ACQUISTA_ALLEATO;
		} else if (cartaAcquistata.getClasse().equalsIgnoreCase("Incantesimo")) {
			tipoTrigger = gestoreEffetti.TipoTrigger.ACQUISTA_INCANTESIMO;
		} else if (cartaAcquistata.getClasse().equalsIgnoreCase("Oggetto")) {
			tipoTrigger = gestoreEffetti.TipoTrigger.ACQUISTA_OGGETTO;
		}

		if (tipoTrigger != null && stato.getGestoreTrigger().hasTrigger(tipoTrigger)) {
			// C'è un trigger attivo
			mostraDialogPosizionamentoCarta(cartaAcquistata, giocatore, tipoTrigger);
		} else {
			// Nessun trigger → carta va negli scarti
			giocatore.getScarti().aggiungiCarta(cartaAcquistata);
		}
	}

	/**
	 * Mostra dialog per scegliere dove posizionare carta acquistata
	 */
	private void mostraDialogPosizionamentoCarta(Carta carta, Giocatore giocatore, TipoTrigger tipoTrigger) {
		javafx.application.Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

			// ⭐ NUOVO: Titolo diverso in base al trigger
			String nomeCartaTrigger = getNomeCartaTrigger(tipoTrigger);
			alert.setTitle(nomeCartaTrigger);
			alert.setHeaderText("Posizionamento carta acquistata");
			alert.setContentText("Hai acquistato: " + carta.getNome() + " (" + carta.getClasse() + ")\n\n"
					+ nomeCartaTrigger + " ti permette di metterla in cima al mazzo.\n\n" + "Dove vuoi posizionarla?");

			ButtonType btnCimaMazzo = new ButtonType("⬆️ Cima del Mazzo");
			ButtonType btnScarti = new ButtonType("📥 Scarti (normale)");

			alert.getButtonTypes().setAll(btnCimaMazzo, btnScarti);

			alert.showAndWait().ifPresent(scelta -> {
				if (scelta == btnCimaMazzo) {
					giocatore.getMazzo().getCarte().addFirst(carta);
					System.out.println("  ⬆️ " + carta.getNome() + " → cima del mazzo");

					gameUI.getMessagePanel().mostraMessaggio(carta.getNome() + " → Cima del mazzo",
							MessagePanel.TipoMessaggio.INFO);
				} else {
					giocatore.getScarti().aggiungiCarta(carta);
				}

				gameUI.aggiorna();
			});
		});
	}

	/**
	 * Ottiene il nome della carta che ha attivato il trigger
	 */
	private String getNomeCartaTrigger(TipoTrigger tipo) {
		switch (tipo) {
		case ACQUISTA_ALLEATO:
			return "Cappello Parlante";
		case ACQUISTA_INCANTESIMO:
			return "Giratempo";
		case ACQUISTA_OGGETTO:
			return "Wingardium Leviosa";
		default:
			return "Carta speciale";
		}
	}

	public void attaccaMalvagio(int indiceMalvagio) {
		Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());

		if (stato.getFaseCorrente() == FaseTurno.ATTACCA) {
			if (indiceMalvagio >= 0 && indiceMalvagio < stato.getMalvagiAttivi().size()) {
				Malvagio malvagio = stato.getMalvagiAttivi().get(indiceMalvagio);
				if (giocatore.getAttacco() > 0) {
					stato.assegnaAttacco(malvagio, 1);
					// ⭐ NUOVO: Messaggio attacco
					String msg = giocatore.getEroe().getNome() + " attacca " + malvagio.getNome() + " ("
							+ malvagio.getDanno() + "/" + malvagio.getVita() + " ❤️)";
					gameUI.getMessagePanel().mostraMessaggio(msg, MessagePanel.TipoMessaggio.ATTACCO);
					if (malvagio.getDanno() >= malvagio.getVita() - 1) {
						// ⭐ NUOVO: Messaggio sconfitta
						gameUI.getMessagePanel().mostraMessaggio("💀 " + malvagio.getNome() + " sconfitto!",
								MessagePanel.TipoMessaggio.MALVAGIO);
					}
					stato.applicaAttacchi();
					giocatore.setAttacco(giocatore.getAttacco() - 1);

					// Fase già richiamata nel metodo applicaAttacchi
					/*
					 * if (malvagio.getDanno() >= malvagio.getVita()) {
					 * stato.sconfiggiMalvagio(malvagio);
					 * 
					 * if (!stato.getMazzoMalvagi().isEmpty()) { stato.addMalvagioAttivo(); } }
					 */

					gameUI.aggiorna();
					System.out.println("⚔️ Attaccato malvagio: " + malvagio.getNome());
				}
			}
		}
	}

	/**
	 * Distrugge un Horcrux e applica la ricompensa
	 */
	private void distruggiHorcrux(Horcrux horcrux) {
		Giocatore giocatoreCorrente = stato.getGiocatori().get(stato.getGiocatoreCorrente());

		// Applica ricompensa
		horcrux.applicaRicompensa(stato, giocatoreCorrente);

		// Messaggio
		gameUI.getMessagePanel().mostraMessaggio("🔥 " + horcrux.getNome() + " DISTRUTTO!",
				MessagePanel.TipoMessaggio.MALVAGIO);

		// Rimuovi da horcrux attivi
		stato.distruggiHorcrux(horcrux);

		// Verifica vittoria (tutti horcrux + tutti malvagi)
		if (stato.isVittoriaPendente()) {
			Platform.runLater(() -> {
				gameUI.getMessagePanel().mostraMessaggio("🏆 TUTTI GLI HORCRUX DISTRUTTI!",
						MessagePanel.TipoMessaggio.INFO);
			});
		}

		gameUI.aggiorna();
	}

	public void prossimaFase() {
		turnManager.prossimaFase();
		gameUI.aggiorna();
		if (gameUI.getPlayerPanel() != null) {
			gameUI.getPlayerPanel().aggiornaNomeBottoneFase();
		}
	}

	public StatoDiGioco getStato() {
		return stato;
	}

	public GameBoardUI getGameUI() {
		return gameUI;
	}

	public static GameController getInstance() {
		return instance;
	}
}