package carte;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import gioco.Giocatore;
import gioco.StatoDiGioco;
import gestoreEffetti.*;

public class Carta {
	private final String nome;
	private final String id;
	@SerializedName("class")
	private final String classe;
	private final String descrizione;
	private final int costo;
	@SerializedName("path-img")
	private final String pathImmagine;
	private List<Effetto> effetti;
	private List<Trigger> triggers;
	
	public Carta(String nome, String id, String classe, String descrizione, int costo,String pathImmagine, 
			List<Effetto> effetti, List<Trigger> triggers) {
		this.nome = nome;
		this.id = id;
		this.classe = classe;
		this.descrizione = descrizione;
		this.costo = costo;
		this.pathImmagine = pathImmagine;
		this.setEffetti(effetti);
		this.setTriggers(triggers);
	}
	
	public void applicaEffetto(StatoDiGioco stato, Giocatore attivo) {
		for(Effetto effetto : this.getEffetti()) {
			if(effetto.getDurata() == DurataEffetto.ISTANTANEO) {
				EsecutoreEffetti.eseguiEffetto(effetto, stato, attivo);
			}
			else if(effetto.getDurata() == DurataEffetto.TEMPORANEO) {
				stato.getGestoreEffetti().aggiungiEffettoTemporaneo(effetto.getType(), this);
			}
			else if(effetto.getDurata() == DurataEffetto.CONTINUO) {
				stato.getGestoreEffetti().aggiungiEffetto(effetto.getType(), this);
			}
		}
		
		if(this.getTriggers() != null && !(this.getTriggers().isEmpty())) {
			for(Trigger trigger : this.getTriggers()) {
				stato.getGestoreTrigger().registraTrigger(trigger.getType(), this.getEffetti(), this);
			}
		}
	}
	
	public String getNome() {
		return nome;
	}

	public String getId() {
		return id;
	}

	public String getClasse() {
		return classe;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public int getCosto() {
		return costo;
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
