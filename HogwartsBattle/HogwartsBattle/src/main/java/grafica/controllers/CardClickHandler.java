package grafica.controllers;

import gioco.StatoDiGioco;
import grafica.GameController;
import gioco.Giocatore;
import gioco.FaseTurno;
import carte.Carta;

/**
 * CardClickHandler - Gestisce l'interazione tra i click sulle carte
 * e le azioni nel gioco
 * 
 * ResponsabilitÃ :
 * - Determinare quale azione eseguire in base alla fase e al tipo di carta
 * - Validare se l'azione Ã¨ legale
 * - Delegare l'esecuzione al GameController
 */
public class CardClickHandler {
    
    private GameController gameController;
    private StatoDiGioco stato;
    
    public CardClickHandler(GameController gameController) {
        this.gameController = gameController;
        this.stato = gameController.getStato();
    }
    
    /**
     * Gestisce il click su una carta dalla mano del giocatore
     * 
     * @param carta La carta cliccata
     * @param indiceInMano L'indice della carta nella mano del giocatore
     */
    public void onCartaManoClicked(Carta carta, int indiceInMano) {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        FaseTurno faseAttuale = stato.getFaseCorrente();
        
        System.out.println("ðŸŽ¯ Click su carta: " + carta.getNome() + " (indice: " + indiceInMano + ")");
        System.out.println("ðŸ“ Fase corrente: " + faseAttuale);
        
        // Valida se Ã¨ possibile giocare carte in questa fase
        if (!puoGiocareCartaQuiFase(faseAttuale)) {
            System.out.println("âŒ Non puoi giocare carte nella fase " + faseAttuale);
            return;
        }
        
        // Valida se la carta Ã¨ ancora in mano
        if (indiceInMano < 0 || indiceInMano >= giocatore.getMano().size()) {
            System.out.println("âŒ Indice carta non valido");
            return;
        }
        
        // Gioca la carta tramite il controller
        gameController.giocaCarta(indiceInMano);
    }
    
    /**
     * Gestisce il click su una carta dal mercato
     * 
     * @param carta La carta nel mercato
     * @param indiceInMercato L'indice della carta nel mercato
     */
    public void onCartaMercatoClicked(Carta carta, int indiceInMercato) {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        FaseTurno faseAttuale = stato.getFaseCorrente();
        
        System.out.println("ðŸŽ¯ Click su carta mercato: " + carta.getNome() + 
                         " (costo: " + carta.getCosto() + ")");
        System.out.println("ðŸ“ Fase corrente: " + faseAttuale);
        
        // Valida se Ã¨ la fase di acquisto
        if (faseAttuale != FaseTurno.ACQUISTA_CARTE) {
            System.out.println("âŒ Non puoi acquistare carte nella fase " + faseAttuale);
            return;
        }
        
        // Valida l'indice
        if (indiceInMercato < 0 || indiceInMercato >= stato.getMercato().size()) {
            System.out.println("âŒ Indice carta mercato non valido");
            return;
        }
        
        // Valida l'influenza
        if (giocatore.getGettone() < carta.getCosto()) {
            System.out.println("âŒ Influenza insufficiente. Serve: " + carta.getCosto() + 
                             ", Hai: " + giocatore.getGettone());
            return;
        }
        
        // Acquista la carta tramite il controller
        gameController.acquistaCarta(indiceInMercato);
    }
    
    /**
     * Gestisce il click su un malvagio
     * 
     * @param indiceMalvagio L'indice del malvagio nella lista dei malvagi attivi
     */
    public void onMalvagioClicked(int indiceMalvagio) {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        FaseTurno faseAttuale = stato.getFaseCorrente();
        
        System.out.println("ðŸŽ¯ Click su malvagio (indice: " + indiceMalvagio + ")");
        System.out.println("ðŸ“ Fase corrente: " + faseAttuale);
        
        // Valida se Ã¨ la fase di attacco
        if (faseAttuale != FaseTurno.ATTACCA) {
            System.out.println("âŒ Non puoi attaccare nella fase " + faseAttuale);
            return;
        }
        
        // Valida se il giocatore ha segnalini attacco
        if (giocatore.getAttacco() <= 0) {
            System.out.println("âŒ Non hai segnalini attacco disponibili");
            return;
        }
        
        // Valida l'indice del malvagio
        if (indiceMalvagio < 0 || indiceMalvagio >= stato.getMalvagiAttivi().size()) {
            System.out.println("âŒ Indice malvagio non valido");
            return;
        }
        
        // Attacca il malvagio tramite il controller
        gameController.attaccaMalvagio(indiceMalvagio);
    }
    
    /**
     * Verifica se Ã¨ possibile giocare carte nella fase attuale
     */
    private boolean puoGiocareCartaQuiFase(FaseTurno fase) {
        return (fase == FaseTurno.GIOCA_CARTE || fase == FaseTurno.ATTACCA);
    }
    
    /**
     * Ottiene la descrizione dell'azione che si puÃ² fare con questa carta
     * in base alla fase attuale
     */
    public String getDescrzioneAzione(Carta carta, FaseTurno faseAttuale) {
        switch (faseAttuale) {
            case GIOCA_CARTE:
                return "Click per giocare: " + carta.getNome();
            case ATTACCA:
                return "Seleziona un malvagio da attaccare (disponibili: " +
                       stato.getGiocatori().get(stato.getGiocatoreCorrente()).getAttacco() + 
                       " attacchi)";
            case ACQUISTA_CARTE:
                return "Click per acquistare: " + carta.getNome() + 
                       " (Costo: " + carta.getCosto() + " influenza)";
            default:
                return "Non puoi fare azioni nella fase " + faseAttuale;
        }
    }
    
    /**
     * Valida un'intera azione di click e restituisce un messaggio
     * @return true se l'azione Ã¨ valida, false altrimenti
     */
    public boolean validaAzione(Carta carta, int indice, String tipoCarta) {
        FaseTurno faseAttuale = stato.getFaseCorrente();
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        if ("mano".equals(tipoCarta)) {
            if (faseAttuale != FaseTurno.GIOCA_CARTE) {
                return false;
            }
            return indice >= 0 && indice < giocatore.getMano().size();
        } else if ("mercato".equals(tipoCarta)) {
            if (faseAttuale != FaseTurno.ACQUISTA_CARTE) {
                return false;
            }
            if (indice < 0 || indice >= stato.getMercato().size()) {
                return false;
            }
            return giocatore.getGettone() >= carta.getCosto();
        } else if ("malvagio".equals(tipoCarta)) {
            if (faseAttuale != FaseTurno.ATTACCA) {
                return false;
            }
            if (indice < 0 || indice >= stato.getMalvagiAttivi().size()) {
                return false;
            }
            return giocatore.getAttacco() > 0;
        }
        
        return false;
    }
}