package carte;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;
import grafica.Entita;

public class Horcrux extends Carta {
	
	@SerializedName("destroyCondition")
	private List<Entita> segnaliniRichiesti;
	
	private Set<Entita> segnaliniAssegnati;
	
	public Horcrux(String nome, String id, String classe, String descrizione, int costo, String pathImmagine,
			List<Effetto> effetti, List<Trigger> triggers, List<Entita> segnaliniRichiesti) {
		super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
		this.segnaliniRichiesti = segnaliniRichiesti;
		this.segnaliniAssegnati = new HashSet<>();
	}
	
	public Boolean applicaRisultatoDado(Entita facciaDado) {
		return false;
	}
	
	public Boolean horcruxDistrutto() {
		return segnaliniAssegnati.containsAll(segnaliniRichiesti);
	}

}
