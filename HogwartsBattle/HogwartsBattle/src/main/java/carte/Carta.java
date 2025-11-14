package carte;

import java.util.List;

public class Carta {
	private final String nome;
	private final String id;
	private final String descrizione;
	private final String pathImmagine;
	private List<Effetto> effetti;
	private List<Trigger> triggers;
	
	public Carta(String nome, String id, String descrizione, String pathImmagine, 
			List<Effetto> effetti, List<Trigger> triggers) {
		this.nome = nome;
		this.id = id;
		this.descrizione = descrizione;
		this.pathImmagine = pathImmagine;
		this.setEffetti(effetti);
		this.setTriggers(triggers);
	}
	
	public String getNome() {
		return nome;
	}

	public String getId() {
		return id;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public String getPathImmagine() {
		return pathImmagine;
	}

	public List<Effetto> getEffetti() {
		return effetti;
	}

	public void setEffetti(List<Effetto> effetti) {
		this.effetti = effetti;
	}

	public List<Trigger> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}
}
