package grafica;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.layout.Region;
import gioco.*;
import data.*;
import carte.*;
import grafica.screens.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GameController - Classe principale con sistema salvataggio/caricamento integrato
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
     */
    private void caricaPartitaDaSalvataggio(GameSaveData saveData) {
        System.out.println("\n=== CARICAMENTO PARTITA ===");
        System.out.println("Anno: " + saveData.getAnnoCorrente());
        System.out.println("Giocatori: " + saveData.getNumeroGiocatori());
        
        this.annoSelezionato = saveData.getAnnoCorrente();
        this.numeroGiocatori = saveData.getNumeroGiocatori();
        this.saveDataCaricato = saveData;
        this.caricamentoDaSalvataggio = true;
        
        // Ricrea giocatori dal salvataggio
        this.giocatoriSelezionati = ProgressionManager.ricreaGiocatoriDaSalvataggio(
            saveData, 
            annoSelezionato
        );
        
        // Avvia il gioco
        avviaGioco();
    }
    
    /**
     * Mostra schermata caricamento (da implementare)
     */
    private void mostraSchermataCaricamento() {
        // TODO: Implementare lista salvataggi con UI
        System.out.println("⚠️ Schermata caricamento non ancora implementata");
        mostraMenuPrincipale();
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
        
        HeroSelectionScreen screen = new HeroSelectionScreen(
            numeroGiocatori,
            annoSelezionato,
            giocatori -> {
                this.giocatoriSelezionati = giocatori;
                
                System.out.println("✓ Eroi selezionati:");
                for (int i = 0; i < giocatoriSelezionati.size(); i++) {
                    System.out.println("  Giocatore " + (i+1) + ": " + 
                                     giocatoriSelezionati.get(i).getEroe().getNome());
                }
                
                FXGL.getGameScene().clearUINodes();
                
                if (annoSelezionato == 6) {
                    mostraSchermataSelezioneCompetenze();
                } else {
                    avviaGioco();
                }
            }
        );
        
        FXGL.getGameScene().addUINode(screen);
    }
    
    /**
     * STEP 3: (Solo Anno 6) Mostra schermata selezione competenze
     */
    private void mostraSchermataSelezioneCompetenze() {
        System.out.println("\n=== STEP 3: Selezione Competenze (Anno 6) ===");
        
        List<String> nomiEroi = giocatoriSelezionati.stream()
            .map(g -> g.getEroe().getNome())
            .collect(Collectors.toList());
        
        ProficiencySelectionScreen screen = new ProficiencySelectionScreen(
            nomiEroi,
            competenzeSelezionate -> {
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
            }
        );
        
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
                System.out.println("   [" + (i+1) + "] " + g.getEroe().getNome() +
                                 (g.getCompetenza() != null ? " (" + g.getCompetenza().getNome() + ")" : ""));
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
        
        List<Giocatore> giocatoriProssimoAnno = ProgressionManager.preparaGiocatoriProssimoAnno(
            stato.getGiocatori(),
            prossimoAnno
        );
        
        this.annoSelezionato = prossimoAnno;
        this.giocatoriSelezionati = giocatoriProssimoAnno;
        this.numeroGiocatori = giocatoriProssimoAnno.size();
        
        // Se anno 6 e non hanno competenze, mostra selezione
        boolean needProficiencySelection = (prossimoAnno == 6) && 
            giocatoriProssimoAnno.stream().allMatch(g -> g.getCompetenza() == null);
        
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
                giocatore.giocaCarta(stato, carta);
                gameUI.aggiorna();
                System.out.println("▶️ Giocata carta: " + carta.getNome());
            }
        }
    }
    
    public void acquistaCarta(int indiceInMercato) {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        if (stato.getFaseCorrente() == FaseTurno.ACQUISTA_CARTE) {
            if (indiceInMercato >= 0 && indiceInMercato < stato.getMercato().size()) {
                Carta carta = stato.getMercato().get(indiceInMercato);
                if (giocatore.getGettone() >= carta.getCosto()) {
                    giocatore.acquistaCarta(stato.getMercato(), carta);
                    stato.rifornisciMercato();
                    gameUI.aggiorna();
                    System.out.println("💰 Acquistata carta: " + carta.getNome());
                } else {
                    System.out.println("❌ Gettoni insufficienti! Serve: " + carta.getCosto() + 
                        ", Hai: " + giocatore.getGettone());
                }
            }
        }
    }
    
    public void attaccaMalvagio(int indiceMalvagio) {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        if (stato.getFaseCorrente() == FaseTurno.ATTACCA) {
            if (indiceMalvagio >= 0 && indiceMalvagio < stato.getMalvagiAttivi().size()) {
                Malvagio malvagio = stato.getMalvagiAttivi().get(indiceMalvagio);
                if (giocatore.getAttacco() > 0) {
                    stato.assegnaAttacco(malvagio, 1);
                    giocatore.setAttacco(giocatore.getAttacco() - 1);
                    
                    if (malvagio.getDanno() >= malvagio.getVita()) {
                        stato.sconfiggiMalvagio(malvagio);
                        
                        if (!stato.getMazzoMalvagi().isEmpty()) {
                            stato.addMalvagioAttivo();
                        }
                    }
                    
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