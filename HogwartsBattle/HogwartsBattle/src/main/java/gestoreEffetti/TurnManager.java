package gioco;

import carte.ArteOscura;
import carte.Encounter;
import carte.Malvagio;
import gestoreEffetti.EsecutoreEffetti;
import gestoreEffetti.TipoEffetto;
import gioco.FaseTurno;
import gioco.StatoDiGioco;
import carte.Horcrux;

import java.util.List;

/**
 * TurnManager - Gestisce il flusso delle fasi del turno
 * VERSIONE AGGIORNATA con supporto Encounter
 */
public class TurnManager {
    
    private StatoDiGioco stato;
    private EncounterManager encounterManager;
    
    public TurnManager(StatoDiGioco stato) {
        this.stato = stato;
        this.encounterManager = new EncounterManager(stato);
    }
    
    public void iniziaTurno() {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 TURNO DI: " + giocatore.getEroe().getNome());
        System.out.println("=".repeat(60));
        
        stato.resetAttacchi();
        stato.resetTracciaggioTurno(); // Reset tracciamento per Encounter
        
        stato.setFaseCorrente(FaseTurno.ARTI_OSCURE);
        
        eseguiFaseAutomatica();
    }
    
    /**
     * Esegue la fase corrente automaticamente
     */
    public void eseguiFaseAutomatica() {
        FaseTurno fase = stato.getFaseCorrente();
        
        switch (fase) {
            case ARTI_OSCURE:
                eseguiFaseArtiOscure();
                break;
            case MALVAGI:
                eseguiFaseMalvagi();
                break;
            case HORCRUX:
                eseguiFaseHorcrux();
                break;
            case FINE_TURNO:
                eseguiFineTurno();
                break;
            default:
                System.out.println("⏸️ Fase " + fase + " - In attesa di input");
                break;
        }
        
        // ⭐ Aggiorna UI dopo ogni fase automatica
        aggiornaUI();
    }
    
    /**
     * Aggiorna l'interfaccia grafica
     */
    private void aggiornaUI() {
        try {
            if (grafica.GameController.getInstance() != null && 
                grafica.GameController.getInstance().getGameUI() != null) {
                
                javafx.application.Platform.runLater(() -> {
                    grafica.GameController.getInstance().getGameUI().aggiorna();
                });
            }
        } catch (Exception e) {
            System.err.println("⚠️ Errore aggiornamento UI: " + e.getMessage());
        }
    }
    
    /**
     * FASE 1: Rivela e applica carta Arti Oscure + Effetto Encounter
     */
    private void eseguiFaseArtiOscure() {
        System.out.println("\n🌑 === FASE ARTI OSCURE ===");
        
        ArteOscura arteOscura = stato.pescaArteOscura();
        
        if (arteOscura != null) {
            System.out.println("📜 Rivelata: " + arteOscura.getNome());
            System.out.println("   " + arteOscura.getDescrizione());
            
            Giocatore giocatoreAttivo = stato.getGiocatori().get(stato.getGiocatoreCorrente());
            arteOscura.applicaEffetto(stato, giocatoreAttivo);
            
            // Traccia l'evento risolto per gli Encounter
            stato.aggiungiEventoArtiOscureRisolto();
            
            System.out.println("✓ Effetto Arti Oscure applicato");
        } else {
            System.out.println("⚠️ Nessuna carta Arti Oscure disponibile");
        }

        // Risolvi effetto continuo dell'Encounter (se presente)
        if (stato.getEncounterCorrente() != null && !stato.getEncounterCorrente().isCompletato()) {
            encounterManager.risolviEffettoEncounter();
        }
        
        prossimaFase();
    }
    
    /**
     * FASE 2: Applica effetti dei malvagi attivi
     * ⭐ FIX: Ora applica VERAMENTE gli effetti
     */
    private void eseguiFaseMalvagi() {
        System.out.println("\n👹 === FASE MALVAGI ===");

        List<Malvagio> malvagi = stato.getMalvagiAttivi();

        if (malvagi.isEmpty()) {
            System.out.println("✓ Nessun malvagio attivo");
            if(!stato.getMazzoMalvagi().isEmpty()) {
            	stato.addMalvagioAttivo();
            }
            prossimaFase();
            return;
        }

        Giocatore giocatoreAttivo = stato.getGiocatori().get(stato.getGiocatoreCorrente());

        for (Malvagio malvagio : malvagi) {
            if(malvagio.getVitaRimasta() > 0) {
                System.out.println("⚔️ Applico effetto di: " + malvagio.getNome());
                
                // ⭐ Qui è dove VERAMENTE applichiamo l'effetto del malvagio
                malvagio.applicaEffetto(stato, giocatoreAttivo);
            }
        }

        prossimaFase();
    }
    
    /**
     * FASE 3: Applica effetti degli Horcrux attivi (Anno 7)
     */
    private void eseguiFaseHorcrux() {
        System.out.println("\n💀 === FASE HORCRUX ===");
        
        List<Horcrux> horcrux = stato.getHorcruxAttivi();
        
        if (horcrux.isEmpty()) {
            System.out.println("✓ Nessun Horcrux attivo");
            prossimaFase();
            return;
        }
        
        Giocatore giocatoreAttivo = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        for (Horcrux h : horcrux) {
            if (!h.horcruxDistrutto()) {
                System.out.println("📿 Applico effetto di: " + h.getNome());
                h.applicaEffetto(stato, giocatoreAttivo);
            }
        }
        
        prossimaFase();
    }
    
    /**
     * FASE FINALE: Pulizia e preparazione per il prossimo turno
     */
    private void eseguiFineTurno() {
        System.out.println("\n🏁 === FINE TURNO ===");
        
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        // 1. Scarta carte dalla mano
        giocatore.getMano().getCarte().clear();
        
        // 2. Verifica e applica effetti temporanei che devono finire
        stato.getGestoreEffetti().rimuoviEffettiTemporanei();
        
        // ⭐ NUOVO: Verifica completamento Encounter
        if (stato.getEncounterCorrente() != null && !stato.getEncounterCorrente().isCompletato()) {
            if (encounterManager.verificaCompletamento()) {
                System.out.println("🎉 Encounter completato: " + stato.getEncounterCorrente().getNome());
                // La reward può essere usata nel prossimo turno
            }
        }
        
        // 3. Pesca 5 carte
        if (giocatore.getMazzo().getCarte().isEmpty()) {
            giocatore.getMazzo().getCarte().addAll(giocatore.getScarti().getCarte());
            giocatore.getScarti().getCarte().clear();
        }
        
        int carteDaPescare = Math.min(5, giocatore.getMazzo().getCarte().size());
        for (int i = 0; i < carteDaPescare; i++) {
            giocatore.pescaCarta();
        }
        
        System.out.println("  🃏 Pescate " + carteDaPescare + " carte");
        
        // ⭐ Reset tracciamento per il prossimo turno
        stato.resetTracciaggioTurno();
        
        // 4. Prossimo giocatore
        int prossimoGiocatore = (stato.getGiocatoreCorrente() + 1) % stato.getGiocatori().size();
        stato.setGiocatoreCorrente(prossimoGiocatore);
        
        System.out.println("\n✓ Turno completato");
        
        iniziaTurno();
    }
    
    /**
     * Avanza alla fase successiva
     */
    public void prossimaFase() {
        FaseTurno faseCorrente = stato.getFaseCorrente();
        FaseTurno prossimaFase = null;
        
        switch (faseCorrente) {
            case ARTI_OSCURE:
                prossimaFase = FaseTurno.MALVAGI;
                break;
            case MALVAGI:
                prossimaFase = stato.isHasHorcruxes() ? FaseTurno.HORCRUX : FaseTurno.GIOCA_CARTE;
                break;
            case HORCRUX:
                prossimaFase = FaseTurno.GIOCA_CARTE;
                break;
            case GIOCA_CARTE:
                prossimaFase = FaseTurno.ATTACCA;
                break;
            case ATTACCA:
                stato.applicaAttacchi();
                prossimaFase = FaseTurno.ACQUISTA_CARTE;
                break;
            case ACQUISTA_CARTE:
                prossimaFase = FaseTurno.FINE_TURNO;
                break;
            case FINE_TURNO:
                return;
        }
        
        if (prossimaFase != null) {
            stato.setFaseCorrente(prossimaFase);
            System.out.println("\n➡️ Fase successiva: " + getNomeFase(prossimaFase));
            
            if (isFaseAutomatica(prossimaFase)) {
                eseguiFaseAutomatica();
            }
        }
    }
    
    private boolean isFaseAutomatica(FaseTurno fase) {
        return fase == FaseTurno.ARTI_OSCURE || 
               fase == FaseTurno.MALVAGI || 
               fase == FaseTurno.HORCRUX ||
               fase == FaseTurno.FINE_TURNO;
    }
    
    private String getNomeFase(FaseTurno fase) {
        switch (fase) {
            case ARTI_OSCURE: return "Arti Oscure";
            case MALVAGI: return "Malvagi";
            case HORCRUX: return "Horcrux";
            case GIOCA_CARTE: return "Gioca Carte";
            case ATTACCA: return "Attacca";
            case ACQUISTA_CARTE: return "Acquista Carte";
            case FINE_TURNO: return "Fine Turno";
            default: return fase.toString();
        }
    }
    
    public StatoDiGioco getStato() {
        return stato;
    }
    
    public EncounterManager getEncounterManager() {
        return encounterManager;
    }
}
