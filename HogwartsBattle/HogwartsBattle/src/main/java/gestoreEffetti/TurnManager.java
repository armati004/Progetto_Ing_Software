package gioco;

import carte.ArteOscura;
import carte.DarkArtsPotion;
import carte.Malvagio;
import carte.Horcrux;

import java.util.List;

/**
 * TurnManager - Gestisce il flusso delle fasi del turno
 * 
 * AGGIORNATO per espansione Charms & Potions:
 * - Fase RACCOLTA_INGREDIENTI (Pack 2+)
 * - Gestione Encounter (Pack 1-4)
 * - Gestione Dark Arts Potions ongoing (Pack 3+)
 */
public class TurnManager {
    
    private StatoDiGioco stato;
    
    public TurnManager(StatoDiGioco stato) {
        this.stato = stato;
    }
    
    public void iniziaTurno() {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 TURNO DI: " + giocatore.getEroe().getNome());
        System.out.println("=".repeat(60));
        
        // NUOVO: Risolvi effetti ongoing delle Dark Arts Potions all'inizio turno
        if (stato.isHasDarkArtsPotions() && stato.getDarkArtsPotionManager() != null) {
            stato.getDarkArtsPotionManager().risolviEffettiOngoing(giocatore);
        }
        
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
            
            // Se è una Dark Arts Potion, è già stata gestita in pescaArteOscura()
            if (!(arteOscura instanceof DarkArtsPotion)) {
                arteOscura.applicaEffetto(stato, giocatoreAttivo);
                System.out.println("✓ Effetto Arti Oscure applicato");
            }
            
            // NUOVO: Traccia evento per Encounter (Pack 1)
            if (stato.isHasEncounters() && stato.getEncounterManager() != null) {
                stato.getEncounterManager().aggiungiEventoArtiOscureRisolto();
            }
        } else {
            System.out.println("⚠️ Nessuna carta Arti Oscure disponibile");
        }

        // NUOVO: Risolvi effetto ongoing dell'Encounter (Pack 1)
        if (stato.isHasEncounters() && stato.getEncounterManager() != null) {
            stato.getEncounterManager().risolviEffettoOngoing();
        }
        
        prossimaFase();
    }
    
    /**
     * FASE 2: Applica effetti dei malvagi attivi
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
            if(!malvagio.isSconfitto()) {
                System.out.println("⚔️ Applico effetto di: " + malvagio.getNome());
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
        while (!giocatore.getMano().isEmpty()) {
            giocatore.scartaCarta(giocatore.getMano().get(0));
        }
        
        // 2. Reset risorse
        giocatore.setAttacco(0);
        giocatore.setGettone(0);
        
        // 3. Verifica e applica effetti temporanei che devono finire
        stato.getGestoreEffetti().fineTurno();
        
        // NUOVO: Verifica completamento Encounter (Pack 1)
        if (stato.isHasEncounters() && stato.getEncounterManager() != null) {
            stato.getEncounterManager().verificaCompletamentoEncounter();
        }
        
        // 4. Rimescola scarti nel mazzo se necessario
        if (giocatore.getMazzo().getCarte().isEmpty()) {
            giocatore.getMazzo().getCarte().addAll(giocatore.getScarti().getCarte());
            giocatore.getScarti().getCarte().clear();
            java.util.Collections.shuffle(giocatore.getMazzo().getCarte());
        }
        
        // 5. Pesca 5 carte
        int carteDaPescare = Math.min(5, giocatore.getMazzo().getCarte().size());
        for (int i = 0; i < carteDaPescare; i++) {
            giocatore.pescaCarta();
        }
        
        System.out.println("  🃏 Pescate " + carteDaPescare + " carte");
        
        // 6. Riempi mercato
        stato.rifornisciMercato();
        
        // 7. Pulisci lista alleati giocati
        stato.getAlleatiGiocatiInQuestoTurno().clear();
        
        // 8. Prossimo giocatore
        int prossimoGiocatore = (stato.getGiocatoreCorrente() + 1) % stato.getGiocatori().size();
        stato.setGiocatoreCorrente(prossimoGiocatore);
        
        System.out.println("\n✓ Turno completato");
        
        iniziaTurno();
    }
    
    /**
     * Avanza alla fase successiva.
     * AGGIORNATO: Include fase RACCOLTA_INGREDIENTI (Pack 2+)
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
                // NUOVO: Se ci sono pozioni, aggiungi fase raccolta ingredienti
                if (stato.isHasPotions()) {
                    prossimaFase = FaseTurno.RACCOLTA_INGREDIENTI;
                } else {
                    prossimaFase = FaseTurno.ATTACCA;
                }
                break;
                
            case RACCOLTA_INGREDIENTI:
                // Questa fase è gestita manualmente dal giocatore
                prossimaFase = FaseTurno.ATTACCA;
                break;
                
            case ATTACCA:
                applicaAttacchi();
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
    
    /**
     * Applica gli attacchi assegnati ai malvagi.
     * AGGIORNATO: Supporta sia attacco che influenza.
     */
    private void applicaAttacchi() {
        System.out.println("\n⚔️ === APPLICAZIONE ATTACCHI ===");
        
        for (java.util.Map.Entry<Malvagio, Integer> entry : stato.getAttacchiAssegnati().entrySet()) {
            Malvagio malvagio = entry.getKey();
            int attaccoAssegnato = entry.getValue();
            
            if (attaccoAssegnato > 0) {
                if (malvagio.accettaAttacco()) {
                    malvagio.aggiungiAttacco(attaccoAssegnato);
                    System.out.println("  ⚔️ " + malvagio.getNome() + ": " + malvagio.getProgressoSconfitta());
                } else {
                    System.out.println("  ⚠️ " + malvagio.getNome() + " non può essere attaccato con ⚔️!");
                }
                
                // Verifica sconfitta
                if (malvagio.isSconfitto()) {
                    System.out.println("  ✅ " + malvagio.getNome() + " SCONFITTO!");
                    Giocatore giocatoreAttivo = stato.getGiocatori().get(stato.getGiocatoreCorrente());
                    malvagio.defeat(stato, giocatoreAttivo);
                    
                    // Rimuovi malvagio e aggiungi nuovo
                    stato.getMalvagiAttivi().remove(malvagio);
                    if (!stato.getMazzoMalvagi().isEmpty()) {
                        stato.addMalvagioAttivo();
                    }
                }
            }
        }
        
        // Reset attacchi assegnati
        stato.getAttacchiAssegnati().clear();
    }
    
    /**
     * Verifica se una fase è automatica (non richiede input).
     */
    private boolean isFaseAutomatica(FaseTurno fase) {
        return fase == FaseTurno.ARTI_OSCURE || 
               fase == FaseTurno.MALVAGI || 
               fase == FaseTurno.HORCRUX ||
               fase == FaseTurno.FINE_TURNO;
    }
    
    /**
     * Ottiene il nome leggibile di una fase.
     */
    private String getNomeFase(FaseTurno fase) {
        switch (fase) {
            case ARTI_OSCURE: return "Arti Oscure";
            case MALVAGI: return "Malvagi";
            case HORCRUX: return "Horcrux";
            case GIOCA_CARTE: return "Gioca Carte";
            case RACCOLTA_INGREDIENTI: return "Raccolta Ingredienti";
            case ATTACCA: return "Attacca";
            case ACQUISTA_CARTE: return "Acquista Carte";
            case FINE_TURNO: return "Fine Turno";
            default: return fase.toString();
        }
    }
    
    // ----------------------------------------------------------------
    // METODI PUBBLICI
    // ----------------------------------------------------------------
    
    public StatoDiGioco getStato() {
        return stato;
    }
}
