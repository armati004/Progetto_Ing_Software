package carte;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;
import grafica.Entita;

public class Horcrux extends Carta {
	
	private List<Entita> segnaliniRichiesti;
	
	private Set<Entita> segnaliniAssegnati;
	
	private List<Effetto> reward;
	
	public Horcrux(String nome, String id, String classe, String descrizione, int costo, String pathImmagine,
			List<Effetto> effetti, List<Trigger> triggers, List<Entita> segnaliniRichiesti, List<Effetto> reward) {
		super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
		this.segnaliniRichiesti = segnaliniRichiesti;
		this.segnaliniAssegnati = new HashSet<>();
		this.reward = reward;
	}
	
	public Boolean applicaRisultatoDado(Entita facciaDado) {
		if (segnaliniRichiesti.contains(facciaDado) && !segnaliniAssegnati.contains(facciaDado)) {
			segnaliniAssegnati.add(facciaDado);
			return true;
		}
		return false;
	}
	
	//Aggiungi metodo per i reward
	
	public Boolean horcruxDistrutto() {
		return segnaliniAssegnati.containsAll(segnaliniRichiesti);
	}

	public List<Entita> getSegnaliniRichiesti() {
		return segnaliniRichiesti;
	}

	public List<Effetto> getReward() {
		return reward;
	}

}
