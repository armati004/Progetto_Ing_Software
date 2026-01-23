package grafica;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.layout.Region;
import gioco.*;
import data.*;
import carte.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GameController - Classe principale che gestisce il flusso del gioco usando FXGL
 * VERSIONE CORRETTA con resize funzionante
 */
public class GameController extends GameApplication {
    
    private StatoDiGioco stato;
    private GameBoardUI gameUI;
    private TurnManager turnManager;
    private int annoSelezionato = 1;
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
            CardFactory.inizializza();
            HeroFactory.inizializza();
            DiceFactory.inizializza();
            LocationFactory.inizializza();
            VillainFactory.inizializza();
            ProficiencyFactory.inizializza();
            HorcruxFactory.inizializza();
            
            System.out.println("✅ Tutte le factory inizializzate");
            
            // Carica la configurazione del gioco
            GameLoader loader = new GameLoader();
            GameConfig config = loader.caricaConfigurazione(annoSelezionato);
            
            System.out.println("📋 Configurazione anno " + annoSelezionato + " caricata");
            
            // Crea i giocatori
            List<Giocatore> giocatori = creaGiocatori(config);
            
            System.out.println("👥 Creati " + giocatori.size() + " giocatori");
            
            // Crea lo stato di gioco
            stato = new StatoDiGioco(config, giocatori);
            
            System.out.println("🎮 Stato di gioco inizializzato");
            System.out.println("📍 Luogo attuale: " + stato.getLuogoAttuale().getNome());
            System.out.println("👹 Malvagi attivi: " + stato.getMalvagiAttivi().size());
            System.out.println("🛒 Carte nel mercato: " + stato.getMercato().size());
            
            // Crea l'interfaccia grafica principale
            gameUI = new GameBoardUI(stato);
            
            // ⭐ FIX: Imposta dimensioni fisse e binding per resize
            gameUI.setMinSize(1920, 1080);
            gameUI.setPrefSize(1920, 1080);
            gameUI.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            
            // Aggiungi l'UI alla scena FXGL
            FXGL.getGameScene().addUINode(gameUI);
            
            // ⭐ FIX: Binding per resize dinamico
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
            System.err.println("❌ Errore di avvio: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crea i giocatori per il gioco
     */
    private List<Giocatore> creaGiocatori(GameConfig config) {
        List<Giocatore> giocatori = new ArrayList<>();
        List<Eroe> eroiDisponibili = config.getEroiDisponibili();
        
        int numeroGiocatori = config.getNumeroGiocatori();
        
        for (int i = 0; i < numeroGiocatori && i < eroiDisponibili.size(); i++) {
            Giocatore g = new Giocatore(eroiDisponibili.get(i));
            giocatori.add(g);
            System.out.println("✨ Giocatore " + (i + 1) + ": " + g.getEroe().getNome());
        }
        
        return giocatori;
    }
    
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
     * ⭐ CORRETTO: giocaCarta(stato, carta)
     */
    public void giocaCarta(int indiceInMano) {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        if (stato.getFaseCorrente() == FaseTurno.GIOCA_CARTE) {
            if (indiceInMano >= 0 && indiceInMano < giocatore.getMano().size()) {
                Carta carta = giocatore.getMano().get(indiceInMano);
                giocatore.giocaCarta(stato, carta);  // ⭐ FIX: aggiungo stato
                gameUI.aggiorna();
                System.out.println("▶️ Giocata carta: " + carta.getNome());
            }
        }
    }
    
    /**
     * Acquista una carta dal mercato
     * ⭐ CORRETTO: usa acquistaCarta() del giocatore e rifornisce mercato
     */
    public void acquistaCarta(int indiceInMercato) {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        if (stato.getFaseCorrente() == FaseTurno.ACQUISTA_CARTE) {
            if (indiceInMercato >= 0 && indiceInMercato < stato.getMercato().size()) {
                Carta carta = stato.getMercato().get(indiceInMercato);
                if (giocatore.getGettone() >= carta.getCosto()) {
                    // ⭐ FIX: usa il metodo corretto del giocatore
                    giocatore.acquistaCarta(stato.getMercato(), carta);
                    giocatore.getScarti().aggiungiCarta(carta);
                    stato.getMercato().remove(carta);
                    stato.rifornisciMercato();  // ⭐ FIX: rifornisce il mercato
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
     * ⭐ CORRETTO: passa l'oggetto Malvagio a assegnaAttacco()
     */
    public void attaccaMalvagio(int indiceMalvagio) {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        if (stato.getFaseCorrente() == FaseTurno.ATTACCA) {
            if (indiceMalvagio >= 0 && indiceMalvagio < stato.getMalvagiAttivi().size()) {
                Malvagio malvagio = stato.getMalvagiAttivi().get(indiceMalvagio);  // ⭐ FIX: ottiene il Malvagio
                if (giocatore.getAttacco() > 0) {
                    stato.assegnaAttacco(malvagio, 1);  // ⭐ FIX: passa il Malvagio, non l'indice
                    giocatore.setAttacco(giocatore.getAttacco() - 1);  // ⭐ FIX: riduce attacco manualmente
                    
                    // ⭐ FIX: Se il malvagio è sconfitto, rivela un nuovo malvagio
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