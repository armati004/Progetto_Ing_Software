package gioco;

import java.util.List;

import gestoreEffetti.GestoreEffetti;
import gestoreEffetti.GestoreTrigger;

public class StatoDiGioco {
	private List<Giocatore> giocatori;
	private GestoreEffetti gestoreEffetti;
	private GestoreTrigger gestoreTrigger;
	
	
	
	public List<Giocatore> getGiocatori() {
		return giocatori;
	}
	public void setGiocatori(List<Giocatore> giocatori) {
		this.giocatori = giocatori;
	}
	public GestoreEffetti getGestoreEffetti() {
		return gestoreEffetti;
	}
	public void setGestoreEffetti(GestoreEffetti gestoreEffetti) {
		this.gestoreEffetti = gestoreEffetti;
	}
	public GestoreTrigger getGestoreTrigger() {
		return gestoreTrigger;
	}
	public void setGestoreTrigger(GestoreTrigger gestoreTrigger) {
		this.gestoreTrigger = gestoreTrigger;
	}
}
