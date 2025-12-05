package carte;

import java.util.List;
import java.util.Set;

import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;
import grafica.Entita;

public class Horcrux extends Carta {
	
	private List<Entita> segnaliniRichiesti;
	
	private Set<Entita> segnaliniAssegnati;
	
	public Horcrux(String nome, String id, String classe, String descrizione, int costo, String pathImmagine,
			List<Effetto> effetti, List<Trigger> triggers) {
		super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
		
	}
	
	public Boolean applicaRisultatoDado(Entita facciaDado) {
		return false;
	}
	
	public Boolean horcruxDistrutto() {
		return segnaliniAssegnati.containsAll(segnaliniRichiesti);
	}

}
