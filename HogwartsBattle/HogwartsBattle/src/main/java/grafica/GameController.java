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
 * GameController - Classe principale che gestisce il flusso del gioco usando FXGL
 * ⭐ VERSIONE CON SCHERMATE DI SELEZIONE INTEGRATE
 */
public class GameController extends GameApplication {
    
    private StatoDiGioco stato;
    private GameBoardUI gameUI;
    private TurnManager turnManager;
    
    // ⭐ Parametri selezione
    private int numeroGiocatori;
    private int annoSelezionato = 1; // Modifica per testare anno 6
    private List<Giocatore> giocatoriSelezionati;
    
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
            
            // ⭐ STEP 1: Mostra schermata selezione numero giocatori
            mostraSchermataSelezioneNumeroGiocatori();
            
        } catch (Exception e) {
            System.err.println("❌ Errore durante l'inizializzazione:");
            e.printStackTrace();
        }
    }
    
    // ========================================
    // ⭐ METODI DI AVVIO MODIFICATI
    // ========================================
    
    /**
     * STEP 1: Mostra schermata selezione numero giocatori
     */
    private void mostraSchermataSelezioneNumeroGiocatori() {
        System.out.println("\n=== STEP 1: Selezione Numero Giocatori ===");
        
        PlayerCountSelectionScreen screen = new PlayerCountSelectionScreen(numero -> {
            this.numeroGiocatori = numero;
            System.out.println("✓ Numero giocatori: " + numeroGiocatori);
            
            // Rimuovi schermata precedente
            FXGL.getGameScene().clearUINodes();
            
            // Vai allo step 2
            mostraSchermataSelezioneEroi();
        });
        
        // Aggiungi alla scena FXGL
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
                
                // Rimuovi schermata precedente
                FXGL.getGameScene().clearUINodes();
                
                // Vai allo step 3 o avvia gioco
                if (annoSelezionato == 6) {
                    mostraSchermataSelezioneCompetenze();
                } else {
                    avviaGioco();
                }
            }
        );
        
        // Aggiungi alla scena FXGL
        FXGL.getGameScene().addUINode(screen);
    }
    
    /**
     * STEP 3: (Solo Anno 6) Mostra schermata selezione competenze
     */
    private void mostraSchermataSelezioneCompetenze() {
        System.out.println("\n=== STEP 3: Selezione Competenze (Anno 6) ===");
        
        // Estrai i nomi degli eroi
        List<String> nomiEroi = giocatoriSelezionati.stream()
            .map(g -> g.getEroe().getNome())
            .collect(Collectors.toList());
        
        ProficiencySelectionScreen screen = new ProficiencySelectionScreen(
            nomiEroi,
            competenzeSelezionate -> {
                // Assegna le competenze ai giocatori
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
                
                // Rimuovi schermata precedente
                FXGL.getGameScene().clearUINodes();
                
                // Ora avvia il gioco
                avviaGioco();
            }
        );
        
        // Aggiungi alla scena FXGL
        FXGL.getGameScene().addUINode(screen);
    }
    
    /**
     * STEP 4: Avvia il gioco vero e proprio
     */
    private void avviaGioco() {
        System.out.println("\n=== STEP 4: Avvio Gioco ===");
        
        try {
            // Carica la configurazione del gioco
            GameLoader loader = new GameLoader();
            GameConfig config = loader.caricaConfigurazione(annoSelezionato);
            
            System.out.println("📋 Configurazione anno " + annoSelezionato + " caricata");
            
            // Crea lo stato di gioco con i giocatori selezionati
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
            
            // Crea l'interfaccia grafica principale
            gameUI = new GameBoardUI(stato);
            
            // Imposta dimensioni fisse e binding per resize
            gameUI.setMinSize(1920, 1080);
            gameUI.setPrefSize(1920, 1080);
            gameUI.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            
            // Aggiungi l'UI alla scena FXGL
            FXGL.getGameScene().addUINode(gameUI);
            
            // Binding per resize dinamico
            FXGL.getGameScene().getRoot().widthProperty().addListener((obs, oldVal, newVal) -> {
                gameUI.setPrefWidth(newVal.doubleValue());
                gameUI.layout();
            });
            
            FXGL.getGameScene().getRoot().heightProperty().addListener((obs, oldVal, newVal) -> {
                gameUI.setPrefHeight(newVal.doubleValue());
                gameUI.layout();
            });
            
            System.out.println("🎨 Interfaccia grafica inizializzata");
            
            // Inizia la partita
            iniziaPartita();
            
            // Stampa report immagini
            grafica.utils.ImageLoader.stampaReport();
            
        } catch (Exception e) {
            System.err.println("❌ Errore durante l'avvio del gioco:");
            e.printStackTrace();
        }
    }
    
    // ========================================
    // ⭐ METODI ORIGINALI NON MODIFICATI
    // ========================================
    
    /**
     * Inizia il turno del primo giocatore
     */
    public void iniziaPartita() {
        turnManager = new TurnManager(stato);
        turnManager.iniziaTurno();
        gameUI.aggiorna();
    }
    
    /**
     * Gioca una carta dalla mano del giocatore corrente
     */
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
    
    /**
     * Acquista una carta dal mercato
     * ⭐ CORRETTO: rimossa duplicazione
     */
    public void acquistaCarta(int indiceInMercato) {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        if (stato.getFaseCorrente() == FaseTurno.ACQUISTA_CARTE) {
            if (indiceInMercato >= 0 && indiceInMercato < stato.getMercato().size()) {
                Carta carta = stato.getMercato().get(indiceInMercato);
                if (giocatore.getGettone() >= carta.getCosto()) {
                    // ⭐ FIX: acquistaCarta() GIÀ aggiunge agli scarti e rimuove dal mercato
                    giocatore.acquistaCarta(stato.getMercato(), carta);
                    
                    // ⭐ FIX: rifornisce mercato
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
    
    /**
     * Attacca un malvagio
     */
    public void attaccaMalvagio(int indiceMalvagio) {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        if (stato.getFaseCorrente() == FaseTurno.ATTACCA) {
            if (indiceMalvagio >= 0 && indiceMalvagio < stato.getMalvagiAttivi().size()) {
                Malvagio malvagio = stato.getMalvagiAttivi().get(indiceMalvagio);
                if (giocatore.getAttacco() > 0) {
                    stato.assegnaAttacco(malvagio, 1);
                    giocatore.setAttacco(giocatore.getAttacco() - 1);
                    
                    // Se il malvagio è sconfitto, rivela un nuovo malvagio
                    if (malvagio.getDanno() >= malvagio.getVita()) {
                        stato.sconfiggiMalvagio(malvagio);
                        
                        // Pesca nuovo malvagio se il mazzo non è vuoto
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
    
    /**
     * Passa alla fase successiva
     */
    public void prossimaFase() {
        turnManager.prossimaFase();
        gameUI.aggiorna();
        if (gameUI.getPlayerPanel() != null) {
            gameUI.getPlayerPanel().aggiornaNomeBottoneFase();
        }
    }
    
    /**
     * Ottiene lo stato del gioco
     */
    public StatoDiGioco getStato() {
        return stato;
    }
    
    /**
     * Ottiene l'UI principale
     */
    public GameBoardUI getGameUI() {
        return gameUI;
    }
    
    /**
     * Ottiene l'istanza del controller
     */
    public static GameController getInstance() {
        return instance;
    }
}