package carte;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gestoreEffetti.Effetto;
import gestoreEffetti.EsecutoreEffetti;
import gestoreEffetti.Trigger;
import gioco.Giocatore;
import gioco.StatoDiGioco;
import grafica.Entita;
import java.util.Collections; // assicurati che sia importato in testa al file

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
    
    public Boolean horcruxDistrutto() {
        return segnaliniAssegnati != null
        &&segnaliniRichiesti != null
        &&segnaliniAssegnati.containsAll(segnaliniRichiesti);
    }

    public List<Entita> getSegnaliniRichiesti() {
        return segnaliniRichiesti;
    }

    public List<Effetto> getRicompensa() {
        return reward == null ? Collections.emptyList() : Collections.unmodifiableList(reward);
    }

    public void applicaRicompensa(StatoDiGioco stato, Giocatore g) {
        if (reward == null) return;
        for (Effetto e : reward) {
            if (e != null) {
                EsecutoreEffetti.eseguiEffetto(e, stato, g);;
            }
        }
    }

}
