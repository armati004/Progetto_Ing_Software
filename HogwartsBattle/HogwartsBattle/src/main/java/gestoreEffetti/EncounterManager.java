package gestoreEffetti;

import carte.Carta;
import carte.Encounter;
import carte.Eroe;
import carte.Incantesimo;
import carte.Oggetto;
import carte.Alleato;
import carte.TipoCondizioneEncounter;
import gestoreEffetti.Effetto;
import gestoreEffetti.EsecutoreEffetti;
import gioco.StatoDiGioco;

import java.util.List;

/**
 * Gestisce la logica degli Encounter: effetti continui, verifica completamento, reward.
 */
public class EncounterManager {
    private StatoDiGioco stato;

    public EncounterManager(StatoDiGioco stato) {
        this.stato = stato;
    }

    /**
     * Risolve l'effetto continuo dell'Encounter corrente.
     * Viene chiamato all'inizio del turno dopo aver rivelato le Arti Oscure.
     */
    public void risolviEffettoEncounter() {
        Encounter encounterCorrente = stato.getEncounterCorrente();
        
        if (encounterCorrente == null || encounterCorrente.isCompletato()) {
            return;
        }

        System.out.println("🎭 Risolvo effetto Encounter: " + encounterCorrente.getNome());
        
        // Esegui gli effetti continui dell'Encounter
        List<Effetto> effetti = encounterCorrente.getEffetti();
        
        if (effetti != null && !effetti.isEmpty()) {
            Giocatore giocatoreAttivo = stato.getGiocatori().get(stato.getGiocatoreCorrente());
            for (Effetto effetto : effetti) {
                EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatoreAttivo);
            }
        }
    }

    /**
     * Verifica se l'Encounter è stato completato in base alle azioni del turno.
     * Viene chiamato alla fine del turno.
     */
    public boolean verificaCompletamento() {
        Encounter encounterCorrente = stato.getEncounterCorrente();
        
        if (encounterCorrente == null || encounterCorrente.isCompletato()) {
            return false;
        }

        TipoCondizioneEncounter condizione = encounterCorrente.getTipoCondizione();
        int valoreRichiesto = encounterCorrente.getValoreRichiesto();
        Eroe eroeAttivo = stato.getGiocatoreAttivo().getEroe();

        boolean completato = false;

        switch (condizione) {
            case GIOCA_CARTE_TIPO:
                completato = verificaCarteTipoGiocate(valoreRichiesto, eroeAttivo);
                break;
                
            case ACQUISTA_INFLUENZA:
                completato = verificaInfluenzaAcquistata(valoreRichiesto);
                break;
                
            case GIOCA_CARTE_VALORE:
                completato = verificaCarteValoreGiocate(valoreRichiesto, eroeAttivo);
                break;
                
            case ACQUISTA_CARTA_VALORE:
                completato = verificaCartaValoreAcquistata(valoreRichiesto);
                break;
                
            case ASSEGNA_ATTACCO:
                completato = verificaAttaccoAssegnato(valoreRichiesto);
                break;
                
            case RISOLVI_EVENTI_ARTI_OSCURE:
                completato = verificaEventiArtiOscure(valoreRichiesto);
                break;
                
            case ACQUISTA_CARTE:
                completato = verificaCarteAcquistate(valoreRichiesto);
                break;
                
            case GIOCA_CARTE_DISPARI:
                completato = verificaCarteDispariGiocate(valoreRichiesto, eroeAttivo);
                break;
                
            case RIMUOVI_CONTROLLO:
                completato = verificaControlloRimosso(valoreRichiesto);
                break;
                
            default:
                System.out.println("⚠️ Condizione Encounter non implementata: " + condizione);
        }

        if (completato) {
            System.out.println("✅ ENCOUNTER COMPLETATO: " + encounterCorrente.getNome());
            encounterCorrente.setCompletato(true);
            encounterCorrente.setProgressoAttuale(valoreRichiesto);
            return true;
        }

        return false;
    }

    /**
     * Usa la reward dell'Encounter completato.
     */
    public void usaReward() {
        Encounter encounterCorrente = stato.getEncounterCorrente();
        
        if (encounterCorrente == null || !encounterCorrente.isCompletato() || encounterCorrente.isRewardUsata()) {
            System.out.println("❌ Non puoi usare la reward di questo Encounter.");
            return;
        }

        System.out.println("🎁 Uso reward: " + encounterCorrente.getDescrizioneRicompensa());
        
        // Esegui gli effetti della reward
        List<Effetto> reward = encounterCorrente.getReward();
        Giocatore giocatoreAttivo = stato.getGiocatoreAttivo();
        
        if (reward != null && !reward.isEmpty()) {
            for (Effetto effetto : reward) {
                EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatoreAttivo);
            }
        }
        
        encounterCorrente.setRewardUsata(true);
        
        // Se la reward è stata usata, passa al prossimo Encounter
        passaProssimoEncounter();
    }

    /**
     * Passa al prossimo Encounter della pila.
     */
    private void passaProssimoEncounter() {
        List<Encounter> pilaEncounter = stato.getPilaEncounter();
        
        if (pilaEncounter != null && !pilaEncounter.isEmpty()) {
            Encounter prossimo = pilaEncounter.remove(0);
            stato.setEncounterCorrente(prossimo);
            System.out.println("📋 Nuovo Encounter rivelato: " + prossimo.getNome());
        } else {
            stato.setEncounterCorrente(null);
            System.out.println("🎉 Tutti gli Encounter sono stati completati!");
        }
    }

    // ====== METODI DI VERIFICA CONDIZIONI ======

    private boolean verificaCarteTipoGiocate(int numero, Eroe eroe) {
        // Conta quante carte Items sono state giocate questo turno
        int count = 0;
        List<Carta> carteGiocate = stato.getCarteGiocateQuestoTurno();
        
        if (carteGiocate != null) {
            for (Carta carta : carteGiocate) {
                if (carta instanceof Oggetto) {
                    count++;
                }
            }
        }
        
        System.out.println("🔍 Items giocati: " + count + "/" + numero);
        return count >= numero;
    }

    private boolean verificaInfluenzaAcquistata(int valore) {
        int influenzaSpesa = stato.getInfluenzaSpesaQuestoTurno();
        System.out.println("🔍 Influenza acquisita: " + influenzaSpesa + "/" + valore);
        return influenzaSpesa >= valore;
    }

    private boolean verificaCarteValoreGiocate(int numero, Eroe eroe) {
        // Conta quante carte con valore >= 4 sono state giocate
        int count = 0;
        List<Carta> carteGiocate = stato.getCarteGiocateQuestoTurno();
        
        if (carteGiocate != null) {
            for (Carta carta : carteGiocate) {
                if (carta.getCosto() >= 4) {
                    count++;
                }
            }
        }
        
        System.out.println("🔍 Carte valore 4+: " + count + "/" + numero);
        return count >= numero;
    }

    private boolean verificaCartaValoreAcquistata(int valoreMinimo) {
        List<Carta> carteAcquistate = stato.getCarteAcquistateQuestoTurno();
        
        if (carteAcquistate != null) {
            for (Carta carta : carteAcquistate) {
                if (carta.getCosto() >= valoreMinimo) {
                    System.out.println("🔍 Acquisita carta valore " + valoreMinimo + "+: " + carta.getNome());
                    return true;
                }
            }
        }
        
        return false;
    }

    private boolean verificaAttaccoAssegnato(int valore) {
        int attaccoAssegnato = stato.getAttaccoAssegnatoQuestoTurno();
        System.out.println("🔍 Attacco assegnato: " + attaccoAssegnato + "/" + valore);
        return attaccoAssegnato >= valore;
    }

    private boolean verificaEventiArtiOscure(int numero) {
        int eventiRisolti = stato.getEventiArtiOscureRisoltiQuestoTurno();
        System.out.println("🔍 Eventi Arti Oscure risolti: " + eventiRisolti + "/" + numero);
        return eventiRisolti >= numero;
    }

    private boolean verificaCarteAcquistate(int numero) {
        List<Carta> carteAcquistate = stato.getCarteAcquistateQuestoTurno();
        int count = carteAcquistate != null ? carteAcquistate.size() : 0;
        System.out.println("🔍 Carte acquistate: " + count + "/" + numero);
        return count >= numero;
    }

    private boolean verificaCarteDispariGiocate(int numero, Eroe eroe) {
        int count = 0;
        List<Carta> carteGiocate = stato.getCarteGiocateQuestoTurno();
        
        if (carteGiocate != null) {
            for (Carta carta : carteGiocate) {
                if (carta.getCosto() % 2 == 1) {  // Dispari
                    count++;
                }
            }
        }
        
        System.out.println("🔍 Carte dispari giocate: " + count + "/" + numero);
        return count >= numero;
    }

    private boolean verificaControlloRimosso(int numero) {
        int controlloRimosso = stato.getControlloRimossoQuestoTurno();
        System.out.println("🔍 Controllo rimosso: " + controlloRimosso + "/" + numero);
        return controlloRimosso >= numero;
    }
}
