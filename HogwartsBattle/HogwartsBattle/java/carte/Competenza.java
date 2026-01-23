package carte;

import java.util.List;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

public class Competenza extends Carta {
	private Boolean attivabile; // True se si può attivare manualmente
	private String commento; // Descrizione extra/commento

	public Competenza(String nome, String id, String classe, String descrizione, String pathImmagine, Boolean attivabile,
			List<Effetto> effetti, List<Trigger> triggers, String commento) {
		super(nome, id, classe, descrizione, 0, pathImmagine, effetti, triggers);
		this.attivabile = attivabile;
		this.commento = commento;
	}

	public Boolean isAttivabile() {
		return attivabile;
	}

	public String getCommento() {
		return commento;
	}

	public void setCommento(String commento) {
		this.commento = commento;
	}
}
