package grafica;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;

import data.*;
import gioco.*;
import carte.*;
import grafica.components.GameBoard;
import grafica.input.GameInputHandler;
import grafica.ui.UIController;
import grafica.ui.DialogSystem;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Classe principale dell'applicazione Harry Potter: Hogwarts Battle
 * Versione completa con sistema di input grafico integrato
 */
public class GameApp extends GameApplication {
    
    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;
    private static final String GAME_TITLE = "Harry Potter: Hogwarts Battle";
    private static final String GAME_VERSION = "2.0";
    
    // Stato del gioco
    private StatoDiGioco statoDiGioco;
    private GameConfig gameConfig;
    private List<Giocatore> giocatori;
    
    // Gestore flusso di gioco
    private grafica.managers.GameFlowManager flowManager;
    
    // Componenti grafici
    private GameBoard gameBoard;
    private Pane uiRoot;
    
    // Gestori
    private GameInputHandler inputHandler;
    private UIController uiController;
    private DialogSystem dialogSystem;
    
    // UI Elements
    private Text titleText;
    private Text phaseText;
    private Text turnInfoText;
    
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WINDOW_WIDTH);
        settings.setHeight(WINDOW_HEIGHT);
        settings.setTitle(GAME_TITLE);
        settings.setVersion(GAME_VERSION);
        settings.setIntroEnabled(false);
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(false);
        settings.setManualResizeEnabled(false);
        settings.setPreserveResizeRatio(true);
        settings.setApplicationMode(com.almasb.fxgl.app.ApplicationMode.DEVELOPER);
    }
    
    @Override
    protected void initGame() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("   HARRY POTTER: HOGWARTS BATTLE - v" + GAME_VERSION);
        System.out.println("=".repeat(60));
        
        // Inizializzazione
        inizializzaFactory();
        
        // Inizializza il flow manager
        flowManager = grafica.managers.GameFlowManager.getInstance();
        
        // Carica configurazione anno 1 (default)
        int annoIniziale = 1; // Modificabile per test
        caricaConfigurazione(annoIniziale);
        flowManager.setAnnoCorrente(annoIniziale);
        
        // Imposta sfondo
        FXGL.getGameScene().setBackgroundColor(Color.rgb(15, 15, 30));
        
        System.out.println("=".repeat(60));
        System.out.println("   INIZIALIZZAZIONE COMPLETATA");
        System.out.println("=".repeat(60) + "\n");
    }
    
    private void inizializzaFactory() {
        System.out.println("\n📦 Inizializzazione Factory...");
        
        CardFactory.inizializza();
        VillainFactory.inizializza();
        LocationFactory.inizializza();
        HeroFactory.inizializza();
        DiceFactory.inizializza();
        HorcruxFactory.inizializza();
        ProficiencyFactory.inizializza();
        StarterPackLoader.inizializza();
        
        System.out.println("✓ Factory inizializzate");
    }
    
    private void caricaConfigurazione(int anno) {
        System.out.println("\n🎮 Caricamento Anno " + anno + "...");
        
        GameLoader loader = new GameLoader();
        gameConfig = loader.caricaConfigurazione(anno);
        
        System.out.println("\n📋 Configurazione Anno " + anno + ":");
        //System.out.println("  • Luogo: " + gameConfig.getLuogo().getNome());
        //System.out.println("  • Alleati: " + (gameConfig.getContieneAlleati() ? "Sì" : "No"));
        System.out.println("  • Competenze: " + (gameConfig.getContieneCompetenze() ? "Sì" : "No"));
        System.out.println("  • Horcrux: " + (gameConfig.getContieneHorcrux() ? "Sì" : "No"));
    }
    
    @Override
    protected void initInput() {
        System.out.println("\n⌨️  Inizializzazione input...");
        
        // ESC - Menu
        FXGL.onKeyDown(KeyCode.ESCAPE, () -> {
            if (dialogSystem != null && dialogSystem.isDialogVisible()) {
                dialogSystem.chiudiDialogo();
            } else {
                FXGL.getGameController().gotoMainMenu();
            }
        });
        
        // SPACE - Prossima fase
        FXGL.onKeyDown(KeyCode.SPACE, () -> {
            prossimaFase();
        });
        
        // N - Prossimo turno (salta tutte le fasi)
        FXGL.onKeyDown(KeyCode.N, () -> {
            prossimoTurno();
        });
        
        // D - Debug info
        FXGL.onKeyDown(KeyCode.D, () -> {
            stampaDebugInfo();
        });
        
        // R - Refresh/Aggiorna board
        FXGL.onKeyDown(KeyCode.R, () -> {
            aggiornaInterfaccia();
        });
        
        // H - Mostra aiuto
        FXGL.onKeyDown(KeyCode.H, () -> {
            mostraAiuto();
        });
        
        // Tasti 1-4 per cambiare fase manualmente
        FXGL.onKeyDown(KeyCode.DIGIT1, () -> cambiaFaseManuale(FaseTurno.ARTI_OSCURE));
        FXGL.onKeyDown(KeyCode.DIGIT2, () -> cambiaFaseManuale(FaseTurno.GIOCA_CARTE));
        FXGL.onKeyDown(KeyCode.DIGIT3, () -> cambiaFaseManuale(FaseTurno.ATTACCA));
        FXGL.onKeyDown(KeyCode.DIGIT4, () -> cambiaFaseManuale(FaseTurno.ACQUISTA_CARTE));
        
        System.out.println("✓ Input inizializzato");
    }
    
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("currentYear", 1);
        vars.put("currentPlayer", 0);
        vars.put("gamePhase", FaseTurno.ARTI_OSCURE.toString());
    }
    
    @Override
    protected void initUI() {
        System.out.println("\n🎨 Inizializzazione UI...");
        
        // Crea il pane root per tutti gli elementi UI
        uiRoot = new Pane();
        uiRoot.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        FXGL.addUINode(uiRoot);
        
        int anno = flowManager.getAnnoCorrente();
        
        // Verifica se serve selezione eroi (solo anno 1, prima volta)
        if (flowManager.serveSelezioneEroi(anno)) {
            System.out.println("👥 Mostrando selezione eroi...");
            mostraSchermataScelezioneEroi();
        }
        // Verifica se serve selezione competenze (solo anno 6, prima volta)
        else if (flowManager.serveSelezioneCompetenze(anno)) {
            System.out.println("⚡ Mostrando selezione competenze...");
            mostraSchermataSelezioneCompetenze();
        }
        // Altrimenti crea i giocatori e inizia
        else {
            System.out.println("🎮 Caricamento giocatori esistenti...");
            giocatori = flowManager.creaGiocatoriPerAnno(anno);
            iniziaGioco();
            
            // Mostra messaggio evoluzione se applicabile
            String messaggioEvoluzione = flowManager.getMessaggioEvoluzione(anno);
            if (messaggioEvoluzione != null) {
                // Mostra dopo un breve delay
                javafx.application.Platform.runLater(() -> {
                    dialogSystem.mostraInfo("Evoluzione!", messaggioEvoluzione);
                });
            }
        }
        
        System.out.println("✓ UI inizializzata");
    }
    
    /**
     * Mostra la schermata di selezione eroi (solo anno 1)
     */
    private void mostraSchermataScelezioneEroi() {
        // 1. Creiamo un contenitore temporaneo (array di 1 posto)
        grafica.screens.HeroSelectionScreen[] screenRef = new grafica.screens.HeroSelectionScreen[1];

        // 2. Creiamo la schermata e la mettiamo nell'array
        screenRef[0] = new grafica.screens.HeroSelectionScreen(
            gameConfig.getAnno(),
            4, 
            result -> {
                // Callback
                flowManager.setEroiScelti(result.eroiSelezionati);
                
                // CORREZIONE: Usiamo il riferimento dall'array invece della variabile locale
                uiRoot.getChildren().remove(screenRef[0]);
                
                giocatori = flowManager.creaGiocatoriPerAnno(flowManager.getAnnoCorrente());
                iniziaGioco();
            }
        );
        
        // 3. Aggiungiamo alla UI usando il riferimento nell'array
        uiRoot.getChildren().add(screenRef[0]);
    }
    
    /**
     * Mostra la schermata di selezione competenze (solo anno 6)
     */
    private void mostraSchermataSelezioneCompetenze() {
        // 1. Contenitore temporaneo
        grafica.screens.ProficiencySelectionScreen[] screenRef = new grafica.screens.ProficiencySelectionScreen[1];

        screenRef[0] = new grafica.screens.ProficiencySelectionScreen(
            flowManager.getEroiScelti(),
            competenze -> {
                // Callback
                flowManager.setCompetenzeScelte(competenze);
                
                // CORREZIONE: Rimozione tramite array
                uiRoot.getChildren().remove(screenRef[0]);
                
                giocatori = flowManager.creaGiocatoriPerAnno(flowManager.getAnnoCorrente());
                iniziaGioco();
            }
        );
        
        uiRoot.getChildren().add(screenRef[0]);
    }
    
    /**
     * Inizia il gioco dopo la selezione
     */
    private void iniziaGioco() {
        System.out.println("\n🎮 Inizializzazione gioco...");
        
        // Crea lo stato di gioco
        statoDiGioco = new StatoDiGioco(gameConfig, giocatori);
        
        // Inizializza i gestori
        gameBoard = new GameBoard(statoDiGioco);
        uiRoot.getChildren().add(gameBoard);
        
        uiController = new UIController(statoDiGioco, gameBoard, uiRoot);
        dialogSystem = new DialogSystem(uiRoot);
        inputHandler = new GameInputHandler(statoDiGioco, gameBoard, uiController);
        
        // Crea elementi UI sovrapposti
        creaInterfacciaSuperiore();
        creaInterfacciaInfoTurno();
        creaInterfacciaComandi();
        
        // Aggiorna tutto
        aggiornaInterfaccia();
        
        // Mostra messaggio di benvenuto
        uiController.mostraMessaggio("🎮 Benvenuto a Hogwarts Battle! Premi H per l'aiuto", 
                                    Color.GOLD, 5000);
        
        System.out.println("✓ Gioco inizializzato");
        System.out.println("\n🎮 GIOCO PRONTO! Buona fortuna!\n");
    }
    
    /**
     * Crea l'interfaccia superiore (titolo)
     */
    private void creaInterfacciaSuperiore() {
        // Titolo del gioco
        titleText = new Text(GAME_TITLE);
        titleText.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 32));
        titleText.setFill(Color.GOLD);
        titleText.setStroke(Color.DARKRED);
        titleText.setStrokeWidth(2);
        titleText.setTranslateX(50);
        titleText.setTranslateY(40);
        FXGL.addUINode(titleText);
    }
    
    /**
     * Crea l'interfaccia informazioni turno (top-right)
     */
    private void creaInterfacciaInfoTurno() {
        // Info fase corrente
        phaseText = new Text();
        phaseText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        phaseText.setFill(Color.CYAN);
        phaseText.setTranslateX(WINDOW_WIDTH - 450);
        phaseText.setTranslateY(40);
        FXGL.addUINode(phaseText);
        
        // Info turno
        turnInfoText = new Text();
        turnInfoText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        turnInfoText.setFill(Color.LIGHTGRAY);
        turnInfoText.setTranslateX(WINDOW_WIDTH - 450);
        turnInfoText.setTranslateY(65);
        FXGL.addUINode(turnInfoText);
    }
    
    /**
     * Crea l'interfaccia comandi (bottom-right)
     */
    private void creaInterfacciaComandi() {
        Text comandiText = new Text(
            "⌨️ COMANDI:\n" +
            "SPACE = Prossima Fase\n" +
            "N = Prossimo Turno\n" +
            "R = Aggiorna\n" +
            "D = Debug\n" +
            "H = Aiuto\n" +
            "ESC = Menu"
        );
        comandiText.setFont(Font.font("Consolas", FontWeight.NORMAL, 12));
        comandiText.setFill(Color.LIGHTGRAY);
        comandiText.setTranslateX(WINDOW_WIDTH - 200);
        comandiText.setTranslateY(WINDOW_HEIGHT - 150);
        FXGL.addUINode(comandiText);
    }
    
    @Override
    protected void initPhysics() {
        // Non necessario per gioco a turni
    }
    
    @Override
    protected void onUpdate(double tpf) {
        if (statoDiGioco != null) {
            // Aggiorna variabili di gioco
            FXGL.set("gamePhase", statoDiGioco.getFaseCorrente().toString());
            FXGL.set("currentPlayer", statoDiGioco.getGiocatoreCorrente());
            
            // Aggiorna testi UI
            aggiornaTestiInfo();
        }
    }
    
    /**
     * Aggiorna i testi informativi
     */
    private void aggiornaTestiInfo() {
        if (phaseText == null || turnInfoText == null) return;
        
        FaseTurno fase = statoDiGioco.getFaseCorrente();
        int playerIndex = statoDiGioco.getGiocatoreCorrente();
        Giocatore giocatore = statoDiGioco.getGiocatori().get(playerIndex);
        
        // Info fase
        String faseNome = getFaseNomeItaliano(fase);
        phaseText.setText("📍 FASE: " + faseNome);
        
        // Colore fase
        switch (fase) {
            case ARTI_OSCURE:
                phaseText.setFill(Color.DARKRED);
                break;
            case GIOCA_CARTE:
                phaseText.setFill(Color.LIGHTBLUE);
                break;
            case ATTACCA:
                phaseText.setFill(Color.ORANGE);
                break;
            case ACQUISTA_CARTE:
                phaseText.setFill(Color.LIGHTGREEN);
                break;
        }
        
        // Info turno
        turnInfoText.setText(String.format(
            "👤 %s | ❤️ %d | ⚔️ %d | ⚡ %d",
            giocatore.getEroe().getNome(),
            giocatore.getSalute(),
            giocatore.getAttacco(),
            giocatore.getGettone()
        ));
    }
    
    /**
     * Passa alla prossima fase
     */
    private void prossimaFase() {
        if (statoDiGioco == null) return;
        
        FaseTurno faseCorrente = statoDiGioco.getFaseCorrente();
        
        switch (faseCorrente) {
            case ARTI_OSCURE:
                // Esegui fase arti oscure
                inputHandler.cambiaFase(FaseTurno.GIOCA_CARTE);
                break;
                
            case GIOCA_CARTE:
                // Passa ad attacca
                inputHandler.cambiaFase(FaseTurno.ATTACCA);
                break;
                
            case ATTACCA:
                // Passa ad acquista
                inputHandler.cambiaFase(FaseTurno.ACQUISTA_CARTE);
                break;
                
            case ACQUISTA_CARTE:
                // Fine turno
                fineTurno();
                break;
        }
    }
    
    /**
     * Fine turno - passa al prossimo giocatore
     */
    private void fineTurno() {
        Giocatore giocatoreCorrente = statoDiGioco.getGiocatori().get(
            statoDiGioco.getGiocatoreCorrente()
        );
        
        // Esegui fine turno
        statoDiGioco.fineTurno();
        
        // Passa al prossimo giocatore
        int nextPlayer = (statoDiGioco.getGiocatoreCorrente() + 1) % 
                        statoDiGioco.getGiocatori().size();
        statoDiGioco.setGiocatoreCorrente(nextPlayer);
        
        // Nuova fase: Arti Oscure
        inputHandler.cambiaFase(FaseTurno.ARTI_OSCURE);
        
        Giocatore nuovoGiocatore = statoDiGioco.getGiocatori().get(nextPlayer);
        uiController.mostraNotifica(
            "🔄 Turno di " + nuovoGiocatore.getEroe().getNome(),
            Color.GOLD
        );
        
        System.out.println("\n🔄 Turno di " + nuovoGiocatore.getEroe().getNome());
    }
    
    /**
     * Salta al prossimo turno (salta tutte le fasi)
     */
    private void prossimoTurno() {
        dialogSystem.mostraConferma(
            "Prossimo Turno",
            "Vuoi saltare al prossimo turno? Tutte le fasi rimanenti saranno saltate.",
            () -> {
                fineTurno();
                uiController.mostraMessaggio("⏭️ Saltato al prossimo turno");
            },
            null
        );
    }
    
    /**
     * Cambia fase manualmente (con conferma)
     */
    private void cambiaFaseManuale(FaseTurno nuovaFase) {
        if (statoDiGioco.getFaseCorrente() == nuovaFase) {
            uiController.mostraMessaggio("Sei già in questa fase!");
            return;
        }
        
        dialogSystem.mostraConferma(
            "Cambio Fase",
            "Vuoi cambiare manualmente alla fase " + getFaseNomeItaliano(nuovaFase) + "?",
            () -> {
                inputHandler.cambiaFase(nuovaFase);
                uiController.mostraMessaggio("✓ Fase cambiata manualmente");
            },
            null
        );
    }
    
    /**
     * Aggiorna tutta l'interfaccia
     */
    private void aggiornaInterfaccia() {
        gameBoard.aggiornaBoard();
        inputHandler.aggiornaHandlers();
        uiController.mostraMessaggio("🔄 Interfaccia aggiornata", Color.LIGHTBLUE, 2000);
        
        System.out.println("🔄 Interfaccia aggiornata");
    }
    
    /**
     * Mostra la finestra di aiuto
     */
    private void mostraAiuto() {
        dialogSystem.mostraInfo(
            "❓ AIUTO - Come Giocare",
            "INTERAZIONI MOUSE:\n" +
            "• Click sinistro sulle carte = Azione principale\n" +
            "• Click destro sulle carte = Mostra dettagli\n" +
            "• Hover sulle carte = Anteprima\n\n" +
            "FASI DI GIOCO:\n" +
            "1. ARTI OSCURE: Pesca e risolvi evento\n" +
            "2. GIOCA CARTE: Gioca carte dalla mano\n" +
            "3. ATTACCA: Attacca i malvagi\n" +
            "4. ACQUISTA: Compra carte dal mercato\n\n" +
            "TASTI RAPIDI:\n" +
            "SPACE = Prossima fase\n" +
            "N = Prossimo turno\n" +
            "R = Aggiorna interfaccia\n" +
            "D = Debug info\n" +
            "H = Questo aiuto"
        );
    }
    
    /**
     * Stampa informazioni di debug
     */
    private void stampaDebugInfo() {
        if (statoDiGioco == null) {
            System.out.println("❌ Stato di gioco non inizializzato");
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("DEBUG INFO");
        System.out.println("=".repeat(60));
        
        System.out.println("\n🎮 STATO GENERALE:");
        System.out.println("  • Anno: " + statoDiGioco.getAnnoCorrente());
        System.out.println("  • Fase: " + statoDiGioco.getFaseCorrente());
        System.out.println("  • Giocatore corrente: " + (statoDiGioco.getGiocatoreCorrente() + 1));
        
        System.out.println("\n📚 MAZZI:");
        System.out.println("  • Negozio: " + statoDiGioco.getMazzoNegozio().size() + " carte");
        System.out.println("  • Arti Oscure: " + statoDiGioco.getMazzoArtiOscure().size() + " carte");
        System.out.println("  • Malvagi: " + statoDiGioco.getMazzoMalvagi().size() + " carte");
        
        System.out.println("\n🏪 MERCATO:");
        System.out.println("  • Carte disponibili: " + statoDiGioco.getMercato().size());
        for (int i = 0; i < statoDiGioco.getMercato().size(); i++) {
            Carta c = statoDiGioco.getMercato().get(i);
            if (c != null) {
                System.out.println("    " + (i+1) + ". " + c.getNome() + " (⚡" + c.getCosto() + ")");
            }
        }
        
        System.out.println("\n😈 MALVAGI ATTIVI:");
        System.out.println("  • Numero: " + statoDiGioco.getMalvagiAttivi().size());
        for (Malvagio m : statoDiGioco.getMalvagiAttivi()) {
            System.out.println("    • " + m.getNome() + " (❤️" + m.getVita() + " ⚔️" + m.getDanno() + ")");
        }
        
        System.out.println("\n🏰 LUOGO:");
        Luogo luogo = statoDiGioco.getLuogoAttuale();
        if (luogo != null) {
            System.out.println("  • Nome: " + luogo.getNome());
            System.out.println("  • Salute: " + luogo.getNumeroMarchiNeri() + "/" + luogo.getMarchiNeriMax());
        }
        
        System.out.println("\n👥 GIOCATORI:");
        for (int i = 0; i < statoDiGioco.getGiocatori().size(); i++) {
            Giocatore g = statoDiGioco.getGiocatori().get(i);
            String marker = (i == statoDiGioco.getGiocatoreCorrente()) ? "▶" : " ";
            System.out.println(marker + " " + (i+1) + ". " + g.getEroe().getNome());
            System.out.println("      ❤️" + g.getSalute() + 
                             " ⚔️" + g.getAttacco() + 
                             " ⚡" + g.getGettone());
            System.out.println("      ✋ Mano: " + g.getMano().size() + " carte");
            System.out.println("      📚 Mazzo: " + g.getMazzo().size() + " carte");
            System.out.println("      🗑️ Scarti: " + g.getScarti().size() + " carte");
        }
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        uiController.mostraNotifica("Debug info stampata in console", Color.CYAN);
    }
    
    /**
     * Ottiene il nome italiano della fase
     */
    private String getFaseNomeItaliano(FaseTurno fase) {
        switch (fase) {
            case ARTI_OSCURE: return "ARTI OSCURE";
            case GIOCA_CARTE: return "GIOCA CARTE";
            case ATTACCA: return "ATTACCA";
            case ACQUISTA_CARTE: return "ACQUISTA CARTE";
            default: return fase.toString();
        }
    }
    
    /**
     * Main entry point
     */
    public static void main(String[] args) {
        launch(args);
    }
}