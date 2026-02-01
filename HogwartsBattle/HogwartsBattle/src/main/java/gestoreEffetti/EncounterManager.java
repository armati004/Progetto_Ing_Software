package gestoreEffetti;

import java.util.LinkedList;
import carte.Encounter;
import gioco.StatoDiGioco;

/**
 * Gestisce il sistema degli Encounter nel gioco.
 * Gli Encounter sono sfide secondarie che si sovrappongono ai malvagi principali.
 * 
 * Sistema Encounter - Pack 1-4
 */
public class EncounterManager {
    
    private StatoDiGioco stato;
    private LinkedList<Encounter> mazzoEncounter;
    private Encounter encounterCorrente;
    
    // Contatori per verificare condizioni di completamento
    private int eventiArtiOscureRisolti;
    private int carteGiocateQuestoTurno;
    private int influenzaAcquistataQuestoTurno;
    private int attaccoAcquistatoQuestoTurno;
    
    /**
     * Costruttore del manager degli Encounter.
     * 
     * @param stato Stato di gioco corrente
     */
    public EncounterManager(StatoDiGioco stato) {
        this.stato = stato;
        this.mazzoEncounter = new LinkedList<>();
        this.encounterCorrente = null;
        this.eventiArtiOscureRisolti = 0;
        this.carteGiocateQuestoTurno = 0;
        this.influenzaAcquistataQuestoTurno = 0;
        this.attaccoAcquistatoQuestoTurno = 0;
    }
    
    /**
     * Inizializza il sistema Encounter rivelando il primo.
     */
    public void inizializza() {
        if (!mazzoEncounter.isEmpty()) {
            encounterCorrente = mazzoEncounter.pop();
            System.out.println("🎯 Encounter rivelato: " + encounterCorrente.getNome());
        }
    }
    
    /**
     * Aggiunge un Encounter al mazzo.
     * 
     * @param id ID dell'encounter da aggiungere
     */
    public void aggiungiEncounterAlMazzo(String id) {
        Encounter enc = data.EncounterFactory.getEncounterById(id);
        if (enc != null) {
            mazzoEncounter.add(enc);
        }
    }
    
    /**
     * Risolve l'effetto ongoing dell'Encounter corrente.
     * Chiamato all'inizio di ogni turno durante la fase Arti Oscure.
     */
    public void risolviEffettoOngoing() {
        if (encounterCorrente != null && !encounterCorrente.isCompletato()) {
            encounterCorrente.applicaEffettoContinuo(stato, 
                stato.getGiocatori().get(stato.getGiocatoreCorrente()));
        }
    }
    
    /**
     * Incrementa il contatore di eventi Arti Oscure risolti.
     */
    public void aggiungiEventoArtiOscureRisolto() {
        eventiArtiOscureRisolti++;
    }
    
    /**
     * Incrementa il contatore di carte giocate questo turno.
     */
    public void aggiungiCartaGiocata() {
        carteGiocateQuestoTurno++;
    }
    
    /**
     * Incrementa il contatore di influenza acquisita questo turno.
     * 
     * @param qta Quantità di influenza acquisita
     */
    public void aggiungiInfluenzaAcquistata(int qta) {
        influenzaAcquistataQuestoTurno += qta;
    }
    
    /**
     * Incrementa il contatore di attacco acquisito questo turno.
     * 
     * @param qta Quantità di attacco acquisito
     */
    public void aggiungiAttaccoAcquistato(int qta) {
        attaccoAcquistatoQuestoTurno += qta;
    }
    
    /**
     * Verifica se l'Encounter corrente è stato completato.
     * Controlla le condizioni specifiche dell'Encounter.
     * 
     * @return true se l'Encounter è stato completato in questo turno
     */
    public boolean verificaCompletamentoEncounter() {
        if (encounterCorrente == null || encounterCorrente.isCompletato()) {
            return false;
        }
        
        boolean completato = false;
        
        switch (encounterCorrente.getTipoCondizione()) {
            case GIOCA_CARTE_TIPO:
                completato = carteGiocateQuestoTurno >= encounterCorrente.getValoreRichiesto();
                break;
                
            case ACQUISTA_INFLUENZA:
                completato = influenzaAcquistataQuestoTurno >= encounterCorrente.getValoreRichiesto();
                break;
                
            case ACQUISTA_ATTACCO:
                completato = attaccoAcquistatoQuestoTurno >= encounterCorrente.getValoreRichiesto();
                break;
                
            case GIOCA_CARTE_VALORE:
                completato = carteGiocateQuestoTurno >= encounterCorrente.getValoreRichiesto();
                break;
                
            // Altri tipi di condizione possono essere aggiunti qui
            default:
                break;
        }
        
        if (completato) {
            encounterCorrente.setCompletato(true);
            System.out.println("✅ Encounter completato: " + encounterCorrente.getNome());
            
            // Applica reward
            encounterCorrente.applicaReward(stato, stato.getGiocatori().get(stato.getGiocatoreCorrente()));
            
            // Rivela prossimo encounter se disponibile
            if (!mazzoEncounter.isEmpty()) {
                encounterCorrente = mazzoEncounter.pop();
                System.out.println("🎯 Nuovo Encounter rivelato: " + encounterCorrente.getNome());
            } else {
                encounterCorrente = null;
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Reset dei contatori a fine turno.
     * Chiamato dal TurnManager.
     */
    public void resetContatori() {
        eventiArtiOscureRisolti = 0;
        carteGiocateQuestoTurno = 0;
        influenzaAcquistataQuestoTurno = 0;
        attaccoAcquistatoQuestoTurno = 0;
    }
    
    // =============== GETTERS E SETTERS ===============
    
    public Encounter getEncounterCorrente() {
        return encounterCorrente;
    }
    
    public void setEncounterCorrente(Encounter encounter) {
        this.encounterCorrente = encounter;
    }
    
    public LinkedList<Encounter> getMazzoEncounter() {
        return mazzoEncounter;
    }
    
    public int getEventiArtiOscureRisolti() {
        return eventiArtiOscureRisolti;
    }
    
    public int getCarteGiocateQuestoTurno() {
        return carteGiocateQuestoTurno;
    }
    
    public int getInfluenzaAcquistataQuestoTurno() {
        return influenzaAcquistataQuestoTurno;
    }
    
    public int getAttaccoAcquistatoQuestoTurno() {
        return attaccoAcquistatoQuestoTurno;
    }
}
