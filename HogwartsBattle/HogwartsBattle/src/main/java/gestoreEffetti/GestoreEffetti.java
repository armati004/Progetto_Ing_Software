package gestoreEffetti;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import carte.Carta;

public class GestoreEffetti {
	private final Map<TipoEffetto, Set<Carta>> effettiAttivi = new EnumMap<>(TipoEffetto.class);
	private final Set<Carta> carteTemporanee = new HashSet<>();
	
	public void aggiungiEffetto(TipoEffetto effetto, Carta carta) {
		this.effettiAttivi.computeIfAbsent(effetto, _ -> new HashSet<>()).add(carta);
	}
	
	public void aggiungiEffettoTemporaneo(TipoEffetto effetto, Carta carta) {
		this.aggiungiEffetto(effetto, carta);
		this.carteTemporanee.add(carta);
	}
	
	public void rimuoviEffetto(Carta carta) {
		for(Set<Carta> carte : this.effettiAttivi.values()) {
			carte.remove(carta);
		}
	}
	
	public void fineTurno() {
		for(Carta cartaTemporanea : carteTemporanee) {
			this.rimuoviEffetto(cartaTemporanea);
		}
		
		this.carteTemporanee.clear();
	}
	
	public Boolean regolaAttiva(TipoEffetto effetto) {
		return this.effettiAttivi.containsKey(effetto) && !this.effettiAttivi.get(effetto).isEmpty();
	}
}
