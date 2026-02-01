package gestoreEffetti;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import carte.Carta;
import carte.Malvagio;
import gioco.Giocatore;
import gioco.StatoDiGioco;

public class GestoreTrigger {
	private final Map<TipoTrigger, List<TriggerAttivato>> registro = new EnumMap<>(TipoTrigger.class);
	
	/**
	 * Registra un trigger SOLO se non è già registrato per questa carta
	 * ⭐ FIX: Previene registrazioni duplicate
	 */
	public void registraTrigger(TipoTrigger tipo, List<Effetto> effetti, Carta sorgente, DurataEffetto durata) {
		// Controlla se il trigger è già registrato per questa carta
		List<TriggerAttivato> triggerEsistenti = registro.get(tipo);
		
		if (triggerEsistenti != null) {
			for (TriggerAttivato esistente : triggerEsistenti) {
				// Se è già registrato per questa carta specifica, non aggiungere
				if (esistente.getSorgente() == sorgente) {
					System.out.println("Trigger " + tipo + " già registrato per " + sorgente.getNome() + " - SALTATO");
					return;
				}
			}
		}
		
		// Se non è duplicato, registra
		TriggerAttivato attivato = new TriggerAttivato(sorgente, effetti, durata);
		this.registro.computeIfAbsent(tipo, _ -> new ArrayList<>()).add(attivato);
		System.out.println("Trigger " + tipo + " registrato per " + sorgente.getNome());
	}
	
	/**
	 * Verifica se esiste un trigger di un certo tipo
	 */
	public boolean hasTrigger(TipoTrigger tipo) {
	    List<TriggerAttivato> triggers = registro.get(tipo);
	    return triggers != null && !triggers.isEmpty();
	}
	
	public void rimuoviTrigger(Carta sorgente) {
		for(List<TriggerAttivato> lista : registro.values()) {
			lista.removeIf(attivato -> attivato.getSorgente().equals(sorgente));
		}
	}
	
	public void rimuoviTriggerFineTurno() {
		for(List<TriggerAttivato> lista : registro.values()) {
			lista.removeIf(attivato -> attivato.getDurata() == DurataEffetto.TEMPORANEO);
		}
	}
	
	public void attivaTrigger(TipoTrigger tipo, StatoDiGioco stato, Giocatore giocatore) {
		if(!(registro.containsKey(tipo))) {
			return;
		}
		
		List<TriggerAttivato> triggerDaAttivare = registro.get(tipo);
		
		for(TriggerAttivato attivato : triggerDaAttivare) {
			if(attivato.getSorgente() instanceof Malvagio) {
				if(!((Malvagio)attivato.getSorgente()).getBloccoAbilita()) {
					for(Effetto effetto : attivato.getEffetti()) {
						EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatore, attivato.getSorgente());
					}
				}
			}
			else {
				for(Effetto effetto : attivato.getEffetti()) {
					EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatore, attivato.getSorgente());
				}
			}
		}
	}
}

class TriggerAttivato {
	private Carta sorgente;
	private List<Effetto> effetti;
	private DurataEffetto durata;
	
	public TriggerAttivato(Carta sorgente, List<Effetto> effetti, DurataEffetto durata) {
		this.sorgente = sorgente;
		this.effetti = effetti;
		this.durata = durata;
	}

	public Carta getSorgente() {
		return sorgente;
	}

	public List<Effetto> getEffetti() {
		return effetti;
	}
	
	public DurataEffetto getDurata() {
		return durata;
	}
}
