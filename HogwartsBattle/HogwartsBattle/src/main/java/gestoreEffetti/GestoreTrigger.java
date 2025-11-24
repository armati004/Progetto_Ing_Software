package gestoreEffetti;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import carte.Carta;

import gioco.Giocatore;
import gioco.StatoDiGioco;

public class GestoreTrigger {
	private final Map<TipoTrigger, List<TriggerAttivato>> registro = new EnumMap<>(TipoTrigger.class);
	
	public void registraTrigger(TipoTrigger tipo, List<Effetto> effetti, Carta sorgente) {
		TriggerAttivato attivato = new TriggerAttivato(sorgente, effetti);
		this.registro.computeIfAbsent(tipo, _ -> new ArrayList<>()).add(attivato);
	}
	
	public void rimuoviTrigger(Carta sorgente) {
		for(List<TriggerAttivato> lista : registro.values()) {
			lista.removeIf(attivato -> attivato.getSorgente().equals(sorgente));
		}
	}
	
	public void attivaTrigger(TipoTrigger tipo, StatoDiGioco stato, Giocatore giocatore) {
		if(!(registro.containsKey(tipo))) {
			return;
		}
		
		List<TriggerAttivato> triggerDaAttivare = registro.get(tipo);
		
		for(TriggerAttivato attivato : triggerDaAttivare) {
			for(Effetto effetto : attivato.getEffetti()) {
				EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatore);
			}
		}
	}
}

class TriggerAttivato {
	private Carta sorgente;
	private List<Effetto> effetti;
	
	public TriggerAttivato(Carta sorgente, List<Effetto> effetti) {
		this.sorgente = sorgente;
		this.effetti = effetti;
	}

	public Carta getSorgente() {
		return sorgente;
	}

	public List<Effetto> getEffetti() {
		return effetti;
	}
}
