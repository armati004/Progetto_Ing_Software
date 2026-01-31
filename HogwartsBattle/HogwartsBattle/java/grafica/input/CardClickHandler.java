package grafica.input;

import carte.Carta;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

/**
 * CardClickHandler - Handler specializzato per gestire le interazioni con le carte.
 * 
 * Fornisce un'interfaccia semplice per:
 * - Click sinistro (azione principale)
 * - Click destro (mostra dettagli)
 * - Doppio click (azione rapida)
 * - Hover (anteprima)
 * - Drag (riordino, future espansioni)
 */
public class CardClickHandler {
    
    private Carta carta;
    private CardContext context;
    
    // Callbacks
    private Consumer<Carta> onPrimaryClick;
    private Consumer<Carta> onSecondaryClick;
    private Consumer<Carta> onDoubleClick;
    private Consumer<Carta> onHoverEnter;
    private Consumer<Carta> onHoverExit;
    private Consumer<Carta> onDragStart;
    private Consumer<Carta> onDragEnd;
    
    // Stato
    private boolean isEnabled;
    private boolean isHovered;
    private boolean isSelected;
    private long lastClickTime;
    private static final long DOUBLE_CLICK_THRESHOLD = 300; // ms
    
    /**
     * Enum per il contesto in cui si trova la carta
     */
    public enum CardContext {
        HAND,        // Carta in mano del giocatore
        MARKET,      // Carta nel mercato
        VILLAIN,     // Carta malvagio
        DECK,        // Carta nel mazzo
        DISCARD,     // Carta negli scarti
        PLAYED,      // Carta giocata sul tavolo
        PREVIEW      // Carta in anteprima/popup
    }
    
    /**
     * Costruttore
     */
    public CardClickHandler(Carta carta, CardContext context) {
        this.carta = carta;
        this.context = context;
        this.isEnabled = true;
        this.isHovered = false;
        this.isSelected = false;
        this.lastClickTime = 0;
    }
    
    /**
     * Gestisce l'evento di click del mouse
     */
    public void handleMouseClick(MouseEvent event) {
        if (!isEnabled) return;
        
        long currentTime = System.currentTimeMillis();
        boolean isDoubleClick = (currentTime - lastClickTime) < DOUBLE_CLICK_THRESHOLD;
        lastClickTime = currentTime;
        
        if (event.getButton() == MouseButton.PRIMARY) {
            if (isDoubleClick && onDoubleClick != null) {
                onDoubleClick.accept(carta);
            } else if (onPrimaryClick != null) {
                onPrimaryClick.accept(carta);
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            if (onSecondaryClick != null) {
                onSecondaryClick.accept(carta);
            }
        }
        
        event.consume();
    }
    
    /**
     * Gestisce l'ingresso del mouse sulla carta
     */
    public void handleMouseEnter(MouseEvent event) {
        if (!isEnabled) return;
        
        isHovered = true;
        
        if (onHoverEnter != null) {
            onHoverEnter.accept(carta);
        }
    }
    
    /**
     * Gestisce l'uscita del mouse dalla carta
     */
    public void handleMouseExit(MouseEvent event) {
        if (!isEnabled) return;
        
        isHovered = false;
        
        if (onHoverExit != null) {
            onHoverExit.accept(carta);
        }
    }
    
    /**
     * Gestisce l'inizio del drag
     */
    public void handleDragStart(MouseEvent event) {
        if (!isEnabled) return;
        
        if (onDragStart != null) {
            onDragStart.accept(carta);
        }
    }
    
    /**
     * Gestisce la fine del drag
     */
    public void handleDragEnd(MouseEvent event) {
        if (!isEnabled) return;
        
        if (onDragEnd != null) {
            onDragEnd.accept(carta);
        }
    }
    
    // ========================================================================
    // BUILDER PATTERN per configurare i callback
    // ========================================================================
    
    public CardClickHandler onPrimaryClick(Consumer<Carta> callback) {
        this.onPrimaryClick = callback;
        return this;
    }
    
    public CardClickHandler onSecondaryClick(Consumer<Carta> callback) {
        this.onSecondaryClick = callback;
        return this;
    }
    
    public CardClickHandler onDoubleClick(Consumer<Carta> callback) {
        this.onDoubleClick = callback;
        return this;
    }
    
    public CardClickHandler onHoverEnter(Consumer<Carta> callback) {
        this.onHoverEnter = callback;
        return this;
    }
    
    public CardClickHandler onHoverExit(Consumer<Carta> callback) {
        this.onHoverExit = callback;
        return this;
    }
    
    public CardClickHandler onDragStart(Consumer<Carta> callback) {
        this.onDragStart = callback;
        return this;
    }
    
    public CardClickHandler onDragEnd(Consumer<Carta> callback) {
        this.onDragEnd = callback;
        return this;
    }
    
    // ========================================================================
    // UTILITY METHODS
    // ========================================================================
    
    /**
     * Crea un handler con comportamento di default per il contesto
     */
    public static CardClickHandler createDefault(Carta carta, CardContext context, 
                                                 Consumer<Carta> primaryAction) {
        CardClickHandler handler = new CardClickHandler(carta, context);
        
        // Azione principale (contesto-dipendente)
        handler.onPrimaryClick(primaryAction);
        
        // Click destro mostra sempre i dettagli (da implementare)
        handler.onSecondaryClick(c -> {
            System.out.println("📋 Dettagli carta: " + c.getNome());
            // Qui si aprirà un popup con i dettagli
        });
        
        // Hover mostra anteprima
        handler.onHoverEnter(c -> {
            System.out.println("👁️ Hover: " + c.getNome());
        });
        
        return handler;
    }
    
    /**
     * Crea un handler per una carta in mano
     */
    public static CardClickHandler forHandCard(Carta carta, Consumer<Carta> onPlay) {
        return createDefault(carta, CardContext.HAND, onPlay)
            .onDoubleClick(onPlay); // Doppio click = gioca subito
    }
    
    /**
     * Crea un handler per una carta del mercato
     */
    public static CardClickHandler forMarketCard(Carta carta, Consumer<Carta> onBuy) {
        return createDefault(carta, CardContext.MARKET, onBuy);
    }
    
    /**
     * Crea un handler per un malvagio
     */
    public static CardClickHandler forVillain(Carta carta, Consumer<Carta> onAttack) {
        return createDefault(carta, CardContext.VILLAIN, onAttack);
    }
    
    /**
     * Abilita/disabilita l'handler
     */
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }
    
    /**
     * Imposta lo stato di selezione
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
    
    /**
     * Resetta lo stato dell'handler
     */
    public void reset() {
        isHovered = false;
        isSelected = false;
        lastClickTime = 0;
    }
    
    // Getters
    
    public Carta getCarta() {
        return carta;
    }
    
    public CardContext getContext() {
        return context;
    }
    
    public boolean isEnabled() {
        return isEnabled;
    }
    
    public boolean isHovered() {
        return isHovered;
    }
    
    public boolean isSelected() {
        return isSelected;
    }
}