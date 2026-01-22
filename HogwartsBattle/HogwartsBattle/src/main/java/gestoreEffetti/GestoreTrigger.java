package gestoreEffetti;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import carte.Carta;
import gioco.Giocatore;
import gioco.StatoDiGioco;

public class GestoreTrigger {

    /**
     * Classe interna per memorizzare i dettagli di un trigger registrato.
     */
    private static class TriggerRegistrato {
        final Carta sorgente;
        final List<Effetto> effetti;
        final DurataEffetto durata;
        final boolean attivatoUnaVolta; 

        public TriggerRegistrato(Carta sorgente, List<Effetto> effetti, DurataEffetto durata, boolean attivatoUnaVolta) {
            this.sorgente = sorgente;
            this.effetti = effetti;
            this.durata = durata;
            this.attivatoUnaVolta = attivatoUnaVolta;
        }
    }

    private final Map<TipoTrigger, List<TriggerRegistrato>> registro = new EnumMap<>(TipoTrigger.class);

    /**
     * Registra un trigger nel sistema.
     * ECCO LA MODIFICA: Ora accetta 5 parametri!
     */
    public void registraTrigger(TipoTrigger tipo, List<Effetto> effetti, Carta sorgente, DurataEffetto durata, boolean attivatoUnaVolta) {
        TriggerRegistrato nuovoTrigger = new TriggerRegistrato(sorgente, effetti, durata, attivatoUnaVolta);
        this.registro.computeIfAbsent(tipo, _ -> new ArrayList<>()).add(nuovoTrigger);
    }

    /**
     * Rimuove i trigger associati a una carta (es. quando viene scartata).
     */
    public void rimuoviTrigger(Carta sorgente) {
        for (List<TriggerRegistrato> lista : registro.values()) {
            lista.removeIf(t -> t.sorgente.equals(sorgente));
        }
    }

    /**
     * Pulizia fine turno (rimuove trigger TEMPORANEO).
     */
    public void rimuoviTriggerFineTurno() {
        for (List<TriggerRegistrato> lista : registro.values()) {
            lista.removeIf(t -> t.durata == DurataEffetto.TEMPORANEO);
        }
    }

    /**
     * ATTIVAZIONE TRIGGER (Versione Semplice)
     */
    public void attivaTrigger(TipoTrigger tipo, StatoDiGioco stato, Giocatore giocatore) {
        attivaTrigger(tipo, stato, giocatore, null);
    }

    /**
     * ATTIVAZIONE TRIGGER (Versione Completa con Contesto)
     */
    public void attivaTrigger(TipoTrigger tipo, StatoDiGioco stato, Giocatore giocatore, Object contesto) {
        if (!registro.containsKey(tipo)) return;

        List<TriggerRegistrato> listaTrigger = registro.get(tipo);
        Iterator<TriggerRegistrato> iterator = listaTrigger.iterator();

        while (iterator.hasNext()) {
            TriggerRegistrato trigger = iterator.next();

            // Verifica Condizioni Specifiche
            if (!verificaCondizioni(tipo, trigger, stato, giocatore, contesto)) {
                continue;
            }

            // Esecuzione Effetti
            // System.out.println("[TRIGGER] Attivato " + tipo + " da carta " + trigger.sorgente.getNome());
            for (Effetto effetto : trigger.effetti) {
                EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatore);
            }

            // Gestione "Attivato 1 Volta"
            if (trigger.attivatoUnaVolta) {
                iterator.remove();
            }
        }
    }

    private boolean verificaCondizioni(TipoTrigger tipo, TriggerRegistrato trigger, StatoDiGioco stato, Giocatore giocatore, Object contesto) {
        Carta cartaContesto = (contesto instanceof Carta) ? (Carta) contesto : null;

        switch (tipo) {
            case ACQUISTA_CARTA_UMBRIDGE:
                if (cartaContesto != null && cartaContesto.getCosto() < 4) return false; 
                break;
            case COSTO_MINORE_1: 
                if (cartaContesto == null) return false;
                // Controlla se la carta tira un dado (semplificazione)
                boolean tiraDado = false;
                if (cartaContesto.getEffetti() != null) {
                    for (Effetto e : cartaContesto.getEffetti()) {
                        if (e.getType().toString().startsWith("DADO_")) { tiraDado = true; break; }
                    }
                }
                if (!tiraDado) return false;
                break;

            
            case INCANTESIMI_GIOCATI:
            	// Nota: Richiede che tu tenga traccia delle carte giocate nel turno. 
                int countSpell = 0;
                if (stato.getAlleatiGiocatiInQuestoTurno() != null) {
                }
                return true; // Placeholder per evitare blocchi

            case TUTTE_TIPOLOGIE: // Per Diadema Corvonero
                boolean ally = false, spell = false, item = false;
                for(Carta c : giocatore.getMano()) {
                    if("Alleato".equalsIgnoreCase(c.getClasse())) ally = true;
                    if("Incantesimo".equalsIgnoreCase(c.getClasse())) spell = true;
                    if("Oggetto".equalsIgnoreCase(c.getClasse())) item = true;
                }
                if (!(ally && spell && item)) return false;
                break;
                
            case SALUTE_MASSIMA: // Per Pozioni Avanzate
                if (giocatore.getSalute() < giocatore.getSaluteMax()) return false;
                break;

            case AGGIUNTA_MARCHIO_NERO: // Per Draco Malfoy

                return true;

            default:
                return true;
        }
        return true;
    }
}