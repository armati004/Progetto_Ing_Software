package gestoreEffetti;

import java.util.LinkedList;
import carte.Encounter;
import gioco.StatoDiGioco;

public class EncounterManager {
    
    private StatoDiGioco stato;
    private LinkedList<Encounter> mazzoEncounter;
    private Encounter encounterCorrente;
    
    private int eventiArtiOscureRisolti;
    private int carteGiocateQuestoTurno;
    private int influenzaAcquistataQuestoTurno;
    private int attaccoAcquistatoQuestoTurno;
    
    public EncounterManager(StatoDiGioco stato) {
        this.stato = stato;
        this.mazzoEncounter = new LinkedList<>();
        this.encounterCorrente = null;
        this.eventiArtiOscureRisolti = 0;
        this.carteGiocateQuestoTurno = 0;
        this.influenzaAcquistataQuestoTurno = 0;
        this.attaccoAcquistatoQuestoTurno = 0;
    }
    
    public void inizializza() {
        if (!mazzoEncounter.isEmpty()) {
            encounterCorrente = mazzoEncounter.pop();
        }
    }
    
    public void aggiungiEncounterAlMazzo(String id) {
        // Da implementare con EncounterFactory
    }
    
    public void risolviEffettoOngoing() {
        if (encounterCorrente != null && !encounterCorrente.isCompletato()) {
            encounterCorrente.applicaEffettoContinuo(stato, stato.getGiocatori().get(stato.getGiocatoreCorrente()));
        }
    }
    
    public void aggiungiEventoArtiOscureRisolto() {
        eventiArtiOscureRisolti++;
    }
    
    public void aggiungiCartaGiocata() {
        carteGiocateQuestoTurno++;
    }
    
    public void aggiungiInfluenzaAcquistata(int qta) {
        influenzaAcquistataQuestoTurno += qta;
    }
    
    public void aggiungiAttaccoAcquistato(int qta) {
        attaccoAcquistatoQuestoTurno += qta;
    }
    
    public boolean verificaCompletamentoEncounter() {
        if (encounterCorrente == null || encounterCorrente.isCompletato()) {
            return false;
        }
        
        boolean completato = false;
        
        switch (encounterCorrente.getTipoCondizione()) {
            case "GIOCA_CARTE_TIPO":
                completato = carteGiocateQuestoTurno >= encounterCorrente.getValoreRichiesto();
                break;
            case "ACQUISTA_INFLUENZA":
                completato = influenzaAcquistataQuestoTurno >= encounterCorrente.getValoreRichiesto();
                break;
            case "ACQUISTA_ATTACCO":
                completato = attaccoAcquistatoQuestoTurno >= encounterCorrente.getValoreRichiesto();
                break;
            case "GIOCA_CARTE_VALORE":
                completato = carteGiocateQuestoTurno >= encounterCorrente.getValoreRichiesto();
                break;
        }
        
        if (completato) {
            encounterCorrente.setCompletato(true);
            return true;
        }
        
        return false;
    }
    
    public void resetContatori() {
        eventiArtiOscureRisolti = 0;
        carteGiocateQuestoTurno = 0;
        influenzaAcquistataQuestoTurno = 0;
        attaccoAcquistatoQuestoTurno = 0;
    }
    
    public Encounter getEncounterCorrente() { return encounterCorrente; }
    public void setEncounterCorrente(Encounter encounter) { this.encounterCorrente = encounter; }
}
