package grafica.input;

import carte.*;
import gioco.*;
import grafica.components.*;
import grafica.ui.UIController;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * GameInputHandler - Gestore centrale degli input grafici del gioco.
 * 
 * Gestisce:
 * - Click sulle carte del mercato (acquisto)
 * - Click sulle carte in mano (giocare)
 * - Click sui malvagi (attacco)
 * - Hover sulle carte (dettagli)
 * - Selezioni multiple per effetti speciali
 * 
 * Coordina le azioni tra UI e logica di gioco.
 */
public class GameInputHandler {
    
    private StatoDiGioco statoDiGioco;
    private GameBoard gameBoard;
    private UIController uiController;
    
    // Modalità di interazione
    private InteractionMode currentMode;
    
    // Selezione multipla
    private boolean inMultiSelectMode;
    private List<Carta> selectedCards;
    private int requiredSelections;
    private Consumer<List<Carta>> multiSelectCallback;
    
    // Carte selezionate per azioni
    private Carta selectedCard;
    private Malvagio selectedVillain;
    
    // Stato hover
    private CardView hoveredCard;
    
    /**
     * Enum per le modalità di interazione
     */
    public enum InteractionMode {
        NORMAL,          // Modalità normale (click standard)
        PLAY_CARD,       // Gioca carta dalla mano
        ATTACK_VILLAIN,  // Attacca un malvagio
        BUY_CARD,        // Acquista dal mercato
        SELECT_CARDS,    // Selezione multipla carte
        WAITING          // In attesa (animazioni, effetti)
    }
    
    /**
     * Costruttore
     */
    public GameInputHandler(StatoDiGioco statoDiGioco, GameBoard gameBoard, UIController uiController) {
        this.statoDiGioco = statoDiGioco;
        this.gameBoard = gameBoard;
        this.uiController = uiController;
        
        this.currentMode = InteractionMode.NORMAL;
        this.inMultiSelectMode = false;
        this.selectedCards = new ArrayList<>();
        
        inizializzaHandlers();
    }
    
    /**
     * Inizializza gli handler per tutti i componenti interattivi
     */
    private void inizializzaHandlers() {
        System.out.println("🎮 Inizializzazione Input Handlers...");
        
        // Handlers per le carte del mercato
        setupMarketHandlers();
        
        // Handlers per i malvagi
        setupVillainHandlers();
        
        System.out.println("✓ Input Handlers pronti");
    }
    
    /**
     * Setup handlers per le carte del mercato
     */
    private void setupMarketHandlers() {
        List<CardView> marketCards = gameBoard.getMarketCards();
        
        for (int i = 0; i < marketCards.size(); i++) {
            final int index = i;
            CardView cardView = marketCards.get(i);
            
            // Click
            cardView.setOnMouseClicked(event -> handleMarketCardClick(index, event));
            
            // Hover
            cardView.setOnMouseEntered(event -> handleCardHoverEnter(cardView));
            cardView.setOnMouseExited(event -> handleCardHoverExit(cardView));
        }
    }
    
    /**
     * Setup handlers per i malvagi
     */
    private void setupVillainHandlers() {
        List<CardView> villainCards = gameBoard.getVillainCards();
        
        for (int i = 0; i < villainCards.size(); i++) {
            final int index = i;
            CardView cardView = villainCards.get(i);
            
            // Click
            cardView.setOnMouseClicked(event -> handleVillainClick(index, event));
            
            // Hover
            cardView.setOnMouseEntered(event -> handleCardHoverEnter(cardView));
            cardView.setOnMouseExited(event -> handleCardHoverExit(cardView));
        }
    }
    
    /**
     * Setup handlers per le carte in mano del giocatore
     */
    public void setupHandHandlers(List<CardView> handCards) {
        for (int i = 0; i < handCards.size(); i++) {
            final int index = i;
            CardView cardView = handCards.get(i);
            
            // Click
            cardView.setOnMouseClicked(event -> handleHandCardClick(index, event));
            
            // Hover
            cardView.setOnMouseEntered(event -> handleCardHoverEnter(cardView));
            cardView.setOnMouseExited(event -> handleCardHoverExit(cardView));
        }
    }
    
    // ========================================================================
    // HANDLERS SPECIFICI
    // ========================================================================
    
    /**
     * Gestisce il click su una carta del mercato
     */
    private void handleMarketCardClick(int index, MouseEvent event) {
        if (currentMode == InteractionMode.WAITING) return;
        
        // Verifica fase corretta
        if (statoDiGioco.getFaseCorrente() != FaseTurno.ACQUISTA_CARTE) {
            uiController.mostraMessaggio("Non puoi acquistare ora. Fase attuale: " + 
                                        getFaseNome(statoDiGioco.getFaseCorrente()));
            return;
        }
        
        // Ottieni la carta
        List<Carta> mercato = statoDiGioco.getMercato();
        if (index < 0 || index >= mercato.size()) {
            uiController.mostraErrore("Indice mercato non valido");
            return;
        }
        
        Carta carta = mercato.get(index);
        Giocatore giocatore = statoDiGioco.getGiocatori().get(statoDiGioco.getGiocatoreCorrente());
        
        // Verifica costo
        if (giocatore.getGettone() < carta.getCosto()) {
            uiController.mostraMessaggio(
                String.format("Influenza insufficiente! Costo: %d, Disponibile: %d", 
                            carta.getCosto(), giocatore.getGettone())
            );
            return;
        }
        
        // Click sinistro = acquista direttamente
        if (event.getButton() == MouseButton.PRIMARY) {
            eseguiAcquisto(index, carta, giocatore);
        }
        // Click destro = mostra dettagli
        else if (event.getButton() == MouseButton.SECONDARY) {
            uiController.mostraDettagliCarta(carta);
        }
    }
    
    /**
     * Gestisce il click su un malvagio
     */
    private void handleVillainClick(int index, MouseEvent event) {
        if (currentMode == InteractionMode.WAITING) return;
        
        // Verifica fase corretta
        if (statoDiGioco.getFaseCorrente() != FaseTurno.ATTACCA) {
            uiController.mostraMessaggio("Non puoi attaccare ora. Fase attuale: " + 
                                        getFaseNome(statoDiGioco.getFaseCorrente()));
            return;
        }
        
        // Ottieni il malvagio
        List<Malvagio> malvagi = statoDiGioco.getMalvagiAttivi();
        if (index < 0 || index >= malvagi.size()) {
            uiController.mostraErrore("Indice malvagio non valido");
            return;
        }
        
        Malvagio malvagio = malvagi.get(index);
        Giocatore giocatore = statoDiGioco.getGiocatori().get(statoDiGioco.getGiocatoreCorrente());
        
        // Verifica attacco disponibile
        if (giocatore.getAttacco() < 1) {
            uiController.mostraMessaggio("Non hai abbastanza Attacco!");
            return;
        }
        
        // Click sinistro = attacca
        if (event.getButton() == MouseButton.PRIMARY) {
            eseguiAttacco(malvagio, giocatore);
        }
        // Click destro = mostra dettagli
        else if (event.getButton() == MouseButton.SECONDARY) {
            uiController.mostraDettagliCarta(malvagio);
        }
    }
    
    /**
     * Gestisce il click su una carta in mano
     */
    private void handleHandCardClick(int index, MouseEvent event) {
        if (currentMode == InteractionMode.WAITING) return;
        
        Giocatore giocatore = statoDiGioco.getGiocatori().get(statoDiGioco.getGiocatoreCorrente());
        
        // Se siamo in modalità selezione multipla
        if (inMultiSelectMode) {
            handleMultiSelectClick(index, giocatore);
            return;
        }
        
        // Verifica fase corretta
        if (statoDiGioco.getFaseCorrente() != FaseTurno.GIOCA_CARTE) {
            uiController.mostraMessaggio("Non puoi giocare carte ora. Fase attuale: " + 
                                        getFaseNome(statoDiGioco.getFaseCorrente()));
            return;
        }
        
        // Ottieni la carta
        if (index < 0 || index >= giocatore.getMano().size()) {
            uiController.mostraErrore("Indice carta non valido");
            return;
        }
        
        Carta carta = giocatore.getMano().get(index);
        
        // Click sinistro = gioca la carta
        if (event.getButton() == MouseButton.PRIMARY) {
            eseguiGiocaCarta(carta, giocatore);
        }
        // Click destro = mostra dettagli
        else if (event.getButton() == MouseButton.SECONDARY) {
            uiController.mostraDettagliCarta(carta);
        }
    }
    
    /**
     * Gestisce hover in entrata su una carta
     */
    private void handleCardHoverEnter(CardView cardView) {
        hoveredCard = cardView;
        
        // Applica effetto visivo
        cardView.applicaEffettoHover();
        
        // Mostra preview ingrandita
        uiController.mostraPreviewCarta(cardView.getCarta());
    }
    
    /**
     * Gestisce hover in uscita da una carta
     */
    private void handleCardHoverExit(CardView cardView) {
        if (hoveredCard == cardView) {
            hoveredCard = null;
        }
        
        // Rimuovi effetto visivo
        cardView.rimuoviEffettoHover();
        
        // Nascondi preview
        uiController.nascondiPreviewCarta();
    }
    
    // ========================================================================
    // ESECUZIONE AZIONI
    // ========================================================================
    
    /**
     * Esegue l'acquisto di una carta dal mercato
     */
    private void eseguiAcquisto(int index, Carta carta, Giocatore giocatore) {
        try {
            // Sottrai influenza
            giocatore.setGettone(giocatore.getGettone() - carta.getCosto());
            
            // Acquista la carta
            giocatore.acquistaCarta(statoDiGioco.getMazzoNegozio(), carta);
            
            // Rimpiazza nel mercato
            statoDiGioco.rimpiazzaCartaMercato(index);
            
            // Feedback
            uiController.mostraMessaggio("✓ Acquistato: " + carta.getNome());
            uiController.riproduciSuono("acquisto");
            
            // Aggiorna UI
            gameBoard.aggiornaBoard();
            
            System.out.println("💰 Acquistato: " + carta.getNome());
            
        } catch (Exception e) {
            uiController.mostraErrore("Errore nell'acquisto: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Esegue l'attacco su un malvagio
     */
    private void eseguiAttacco(Malvagio malvagio, Giocatore giocatore) {
        try {
            // Sottrai 1 punto attacco
            giocatore.setAttacco(giocatore.getAttacco() - 1);
            
            // Infliggi danno
            malvagio.setDanno(malvagio.getDanno() + 1);
            
            String messaggio = "⚔️ Colpito " + malvagio.getNome() + "! Salute: " + malvagio.getDanno();
            
            // Verifica se il malvagio è stato sconfitto
            if (malvagio.getDanno() >= malvagio.getVita()) {
                statoDiGioco.sconfiggiMalvagio(malvagio);
                messaggio += " (SCONFITTO!)";
                uiController.riproduciSuono("vittoria");
            } else {
                uiController.riproduciSuono("attacco");
            }
            
            // Feedback
            uiController.mostraMessaggio(messaggio);
            
            // Aggiorna UI
            gameBoard.aggiornaBoard();
            
            System.out.println(messaggio);
            
        } catch (Exception e) {
            uiController.mostraErrore("Errore nell'attacco: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Esegue l'azione di giocare una carta dalla mano
     */
    private void eseguiGiocaCarta(Carta carta, Giocatore giocatore) {
        try {
            // Gioca la carta (esegue gli effetti)
            giocatore.giocaCarta(statoDiGioco, carta);
            
            // Feedback
            uiController.mostraMessaggio("✓ Giocata: " + carta.getNome());
            uiController.riproduciSuono("gioca_carta");
            
            // Aggiorna UI
            gameBoard.aggiornaBoard();
            
            System.out.println("🎴 Giocata: " + carta.getNome());
            
        } catch (Exception e) {
            uiController.mostraErrore("Errore nel giocare la carta: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ========================================================================
    // GESTIONE SELEZIONE MULTIPLA
    // ========================================================================
    
    /**
     * Avvia la modalità selezione multipla
     */
    public void avviaSelezioneMutipla(int numeroSelezioni, Consumer<List<Carta>> callback) {
        inMultiSelectMode = true;
        requiredSelections = numeroSelezioni;
        multiSelectCallback = callback;
        selectedCards.clear();
        
        uiController.mostraMessaggio("Seleziona " + numeroSelezioni + " carte");
        System.out.println("🎯 Modalità selezione multipla attivata: " + numeroSelezioni + " carte");
    }
    
    /**
     * Gestisce il click in modalità selezione multipla
     */
    private void handleMultiSelectClick(int index, Giocatore giocatore) {
        if (index < 0 || index >= giocatore.getMano().size()) return;
        
        Carta carta = giocatore.getMano().get(index);
        
        // Toggle selezione
        if (selectedCards.contains(carta)) {
            selectedCards.remove(carta);
            uiController.mostraMessaggio("Deselezionata: " + carta.getNome() + 
                                        " (" + selectedCards.size() + "/" + requiredSelections + ")");
        } else {
            if (selectedCards.size() < requiredSelections) {
                selectedCards.add(carta);
                uiController.mostraMessaggio("Selezionata: " + carta.getNome() + 
                                            " (" + selectedCards.size() + "/" + requiredSelections + ")");
                
                // Se abbiamo raggiunto il numero richiesto, completa
                if (selectedCards.size() == requiredSelections) {
                    completaSelezioneMutipla();
                }
            } else {
                uiController.mostraMessaggio("Hai già selezionato " + requiredSelections + " carte!");
            }
        }
        
        // Aggiorna visualizzazione selezione
        uiController.aggiornaSelezioneCarte(selectedCards);
    }
    
    /**
     * Completa la selezione multipla
     */
    private void completaSelezioneMutipla() {
        inMultiSelectMode = false;
        
        if (multiSelectCallback != null) {
            multiSelectCallback.accept(new ArrayList<>(selectedCards));
        }
        
        selectedCards.clear();
        uiController.mostraMessaggio("Selezione completata!");
        
        System.out.println("✓ Selezione multipla completata");
    }
    
    /**
     * Annulla la selezione multipla
     */
    public void annullaSelezioneMutipla() {
        inMultiSelectMode = false;
        selectedCards.clear();
        multiSelectCallback = null;
        
        uiController.mostraMessaggio("Selezione annullata");
    }
    
    // ========================================================================
    // CAMBIO FASE
    // ========================================================================
    
    /**
     * Gestisce il cambio di fase
     */
    public void cambiaFase(FaseTurno nuovaFase) {
        FaseTurno faseCorrente = statoDiGioco.getFaseCorrente();
        
        // Validazione cambio fase
        if (!isValidPhaseTransition(faseCorrente, nuovaFase)) {
            uiController.mostraErrore("Transizione di fase non valida!");
            return;
        }
        
        statoDiGioco.setFaseCorrente(nuovaFase);
        
        // Azioni specifiche per fase
        switch (nuovaFase) {
            case ARTI_OSCURE:
                eseguiFaseArtiOscure();
                break;
            case GIOCA_CARTE:
                uiController.mostraMessaggio("Fase: Gioca le tue carte");
                break;
            case ATTACCA:
                uiController.mostraMessaggio("Fase: Attacca i malvagi");
                break;
            case ACQUISTA_CARTE:
                uiController.mostraMessaggio("Fase: Acquista carte dal mercato");
                break;
        }
        
        gameBoard.aggiornaBoard();
    }
    
    /**
     * Esegue la fase Arti Oscure
     */
    private void eseguiFaseArtiOscure() {
        ArteOscura arteOscura = statoDiGioco.pescaArteOscura();
        
        if (arteOscura != null) {
            uiController.mostraArteOscura(arteOscura);
            System.out.println("💀 Arte Oscura: " + arteOscura.getNome());
        }
    }
    
    /**
     * Verifica se la transizione di fase è valida
     */
    private boolean isValidPhaseTransition(FaseTurno da, FaseTurno a) {
        // Sequenza normale delle fasi
        switch (da) {
            case ARTI_OSCURE:
                return a == FaseTurno.GIOCA_CARTE;
            case GIOCA_CARTE:
                return a == FaseTurno.ATTACCA;
            case ATTACCA:
                return a == FaseTurno.ACQUISTA_CARTE;
            case ACQUISTA_CARTE:
                return a == FaseTurno.ARTI_OSCURE; // Fine turno
            default:
                return false;
        }
    }
    
    // ========================================================================
    // UTILITY
    // ========================================================================
    
    /**
     * Ottiene il nome italiano della fase
     */
    private String getFaseNome(FaseTurno fase) {
        switch (fase) {
            case ARTI_OSCURE: return "Arti Oscure";
            case GIOCA_CARTE: return "Gioca Carte";
            case ATTACCA: return "Attacca";
            case ACQUISTA_CARTE: return "Acquista Carte";
            default: return fase.toString();
        }
    }
    
    /**
     * Aggiorna gli handlers dopo un refresh del board
     */
    public void aggiornaHandlers() {
        setupMarketHandlers();
        setupVillainHandlers();
    }
    
    // Getters/Setters
    
    public InteractionMode getCurrentMode() {
        return currentMode;
    }
    
    public void setCurrentMode(InteractionMode mode) {
        this.currentMode = mode;
    }
    
    public boolean isInMultiSelectMode() {
        return inMultiSelectMode;
    }
    
    public List<Carta> getSelectedCards() {
        return new ArrayList<>(selectedCards);
    }
}