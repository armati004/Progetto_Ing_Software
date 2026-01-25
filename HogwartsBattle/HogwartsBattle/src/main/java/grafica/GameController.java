package grafica;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import gioco.*;
import data.*;
import carte.*;
import grafica.screens.*;
import grafica.panels.MessagePanel;
import java.util.ArrayList;
import java.util.List;
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
	 * Carica partita da salvataggio
	 * Se la partita è stata vinta, avanza all'anno successivo
	 */
	private void caricaPartitaDaSalvataggio(GameSaveData saveData) {
	    System.out.println("\n=== CARICAMENTO PARTITA ===");
	    System.out.println("Anno salvato: " + saveData.getAnnoCorrente());
	    System.out.println("Giocatori: " + saveData.getNumeroGiocatori());
	    System.out.println("Vittoria: " + saveData.isVittoriaUltimaPartita());
	    
	    this.numeroGiocatori = saveData.getNumeroGiocatori();
	    this.saveDataCaricato = saveData;
	    this.caricamentoDaSalvataggio = true;
	    
	    // ⭐ NUOVO: Controlla se la partita era vinta
	    if (saveData.isVittoriaUltimaPartita()) {
	        // Partita vinta → carica anno SUCCESSIVO
	        int annoSuccessivo = ProgressionManager.calcolaProssimoAnno(saveData.getAnnoCorrente());
	        
	        if (annoSuccessivo > saveData.getAnnoCorrente()) {
	            // C'è un anno successivo
	            System.out.println("✓ Partita vinta! Avanzamento all'anno " + annoSuccessivo);
	            
	            this.annoSelezionato = annoSuccessivo;
	            
	            // Prepara giocatori per il prossimo anno
	            List<Giocatore> giocatoriTemp = ProgressionManager.ricreaGiocatoriDaSalvataggio(
	                saveData, 
	                saveData.getAnnoCorrente() // Anno vecchio per ricreazione
	            );
	            
	            this.giocatoriSelezionati = ProgressionManager.preparaGiocatoriProssimoAnno(
	                giocatoriTemp,
	                annoSuccessivo
	            );
	            
	            // Se anno 6 e non hanno competenze, mostra selezione
	            boolean needProficiencySelection = (annoSuccessivo == 6) && 
	                giocatoriSelezionati.stream().allMatch(g -> g.getCompetenza() == null);
	            
	            if (needProficiencySelection) {
	                mostraSchermataSelezioneCompetenze();
	            } else {
	                avviaGioco();
	            }
	        } else {
	            // Anno 7 già completato - gioco finito
	            System.out.println("🏆 Gioco completato! Tutti e 7 gli anni sono stati vinti!");
	            
	            // Mostra messaggio o torna al menu
	            mostraMessaggioGiocoCompletato();
	        }
	    } else {
	        // Partita NON vinta → carica stesso anno
	        System.out.println("✓ Partita in corso, caricamento anno " + saveData.getAnnoCorrente());
	        
	        this.annoSelezionato = saveData.getAnnoCorrente();
	        
	        // Ricrea giocatori dallo stesso anno
	        this.giocatoriSelezionati = ProgressionManager.ricreaGiocatoriDaSalvataggio(
	            saveData, 
	            annoSelezionato
	        );
	        
	        // Avvia il gioco
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
	            javafx.scene.control.Alert.AlertType.INFORMATION
	        );
	        
	        alert.setTitle("Gioco Completato!");
	        alert.setHeaderText("🏆 CONGRATULAZIONI! 🏆");
	        alert.setContentText(
	            "Avete completato tutti e 7 gli anni di Hogwarts Battle!\n\n" +
	            "🎓 Hogwarts è al sicuro grazie a voi!\n\n" +
	            "Potete iniziare una nuova partita dal menu principale."
	        );
	        
	        alert.showAndWait();
	        
	        // Torna al menu principale
	        FXGL.getGameScene().clearUINodes();
	        mostraMenuPrincipale();
	    });
	}

	/**
	 * Mostra schermata caricamento con lista salvataggi
	 */
	private void mostraSchermataCaricamento() {
	    System.out.println("\n=== SCHERMATA CARICAMENTO ===");
	    
	    LoadGameScreen screen = new LoadGameScreen(
	        // Callback quando si carica una partita
	        saveData -> {
	            FXGL.getGameScene().clearUINodes();
	            caricaPartitaDaSalvataggio(saveData);
	        },
	        // Callback per tornare indietro
	        unused -> {
	            FXGL.getGameScene().clearUINodes();
	            mostraMenuPrincipale();
	        }
	    );
	    
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
			GameLoader loader = new GameLoader();
			GameConfig config = loader.caricaConfigurazione(annoSelezionato);

			System.out.println("📋 Configurazione anno " + annoSelezionato + " caricata");

			stato = new StatoDiGioco(config, giocatoriSelezionati);

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

			gameUI = new GameBoardUI(stato);

			gameUI.setMinSize(1920, 1080);
			gameUI.setPrefSize(1920, 1080);
			gameUI.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

			FXGL.getGameScene().addUINode(gameUI);

			FXGL.getGameScene().getRoot().widthProperty().addListener((obs, oldVal, newVal) -> {
				gameUI.setPrefWidth(newVal.doubleValue());
				gameUI.layout();
			});

			FXGL.getGameScene().getRoot().heightProperty().addListener((obs, oldVal, newVal) -> {
				gameUI.setPrefHeight(newVal.doubleValue());
				gameUI.layout();
			});

			System.out.println("🎨 Interfaccia grafica inizializzata");

			iniziaPartita();

			grafica.utils.ImageLoader.stampaReport();

		} catch (Exception e) {
			System.err.println("❌ Errore durante l'avvio del gioco:");
			e.printStackTrace();
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

		// Salva progresso
		ProgressionManager.salvaProgressoVittoria(stato);

		// Autosave
		SaveManager.autosave(stato);

		int annoCompletato = stato.getAnnoCorrente();
		boolean ultimoAnno = ProgressionManager.giocoCompletato(stato);

		// Mostra schermata vittoria
		FXGL.getGameScene().clearUINodes();

		VictoryScreen victoryScreen = new VictoryScreen(annoCompletato, scelta -> {
			FXGL.getGameScene().clearUINodes();

			if (scelta.equals("continue") && !ultimoAnno) {
				avanzaAnnoSuccessivo();
			} else {
				mostraMenuPrincipale();
			}
		});

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
				prossimoAnno);

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
	                    MessagePanel.TipoMessaggio.ACQUISTA_CARTA
	                );
	                
	                giocatore.acquistaCarta(stato.getMercato(), carta);
	                
	                // ⭐ NUOVO: Gestisci trigger acquisto (Cappello Parlante)
	                gestisciTriggerAcquisto(carta, giocatore);
	                
	                stato.rifornisciMercato();
	                gameUI.aggiorna();
	            }
	        }
	    }
	}

	/**
	 * Gestisce trigger dopo acquisto (es. Cappello Parlante)
	 */
	private void gestisciTriggerAcquisto(Carta cartaAcquistata, Giocatore giocatore) {
	    // Controlla se ci sono trigger ACQUISTA_ALLEATO attivi
	    boolean hasTriggerAlleato = false;
	    
	    if (cartaAcquistata.getClasse().equalsIgnoreCase("Alleato")) {
	        // Verifica se c'è trigger attivo (es. Cappello Parlante giocato)
	        hasTriggerAlleato = stato.getGestoreTrigger().hasTrigger(
	            gestoreEffetti.TipoTrigger.ACQUISTA_ALLEATO
	        );
	    }
	    
	    if (hasTriggerAlleato) {
	        // Mostra dialog per scegliere dove mettere la carta
	        mostraDialogPosizionamentoCarta(cartaAcquistata, giocatore);
	    } else {
	        // Carta va normalmente negli scarti
	        giocatore.getScarti().aggiungiCarta(cartaAcquistata);
	    }
	}

	/**
	 * Mostra dialog per scegliere dove posizionare carta acquistata
	 */
	private void mostraDialogPosizionamentoCarta(Carta carta, Giocatore giocatore) {
	    javafx.application.Platform.runLater(() -> {
	        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	        alert.setTitle("Cappello Parlante");
	        alert.setHeaderText("Posizionamento carta acquistata");
	        alert.setContentText(
	            "Hai acquistato: " + carta.getNome() + "\n\n" +
	            "Il Cappello Parlante ti permette di metterla in cima al mazzo.\n\n" +
	            "Dove vuoi posizionarla?"
	        );
	        
	        ButtonType btnCimaMazzo = new ButtonType("⬆️ Cima del Mazzo");
	        ButtonType btnScarti = new ButtonType("📥 Scarti (normale)");
	        
	        alert.getButtonTypes().setAll(btnCimaMazzo, btnScarti);
	        
	        alert.showAndWait().ifPresent(scelta -> {
	            if (scelta == btnCimaMazzo) {
	                giocatore.getMazzo().getCarte().addFirst(carta);
	                System.out.println("  ⬆️ " + carta.getNome() + " → cima del mazzo");
	                
	                gameUI.getMessagePanel().mostraMessaggio(
	                    carta.getNome() + " → Cima del mazzo",
	                    MessagePanel.TipoMessaggio.INFO
	                );
	            } else {
	                giocatore.getScarti().aggiungiCarta(carta);
	            }
	            
	            gameUI.aggiorna();
	        });
	    });
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